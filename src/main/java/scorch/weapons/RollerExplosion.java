package scorch.weapons;

/*
  Class:       RollerExplosion.class
  Author:      Mikhail Kruk

  Description: 
  The weapon which rolls down the hill and then explodes
*/

import java.awt.Color;
import java.util.Random;

import scorch.*;

public class RollerExplosion extends Explosion implements Directional
{
    public static final int BABY_ROLLER = 15, ROLLER = 30, HEAVY_ROLLER = 55;

    private static final int b = Color.black.getRGB(), 
	w = Color.white.getRGB();

    private static final int[][] roller1 = {
	{0,w,b,0},
	{b,w,b,w},
	{w,b,w,b},
	{0,b,w,0},};

    private static final int[][] roller2 = {
	{0,b,w,0},
	{w,b,w,b},
	{b,w,b,w},
	{0,w,b,0},};

    private static final int[][][] rollerImgs = { roller1, roller2 };

    // width and hight are actually 1/2 of the real dimensions
    private static final int width = roller1[0].length/2, 
	height = roller1.length/2;
    private int radius;

    private int direction = 0;
    private double speed = 0;
    private int frameNum = 0;
    private Explosion explosion = null;

    // netscape is secure
    public RollerExplosion()
    {
    }

    public RollerExplosion(Bitmap bitmap, Random rand, int x, int y, int r)
    {
	super(bitmap);
      	setPosition(x,y);
	radius = r;
    }

    public void setArgument(int arg)
    {
	radius = arg;
    }

    public void setSpeed(double speed)
    {
	this.speed = speed;
    }

    public boolean drawNextFrame(boolean update)
    {
	boolean fall = false;
	int i;

	if( explosion != null )
	    return explosion.drawNextFrame(update);

	if( frameNum++ == 0 ) // pop roller up so that it won't get stuck
	  y -= height;

	for(int j = 0; j < 3; j++)
	    {
		i = 0; fall = true;
		while(i < width*2 && fall)
		    {
			fall = bitmap.isBackground(x-width+i, y+height);
			i++;
		    }
		
		if(fall)
		    {
			y++;
		    }
		else
		    {
			if( direction == 0) // first frame
			    {
				// determine where will we roll
				if( speed < 0 )
				    {
					if( checkDirection(x-width) ) 
					    direction = -1;
					else
					    if( checkDirection(x+width) ) 
						direction = 1;
				    }
				else
				    {
					if( checkDirection(x+width) ) 
						direction = 1;
					else
					    if( checkDirection(x-width) ) 
						direction = -1;
				    }
			
				x += direction;
			    }
			else
			    {
				// continue to roll in the selected direction
				if( checkDirection( x + direction*(width+1) ) )
				    x += direction;
				else 
				    direction = 0; // we are done (stable)
			    }
		    }
		
		// if we hit tank, stop moving and explode
		if( checkTankCollision() ) 
		    {
			direction = 0;
		    }

		drawRoller();
		if( direction == 0 && !fall ) break;
	    }
	if ( direction == 0 && !fall )
	    explosion = new SimpleExplosion(bitmap, x, y, radius);
	return true;
    }

    // check if we can roll to the position xc
    private boolean checkDirection(int xc)
    {
	boolean result = true;
	int i = 0;
	while(i < height*2 && result)
	    {
		result = bitmap.isBackground(xc, y-height+i);
		i++;
	    }
	return result;
    }

    // check if we are hitting a tank (not background and not sand)
    private boolean checkTankCollision()
    {
	for(int i = 0; i < width*2; i++)
	    {
		if(!bitmap.isGround(x-width+i,y-height) &&
		   !bitmap.isBackground(x-width+i,y-height))
		    return true;
		if(!bitmap.isGround(x-width+i,y+height) &&
		   !bitmap.isBackground(x-width+i,y+height))
		    return true;
	    }
	for(int i = 0; i < height*2; i++)
	    {
		if(!bitmap.isGround(x-width,y-height+i) &&
		   !bitmap.isBackground(x-width,y-height+i))
		    return true;
		if(!bitmap.isGround(x+width,y+height+i) &&
		   !bitmap.isBackground(x+width,y+height+i))
		    return true;
	    }
	return false;
    }

    // draw roller (or just hide if direction is 0)
    private void drawRoller()
    {
	int idx = frameNum % 2;
	
	bitmap.drawSprite(x-width, y-height, rollerImgs[idx], 0);
	bitmap.newPixels(x-width-1, y-height-1, width*2+2, height*2+2);
	bitmap.setColor(null);
	bitmap.hideSprite(x-width, y-height, rollerImgs[idx], 0);
    }

    public int calculateDamage(ScorchPlayer sp)
    {
	if( explosion != null )
	    return explosion.calculateDamage(sp);
	else
	    return 0;
    }
    
    public ExplosionInfo getExplosionInfo()
    {
	if( explosion != null )
	    return explosion.getExplosionInfo();
	else
	    return null;
    }
}
