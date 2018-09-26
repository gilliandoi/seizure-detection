package test.main.bk_01;

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
 * Created by CTC0138 on 2017/04/06.
 */
public class CNNEEGCreator {
    private static final Logger log = LoggerFactory.getLogger(CNNEEGCreator.class);

    // 入力データ許可種類：bmpファイル
    public static final String[] ALLOWED_FORMATS = new String[]{"bmp"};

    //対象フォルダパス(分割した画像ファイル)
    private static String DATA_ROOT = "C:/R/output/train/";

    //モデル保存
    protected static boolean save = true;

    public static void main(String[] args) throws Exception {
        int numRows = 28;
        int numColumns = 28;
        int nChannels = 1;
        int outputNum = 2;
        int numSamples = 2000;
        int batchSize = 128;
        int iterations = 1;
        int splitTrainNum = (int) (batchSize*.8);
        int seed = 123;
        // 訓練ステップ数
        int epochs = 6;
        // データグループ数
        int nCores = 2;
        Random rng = new Random(seed);
        int listenerFreq = iterations/5;
        DataSet mnist;
        SplitTestAndTrain trainTest;
        DataSet trainInput;
        String modelFilePath = System.getProperty("user.dir")+"/dl4j-epilepsy/src/main/resources/";
        String binFile = modelFilePath + "convolution.bin";
        String confFile = modelFilePath + "convolution.json";

        log.info("\nLoad data....");

        //TODO START
        // ファイル名を元に、ラベルを生成する
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        //対象フォルダパス(BMPファイル)
        File mainPath = new File(DATA_ROOT);
        //データ分割
        FileSplit fileSplit = new FileSplit(mainPath, ALLOWED_FORMATS, rng);
        //各訓練バッチ中のラベル数量を平衡する
        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, labelMaker, numSamples, outputNum, batchSize);
        InputSplit[] inputSplit = fileSplit.sample(pathFilter, 100, 0);
        // 訓練データ
        InputSplit trainData = inputSplit[0];
        // テストデータ
        // InputSplit testData = inputSplit[1];
        InputSplit testData = trainData;

        // Matデータを読み込んで、Readerにセット
        RecordReader recordReader = new EEGRecordReader(nChannels,labelMaker);
        // 訓練データを読み込む
        recordReader.initialize(trainData);

        // データIterator
        DataSetIterator dataIter;

        // 訓練（without transformations）
        // データIteratorを作成（Reader、バッチサイズ、ラベルIndex、ラベル数量）
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);

        // 訓練Iterator
        MultipleEpochsIterator trainIter;
        // 訓練Iteratorを作成（訓練ステップ、データIterator、データグループ数）
        trainIter = new MultipleEpochsIterator(epochs, dataIter, nCores);

        //TODO END

        log.info("\nBuild model....");

        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .regularization(true).l1(0.0005)
                .learningRate(0.01)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .list()
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        .nIn(nChannels)
                        .stride(1, 1)
                        .nOut(20)
//                    .dropOut(0.5)
                        .activation("relu")
                        .build())
                .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(2, new ConvolutionLayer.Builder(5, 5)
                        .nIn(nChannels)
                        .stride(1, 1)
                        .nOut(50)
//                    .dropOut(0.5)
                        .activation("relu")
                        .build())
                .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(4, new DenseLayer.Builder().activation("relu")
                        .nOut(500)
                        .build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .activation("softmax")
                        .build())
                .backprop(true).pretrain(false);
        new ConvolutionLayerSetup(builder, numRows, numColumns, nChannels);

        MultiLayerConfiguration conf = builder.build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        log.info("Train model....\n");
        model.setListeners(Arrays.asList((IterationListener) new ScoreIterationListener(listenerFreq)));
        model.fit(trainIter);

        /*
        while(dataIter.hasNext()) {
            mnist = dataIter.next();
            trainTest = mnist.splitTestAndTrain(splitTrainNum, new Random(seed)); // train set that is the result
            trainInput = trainTest.getTrain(); // get feature matrix and labels for training
            testInput.add(trainTest.getTest().getFeatureMatrix());
            testLabels.add(trainTest.getTest().getLabels());
            model.fit(trainInput);
        }*/

        log.info("\nEvaluate weights....");

        log.info("\nEvaluate model....");
        // テストデータを読み込む
        recordReader.initialize(testData);
        // テストデータIteratorを作成（データReader、バッチサイズ、ラベルIndex、ラベル数量）
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);

        List<String> labelNames=null;

        Evaluation eval = new Evaluation(outputNum);
        while (dataIter.hasNext()) {
            DataSet dataSet = dataIter.next();
            if(labelNames==null) {
                labelNames = dataSet.getLabelNames();
            }
            INDArray output = model.output(dataSet.getFeatureMatrix());
            eval.eval(dataSet.getLabels(), output);
        }

        //ラベル出力
        if(labelNames!=null) {
            for (int i = 0; i < labelNames.size(); i++) {
                log.info(i+":"+labelNames.get(i));
            }
        }

        log.info(eval.stats());

        // モデル保存
        if (save) {
            log.info("\nSave model....");
            try (OutputStream fos = new FileOutputStream(binFile);
                 DataOutputStream dos = new DataOutputStream(fos)) {
                Nd4j.write(model.params(), dos);
            }
            FileUtils.writeStringToFile(new File(confFile), model.getLayerWiseConfigurations().toJson());
        }

        // モデルパス
        log.info("\n"+modelFilePath);
        log.info("\n"+binFile);
        log.info("\n"+confFile);

        log.info("****************Example finished********************");
    }
}