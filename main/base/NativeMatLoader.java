package test.main.base;


import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteOrder;

/**
 * オリジナルMatファイルを読み込む
 */
public class NativeMatLoader implements Serializable {
    //BMP画像サイズ指定
    //static final int WIDTH=26,HEIGHT=26;
    static final int WIDTH=183,HEIGHT=183;

    protected String DATA = "data";
    protected int channels = -1;
    protected double normalizeValue = 0.0D;
    protected boolean normalizeIfNeeded = false;

    public static final String[] ALLOWED_FORMATS = new String[]{"mat"};
    ToMat converter;

    public NativeMatLoader(int channels) {
        this.converter = null;
        this.channels = channels;
    }

    public NativeMatLoader(int channels, double normalizeValue) {
        this(channels);
        this.normalizeIfNeeded = normalizeValue > 0.0D;
        this.normalizeValue = normalizeValue;
    }

    public String[] getAllowedFormats() {
        return ALLOWED_FORMATS;
    }

    public INDArray asRowVector(File f) throws IOException {
        return this.asMatrix(f).ravel();
    }

    public INDArray asRowVector(Mat image) throws IOException {
        return this.asMatrix(image).ravel();
    }

    static Mat convert(PIX pix) {
        PIX tempPix = null;
        PIX height;
        if (pix.colormap() != null) {
            height = lept.pixRemoveColormap(pix, 2);
            pix = height;
            tempPix = height;
        } else if (pix.d() < 8) {
            height = null;
            switch (pix.d()) {
                case 1:
//                    height = lept.pixConvert1To8((PIX)null, pix, 0, -1);
                    break;
                case 2:
//                    height = lept.pixConvert2To8(pix, 0, 85, -86, -1, 0);
                    break;
                case 3:
                default:
                    assert false;
                    break;
                case 4:
                    height = lept.pixConvert4To8(pix, 0);
            }

            pix = height;
            tempPix = height;
        }

        int height1 = pix.h();
        int width = pix.w();
        int channels = pix.d() / 8;
        Mat mat = new Mat(height1, width, opencv_core.CV_8UC(channels), pix.data(), (long) (4 * pix.wpl()));
        Mat mat2 = new Mat(height1, width, opencv_core.CV_8UC(channels));
        int[] swap = new int[]{0, 3, 1, 2, 2, 1, 3, 0};
        int[] copy = new int[]{0, 0, 1, 1, 2, 2, 3, 3};
        int[] fromTo = channels > 1 && ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? swap : copy;
        opencv_core.mixChannels(mat, 1L, mat2, 1L, fromTo, (long) (fromTo.length / 2));
        if (tempPix != null) {
            lept.pixDestroy(tempPix);
        }

        return mat2;
    }

    //txtファイル読み込み
    public INDArray asMatrix(File f) throws IOException {

        BufferedImage img_src = null;
        float[][] dataF = null;

        try {
            //原画像を読み込む
            img_src = ImageIO.read(f);

            //原画像を拡張し、二次元二値データを作る
            dataF = create2DBinaryExpanded(img_src);

            //28*28の行列を1*784に変更
            float[] featureVec = new float[(WIDTH+2)*(HEIGHT+2)];
            int x =0;
            for (int i = 0; i < dataF.length; i++) {
                for (int j = 0; j < dataF[i].length; j++) {
                    featureVec[x] = dataF[i][j];
                    x++;
                }
            }

            INDArray matrixD = Nd4j.create(featureVec);
            return matrixD;

        } catch (Exception e) {
            e.printStackTrace();
            img_src = null;
        }

        return null;
    }

    //原画像を拡張し、細線化のための二次元二値データを生成するメソッド
    public float[][] create2DBinaryExpanded(Image img){

        int[] rgb=new int[WIDTH*HEIGHT];
        Color color;
        int d;

        //原画像imgを一次元RGBデータrgb[]にする
        PixelGrabber grabber=
            new PixelGrabber(img,0,0,WIDTH,HEIGHT,rgb,0,WIDTH);
        try{
            grabber.grabPixels();
        }catch(InterruptedException e){}

        float[][] new_pixels=new float[WIDTH+2][HEIGHT+2];

        //拡張画像を作り、その二次元二値データをすべて白(0)に設定する
        for(int j=0;j<HEIGHT+2;j++)
            for(int i=0;i<WIDTH+2;i++)
                new_pixels[i][j]=0;

        //原画像の一次元RGBデータrgb[]を、
        //拡張画像の中央部に二次元化して書き込む
        for(int j=1;j<HEIGHT+1;j++)
            for(int i=1;i<WIDTH+1;i++){
                color=new Color(rgb[(j-1)*WIDTH+(i-1)]);
                d=color.getRed();
                if(d<128) new_pixels[i][j]=1;  //黒に設定
            }

            return new_pixels;
    }

