package scorch.weapons;

/*

  Class: RoundsExplosion
  Author: Mikhail Kruk

  Description: 
  This class adds the standard calcDamage() functionality to all 
  the round explosions. Damage is calulated using the distance of a player
  from the center of explosion and its radius. If there ever will be 
  explosions of different power a variable coefficient can be added to this 
  class and included in the calculation
*/

import java.awt.*;
import java.util.Random;

import scorch.*;
import scorch.utility.Debug;

public abstract class RoundExplosion extends Explosion
{
    protected int radius;
    protected ExplosionInfo ei;

    public RoundExplosion()
    {
	super();
    }

    public RoundExplosion(Bitmap bitmap)
    {
	super(bitmap);
    }

    public RoundExplosion(Bitmap bitmap, int x, int y)
    {
	super(bitmap, x, y);
    }


    private double dist(double x1, double y1, double x2, double y2)
    {
	return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
    }

    public int calculateDamage(ScorchPlayer sp)
    {
	int lx = sp.getX(), rx = lx+sp.getWidth(), 
	    uy = sp.getTurretY(1), ly = sp.getY()+sp.getHeight();
	double d1 = dist(lx,uy,x,y), d2 = dist(lx,ly,x,y), 
	    d3 = dist(rx,uy,x,y), d4 = dist(rx,ly,x,y);
	double distance = Math.min(Math.min(d1,d2), Math.min(d3,d4));
	double splash_radius = 1.02 * (double)radius; // add splash damage
	
	// if direct hit set power to 0
	if( x >= lx && x <= rx && y >= uy && y <= ly )
	    {
		//System.err.println(sp.getName()+": direct hit!");
		// take abs just in case it's already negative...
		return Math.abs(sp.getPowerLimit());
	    }

	// maybe (?) multiply by 2 becaue there is actually some damage
	// on the edge of explosion. may be it should depend on weapon?
	if( splash_radius > distance )
	    return (int)(sp.maxPower - (sp.maxPower*distance)/splash_radius);
	else
	    return 0;
    }

    
    public ExplosionInfo getExplosionInfo()
    {
	if( ei == null )
	    {
		ei = new ExplosionInfo();
		ei.explosionArea = 
		    new Rectangle(x-radius,y-radius,radius*2,radius*2);
	    }
	return ei;
    }
}
