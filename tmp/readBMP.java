package test.tmp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

public class readBMP extends JApplet{

    Image img_src,img_exp;
    byte change_flag;

    static final int WIDTH=200,HEIGHT=200;
    static final int
        UPPER_LEFT=2,LOWER_RIGHT=6,UPPER_RIGHT=0,LOWER_LEFT=4;

    byte[][] new_pixels=new byte[WIDTH+2][HEIGHT+2];
    byte[][] old_pixels=new byte[WIDTH+2][HEIGHT+2];

    public void init(){

        File file = new File("C:/R/output/test/interictal/Dog_1_interictal_segment_0001_001.bmp");

        //原画像を読み込む
        img_src=loadImage(file);
        //原画像を拡張し、二次元二値データを作る
        create2DBinaryExpanded(img_src);

    }

    //原画像を読み込むメソッド
    public static Image loadImage(File f) {
        try {
            BufferedImage img = ImageIO.read(f);
            return img;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //原画像を拡張し、細線化のための二次元二値データを生成するメソッド
    public void create2DBinaryExpanded(Image img){

        int[] rgb=new int[WIDTH*HEIGHT];
        Color color;
        int d;

        //原画像imgを一次元RGBデータrgb[]にする
        PixelGrabber grabber=
            new PixelGrabber(img,0,0,WIDTH,HEIGHT,rgb,0,WIDTH);
        try{
            grabber.grabPixels();
        }catch(InterruptedException e){}

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

    }

    public void paint(Graphics g){

        int width=WIDTH+2;
        int height=HEIGHT+2;
        int size=width*height;

        g.drawImage(img_src,10,10,null);   //原画像を画面の左側に描画

        //細線化を実施する
        do{

            change_flag=0;

            drawAndCopy(g,width,height); //描画とコピー
            for(int j=1;j<height-1;j++)  //左上から細線化
                for(int i=1;i<width-1;i++)
                    if(old_pixels[i][j]==1) thinImage(i,j,UPPER_LEFT);

            drawAndCopy(g,width,height);     //描画とコピー
            for(int j=height-2;j>=1;j--)    //右下から細線化
                for(int i=width-2;i>=1;i--)
                    if(old_pixels[i][j]==1) thinImage(i,j,LOWER_RIGHT);

            drawAndCopy(g,width,height);     //描画とコピー
            for(int j=1;j<height-1;j++) //右上から細線化
                for(int i=width-2;i>=1;i--)
                    if(old_pixels[i][j]==1) thinImage(i,j,UPPER_RIGHT);

            drawAndCopy(g,width,height); //描画とコピー
            for(int j=height-2;j>=1;j--) //左下から細線化
                for(int i=1;i<width-1;i++)
                    if(old_pixels[i][j]==1) thinImage(i,j,LOWER_LEFT);

        }while(change_flag==1);

    }

    //描画とコピーのメソッド
    void drawAndCopy(Graphics g,int _width,int _height){
        for(int j=0;j<_height;j++)
            for(int i=0;i<_width;i++){
                if(new_pixels[i][j]==1)  g.setColor(Color.black);
                else                     g.setColor(Color.white);
                //細線化画像を画面右側に描画
                g.drawRect(WIDTH+40+i,10+j,1,1);
                //次の細線化のためのコピー
                old_pixels[i][j]=new_pixels[i][j];
            }
    }

    //細線化のメソッド
    public void thinImage(int i,int j,int start){

        byte[] p=new byte[8];
        int product,sum;

        p[0]=old_pixels[i-1][j-1];
        p[1]=old_pixels[i-1][j];
        p[2]=old_pixels[i-1][j+1];
        p[3]=old_pixels[i][j+1];
        p[4]=old_pixels[i+1][j+1];
        p[5]=old_pixels[i+1][j];
        p[6]=old_pixels[i+1][j-1];
        p[7]=old_pixels[i][j-1];

        for(int k=start;k<start+3;k++){
            product=p[k % 8]*p[(k+1) % 8]*p[(k+2) % 8];
            sum=p[(k+4) % 8]+p[(k+5) % 8]+p[(k+6) % 8];
            if(product==1 && sum==0){
                new_pixels[i][j]=0;   //消去する
                change_flag=1;
                return;
            }
        }

    }

}
