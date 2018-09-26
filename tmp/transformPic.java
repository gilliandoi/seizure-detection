package test.tmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.util.OtsuBinaryFilter;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

/**
 * Created by CTC0138 on 2017/04/06.
 */
public class transformPic {
    private static final Logger log = LoggerFactory.getLogger(transformPic.class);

    public static void main(String[] args) throws Exception {
        //getData("C:/R/output/test/Dog_1_interictal_segment_0001_001.jpg");
        //ImageDemo imageDemo = new ImageDemo();
        //imageDemo.binaryImage("C:/R/output/test/Dog_1_interictal_segment_0001_001.jpg","\"C:/R/output/test/Dog_1_interictal_segment_0001_001_fix.jpg\"");
        File file = new File("C:/R/output/test/Dog_1_interictal_segment_0001_001.jpg");
        BufferedImage image = ImageIO.read(file);
        byte[] data = getMatrixRGB(image);
        for(int i= 0 ; i < data.length ; i++){

            System.out.print(data[i]);
        }
    }

    /**
     * 获取图像RGB格式数据
     * @param image
     * @return
     */
    public static byte[] getMatrixRGB(BufferedImage image){
        if(image.getType()!=BufferedImage.TYPE_3BYTE_BGR){
            // 转sRGB格式
            BufferedImage rgbImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(image, rgbImage);
            // 从Raster对象中获取字节数组
            return (byte[]) rgbImage.getData().getDataElements(0, 0, rgbImage.getWidth(), rgbImage.getHeight(), null);
        }else{
            return (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        }
    }

    /**
     * 获取灰度图像的字节数组
     * @param image
     * @return
     */
    public static byte[] getMatrixGray(BufferedImage image) {
        // 转灰度图像
        BufferedImage grayImage = new BufferedImage(200, 200,BufferedImage.TYPE_BYTE_GRAY);
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(image, grayImage);
        // getData方法返回BufferedImage的raster成员对象
        return (byte[]) grayImage.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
    }
}
