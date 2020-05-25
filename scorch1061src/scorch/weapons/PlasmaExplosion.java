//
//
//   NON-FUNCTIONAL [slowdowns??]
//
//

// Nathan Roslavker

package scorch.weapons;

import java.awt.*;

import scorch.Bitmap;

public class PlasmaExplosion extends RoundExplosion
{
	  
    final static int DEF_RADIUS=20;
    final static long DEF_DURATION=45;

    private int frameNum = 0;
    
    static PlasmaColorModel pallete=new PlasmaColorModel(32);
		
    int width;
    int height;
    int radius;
		
    int data[][];
    
    long duration;
		
    static int SinArray[];
    static{
	SinArray=new int[360];
	for(int x=0;x<SinArray.length;x++)
	    SinArray[x]=(int)(Math.sin(rad((double)x))*1024);
    }
		
    static double rad(double angle){
			return(double)(angle*Math.PI/180.);
    }
        			
    public PlasmaExplosion(Bitmap bitmap, int x, int y, 
			   int radius, long duration)
    {	
	System.out.println("PLASMA EXPLOSION IS DISABLED. PLEASE DO NOT USE IT");
	/*
	super(bitmap,x,y);
	this.radius=radius;
	width=radius*2;
	height=radius*2;		
	data=new int [height][width];
	XValues=new int[width];
	this.duration=duration;*/
    }
	
    public void setArgument(int arg)
    {
	// ???
    }

    public PlasmaExplosion(Bitmap bitmap, int x, int y){
	this(bitmap, x, y, DEF_RADIUS, DEF_DURATION);
    }
	
    public ExplosionInfo getExplosionInfo(){				
	ExplosionInfo ie=new ExplosionInfo();
	ie.center=new Point(x+radius, y+radius);
	ie.radius=radius;
	ie.explosionArea=new Rectangle(x,y,width, height);
	return ie;		
    }
    
    int XValues[];
    int ToAdd=0;
    int YAnswer;
    int XAnswer;
    boolean first=true;
    int counter=radius/10;
    int step=2;
						
    public boolean drawNextFrame(){
	
	frameNum++;
	//	bitmap.setColor(b);
	//bitmap.setDensity(0.5f);
	if(first){	  	
	    first=false;
	}
	  
	if(frameNum<duration){
	    ToAdd++;
	    
	    for(int x=0;x<width;x++){
	    	
		XValues[x]=20*SinArray[((x<<2) + (ToAdd     )) % 360]+
		    30*SinArray[((x   ) + (ToAdd << 14)) % 360]+
		    50*SinArray[((x>>2) + (ToAdd >> 4)) % 360];
	    }
	    
	    for(int y=0;y<height;y++){
		
		YAnswer = 40*SinArray[((y<<3) + (ToAdd      )) % 360]+
		    40*SinArray[((y      ) + (ToAdd<<14)) % 360]+
		    20*SinArray[((y      ) + (ToAdd>>6)) % 360];
		for(int x=0;x<XValues.length;x++){
		    XAnswer=XValues[x];
		    data[y][x]=Math.abs(((XAnswer+YAnswer)>>10));
		    //System.out.println(data[y][x]);
		    //bitmap.drawCircle(50,50,y);		    
		}
	    }
	    bitmap.setColor(null);
	    bitmap.setClipping(true);
	    bitmap.fillCircle(this.x+radius,this.y+radius,counter);
	    bitmap.setClipping(false);
	    bitmap.drawSpriteCl(this.x,this.y,data,0,pallete);
	    bitmap.newPixels(this.x,this.y,width+1,height+1);
	    if(counter<radius)
	    	counter+=step;
	    return true;
	}else{
	    bitmap.setDensity(1);
	    bitmap.setColor(null);
	    bitmap.fillCircle(this.x+radius,this.y+radius,radius);
	    bitmap.newPixels(this.x,this.y,width+1,height+1);
	    return false;
	}
    }
    
    public void setPosition(int x, int y){
	this.x=x-width/2;
	this.y=y-height/2;		
    }
}



class PlasmaColorModel extends ScorchColorModel
{
    public PlasmaColorModel(int bits){  
	System.out.println("PLASMA COLOR MODEL IS DISABLED. PLEASE DON'T USE IT");
	/*	super(bits);
	byte c=(byte)255;
	byte step=(byte)16;
	for(int i=0;i<r.length;i++){
	    r[i]=c;
	    g[i]=0;
	    b[i]=0;
	    if((i%(r.length/6))==0)
		step*=-1;
	    c+=step;
	    }	*/		               
    }
    
    public int getRGB(int pixel){
	return ((((int)255)<<24)|(r[pixel]<<16)|(g[pixel]<<8)|b[pixel]);
    }
}
