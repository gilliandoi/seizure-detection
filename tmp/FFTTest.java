package test.tmp;


import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.convolution.Convolution;
import org.nd4j.linalg.factory.Nd4j;
import test.util.FFT4g;

import java.util.Arrays;

/**
 * ログファイル出力テスト
 * Created by CTC0138 on 2017/03/15.
 */
public class FFTTest {
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
