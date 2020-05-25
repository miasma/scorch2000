/*
  Class:  ExplosionInfo
  Author: Nathan Roslavker
  Fixes: Mikhail Kruk

  Purpose: To communicate the explosion information from
           children of GenericMissile and Explosion to the ScorchField
*/          
           
package scorch.weapons;

import java.awt.Rectangle;

public class ExplosionInfo 
{
    //the aread that was redrawn by the explosion   
    public Rectangle explosionArea;

    public ExplosionInfo()
    {
    }

    public ExplosionInfo(Rectangle rect)
    {
	explosionArea = rect;
    }

    public String toString()
    {

        return "ExplosionInfo: rectangle: "+explosionArea;
    }
}
