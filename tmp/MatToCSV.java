package test.tmp;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.*;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * matファイルをCSVファイルに一括変換
 * Created by CTC0138 on 2016/12/06.
 */
public class MatToCSV {
    private static final Logger log = LoggerFactory.getLogger(MatToCSV.class);

    private static String DATA = "data";
    private static String DATA_LENGTH_SEC = "data_length_sec";
    private static String SAMPLING_FREQUENCY = "sampling_frequency";
    private static String CHANNELS = "channels";
    private static String SEQUENCE = "sequence";
    private static String DATA_ROOT = "src/main/resources/data/";

    //Matrixの表示上限（ログ）
    private static int max = 400;

    // 入力データ許可種類
    protected static String[] ALLOWED_FORMATS = new String[]{"mat"};
    // MATファイルの拡張子
    protected static String MAT_FLG = "mat";
    // CSVファイルの拡張子
    protected static String CSV_FLG = "csv";

    // 訓練データ数（ラベルありデータ数）
    protected static int numExamples = 6;
    // データ種類（発作：preictal_segment、非発作：interictal_segment）
    protected static int numLabels = 2;
    // 毎回の訓練対象数
    protected static int batchSize = 2;
    protected static long seed = 42;
    // 乱数生成
    protected static Random rng = new Random(seed);

    /**
     * matファイルをCSVファイルに一括変換
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        log.info("Write data from .mat to .csv ...");

        /**cd
         * Data Setup -> organize and limit data file paths:
         *  - mainPath = path to image files
         *  - fileSplit = define basic dataset split with limits on format
         *  - pathFilter = define additional file load filter to limit size and balance batch content
         **/
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        //対象フォルダパス
        File mainPath = new File(System.getProperty("user.dir"), DATA_ROOT+"input/");
        //データ分割
        FileSplit folderSplit = new FileSplit(mainPath, ALLOWED_FORMATS, rng);
        //子フォルダ取得
        File rootDir = folderSplit.getRootDir();
        File[] folders=rootDir.listFiles();

        for (int i=0;i<folders.length;i++){
            //子フォルダ取得
            File[] files = folders[i].listFiles();

            //出力用フォルダパス
            String outputFolderPath=DATA_ROOT+"output/"+folders[i].getName();
            File outputFolder = new File(outputFolderPath);

            //出力用子フォルダ作成
            if(!outputFolder.exists()){
                outputFolder.mkdirs();
            }
            outputFolder.createNewFile();

            for (int j=0;j<folders.length;j++){
                log.info(files[j].getPath());

                // matファイルをCSVに変換
                String csvFilePath=outputFolderPath+"/"+files[j].getName().replaceAll(MAT_FLG,CSV_FLG);
                matFileToCSV(files[j].getPath(),csvFilePath);
                log.info("Write："+csvFilePath + " OK!");
            }
        }

        log.info("All datas have exported to CSV files.");
    }

    /**
     * matファイルをCSVに変換
     *
     * @param matFilePath matファイルパス
     * @param csvFilePath　出力CSVファイルパス
     * @throws Exception
     */
    public static void matFileToCSV(String matFilePath,String csvFilePath) throws Exception {
        //ファイルを読み込み
        MatFileReader read = new MatFileReader(matFilePath);
        Map<String, MLArray> mlArrayRetrived = read.getContent();
        Iterator it = mlArrayRetrived.values().iterator();
        MLArray mlArray = null;
        while (it.hasNext()) {
            mlArray = (MLArray) it.next();
            break;
        }

        //matファイルのデータ構造を取得
        MLStructure struct = (MLStructure) mlArray;
        MLDouble data = (MLDouble) struct.getField(DATA);

        //データ構造をログで出力
        if (data != null) {
            double[][] dataD = data.getArray();
//            System.out.println(DATA + data);

            csvOutput(csvFilePath,dataD);
            // ログでDATAを出力
            // output(dataD);

        }
    }

        /**
         * CSV出力
         * @param obj
         */
    public static void csvOutput(String path,double[][] obj) throws Exception  {

        //出力先指定
        File tempFile = new File(path);


        CSVRecordWriter writer = new CSVRecordWriter(tempFile);
        List<Writable> collection = new ArrayList();

        //ファイル書き込み
        for (int i = 0; i < obj.length; i++) {
            double[] ary = obj[i];
            StringBuffer sb = new StringBuffer();

            //行を書き込み
            for(int j = 0; j < ary.length; j++){
                sb. append(ary[j]+",");
            }

            collection.add(new Text(sb.toString()));

        }

        writer.write(collection);
    }

    /**
     * Matrix出力
     * @param obj
     */
    public static void output(double[][] obj) {

        for (int i = 0; i < obj.length; i++) {
            for (int j = 0; j < obj[i].length; j++) {
                if (j < max) {
                    System.out.print(obj[i][j] + "\t");
                } else if (j == max) {
                    System.out.print("...");
                }
            }
            System.out.println();
        }

        if (obj.length > max) {
            System.out.println("...");
        }
        System.out.println();
    }

    /**
     * Matrix出力
     * @param obj
     */
    public static void output(MLCell obj) {
        for (int i = 0; i < obj.getSize(); ++i) {
            if (i <= max) {
                MLChar mlChar = (MLChar) obj.get(i);
                System.out.print(mlChar.getString(0) + "\t");
            }
        }
        if (obj.getSize() > max) {
            System.out.println("...");
        }
        System.out.println("\n");
    }
}
