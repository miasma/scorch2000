package scorch.weapons;

/*
  Class:       MIRVExplosion
  Author:      Mikhail Kruk
  Description: for the MIRV weapon
*/

import java.util.*;

import scorch.*;

public class MIRVExplosion extends ParticlesExplosion implements Directional
{
    public final static int MIRV = 25, DEATH_HEAD = 55, offset = 5;
    private double speed;
    
    // netscape is secure
    public MIRVExplosion()
    {
	delayExplosions = false;
    }

    public MIRVExplosion(Bitmap bitmap, Random rand, int n)
    {
	super(bitmap);
	setRand(rand);
	setArgument(n);
	delayExplosions = false;
    }

    public void setArgument(int arg)
    {
	super.setArgument(arg == MIRV ? 5 : 9);
	radius = arg;
    }

    public void setSpeed(double speed)
    {
	this.speed = speed;
    }

    protected void initParticles()
    {
	double power = speed-offset*enumber/2.0;
	GenericMissile msl;

	for(int i = 0; i < enumber; i++)
	    {
		msl = new RoundMissile
		    (bitmap, new Physics(x, bitmap.getHeight()-y,0, power),
		     new SimpleExplosion(bitmap, radius));

		particles.addElement(msl);
		power+=offset;
	    }
    }
}
