package scorch.weapons;

/*
  Class:  RoundMissile
  Author: Nathan Roslavker

  Desciption: 
  A basic missile that inherits all the
  properties of a GenericMissile.
  SimpleExplosion is used by default to 
  explode the missile upon collision
*/

import java.awt.*;

import scorch.*;
	            
public class RoundMissile extends GenericMissile
{
    // Missile sprite
    private final static int data[][]=
    {
	{0,b,b,0},
	{b,b,w,b},
	{w,b,w,w},
	{0,w,w,0},
    };
    
    public RoundMissile(Bitmap bitmap, Physics physics, Explosion explosion)
    {		
	super(bitmap, physics, data, explosion);
    }
}
