package scorch.weapons;

/*Directional
  Classes: Explosion
  Author:  Nathan Roslavker
  Purpose: This a base class for all explosion classes. It implements
           Animation to be able to draw itself and communicates to the
           the explosion information through ExplosionInfo.
           Therefore the extending class has to implement drawNextFrame()
           and override getExplosionInfo() 
*/

//import java.awt.Point;
import java.util.Random;

import scorch.*;

public abstract class Explosion extends Audible implements Explodable
{
    protected Bitmap bitmap;
    protected Random rand;
    protected int x, y;

    // place holder constructor since netscape does not allow to reflect 
    // constructors with arguments. cool, eh?
    public Explosion()
    {
    }

    public Explosion(Bitmap bitmap, int x, int y)
    {
	this.bitmap=bitmap;
	setPosition(x, y);	
    }

    public Explosion (Bitmap bitmap)
    {
	this(bitmap,0,0);
    }

    public void setPosition(int x, int y){
	this.x=x;
	this.y=y;
    }

    /*public void setPosition(Point pos)
    {
	setPosition(pos.x,pos.y);
	}*/
    
    public void setRand(Random rand)
    {
	this.rand = rand;
    }

    public void setBitmap(Bitmap bitmap)
    {
	this.bitmap = bitmap;
    }

    public abstract ExplosionInfo getExplosionInfo();          
    public abstract void setArgument(int arg);

    // default explosion doesn't do any damage
    public int calculateDamage(ScorchPlayer sp)
    {
	return 0;
    }
    
    // todo make those methods abstract
    public void drawFrame(boolean update)
    {
    }
    public void hideFrame()
    {
    }
}
