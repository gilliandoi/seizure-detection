package test.drawEEG;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.*;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.util.FFT4g;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Matファイル情報出力
 * Created by CTC0138 on 2016/12/06.
 */
public class MatInfoOutput {
    private static final Logger log = LoggerFactory.getLogger(MatInfoOutput.class);

    private static String DATA = "data";
    private static String DATA_LENGTH_SEC = "data_length_sec";
    private static String SAMPLING_FREQUENCY = "sampling_frequency";
    private static String CHANNELS = "channels";
    private static String SEQUENCE = "sequence";

    private static int WIDTH = 1200;
    private static int HEIGHT = 600;

    //Matrixの表示上限（ログ）
    private static int max = 400;

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();

        log.info("Load data....");

        //ファイルを読み込み
        //MatFileReader read = new MatFileReader("src/main/resources/data/input/interictal/Dog_1_interictal_segment_0001.mat");
        MatFileReader read = new MatFileReader("C:/R/data/Patient_8_ictal_segment_1.mat");
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

        //脳波出力
        if (data != null) {
            //脳波図出力
            double[][] dataD = data.getArray();


            int showToTime = dataD[0].length; //1200msまで、最後まで：dataD[0].length
            int showFromTime = 0; //0ms

            // 创建一个绘制波形的面板(脳波データ,電極指定(0：すべて、1～16),時間範囲From,時間範囲To)
            DrawPanel drawPanel = new DrawPanel(dataD,0,showFromTime,showToTime);
            //double[] dataD1 = dataD[0]; //获取第一声道

            frame.add(drawPanel);
            frame.setTitle("脳波出力");
            frame.setSize(WIDTH, HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

        logOutput(struct);

        System.out.println("write done!");

    }

    /**
     * Matファイルの構成をログ出力
     * @param obj
     */
    public static void logOutput(MLStructure struct) throws Exception  {
        MLDouble data = (MLDouble) struct.getField(DATA);
        MLDouble data_length_sec = (MLDouble) struct.getField(DATA_LENGTH_SEC);
        MLDouble sampling_frequency = (MLDouble) struct.getField(SAMPLING_FREQUENCY);
        MLCell channels = (MLCell) struct.getField(CHANNELS);
        MLDouble sequence = (MLDouble) struct.getField(SEQUENCE);

        //データ構造をログで出力
        if (data != null) {
            System.out.println(DATA + data);
            double[][] dataD = data.getArray();
            output(dataD);
        } else {
            System.out.println(DATA + ":null\n");
        }

        if (data_length_sec != null) {
            double[][] data_length_secD = data_length_sec.getArray();
            System.out.println(DATA_LENGTH_SEC + data_length_sec);
            output(data_length_secD);
        } else {
            System.out.println(DATA_LENGTH_SEC + ":null\n");
        }

        if (sampling_frequency != null) {
            double[][] sampling_frequencyD = sampling_frequency.getArray();
            System.out.println(SAMPLING_FREQUENCY + sampling_frequency);
            output(sampling_frequencyD);
        } else {
            System.out.println(SAMPLING_FREQUENCY + ":null\n");
        }

        System.out.println(CHANNELS + channels);
        output(channels);

        if (sequence != null) {
            double[][] sequenceD = sequence.getArray();
            System.out.println(SEQUENCE + sequence);
            output(sequenceD);
        } else {
            System.out.println(SEQUENCE + ":null\n");
        }
    }

    /**
     * CSV出力
     * @param obj
     */
    public static void csvOutput(String path,double[][] obj) throws Exception  {

        //出力先指定
        File tempFile = new File(path);
        tempFile.deleteOnExit();

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
