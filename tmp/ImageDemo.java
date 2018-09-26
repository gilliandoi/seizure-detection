package test.tmp;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageDemo {

    public void binaryImage(String path,String outputPath) throws IOException{
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
        for(int i= 0 ; i < width ; i++){
            for(int j = 0 ; j < height; j++){
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        outputImageValue(image);
        outputImageValue(grayImage);

        ImageIO.write(grayImage, "jpg", new File("C:/R/output/test/Dog_1_interictal_segment_0001_001_fix.jpg"));
    }

    public void grayImage(String path,String outputPath) throws IOException{
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY
        for(int i= 0 ; i < width ; i++){
            for(int j = 0 ; j < height; j++){
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        outputImageValue(grayImage);

        ImageIO.write(grayImage, "jpg", new File("C:/R/output/test/Dog_1_interictal_segment_0001_001_fix.jpg"));
    }

    public static void outputImageValue(BufferedImage bimg){
        int [][] data = new int[bimg.getWidth()][bimg.getHeight()];
        //方式一：通过getRGB()方式获得像素矩阵
        //此方式为沿Height方向扫描
        for(int i=0;i<bimg.getWidth();i++){
            for(int j=0;j<bimg.getHeight();j++){
                data[i][j]=bimg.getRGB(i,j);
                //输出一列数据比对
                if(i==0)
                    System.out.printf("%x\t",data[i][j]);
            }
        }
        Raster raster = bimg.getData();
        System.out.println("");
//        int [] temp = new int[raster.getWidth()*raster.getHeight()*raster.getNumBands()];
//        //方式二：通过getPixels()方式获得像素矩阵
//        //此方式为沿Width方向扫描
//        int [] pixels  = raster.getPixels(0,0,raster.getWidth(),raster.getHeight(),temp);
//        for (int i=0;i<pixels.length;) {
//            //输出一列数据比对
//            if((i%raster.getWidth()*raster.getNumBands())==0)
//                System.out.printf("ff%x%x%x\t",pixels[i],pixels[i+1],pixels[i+2]);
//            i+=3;
//        }
    }
}
