package test.tmp;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.FileRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by CTC0138 on 2016/12/06.
 */
public class test01_bk {
    private static final Logger log = LoggerFactory.getLogger(test01_bk.class);

    private static String DATA_ROOT = "src/main/resources/data/";

    // 入力データ許可種類：csvファイル
    public static final String[] ALLOWED_FORMATS = new String[]{"csv"};

    //80%訓練、20%テスト
    protected static double splitTrainTest = 0.8;

    // 訓練データ数（ラベルありデータ数）
    protected static int numExamples = 6;
    // データ種類（発作前：preictal、発作間：interictal）
    protected static int numLabels = 2;
    // 毎回の訓練対象数
    protected static int batchSize = 6;
    protected static long seed = 42;
    // 乱数生成
    protected static Random rng = new Random(seed);
    // 訓練ステップ数
    protected static int epochs = 50;
    // データケース数
    protected static int nCores = 2;


    protected static int height = 100;
    protected static int width = 100;
    protected static int iterations = 1;
    protected static int channels = 3;

    //Matrixの表示上限（ログ）
    private static int max = 400;

    public static void main(String[] args) throws Exception {


        log.info("Load data....");
        /**cd
         * Data Setup -> organize and limit data file paths:
         *  - mainPath = path to csv files
         *  - fileSplit = define basic dataset split with limits on format
         *  - pathFilter = define additional file load filter to limit size and balance batch content
         **/
        // ファイル名を元に、ラベルを生成する
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        //対象フォルダパス(CSVファイル)
        File mainPath = new File(System.getProperty("user.dir"), DATA_ROOT+"output/");
        //データ分割
        FileSplit fileSplit = new FileSplit(mainPath, ALLOWED_FORMATS, rng);
        //各訓練バッチ中のラベル数量を平衡する
        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, labelMaker, numExamples, numLabels, batchSize);

        /**
         * Data Setup -> train test split
         *  - inputSplit = define train and test split
         **/
        // 訓練データ、テストデータを分割する
        // InputSplit[] inputSplit = fileSplit.sample(pathFilter, numExamples * (1 + splitTrainTest), numExamples * (1 - splitTrainTest));
        InputSplit[] inputSplit = fileSplit.sample(pathFilter,80,20);
        // 訓練データ
        InputSplit trainData = inputSplit[0];
        // テストデータ
        InputSplit testData = inputSplit[1];

        log.info("Build model....");
        MultiLayerNetwork network;
        network = alexnetModel();

        /**
         * Data Setup -> define how to load data into net://データを元に、ネットを作る
         *  - recordReader = the reader that loads and converts mat data pass in inputSplit to initialize
         *  - dataIter = a generator that only loads one batch at a time into memory to save memory
         *  - trainIter = uses MultipleEpochsIterator to ensure model runs through the data for all epochs
         **/

        RecordReader recordReader = new FileRecordReader();
        DataSetIterator dataIter;
        MultipleEpochsIterator trainIter;

        log.info("Train model....");

        // Train without transformations
        recordReader.initialize(trainData);
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);

//        scaler.fit(dataIter);
//        dataIter.setPreProcessor(scaler);
        trainIter = new MultipleEpochsIterator(epochs, dataIter, nCores);

        if(network != null) {
            network.fit(trainIter);
        }

        log.info("End");

    }

    public static MultiLayerNetwork alexnetModel() {
        return null;
    }

    public static MultiLayerNetwork alexnetModel2() {
        /**
         * AlexNet model interpretation based on the original paper ImageNet Classification with Deep Convolutional Neural Networks
         * and the imagenetExample code referenced.
         * http://papers.nips.cc/paper/4824-imagenet-classification-with-deep-convolutional-neural-networks.pdf
         **/

        double nonZeroBias = 1;
        double dropOut = 0.5;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.DISTRIBUTION)
                .dist(new NormalDistribution(0.0, 0.01))
                .activation("relu")
                .updater(Updater.NESTEROVS)
                .iterations(iterations)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(1e-2)
                .biasLearningRate(1e-2*2)
                .learningRateDecayPolicy(LearningRatePolicy.Step)
                .lrPolicyDecayRate(0.1)
                .lrPolicySteps(100000)
                .regularization(true)
                .l2(5 * 1e-4)
                .momentum(0.9)
                .miniBatch(false)
                .list()
                .layer(0, convInit("cnn1", channels, 96, new int[]{11, 11}, new int[]{4, 4}, new int[]{3, 3}, 0))
                .layer(1, new LocalResponseNormalization.Builder().name("lrn1").build())
                .layer(2, maxPool("maxpool1", new int[]{3,3}))
                .layer(3, conv5x5("cnn2", 256, new int[] {1,1}, new int[] {2,2}, nonZeroBias))
                .layer(4, new LocalResponseNormalization.Builder().name("lrn2").build())
                .layer(5, maxPool("maxpool2", new int[]{3,3}))
                .layer(6,conv3x3("cnn3", 384, 0))
                .layer(7,conv3x3("cnn4", 384, nonZeroBias))
                .layer(8,conv3x3("cnn5", 256, nonZeroBias))
                .layer(9, maxPool("maxpool3", new int[]{3,3}))
                .layer(10, fullyConnected("ffn1", 4096, nonZeroBias, dropOut, new GaussianDistribution(0, 0.005)))
                .layer(11, fullyConnected("ffn2", 4096, nonZeroBias, dropOut, new GaussianDistribution(0, 0.005)))
                .layer(12, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .name("output")
                        .nOut(numLabels)
                        .activation("softmax")
                        .build())
                .backprop(true)
                .pretrain(false)
                .cnnInputSize(height,width,channels).build();

        return new MultiLayerNetwork(conf);

    }

    private static ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }

    private static ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {1,1}).name(name).nOut(out).biasInit(bias).build();
    }

    private static ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(new int[]{5,5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }

    private static SubsamplingLayer maxPool(String name, int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }

    private static DenseLayer fullyConnected(String name, int out, double bias, double dropOut, Distribution dist) {
        return new DenseLayer.Builder().name(name).nOut(out).biasInit(bias).dropOut(dropOut).dist(dist).build();
    }

    /**
     * used for testing and training
     *
     * @param csvFileClasspath
     * @param batchSize
     * @param labelIndex
     * @param numClasses
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static DataSetIterator readCSVDataset(
            String csvFileClasspath, int batchSize, int labelIndex, int numClasses)
            throws IOException, InterruptedException{

        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new File(csvFileClasspath)));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr,batchSize,labelIndex,numClasses, true);
        return iterator;
    }

}
