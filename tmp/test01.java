package test.tmp;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.base.EEGRecordReader;

import java.io.File;
import java.util.Random;

/**
 * Created by CTC0138 on 2016/12/06.
 */
public class test01 {
    private static final Logger log = LoggerFactory.getLogger(test01.class);

    //対象フォルダパス(分割したtxtファイル)
    private static String DATA_ROOT = "src/main/resources/data/output/";

    // 入力データ許可種類：txtファイル
    public static final String[] ALLOWED_FORMATS = new String[]{"txt"};

    // 電極
    protected static int electrodeLength = 16;

    // 持続時間
    protected static int timeLength = 239766/600;

    // Filterの個数（入力、出力層以外の層数）
    protected static int channels = 16;

    //80%訓練、20%テスト
    protected static double splitTrainTest = 0.8;

    // 訓練データ数（ラベルありデータ数）
    protected static int numExamples = 6;
    // ラベル数量、データ種類（発作前：preictal、発作間：interictal）
    protected static int numLabels = 2;
    // バッチサイズ（毎回の訓練対象数）
    protected static int batchSize = 10;
    protected static long seed = 42;
    // 乱数生成
    protected static Random rng = new Random(seed);
    // 訓練ステップ数
    protected static int epochs = 3;
    // データグループ数
    protected static int nCores = 2;
    // パラメータの更新より、scoreの出力頻率
    protected static int listenerFreq = 1;
    // 繰り返し数
    protected static int iterations = 1;
    // The number of possible outcomes
    protected static int numOfClasses = 1;


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
        //対象フォルダパス(Matファイル)
        File mainPath = new File(System.getProperty("user.dir"), DATA_ROOT);
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
        InputSplit[] inputSplit = fileSplit.sample(pathFilter, 80, 20);
        // 訓練データ
        InputSplit trainData = inputSplit[0];
        // テストデータ
        InputSplit testData = inputSplit[1];

        /**
         * Data Setup -> define how to load data into net://データを元に、ネットを作る
         *  - recordReader = the reader that loads and converts mat data pass in inputSplit to initialize
         *  - dataIter = a generator that only loads one batch at a time into memory to save memory
         *  - trainIter = uses MultipleEpochsIterator to ensure model runs through the data for all epochs
         **/
        // Matデータを読み込んで、Readerにセット
        RecordReader recordReader = new EEGRecordReader(channels,labelMaker);
        // 訓練データを読み込む
        recordReader.initialize(trainData);

        // データIterator
        DataSetIterator dataIter;
        // 訓練Iterator
        MultipleEpochsIterator trainIter;



        // 訓練（without transformations）
        // データIteratorを作成（Reader、バッチサイズ、ラベルIndex、ラベル数量）
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);

        // TODO 訓練データのdataSetをコンソールで出力
//        while (dataIter.hasNext()) {
//
//            DataSet ds = dataIter.next();
//            log.info(ds.toString());
//            printMinMax(ds);
//        }

        /**
         * Data Setup -> normalization 前処理
         *  - how to normalize images and generate large dataset to train on
         **/
        //TODO 周波数範囲（1～47Hz）
        /*
        DataSet ds = dataIter.next();
        System.out.println("===========> Before Normalize <=================");
        printMinMax(ds);

        DataNormalization scaler = new NormalizerMinMaxScaler(1,47);
        scaler.fit(ds);
        scaler.transform(ds);
        System.out.println("===========> After Normalize <=================");
        printMinMax(ds);*/


        // TODO 訓練データ標準化(mean 0,unit variance)
        /*
        DataSet ds = dataIter.next();
        System.out.println("===========> Before Normalize <=================");
        System.out.println(ds.getFeatures());
        DataNormalization scaler = new NormalizerStandardize();
        scaler.fit(ds);
        scaler.transform(ds);
        System.out.println("===========> After Normalize <=================");
        System.out.println(ds.getFeatures());*/

        DataNormalization scaler = new NormalizerStandardize();
        // 規範化情報取得
        scaler.fit(dataIter);
        // データ規範化
        dataIter.setPreProcessor(scaler);


        // 訓練Iteratorを作成（訓練ステップ、データIterator、データグループ数）
        trainIter = new MultipleEpochsIterator(epochs, dataIter, nCores);

        log.info("Build model....");
        // モデル構築
        MultiLayerNetwork network;
