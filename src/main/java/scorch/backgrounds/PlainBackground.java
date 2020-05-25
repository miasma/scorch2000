package scorch.backgrounds;

/*
  Class:  PlainBackground
  Author: Mikhail Kruk

  Description: the simplest background -- just one color
*/

import java.awt.Color;

public class PlainBackground extends Background {
    private final int color;

    public PlainBackground(int width, int height, Color color) {
        super(width, height);

        this.color = color.getRGB();
    }

    public int getPixelColor(int x, int y) {
        return color;
    }
}
