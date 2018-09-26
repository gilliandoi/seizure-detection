package test.drawEEG;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Created by CTC0138 on 2017/03/17.
 */
@SuppressWarnings("serial")
public class DrawPanel extends JPanel {
    private double[][] data = null;
    private int rowIndex = 0;
    private int fromTime = 0;
    private int toTime = 0;
    //3つ点を1つ点として描く
    private int pointAccount=1;

    int ww;
    int hh;

    /**
     * パネル表示
     *
     * @param data　脳波データ
     * @param rowIndex　電極指定(0：すべて、1～16)
     * @param fromTime　時間範囲From
     * @param toTime　時間範囲To
     */
    public DrawPanel(double[][] data, int rowIndex, int fromTime, int toTime) {
        this.rowIndex = rowIndex;
        this.data = data;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    @Override
    protected void paintComponent(Graphics g) {
        //パネル初期表示
        ww = getWidth();
        hh = getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ww, hh);
        g.setColor(Color.RED);

        try {
            if(rowIndex==0){
                //すべての電極行を出力する
                for (int i=0;i<16;i++){
                    //各電極行を出力する
                    rowsOutput(i, g);
                }
            }else {
                //指定した電極行を出力する
                rowOutput(rowIndex - 1, g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 一行のEEG出力
     *
     * @param rowIndex　行Index
     * @param g
     * @throws Exception
     */
    public void rowOutput(int rowIndex,Graphics g) throws Exception {
        double[] dataIndex = data[rowIndex];
        int prex = 0, prey = 0; //前座標
        int x = 0, y = 0;
        // Xの表示範囲缩小倍数
        int rateX = (this.toTime-this.fromTime) / ww;
        // Yの表示範囲拡大倍数
        double rateY = (hh / 2.0 / 32768.0) * 30;

        //for(int i=0; i<ww; ++i){
        for (int i = fromTime; i < toTime; ++i) {
            x = (i-fromTime) / rateX;

            // 各点取出并绘制
            // 实际中应该按照采样率来设置间隔
            y = hh - (int) (dataIndex[i*pointAccount] * rateY + hh / 2);

            System.out.print(y);
            System.out.print(" ");

            if (i != 0) {
                g.drawLine(x, y, prex, prey);
            }
            prex = x;
            prey = y;
        }
    }

    /**
     * 複数行EEG出力
     *
     * @param rowIndex　行Index
     * @param g
     * @throws Exception
     */
    public void rowsOutput(int rowIndex,Graphics g) throws Exception {
        double[] dataIndex = data[rowIndex];
        int prex = 0, prey = 0; //前座標
        int x = 0, y = 0;
        // Xの表示範囲缩小倍数
        int rateX = (this.toTime-this.fromTime) / ww;
        // Yの表示範囲拡大倍数
        double rateY = (hh / 2.0 / 32768.0) * 20;

        for (int i = fromTime; i < toTime; ++i) {
            x = (i-fromTime) / rateX;

            // 各点取出并绘制
            // 实际中应该按照采样率来设置间隔
            y = hh - (int) (dataIndex[i*pointAccount] * rateY + hh / 16 * (16-rowIndex)) + 10;

            //System.out.print(y);
            //System.out.print(" ");

            if (i != 0) {
                g.drawLine(x, y, prex, prey);
            }
            prex = x;
            prey = y;
        }
        System.out.println(" ");
    }
}