//        network = customModel();
//        network = customModel_default();
        network = lenetModel();
        // ネット初期化
        network.init();
        // パラメータを更新する際に、Scoreを出力するため
        // Listenerを設定
        network.setListeners(new ScoreIterationListener(listenerFreq));

        // 訓練
        log.info("Train model....");
        network.fit(trainIter);

        // モデルのテスト
        log.info("Evaluate model....");
        // テストデータを読み込む
        recordReader.initialize(testData);
        // テストデータIteratorを作成（データReader、バッチサイズ、ラベルIndex、ラベル数量）
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);


        // TODO テストデータ標準化
//        // 規範化情報取得
//        scaler.fit(dataIter);
//        // データ規範化
//        dataIter.setPreProcessor(scaler);

        // モデル評価
        Evaluation eval = network.evaluate(dataIter);
        // 評価結果を出力
        log.info(eval.stats(true));


    }

    private static void printMinMax(DataSet dataSet){
        System.out.println("Min: " + dataSet.getFeatures().min(new int[]{0}));
        System.out.println("Max: " + dataSet.getFeatures().max(new int[]{0}));
    }

    // モデル作成
    public static MultiLayerNetwork customModel() {

        int seed = 123;
        int iterations = 1;

        WeightInit weightInit = WeightInit.XAVIER;
        String activation = "relu";
        Updater updater = Updater.NESTEROVS;
        double lr = 1e-3;
        double mu = 0.9;
        double l2 = 5e-4;
        boolean regularization = true;

        SubsamplingLayer.PoolingType poolingType = SubsamplingLayer.PoolingType.MAX;
        double nonZeroBias = 1;
        double dropOut = 0.5;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).iterations(iterations)
                .activation(activation).weightInit(weightInit)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).learningRate(lr).momentum(mu)
                .regularization(regularization).l2(l2).updater(updater).useDropConnect(true)

                // AlexNet
                .list()
                .layer(0,
                        new ConvolutionLayer.Builder(new int[] { 6, 6 }, new int[] { 4, 4 }, new int[] { 3, 3 })
                                .name("cnn1").nIn(channels).nOut(6).build())
                .layer(1, new LocalResponseNormalization.Builder().name("lrn1").build())
                .layer(2,
                        new SubsamplingLayer.Builder(poolingType, new int[] { 1, 1 }, new int[] { 2, 2 })
                                .name("maxpool1").build())
                .layer(3,
                        new ConvolutionLayer.Builder(new int[] { 4, 4 }, new int[] { 1, 1 }, new int[] { 2, 2 })
                                .name("cnn2").nOut(channels).biasInit(nonZeroBias).build())
                .layer(4,
                        new LocalResponseNormalization.Builder().name("lrn2").k(2).n(5).alpha(1e-4).beta(0.75).build())
                .layer(5,
                        new SubsamplingLayer.Builder(poolingType, new int[] { 2, 2 }, new int[] { 2, 2 })
                                .name("maxpool2").build())
                .layer(6,
                        new ConvolutionLayer.Builder(new int[] { 2, 2 }, new int[] { 1, 1 }, new int[] { 1, 1 })
                                .name("cnn3").nOut(channels).build())
                .layer(7,
                        new ConvolutionLayer.Builder(new int[] { 2, 2 }, new int[] { 1, 1 }, new int[] { 1, 1 })
                                .name("cnn4").nOut(channels).biasInit(nonZeroBias).build())
                .layer(8,
                        new ConvolutionLayer.Builder(new int[] { 2, 2 }, new int[] { 1, 1 }, new int[] { 1, 1 })
                                .name("cnn5").nOut(channels).biasInit(nonZeroBias).build())
                .layer(9,
                        new SubsamplingLayer.Builder(poolingType, new int[] { 2, 2 }, new int[] { 2, 2 })
                                .name("maxpool3").build())
                .layer(10,
                        new DenseLayer.Builder().name("ffn1").nOut(4096).biasInit(nonZeroBias).dropOut(dropOut).build())
                .layer(11,
                        new DenseLayer.Builder().name("ffn2").nOut(4096).biasInit(nonZeroBias).dropOut(dropOut).build())
                .layer(12,
                        new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).name("output")
                                .nOut(numOfClasses).activation("softmax").build())
                .backprop(true).pretrain(false)
                .cnnInputSize(1, 299, 1)
                .build();

        return new MultiLayerNetwork(conf);

    }

    // モデル作成
    public static MultiLayerNetwork customModel_default() {

        // ドロップアウトの確率
        double dropOut = 0.5;

        // conf設定
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                // 乱数生成
                .seed(seed)
                // 繰り返し回数
                .iterations(iterations)
                // 学習率
                .learningRate(1e-6f)
                // 共役勾配法を使用
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT)
                // L1正則化とL2正則化の値を指定
                .l1(1e-1).regularization(true).l2(2e-4)
                // ドロップアウトを行う
                .useDropConnect(true)
                // 各層の値を格納するリストを設定
                .list()
                // 0層
                .layer(0,new RBM.Builder(RBM.HiddenUnit.RECTIFIED,RBM.VisibleUnit.GAUSSIAN)
                        // 入力層のユニットサイズ
                        .nIn(timeLength)
                        // 出力ラベル（クラス）の数
                        .nOut(3)
                        // XAVIERという一様分布で重みを初期化
                        .weightInit(WeightInit.XAVIER)
                        .k(1)
                        // 活性化関数にReLUを指定
                        .activation("relu")
                        // 損失関数に2乗平均平方根誤差を指定
                        .lossFunction(LossFunctions.LossFunction.RMSE_XENT)
                        // ADAGRADで学習率を調整
                        .updater(Updater.ADADELTA)
                        // ドロップアウトの確率
                        .dropOut(dropOut)
                        .build()
                )
                // 1層
                .layer(1,new RBM.Builder(RBM.HiddenUnit.RECTIFIED,RBM.VisibleUnit.GAUSSIAN)
                        // 入力層のユニットサイズ
                        .nIn(3)
                        // 出力ラベル（クラス）の数
                        .nOut(2)
                        // XAVIERという一様分布で重みを初期化
                        .weightInit(WeightInit.XAVIER)
                        .k(1)
                        // 活性化関数にReLUを指定
                        .activation("relu")
                        // 損失関数に2乗平均平方根誤差を指定
                        .lossFunction(LossFunctions.LossFunction.RMSE_XENT)
                        // ADAGRADで学習率を調整
                        .updater(Updater.ADADELTA)
                        // ドロップアウトの確率
                        .dropOut(dropOut)
                        .build()
                )
                // 2層　出力層の設定
                .layer(2,new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        // 入力層のユニットサイズ
                        .nIn(2)
                        // 出力ラベル（クラス）の数
                        .nOut(numLabels)
                        // 活性化関数にsoftmaxを指定
                        .activation("softmax")
                        .build()
                )
                // 入力データタイプ指定(一行)
