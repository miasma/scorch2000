package scorch.weapons;

/*
  Class:  MIRVMissile
  Author: Mikhail Kruk
  Desciption: A missile which explodes on the apogee of it's trajectory and
  creates a number of particles which fall on the ground and explode
*/

import scorch.*;
	            
public class MIRVMissile extends GenericMissile
{
    // Missile sprite
    private final static int[][] data =
    {
	{0,b,b,0},
	{b,b,w,b},
	{w,b,w,w},
	{0,w,w,0},
    };
    
    public MIRVMissile(Bitmap bitmap, Physics physics, Explosion explosion)
    {		
	super(bitmap, physics, data, explosion);
    }
    
    public boolean drawNextFrame(boolean update)
    {
	boolean res = super.drawNextFrame(update);
	if( (state == MISSILE) && (step > 2 && yt[step-1] < yt[step-2]) )
	    {
		initExplosion(xt[step-2],  bitmap.getHeight()-yt[step-2]);
		return true;
	    }
	else
	    return res;
    }
}

