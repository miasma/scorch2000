package scorch.backgrounds;

/*
  Class:  StarsBackground
  Author: Mikhail Kruk

  Description: the night stars background.
*/

import java.awt.Color;
import java.util.Random;

public class StarsBackground extends Background
{
    private int[] stars;
    private Random rand;

    public StarsBackground(int width, int height, Random rand)
    {
	super(width, height);

        this.rand = rand; 

	stars = new int[width];
	
	for(int i = 0; i < width; i++)
	    {
		if( i % 3 == 0 )
		    stars[i] = Math.abs(rand.nextInt()) % height;
		else
		    stars[i] = -1;
	    }
    }

    public int getPixelColor(int x, int y)
    {
	int i;

	if( x < 0 || x >= width || y < 0 || y >= height )
	    return 0;

	i = 255 - (255*y/height);

	if( stars[x] == y )
	    return (255 << 24) | (i << 16) | (i << 8) | i;
	else
	    return (255 << 24) | (0 << 16) | (0 << 8) | 0;
    }
}
