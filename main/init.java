package test.main;

import org.apache.commons.io.FileUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.setup.ConvolutionLayerSetup;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.main.base.EEGRecordReader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by CTC0138 on 2017/07/24.
 */
public class init {
    private static final Logger log = LoggerFactory.getLogger(init.class);

    // 入力データ許可種類：画像ファイル
    public static final String[] ALLOWED_FORMATS = new String[]{"bmp"};
    // テストデータラベル
    public static final String LABEL_NAME_TEST = "test";


    //対象フォルダパス(分割した画像ファイル)
    private static String DATA_ROOT = "C:/R/output/";
    private static String TRAIN_DATA_ROOT = DATA_ROOT + "train/";
    private static String TEST_DATA_ROOT = DATA_ROOT + "test/";

    public static void main(String[] args) throws Exception {

        log.info("\nLoad data....");

        //対象フォルダパス(BMPファイル)
        File mainPath = new File(DATA_ROOT);
        File[] files = mainPath.listFiles();

        if( files == null )
            return;
        for( File file : files ) {
            if( !file.exists() ) {
                continue;
            }
            else if( file.isFile() ) {
                moveFile(file);
            }
        }

        log.info("\nFinish");
    }

    /**
     * ファイル移動
     */
    public static void moveFile(File file) {
        String fileName = file.getName();
        String[] fileNameSplit = fileName.split("_");

        String moveToPath = "";
        if (LABEL_NAME_TEST.equals(fileNameSplit[2])){
            // テストデータ
            moveToPath = TEST_DATA_ROOT+fileName;
        }else {
            // 訓練データ
            moveToPath = TRAIN_DATA_ROOT + fileNameSplit[2] + "/" + fileName;
        }

        File file1 = file;
        File file2 = new File(moveToPath);
        if (file1.renameTo(file2)) {
            System.out.println(fileName+"　:OK!");
        } else {
            System.out.println(fileName+"　:NG!");
        }
    }

}
