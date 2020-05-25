package scorch;

/*
  Class: GradientPanel
  Author: Mikhail Kruk
  Description: currently not used...
*/

import java.awt.*;

public class GradientPanel extends Panel implements Runnable {
    private final int steps = 21;
    private int current = 0;
    private int stripWidth;
    private int stripHeight;
    private final int[] colors;
    private Image backBuffer;
    private Graphics backBufferG;
    private boolean animate = true;

    public GradientPanel(int w, int h, Color color1, Color color2) {
        super();

        setSize(w, h);

        int red1, red2, green1, green2, blue1, blue2;
        int i;

        red1 = color1.getRed();
        green1 = color1.getGreen();
        blue1 = color1.getBlue();

        red2 = color2.getRed();
        green2 = color2.getGreen();
        blue2 = color2.getBlue();

        colors = new int[steps * 2];
        for (i = 0; i < steps; i++)
            colors[i] = (255 << 24) | ((red1 + (red2 - red1) / steps * i) << 16) |
                    ((green1 + (green2 - green1) / steps * i) << 8) |
                    (blue1 + (blue2 - blue1) / steps * i);
        for (int j = i; j < steps * 2; j++)
            colors[j] = colors[2 * i - j];
    }

    public void paint(Graphics g) {
        update(g);
    }

    public void update(Graphics g) {
        if (backBuffer == null) {
            Dimension d = getSize();
            backBuffer = createImage(d.width, d.height);
            backBufferG = backBuffer.getGraphics();

            stripWidth = Math.round(d.width / (steps * 2f));
            stripHeight = d.height;

            Thread th = new Thread(this, "gradient-thread");
            th.start();
        }
        g.drawImage(backBuffer, 0, 0, this);
    }

    public void draw() {
        for (int i = 0; i < steps * 2; i++) {
            backBufferG.setColor(new Color(colors[(current + i) % (2 * steps)]));
            backBufferG.fillRect
                    (i * stripWidth, 0, (i + 1) * stripWidth, stripHeight);
        }

        current++;
        if (current == steps * 2) current = 0;
    }

    public void run() {
        while (animate) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            draw();
            repaint();
        }
    }

    public void stop() {
        animate = false;
    }
}