//                .setInputType(InputType.convolutionalFlat(1,timeLength,1))
                .build();

        //　モデルを作成して、戻る
        return new MultiLayerNetwork(conf);

    }

    // モデル作成(RBM)
    public static MultiLayerNetwork customModel_RMB() {
        /**
         * AlexNet model interpretation based on the original paper ImageNet Classification with Deep Convolutional Neural Networks
         * and the imagenetExample code referenced.
         * http://papers.nips.cc/paper/4824-imagenet-classification-with-deep-convolutional-neural-networks.pdf
         **/

        // ドロップアウトの確率
        double dropOut = 0.5;

        // conf設定
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                // 乱数生成
                .seed(seed)
                // 繰り返し回数
                .iterations(iterations)
                // 学習率
                .learningRate(1e-6f)
                // 共役勾配法を使用
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT)
                // L1正則化とL2正則化の値を指定
                .l1(1e-1).regularization(true).l2(2e-4)
                // ドロップアウトを行う
                .useDropConnect(true)
                // 各層の値を格納するリストを設定
                .list()
                // 0層
                .layer(0,new RBM.Builder(RBM.HiddenUnit.RECTIFIED,RBM.VisibleUnit.GAUSSIAN)
                        // 入力層のユニットサイズ
                        .nIn(timeLength)
                        // 出力ラベル（クラス）の数
                        .nOut(3)
                        // XAVIERという一様分布で重みを初期化
                        .weightInit(WeightInit.XAVIER)
                        .k(1)
                        // 活性化関数にReLUを指定
                        .activation("relu")
                        // 損失関数に2乗平均平方根誤差を指定
                        .lossFunction(LossFunctions.LossFunction.RMSE_XENT)
                        // ADAGRADで学習率を調整
                        .updater(Updater.ADADELTA)
                        // ドロップアウトの確率
                        .dropOut(dropOut)
                        .build()
                )
                // 1層
                .layer(1,new RBM.Builder(RBM.HiddenUnit.RECTIFIED,RBM.VisibleUnit.GAUSSIAN)
                        // 入力層のユニットサイズ
                        .nIn(3)
                        // 出力ラベル（クラス）の数
                        .nOut(2)
                        // XAVIERという一様分布で重みを初期化
                        .weightInit(WeightInit.XAVIER)
                        .k(1)
                        // 活性化関数にReLUを指定
                        .activation("relu")
                        // 損失関数に2乗平均平方根誤差を指定
                        .lossFunction(LossFunctions.LossFunction.RMSE_XENT)
                        // ADAGRADで学習率を調整
                        .updater(Updater.ADADELTA)
                        // ドロップアウトの確率
                        .dropOut(dropOut)
                        .build()
                )
                // 2層　出力層の設定
                .layer(2,new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        // 入力層のユニットサイズ
                        .nIn(2)
                        // 出力ラベル（クラス）の数
                        .nOut(numLabels)
                        // 活性化関数にsoftmaxを指定
                        .activation("softmax")
                        .build()
                )
                // 入力データタイプ指定(一行)
