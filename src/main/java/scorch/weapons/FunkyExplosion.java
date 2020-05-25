package scorch.weapons;

/*
  Class:       FunkyExplosion.class
  Author:      Mikhail Kruk
  Description: The classic funky bomb -- a multi-particle explosion
*/

import java.util.*;
import java.awt.*;

import scorch.*;

public class FunkyExplosion extends ParticlesExplosion
{
    private final int[] colors = {Bitmap.getColor(255,0,0),
			    Color.orange.getRGB(), 
			    Color.yellow.getRGB(), 
			    Bitmap.getColor(0,255,0),
			    Bitmap.getColor(0,0x7F,0xFF), 
			    Bitmap.getColor(0,0,255)};

    private final static int FUNKY_SIZE = 30;

    private Explosion explosion; // store the baby nuke aftershock here
    private int a_radius; // aftershock radius

    // netscape is secure
    public FunkyExplosion()
    {
    }

    public FunkyExplosion(Bitmap bitmap, Random rand, int n)
    {
	super(bitmap);
	setRand(rand);
	setArgument(n);
    }

    protected void initParticles()
    {
	int angle, xoffset, yoffset, power;
	double angler;
	TracerMissile fmsl;

	for(int i = 0; i < enumber; i++)
	    {
		power = (int)(Math.abs(rand.nextInt() % 500) / 8.0);
		angle = 20+Math.abs(rand.nextInt() % 140);
		angler = (double) angle*Math.PI/180.00;
            // the offset of the trajectories
            int offset = 5;
            xoffset = (int)(Math.cos(angler)* offset);
		yoffset = (int)(Math.sin(angler)* offset);
		fmsl = new TracerMissile
		    (bitmap, new Physics(x+xoffset, 
					 bitmap.getHeight()-y+yoffset, 
					 angle, power),
		     new ColorStripExplosion
			 (bitmap, null, 0, 0, FUNKY_SIZE,
			  colors[i % colors.length]));
		particles.addElement(fmsl);
	    }

	radius = FUNKY_SIZE;
	a_radius = Math.round(enumber*10);
    }

    public boolean drawNextFrame(boolean update)
    {
	boolean res; 
	
	if( explosion == null )
	    {
		res = super.drawNextFrame(update);
		if( !res )
		    explosion = new SimpleExplosion(bitmap, x, y, a_radius);
		res =  true;
	    }		
	else
	    res = explosion.drawNextFrame(update);

	return res;
    }

    public int calculateDamage(ScorchPlayer sp)
    {
	if( explosion == null ) 
	    return 0;
	else
	    return super.calculateDamage(sp)+explosion.calculateDamage(sp);
    }

    public ExplosionInfo getExplosionInfo()
    {
	ExplosionInfo ei = super.getExplosionInfo();
	ei.explosionArea.add(new 
	    Rectangle(x-a_radius, y-a_radius,2*a_radius, 2*a_radius));
	return ei;
    }
}

// psychodelic multi-color explosion
class ColorStripExplosion extends RoundExplosion
{
    private int frameNum = 0;
	private final int numSteps = 6;
    private final int color;

	private ExplosionInfo EI;
    
    public ColorStripExplosion(Bitmap bitmap, Random rand, 
			       int x, int y, int r, int color)
    {
	super(bitmap);
	this.rand = rand;
	
      	setPosition(x,y);
	this.radius = r;
	this.color = color;
    }

    public void setArgument(int arg)
    {
	radius = arg;
    }

    public void drawFrame(boolean update)
    {
	double scale;

	for(int i = 0; i < numSteps; i++)
	    {
		// palete scale
		scale = 1.0/(numSteps+1)*
		    (1+numSteps-((frameNum+i) % numSteps));
		bitmap.setColor(Bitmap.scaleColor(color, scale));
		// radius scale
		scale = 1.0/numSteps*(numSteps-i);
		
		bitmap.fillCircle(x,y,(int)(radius*scale));
	    }

	if( update )
	    bitmap.newPixels(x-radius, y-radius, 2*radius+2, 2*radius+2);
    }

    public void hideFrame()
    {
	bitmap.setColor(null);
	bitmap.fillCircle(x, y, radius);
    }
    
    public boolean drawNextFrame(boolean update)
    {
	drawFrame(update);

		int duration = 4 * numSteps;
		if( frameNum++ >= duration)
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
	
	return true;
    }
}
