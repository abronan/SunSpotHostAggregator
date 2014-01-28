package org.sunspotworld.heatsensors.views;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DateFormat;
import javax.swing.JPanel;

/**
 * Displays a graphic with the temperatures received.
 * Integrated in {@link TemperatureMonitor#contentGraph}
 * 
 * @author Alexandre
 */
public class DataPanel extends JPanel {

    private static final int MAX_SAMPLES = 10000;
    private int index = 0;
    private long[] time = new long[MAX_SAMPLES];
    private int[] val = new int[MAX_SAMPLES];
    DateFormat fmt = DateFormat.getDateTimeInstance();

    /** Creates new panel DataPanel */
    public DataPanel() {
        this.setBackground(new java.awt.Color(255, 255, 255));
    }
    
    /**
     * Add data to the graphic.
     * 
     * @param t the time we received the temperature value
     * @param v the received value
     */
    public void addData(long t, int v) {
        time[index] = t;
        val[index++] = v;
        revalidate();
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        int left = this.getX() + 10;                // get size of pane
        int top = this.getY() + 10;
        int right = left + this.getWidth() - 20;
        int bottom = top + this.getHeight() - 20;
        
        int y0 = bottom - 45;                       // leave some room for margins
        int yn = top;
        int x0 = left + 33;
        int xn = right;
        int ym = 0;                                 // middle line value
        double vscale = (yn - y0) / 100.0;          // temperature values range from -40 to 60
        double tscale = 1.0 / 2000.0;               // 1 pixel = 2 seconds = 2000 milliseconds
        
        /** Draw X axis = time. */
        g.setColor(Color.BLACK);
        g.drawLine(x0, yn, x0, y0);                 // draw vertical line
        g.drawLine(x0, y0, xn, y0);                 // draw horizontal line
        int tickInt = 60 / 2;
        for (int xt = x0 + tickInt; xt < xn; xt += tickInt) {   // tick every 1 minute
            g.drawLine(xt, y0 + 3, xt, y0 - 3);
            int min = (xt - x0) / (60 / 2);
            g.drawString(Integer.toString(min), xt - (min < 10 ? 3 : 7) , y0 + 20);
        }
        
        /** Draw Y axis = sensor reading. */
        g.setColor(Color.BLUE);
        for (int vt = 60; vt >= -40; vt -= 10) {                // tick every 10
            int v = y0 + (int)((vt + 40) * vscale);
            if(vt == 0) ym = v;
            g.drawLine(x0 - 3, v, x0 + 3, v);
            g.drawString(Integer.toString(vt), x0 - 38 , v + 5);
        }
        
        /** Draw middle horizontal line. */
        g.setColor(Color.RED);
        g.drawLine(x0, ym, xn, ym);

        /** Graph sensor values. */
        g.setColor(Color.BLUE);
        int xp = -1;
        int vp = -1;
        for (int i = 0; i < index; i++) {
            int x = x0 + (int)((time[i] - time[0]) * tscale);
            int v = y0 + (int)((val[i] + 40) * vscale);
            if (xp > 0) {
                g.drawLine(xp, vp, x, v);
            }
            xp = x;
            vp = v;
        }
    }

}