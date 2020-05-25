package scorch.weapons;

/*
  Class:       ParticlesExplosion.class
  Author:      Mikhail Kruk

  Description: The super class of all explosions with multiplay exploding
  particles (Funky Bomb, MIRV)
*/

import java.util.*;
import java.awt.*;

import scorch.*;

public abstract class ParticlesExplosion extends Explosion
{
    protected int enumber, radius;

    private static final int START = 0, FLYING = 1, EXPLODING = 2, END = 3;

    private int state = START;
    protected Vector particles, exp_particles;
    protected boolean delayExplosions = true;
    private ExplosionInfo ei;

    private int lx = Integer.MAX_VALUE, uy = Integer.MAX_VALUE, rx = 0, ly = 0;

    // netscape is secure
    public ParticlesExplosion()
    {
    }

    public ParticlesExplosion(Bitmap bitmap)
    {
	super(bitmap);
    }

    public ParticlesExplosion(Bitmap bitmap, Random rand, int n)
    {
	super(bitmap);
	setRand(rand);
	setArgument(n);
    }

    public void setArgument(int arg)
    {
	enumber = arg;
	particles = new Vector(enumber);
	exp_particles = new Vector(enumber);;
    }

    protected abstract void initParticles();

    public boolean drawNextFrame(boolean update)
    {
	boolean res = false;
	GenericMissile msl;
	
	switch( state )
	    {
	    case START:
		initParticles();
		state = FLYING;
		res = true;
		break;
	    case FLYING:
		// res is used here to tell if all the particles have reached
		// the exploding state yet (out of screen is good too)
		res = false; 
		int j = 0;
		while(j < particles.size())
		    {
			msl = (GenericMissile)particles.elementAt(j);
			if( !msl.isExploding() )
			    {
				if( msl.drawNextFrame(true) )
				    {
					res = true;
					j++;
				    }
				else
				    particles.removeElement(msl);
			    }
			else
			    {				
				if( msl.getX() < lx ) lx = msl.getX();
				if( msl.getX() > rx ) rx = msl.getX();
				if( msl.getY() < uy ) uy = msl.getY();
				if( msl.getY() > ly ) ly = msl.getY();

				particles.removeElement(msl);
				exp_particles.addElement(msl);

				if( delayExplosions ) updateExploding();
			    }
		    }

		if( !delayExplosions ) updateExploding();

		if( !res ) 
		    {
			particles = exp_particles;

			state = EXPLODING;
			// take the size of explosions into account
			lx-=radius; uy-=radius;
			rx+=radius; ly+=radius;
		    }
		res = true;
		break;
	    case EXPLODING:
		res = false;
		for(int i = 0; i < particles.size(); i++)
		    {
			msl = (GenericMissile)particles.elementAt(i);
			res |= msl.drawNextFrame(false);
		    }
		bitmap.newPixels(lx, uy, rx-lx+1, ly-uy+1);
		if( !res ) state = END;
		break;
	    case END:
		res = false;
		break;
	    default:
		System.err.println("ParticlesExplosion: illegal state");
	    }

	return res;
    }

    private void updateExploding()
    {
	for(int i = 0; i < exp_particles.size(); i++)
	    {
		if(delayExplosions)
		    ((GenericMissile)exp_particles.
		     elementAt(i)).drawFrame(false);
		else
		    ((GenericMissile)exp_particles.
		     elementAt(i)).drawNextFrame(false);
		    
	    }

	bitmap.newPixels
	    (lx-radius, uy-radius, rx-lx+1+2*radius, ly-uy+1+2*radius);

	for(int i = 0; i < exp_particles.size(); i++)
	    ((GenericMissile)exp_particles.elementAt(i)).
		hideFrame();
    }

    public int calculateDamage(ScorchPlayer sp)
    {
	int d = 0;
	
	for(int i = 0; i < particles.size(); i++)
	    d+=((Explodable)particles.elementAt(i)).calculateDamage(sp);

	return d;
    }

    // TODO do we need those calculations?
    public ExplosionInfo getExplosionInfo()
    {
	boolean def = false;
	
	// we need to recalculate boundaries to disregard the particles
	// which explode outside the screen
	int lx = Integer.MAX_VALUE, uy = Integer.MAX_VALUE, rx = 0, ly = 0;

	for(int i = 0; i < particles.size(); i++)
	    {
		ei = ((Explodable)particles.elementAt(i)).getExplosionInfo();

		if( ei == null || ei.explosionArea == null ) continue;
		def = true;

		if( ei.explosionArea.x < lx ) lx = ei.explosionArea.x;
		if( ei.explosionArea.y < uy ) uy = ei.explosionArea.y;
		if( ei.explosionArea.width+ei.explosionArea.x > rx ) 
		    rx = ei.explosionArea.width+ei.explosionArea.x;
		if( ei.explosionArea.height+ei.explosionArea.y > ly ) 
		    ly = ei.explosionArea.height+ei.explosionArea.y;
	    }

	if( def )
	    ei = new ExplosionInfo(new Rectangle(lx, uy, rx-lx, ly-uy));
	else
	    ei = null;

	return ei;
    }
}
