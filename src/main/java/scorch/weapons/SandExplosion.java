/*
  Class:  SandBomb
  Author: Nathan Roslavker
  Fixes: Mikhail Kruk

  Description: 
  lots of sand go up in the air and then fall on the ground
*/

package scorch.weapons;

import java.awt.Rectangle;

import scorch.Bitmap;

public class SandExplosion extends Explosion
{
    public static final int MAX_HEIGHT = 250;

    private int sand = -1;          // sand color
    private int current_size=3;     // line length
    private int size = MAX_HEIGHT;  // max height of explosion
    private int yl;                 //increasing height(decreasing yl)
    
    private int step_size = 7;      // lines per frame
    private int lineCount = 0;      // number of lines drawn (<=size)

    // empty constructor. thanks, netscape
    public SandExplosion()
    {
    }
	
    public SandExplosion(Bitmap bitmap)
    {
	super(bitmap);
    }
        
    public void setPosition(int x,int y)
    {
	this.x=x;
	this.y=y;
	yl=y;
    }
    
    public void setArgument(int arg)
    {
	size = arg;
    }

    public void setStepSize(int stepSize){
	step_size=stepSize;
    }
    
    /*
      Draw constantly increasing in length
      horizontal lines with low density on top of each other.
      This creates an illusion of sand being scattered by an
      explosion
    */
    public boolean drawNextFrame( boolean update )
    {
	if( sand == -1 ) sand = bitmap.getSandColor();
	
	for(int i = 0; i <= step_size && yl >= 0 && lineCount < size; i++)
	    {
		bitmap.setDensity(0.2f);			
		bitmap.setColor(sand);
		
		bitmap.drawLine(x-current_size/2,yl,x+current_size/2,yl);
		
		lineCount++;
		yl--;
		current_size++;
		bitmap.setDensity(1);
		
	    }
	if(yl >= 0 && lineCount < size)
	    {
		bitmap.newPixels(x-current_size/2-2, yl-1, 
				 current_size+2, step_size+2);
		return true;
	    }
	else
	    return false;
    }
    
    public ExplosionInfo getExplosionInfo()
    {
	ExplosionInfo ie = new ExplosionInfo();
	ie.explosionArea = 
	    new Rectangle(x-current_size/2-1, yl+1, current_size, y-yl);
	
	return ie;
    }
}