//                .setInputType(InputType.convolutionalFlat(1,timeLength,1))
                .build();

        //　モデルを作成して、戻る
        return new MultiLayerNetwork(conf);

    }

    public static MultiLayerNetwork lenetModel() {
        /**
         * Revisde Lenet Model approach developed by ramgo2 achieves slightly above random
         * Reference: https://gist.github.com/ramgo2/833f12e92359a2da9e5c2fb6333351c5
         **/
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .regularization(false).l2(0.005) // tried 0.0001, 0.0005
                .activation("relu")
                .learningRate(0.0001) // tried 0.00001, 0.00005, 0.000001
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.RMSPROP).momentum(0.9)
                .list()
                .layer(0, convInit("cnn1", channels, 50 ,  new int[]{1, 5}, new int[]{1, 1}, new int[]{0, 0}, 0))
                .layer(1, maxPool("maxpool1", new int[]{1,2}))
                .layer(2, conv5x5("cnn2", 100, new int[]{1, 5}, new int[]{1, 1}, 0))
                .layer(3, maxPool("maxool2", new int[]{1,2}))
                .layer(4, new DenseLayer.Builder().nOut(500).build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(numLabels)
                        .activation("softmax")
                        .build())
                .backprop(true).pretrain(false)
                .cnnInputSize(1, 299, 1)
                .build();

        return new MultiLayerNetwork(conf);

    }

    public static MultiLayerNetwork alexnetModel() {
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
                //.cnnInputSize(height,width,channels)
                .build();

        return new MultiLayerNetwork(conf);

    }

    private static ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }

    private static ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {1,1}).name(name).nOut(out).biasInit(bias).build();
    }

    private static ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(new int[]{1,5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }

    private static SubsamplingLayer maxPool(String name,  int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }

    private static DenseLayer fullyConnected(String name, int out, double bias, double dropOut, Distribution dist) {
        return new DenseLayer.Builder().name(name).nOut(out).biasInit(bias).dropOut(dropOut).dist(dist).build();
    }

}
