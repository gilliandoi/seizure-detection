package test.main.bk_02;

import org.apache.commons.io.FileUtils;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.main.base.EEGRecordReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

/**
 * Created by CTC0138 on 2017/04/06.
 */
public class CNNEEGReader {

    private static final Logger log = LoggerFactory.getLogger(CNNEEGReader.class);

    //対象フォルダパス(分割した画像ファイル)
    //テストデータ
    private static String DATA_ROOT = "C:/R/output/test/";

    // 入力データ許可種類：bmpファイル
    public static final String[] ALLOWED_FORMATS = new String[]{"bmp"};

    public static void main(String[] args) throws Exception {
        String binFile = System.getProperty("user.dir")+"/dl4j-epilepsy/src/main/resources/convolution.bin";
        String confFile = System.getProperty("user.dir")+"/dl4j-epilepsy/src/main/resources/convolution.json";
        int nChannels = 1;
        int outputNum = 2;
        int batchSize = 500;
        int seed = 123;
        Random rng = new Random(seed);

        log.info("Load stored model ...");
        MultiLayerConfiguration confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(confFile)));
        DataInputStream dis = new DataInputStream(new FileInputStream(new File(binFile)));
        INDArray newParams = Nd4j.read(dis);
        dis.close();
        MultiLayerNetwork model = new MultiLayerNetwork(confFromJson);
        model.init();
        model.setParams(newParams);

        System.out.println(model);

        log.info("Evaluate weights....");

        log.info("Evaluate model....");

        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        RecordReader recordReader = new EEGRecordReader(nChannels,labelMaker);
        //対象フォルダパス(BMPファイル)
        File mainPath = new File(DATA_ROOT);
        //データ分割
        FileSplit fileSplit = new FileSplit(mainPath, ALLOWED_FORMATS, rng);
        recordReader.initialize(fileSplit);
        DataSetIterator dataIter =  new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);

        Evaluation eval = new Evaluation(outputNum);
        while (dataIter.hasNext()) {
            DataSet dataSet = dataIter.next();
            INDArray output = model.output(dataSet.getFeatureMatrix());
            eval.eval(dataSet.getLabels(), output);
        }
        log.info(eval.stats());

    }

}
