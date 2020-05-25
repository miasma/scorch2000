package scorch.weapons;

/*
 Class:       LaserExplosion
 Author:      Nathan Roslavker
 Description: This explosion animates a laser beam
              going from the place of explsion all the way 
              to the top of the screen. The diameter width 
              of the beam can be specified in the constructor
*/   

//import java.awt.*;

import scorch.*;
              
public class LaserExplosion extends Explosion
{
    int width;
    LaserColorModel pallete;
    long duration=60;
    int yl;
    final static int START=0;
    final static int LASER=1;
    final static int END=2;
    int state=START;
    private int frameNum = 0;

    //here width means the width of the laser beam
    public LaserExplosion(Bitmap bitmap, int width)
    {
	super(bitmap);	
	this.width=width;		
	pallete=new LaserColorModel(32);
    }

    public void setArgument(int arg)
    {
	width = arg;
    }
	
    public void setDuration(int duration){
	this.duration=duration;
    }
    
    public void setPosition(int x, int y){
	this.x=x;
	this.y=y+5;
	yl=y;
    }
	
    public ExplosionInfo getExplosionInfo(){
	return null;
    }
    
    int offset = 0;
    
    public boolean drawNextFrame( boolean update )
    {
	boolean returnValue;	
	frameNum++;
	
	switch(state)
	    {
	    case(START):
		state=LASER;			
		
	    case(LASER):
		for(int i=width/2;i>=0;i--){
		    bitmap.setColor(pallete.getRGB(((32-i)+offset)%32));
		    bitmap.drawLine(x+i,yl,x+i,y);
		    bitmap.drawLine(x-i,yl,x-i,y);			
		}	
		bitmap.newPixels(x-width/2,0,width+1,y+1);
		offset++;
		if(yl>0){
		    yl=yl-7;
		    returnValue=true;
		}else{
		    if(frameNum < duration)
			returnValue=true;
		    else{
			bitmap.setColor(null);
			bitmap.setDensity(1);
			bitmap.fillRect(x-width/2,0,width+1,y+1);
			bitmap.newPixels(x-width/2,0,width+1,y+1);
			
			returnValue=false;
			state=END;
		    }
		}
		break;
	    default:
		returnValue=false;
	    }						
	return returnValue;
    }					
}

/*
  Class:  LaserColorModel
  Author: Nathan Roslavker
  Descritpion: Color pallete used in animating the laser beam
*/        

class LaserColorModel extends ScorchColorModel
{
    protected int r[] = new int[32];
    protected int g[] = new int[32];
    protected int b[] = new int[32];

    public LaserColorModel(int size)
    {
	super();
	int c=255;
	int step=16;
	for(int i=0;i<r.length;i++)
	    {
		r[i]=c;
		g[i]=c;
		b[i]=255;
		if((i%(r.length/4))==0)
		    step*=-1;
		c+=step;
	    }
    }		

    public int getRGB(int pixel){
    	if(pixel<r.length)
	    return ((((int)255)<<24)|(r[pixel]<<16)|(g[pixel]<<8)|b[pixel]);   
    	else
	    return ((((int)255)<<24)|(r[r.length-1]<<16)|(g[g.length-1]<<8)|b[b.length-1]);   
    }
}
