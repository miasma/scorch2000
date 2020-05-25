package scorch.weapons;

/*

  Class:       SimpleExplosion.
  Authors:     Nathan Roslavker, Mikhail Kruk, and Alex Rasin
  Description: Creates an illusion of an explosion by drawing
               concentric circles of various colors with low
               density.             
*/

import scorch.*;
import java.util.Random;

import java.awt.*;

public class SimpleExplosion extends RoundExplosion
{
    private static int sndEXPL;

    private ExplosionInfo ei;
    
    public final static int 
	MISSILE = 10, 
	BABY_NUKE = 60, 
	NUKE = 100;

    protected final static int 
	FILL = 0, 
	DRAW = 1, 
	ERASE = 2, 
	DONE = 3;
    
    private int counter = 0, c = 150, state = FILL;
    private float rd = .2f, step;

    public SimpleExplosion()
    {
    }

    public SimpleExplosion(Bitmap bitmap, int radius)
    {
	this(bitmap, 0, 0, radius);
    }

    public SimpleExplosion(Bitmap bitmap, int x, int y, int radius)
    {
	super(bitmap);
	
	setPosition(x, y);
	setArgument(radius);
	ei = null;
    }

    public void setArgument(int arg)
    {
	radius = arg;
	step = c/radius;
    }

    public boolean drawNextFrame(boolean update)
    {
	boolean returnValue;
	//bitmap.setDensity(0.2f);	
	switch(state)
	    {
	    case (FILL):
		if( counter == 0 )
		    startSound(sndEXPL);
		
		if(counter < radius)
		    {
			bitmap.setColor(new Color(255,c,0));
			bitmap.setDensity(0.2f);
			bitmap.fillCircle(x,y,counter);
			
			bitmap.setColor(Color.yellow);
			bitmap.drawCircle(x,y, (int)(counter*.8));
			bitmap.setColor(Color.darkGray);
			bitmap.drawCircle(x,y, (int)(counter*.5));
			bitmap.setColor(null);
			bitmap.drawCircle(x,y, (int)(counter*.3));
			bitmap.newPixels(x-counter, y-counter, 
					 counter*2+1,counter*2+1);
			c -= step;
			counter += 3;
		    }
		else
		    {
			state = DRAW; //advance to the next state
			//counter = 0;//reset the radius counter
			step = 0.3f;
		    }
		
		returnValue=true;
		break;
	    case (DRAW):
		bitmap.setDensity(step);
		bitmap.setColor
		    (new Color((int)(255*(float)(1-rd)),0,0));
		
		bitmap.fillCircle(x,y,radius);
		bitmap.newPixels(x-radius,y-radius,
				 radius*2+1,radius*2+1);
		
		rd += step;

		if( rd > 1 ) 
		    state = ERASE;
		returnValue = true;
		break;
	    case (ERASE):
		bitmap.setDensity(1);
		bitmap.setColor(null);	   
		bitmap.fillCircle(x,y,radius);
		bitmap.newPixels(x-radius,y-radius,radius*2+1,radius*2+1);

		ei = new ExplosionInfo(getUpdatedArea());
		returnValue = false;
		state = DONE;
		break;
	    case DONE:
		returnValue = false;
		break;
	    default:
		returnValue = false;
		System.err.println("SimpleExplosion.drawNextFrame(): internal error");
	    }
	
	bitmap.setDensity(1);
	return returnValue;
    }

    /******************************************************************/
    public Rectangle getUpdatedArea(){
	return new Rectangle(x-radius,y-radius,radius*2,radius*2);
    }
    /******************************************************************/
    public ExplosionInfo getExplosionInfo()
    {
	return ei;
    }
    /******************************************************************/

    public static void loadSounds(ScorchApplet owner)
    {
	sndEXPL = addSound(owner.getAudioClip(owner.getCodeBase(), 
					      "Sound/explosion.au"));
    }
}
