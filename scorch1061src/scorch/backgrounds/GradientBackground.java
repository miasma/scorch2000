package scorch.backgrounds;

/*
  Class: GradientBackground
  Author: Mikhail Kruk

  Description: the background which smoothly gradients from one color to 
  another
*/

import java.awt.Color;
import scorch.Bitmap;

public class GradientBackground extends Background
{
    private int steps;
    private int[] colors;

    public GradientBackground(int width, int height, 
			      Color color1, Color color2, int steps)
    {
	super(width, height);

	this.steps = steps;
	
	int red1, red2, green1, green2, blue1, blue2;

	red1 = color1.getRed();
	green1 = color1.getGreen();
	blue1 = color1.getBlue();

	red2 = color2.getRed();
	green2 = color2.getGreen();
	blue2 = color2.getBlue();
	
	colors = new int[steps];
        for(int i = 0; i < steps; i++)
	    colors[i] = Bitmap.getColor(red1+(red2-red1)/steps * i,
					green1+(green2-green1)/steps * i,
					blue1+(blue2-blue1)/steps * i);

	    /*          colors[i] = (255 << 24) | ((red1+(red2-red1)/steps * i) << 16) |
	              ((green1+(green2-green1)/steps * i) << 8) |
		      (blue1+(blue2-blue1)/steps * i);*/
    }

    public int getPixelColor(int x, int y)
    {
	int strip;

	if( x < 0 || x >= width || y < 0 || y >= height )
	    return 0;

	strip = (int)Math.round(Math.floor((float)y/((float)height/steps)));
	
	if( strip < 0 || strip >= steps )
	    {
		System.err.println("GradientBackground.getPixelColor(): strip out of range. x: "+x+"y: "+y+". Please send this error message to meshko@cs.brandeis.edu");
		return colors[0];
	    }
	return colors[strip];
    }
}