    ////maxファイル読み込み
//    public INDArray asMatrix(File f) throws IOException {
//        //ファイルを読み込み
//        MatFileReader read = new MatFileReader(f.getPath());
//        Map<String, MLArray> mlArrayRetrived = read.getContent();
//        Iterator it = mlArrayRetrived.values().iterator();
//        MLArray mlArray = null;
//        while (it.hasNext()) {
//            mlArray = (MLArray) it.next();
//            break;
//        }
//
//        //matファイルのデータ構造を取得
//        MLStructure struct = (MLStructure) mlArray;
//        MLDouble data = (MLDouble) struct.getField(DATA);
//
//        //データ構造をログで出力
//        if (data != null) {
//            double[][] dataD = data.getArray();
//
//            //各行に対して、FFT
//            for (int i = 0; i < dataD.length; i++) {
//                double[] dataDRow = dataD[i];
//                int n = dataDRow.length;
//                n=100;
//                FFT4g fft = new FFT4g(n);
//                fft.rdft(1, dataDRow);
//                for(int k = 0; k < n; k++){
//                    System.out.println("" + k + "," + dataDRow[k]);
//                }
//            }
//
//            INDArray matrixD = Nd4j.create(dataD);
//
//            return matrixD;
//
//        } else {
//            throw new IllegalStateException("Could not EEG data from .mat file.");
//        }
//    }

    public INDArray asMatrix(Mat image) throws IOException {

        if (this.channels > 0 && image.channels() != this.channels) {
            byte var15;
            var15 = -1;
            label149:
            switch (image.channels()) {
                case 1:
                    switch (this.channels) {
                        case 3:
                            var15 = 8;
                            break;
                        case 4:
                            var15 = 9;
                    }
                case 2:
                default:
                    break;
                case 3:
                    switch (this.channels) {
                        case 1:
                            var15 = 6;
                            break label149;
                        case 4:
                            var15 = 2;
                        default:
                            break label149;
                    }
                case 4:
                    switch (this.channels) {
                        case 1:
                            var15 = 11;
                            break;
                        case 3:
                            var15 = 3;
                    }
            }

            if (var15 < 0) {
                throw new IOException("Cannot convert from " + image.channels() + " to " + this.channels + " channels.");
            }

            Mat cols = new Mat();
            opencv_imgproc.cvtColor(image, cols, var15);
            image = cols;
        }

        int var17 = image.rows();
        int var16 = image.cols();
        int channels = image.channels();
        Indexer idx = image.createIndexer();
        INDArray ret = Nd4j.create(new int[]{channels, var17, var16});
        Pointer pointer = ret.data().pointer();
        int[] stride = ret.stride();
        boolean done = false;
        UByteIndexer i;
        int j;
        int i1;
        int j1;
        UShortIndexer var20;
        IntIndexer var21;
        FloatIndexer var22;
        if (pointer instanceof FloatPointer) {
            FloatIndexer k = FloatIndexer.create((FloatPointer) pointer, new long[]{(long) channels, (long) var17, (long) var16}, new long[]{(long) stride[0], (long) stride[1], (long) stride[2]});
            if (idx instanceof UByteIndexer) {
                i = (UByteIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            k.put((long) j, (long) i1, (long) j1, (float) i.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            } else if (idx instanceof UShortIndexer) {
                var20 = (UShortIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            k.put((long) j, (long) i1, (long) j1, (float) var20.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            } else if (idx instanceof IntIndexer) {
                var21 = (IntIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            k.put((long) j, (long) i1, (long) j1, (float) var21.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            } else if (idx instanceof FloatIndexer) {
                var22 = (FloatIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            k.put((long) j, (long) i1, (long) j1, var22.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            }
        } else if (pointer instanceof DoublePointer) {
            DoubleIndexer var18 = DoubleIndexer.create((DoublePointer) pointer, new long[]{(long) channels, (long) var17, (long) var16}, new long[]{(long) stride[0], (long) stride[1], (long) stride[2]});
            if (idx instanceof UByteIndexer) {
                i = (UByteIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            var18.put((long) j, (long) i1, (long) j1, (double) i.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            } else if (idx instanceof UShortIndexer) {
                var20 = (UShortIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            var18.put((long) j, (long) i1, (long) j1, (double) var20.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            } else if (idx instanceof IntIndexer) {
                var21 = (IntIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            var18.put((long) j, (long) i1, (long) j1, (double) var21.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            } else if (idx instanceof FloatIndexer) {
                var22 = (FloatIndexer) idx;

                for (j = 0; j < channels; ++j) {
                    for (i1 = 0; i1 < var17; ++i1) {
                        for (j1 = 0; j1 < var16; ++j1) {
                            var18.put((long) j, (long) i1, (long) j1, (double) var22.get((long) i1, (long) j1, (long) j));
                        }
                    }
                }

                done = true;
            }
        }

        if (!done) {
            for (int var19 = 0; var19 < channels; ++var19) {
                for (int var23 = 0; var23 < var17; ++var23) {
                    for (j = 0; j < var16; ++j) {
                        if (channels > 1) {
                            ret.putScalar(var19, var23, j, idx.getDouble(new long[]{(long) var23, (long) j, (long) var19}));
                        } else {
                            ret.putScalar(var23, j, idx.getDouble(new long[]{(long) var23, (long) j}));
                        }
                    }
                }
            }
        }

        image.data();
        if (this.normalizeIfNeeded) {
            ret = this.normalizeIfNeeded(ret);
        }

        return ret.reshape(ArrayUtil.combine(new int[][]{{1}, ret.shape()}));
    }

    protected INDArray normalizeIfNeeded(INDArray image) {
        return image.div(Double.valueOf(this.normalizeValue));
    }

}
