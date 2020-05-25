package scorch.weapons;

/* Class:  FireExplosion
   Author: Nathan Roslavker
   Puropose: This class draws flames of fire
	     calculated in real time. It is one of the ways
	     for a tank to explode.
*/

import scorch.*;

import java.awt.*;

public class FireExplosion extends Explosion{
    protected FireColorModel pallete;
    protected int width;
    protected int height;
    protected int pixels=0;
    protected float intensity=0;
    protected int data[];
    protected int buffer[][];
    protected int state=0;        
    protected ExplosionInfo ie;
    protected long duration=50;

    private int frameNum = 0;

    private static int sndFIRE;
    
/***************************************************************/    
    public FireExplosion(Bitmap bitmap, int width, int height){
	super(bitmap);
	this.width=width;
	this.height=height;	
	pallete=new FireColorModel(32);
	data=new int[width*height];
	buffer=new int[height][width];
	ie=new ExplosionInfo();				
    }
 /***************************************************************/                
    public FireExplosion(Bitmap bitmap, int width, int height,int x, int y){
	super(bitmap);
	this.width=width;
	this.height=height;
	setPosition(x,y);
	pallete=new FireColorModel(32);
	data=new int[width*height];
	buffer=new int[height][width];	
    }
/***************************************************************/
    
    public void setArgument(int arg)
    {
	// ??
    }
    
    public void setPosition(int x, int y){
	this.x=x-width/2;
	this.y=y-height;
    }
/***************************************************************/  
    public void setDuration(long time){
	duration=time;
    }
    
    public static void loadSounds(ScorchApplet owner)
    {
        //URL url = FireExplosion.class.getResource("Sound/fire.au");      
        //System.out.println(url);
 	sndFIRE = addSound(owner.getAudioClip(owner.getCodeBase(), "Sound/fire.au"));
        //sndFIRE = addSound(owner.getAudioClip(url));
    }


/***************************************************************/    
    static protected long Random(long min, long max){
			double rnd=Math.random();
			long returnValue= Math.round((rnd*(double)(max-min)+(double)min));
			return returnValue;
	
    }
/***************************************************************/
    static protected int random(int max){
			int returnValue;
			double rnd=Math.random();
			returnValue=(int)Math.round(rnd*((double)max));
			if(returnValue<0)
	  	  System.err.println("PANIC");
			return returnValue;
    }
    
/***************************************************************/

