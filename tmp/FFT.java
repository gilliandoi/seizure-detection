package test.tmp;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import test.util.FFT4g;

/**
 * ログファイル出力テスト
 * Created by CTC0138 on 2017/03/15.
 */
public class FFT {
    public static void main(String args[]) {
        double[] data = {1, 2, 3, 4, 5, 6, 7, 8};
        int n = data.length;
        FFT4g fft = new FFT4g(n);
        fft.rdft(1, data);
        for(int k = 0; k < n; k++){
            System.out.println("" + k + "," + data[k]);
        }
    }
}
