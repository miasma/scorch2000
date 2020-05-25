package scorch.weapons;

/*
  Class:       DiggerExplosion.class
  Author:      Mikhail Kruk

  Description: The weapon which diggs through the ground
*/

import java.awt.*;

import scorch.*;
import java.util.Random;

public class DiggerExplosion extends Explosion
{
    public static final int BABY_DIGGER = 1000, 
	DIGGER = 3000,
	HEAVY_DIGGER = 5000;

    private static final int b=Color.black.getRGB(), w=Color.white.getRGB();

    private static final int size = 1;
    private static final int num = 10;

    private int frameNum = 0, minX = Integer.MAX_VALUE, maxX = 0, 
	minY = Integer.MAX_VALUE, maxY = 0;

    private final int[] xcoords = new int[num];
    private final int[] ycoords = new int[num];
    private ExplosionInfo EI;
    private int duration;
    
    // fake constructor for netscape
    public DiggerExplosion()
    {
    }

    public DiggerExplosion(Bitmap bitmap, Random rand, int x, int y, int d)
    {
	super(bitmap);
	this.rand = rand;
	
      	setPosition(x,y);
	duration = d;
    }

    public void setArgument(int arg)
    {
	duration = arg;
    }

    public void setPosition(int x, int y)
    {
	super.setPosition(x, y);
	if( xcoords != null && ycoords != null )
	    {
		for(int i = 0; i < num; i++)
		    {
			xcoords[i] = x;
			ycoords[i] = y;
		    }
	    }
    }
    
    public boolean drawNextFrame(boolean update)
    {
	int d;
	
	while(frameNum < duration )
	    {
		for(int i = 0; i < num; i++)
		    {
			d = Math.abs(rand.nextInt() % 4);
			switch( d )
			    {
			    case 0:
				if(//frameNum > duration / 2 || 
				   bitmap.isGround(xcoords[i]+size+1,
						   ycoords[i]))
				    {
					xcoords[i]+=size;
					frameNum++;
				    }
				break;
			    case 1:
				if(//frameNum > duration / 2 || 
				   bitmap.isGround(xcoords[i]-size-1,
						   ycoords[i]))
				    {
					xcoords[i]-=size;
					frameNum++;
				    }
				break;
			    case 2:
				if(//frameNum > duration / 2 || 
				   bitmap.isGround(xcoords[i],
						   ycoords[i]-size-1))
				    {
					ycoords[i]-=size;
					frameNum++;
				    }
				break;
			    case 3:
				ycoords[i]+=size;
				frameNum++;
				break;
			    default:
				System.err.println
				    ("Digger.drawNextFrame(): bad direction");
				frameNum = duration;
			    }
			if( xcoords[i] > maxX ) maxX = xcoords[i];
			if( xcoords[i] < minX ) minX = xcoords[i];
			if( ycoords[i] > maxY ) maxY = ycoords[i];
			if( ycoords[i] < minY ) minY = ycoords[i];
			drawDigger(xcoords[i], ycoords[i]);
		    }
	    }

	EI = new ExplosionInfo(new Rectangle
	    (minX-size, minY-size, maxX-minX+2*size, maxY-minY+2*size));
	bitmap.newPixels(minX-size, minY-size, 
			 maxX-minX+2*size, maxY-minY+2*size);
	return false;
    }
    
    private void drawDigger(int x, int y)
    {
	bitmap.setColor(null);
	bitmap.setPixel(x, y);	
    }

    public ExplosionInfo getExplosionInfo()
    {
	return EI;
    }
}