    public boolean drawNextFrame(boolean update)
    {
	pixels=0;
	int color=0;
	frameNum++;
	
	switch (state)
	    {
	    case 0:
		{
		    if( intensity == 0 )
            		    loopSound(sndFIRE);
                    // raise flame
                    intensity += 0.009f;
		    
                    // maximum flame height
                    if (intensity>0.2f) {			
			state = 1;
		    }
                }
                break;
		
	    case 1:
		// constant flame
		if(frameNum >= duration)
		    {
			state = 2;
		    }
		break;
		
	    case 2:
		intensity-=0.007f;
		if(intensity<=0)
		    {
			stopSound(sndFIRE);
			state=4;
			bitmap.setSandMode(true);
			bitmap.setClipping(false);
			bitmap.setDensity(1);
			bitmap.setColor(null);
			bitmap.fillEllipse(x+width/2,y+height/2+20,width/2,40);
			bitmap.setSandMode(false);
			return false;
		    }
		break;
	    default:
		return false;
		
            }
	try{
	for(int y=1;y<height-4;y+=2){
				//char8 *pixel = pixels+ y*width;
	    int pixel=pixels+y*width;
	    
	    for(int x=0;x<width;x++){
		//sum top pixels
		//char8 *p = pixel+(width<<1);
		int p = pixel + (width<<1);
		//int32 top= *p;
		int top=data[p];
		top+=data[p-1];
		top+=data[p+1];
		
		//bottom pixel
		//int32 bottom = *(pixel + (width<<2));
		int bottom = data[pixel+(width<<2)];
		
		//combine pixels
		int c1 = (top + bottom) >>2;
		if(c1>1) c1--;
		
		//interpolate
		int c2 = (c1 + bottom) >>1;
		
		//store pixels
		data[pixel]=(byte)c1;
		
		data[pixel+width]=(byte)c2;
					
		//next pixel
		
		pixel++;
	    }
	}	
	
	//setup flame generator pointer
	//char8 *generator = pixels + width*(height-4);
	
	int generator = pixels + width*(height-4);
	
	//update flame generator
	for (int x=0;x<width;x+=4){
	    color = random((int)(255.0f*intensity));
	    data[generator]=color;
	    data[generator+1]=color;
	    data[generator+2]=color;
	    data[generator+3]=color;
	    data[generator+width]=color;
	    data[generator+width+1]=color;
	    data[generator+width+2]=color;
	    data[generator+width+3]=color;
	    data[generator+width*2]=color;
	    data[generator+width*2+1]=color;
	    data[generator+width*2+2]=color;
	    data[generator+width*2+3]=color;
	    data[generator+width*3]=color;
	    data[generator+width*3+1]=color;
	    data[generator+width*3+2]=color;
	    data[generator+width*3+3]=color;
	    
	    generator+= 4;	
	}
	}catch(ArrayIndexOutOfBoundsException e){
	    
	}
	/*for(int i=0;i<data.length;i++)
	    if(bitmap.getPixel(x+i%width,y+(int)(i/width))==Color.black.getRGB())
	    data[i]=21;*/
		
	/*for(int i=0;i<data.length;i++)
	  buffer[(int)(i/width)][i%width]=pallete.getRGB(data[i]);*/
	bitmap.setSandMode(true);
	bitmap.setDensity(1);
	bitmap.setColor(Color.black.getRGB());
	bitmap.setClipping(true);
	//bitmap.fillCircle(x+width/2,y+height/2,20);
	bitmap.fillEllipse(x+width/2,y+height/2+20,width/2,40);
	bitmap.setClipping(false);
	//bitmap.setColor(null);
	//bitmap.fillRect(x+width/2, y+height/2+20, width/2, 40);
	bitmap.drawSpriteCl(x,y,data,width,0,pallete);
	bitmap.newPixels(x,y,width, height);
	bitmap.setSandMode(false);
	return true;
    }
/***************************************************************/
    
    public Rectangle getUpdatedArea(){
	return new Rectangle(x,y,width,height);
    }
/***************************************************************/
    public ExplosionInfo getExplosionInfo(){
	ExplosionInfo ie=new ExplosionInfo();
	ie.explosionArea=new Rectangle(x,y,width,height);

	//ie.center=new Point(x+width/2,y+height/2);
	//ie.radius=width/2;

	return ie;
    }
}

/***************************************************************/
/*
  Class:   FireColorModel
  Author   Nathan Roslavker
  Desciption:
           Color pallete used in animating fire. Translates
           index-based colors into RGB values.
*/

class FireColorModel extends ScorchColorModel
{
    public FireColorModel(int bits){  
  	super();
  	int i=0;
		byte c=0;
	
	while (i<64)
	    {
		//data[i] = pack(c,0,0);
		r[i]=c;
		g[i]=0;
		b[i]=0;
		c+=4;
		i++;
		}
	
	// red to yellow
	c=0;
	while (i<128)
	    {
		//data[i] = pack(255,c,0);
		r[i]=(byte)255;
		g[i]=c;
		b[i]=0;
		c+=4;
		i++;
	    }
	
	// yellow to white
	c=0;
	while (i<192)
	    {
		//data[i] = pack(255,255,c);
		r[i]=(byte)255;
		g[i]=(byte)255;
		b[i]=c;
		c+=4;
		i++;
	    }
	
	// white
	while (i<256) 
	    {
		//data[i] = pack(255,255,255);
		r[i]=b[i]=g[i]=(byte)255;
		i++;
	    }		
    }
    
    public int getRGB(int pixel){

	if(pixel<20)
	    return 0;
	else
	    return ((((int)255)<<24)|(r[pixel]<<16)|(g[pixel]<<8)|b[pixel]);
    }
}
