package scorch.weapons;

/*
  Class:       GradientExplosion.class
  Author:      Mikhail Kruk
  Description: this class is not currently used
*/

import java.awt.*;

import scorch.*;

public class GradientExplosion extends RoundExplosion
{
    private int frameNum = 0;

    // fake constructor for netscape
    public GradientExplosion()
    {
    }

    public GradientExplosion(Bitmap bitmap, int r)
    {
	super(bitmap);
	radius = r;
    }

    public void setArgument(int arg)
    {
	radius = arg;
    }

    public void drawFrame(boolean update)
    {
	int i = 0;

	while( (i++ < 2) && (++frameNum < radius) )
	    {
		bitmap.setColor(Color.red);
		bitmap.fillGradientCircle(x,y,frameNum);
	    }

	if( update )
	    bitmap.newPixels
		(x-radius, y-radius, 2*radius+1, 2*radius+1);
    }

    public boolean drawNextFrame(boolean update)
    {
	drawFrame(update);

	if( frameNum >= radius )
	    {
		bitmap.setColor(null);
		bitmap.fillCircle(x, y, radius);
		if( update )
		    {
			bitmap.newPixels(x-radius, y-radius, 
					 2*radius+1, 2*radius+1);
		    }
		return false;
	    }
	else
	    return true;
    }
}
