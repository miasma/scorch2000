package scorch;

/*
  Class:  Bitmap
  Authors: Mikhail Kruk, Nathan Roslavker

  Description: our proprietary java graphics engine. Supports many graphics
  primitives (pixels, rectangles, circles, ovals), gradient circles, 
  sprites etc

  TODO: implement my optimization idea, see if optimezes anything.
  the basic idea is to make drawer class, with a couple implementations 
  for different drawing modes (i.e. background drawing, direct draw drading,
  regular drawing, sendmode etc. This will eliminate conditions which are
  checked when *each* pixel is drawn. Hopefully the speed will increase.
*/

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import scorch.*;
import scorch.backgrounds.*;

public final class Bitmap
{
    private int pixels[], width, height;
    private MemoryImageSource producer;
    
    private Background background;
    private Random rand;

    private int color;
    private boolean bk_color = false, sandMode = false, directDraw = false;
    private float density = 1;
    private int clipping = 255; // we can get rid of this, almost (fire)
    private int sand = 0;
    
    public Bitmap(int width, int height, Background bg)
    {
	this(width, height, bg, null);
    }

    public Bitmap(int width, int height, Background bg, Random rand)
    {
	this.width = width;
	this.height = height;
	this.background = bg;
	this.rand = rand;

	pixels = new int[width*height];
	
	producer = new MemoryImageSource(width, height, pixels, 0, width);
	producer.setAnimated(true);
        producer.setFullBufferUpdates(false);
    }
    
    public Bitmap(int width, int height, Image img, Background bg)
    {
	this(width, height, bg);

	PixelGrabber pg = new PixelGrabber
	    (img, 0, 0, width, height, pixels, 0, width);


	try
	    {
		pg.grabPixels();
	    }
	catch(Exception e)
	    {
		System.err.println(e);
	    }
    }
       

    public synchronized Background getBackground()
    {
	return background;
    }

    public synchronized void setClipping(boolean mode){
	clipping=(mode==true ? 200 : 255);
    }

    public synchronized boolean getClipping(){
	return clipping < 255;
    }

    public synchronized void setSandMode(boolean b)
    {
	sandMode = b;
    }

    public synchronized void setSandColor(int c)
    {
	sand = c;
    }

    public synchronized int getSandColor()
    {
	return sand;
    }

    // color -- transparancy
    public synchronized void drawSprite
	(int x, int y, int[][] sprite, int color)
    {
	drawSprite(x, y, sprite, color, null);
    }

    public synchronized void drawSpriteCl
	(int x, int y, int[][] sprite, int color){
	
	setDensity(1);
	for(int i = 0; i < sprite.length; i++)
	    for(int j = 0; j < sprite[0].length; j++)
		if(sprite[i][j] != color&&(getPixel(x+j, y+i)>>>24)!=0)
		    setPixel(x+j, y+i, sprite[i][j]);		
    }

    public synchronized void drawSprite
	(int x, int y, int[][] sprite, int color, ColorModel cm)
    {
	int tc;
	setDensity(1);

	for(int i = 0; i < sprite.length; i++)
	    for(int j = 0; j < sprite[0].length; j++)
		{
		    tc = cm != null ? cm.getRGB(sprite[i][j]) : sprite[i][j];
		    if(tc != color)
			setPixel(x+j, y+i, tc);
		    
		}
    }
        
    public synchronized void drawSpriteCl
	(int x, int y, int[][] sprite, int color, ColorModel cm)
    {
	int tc;
	setDensity(1);

	for(int i = 0; i < sprite.length; i++)
	    for(int j = 0; j < sprite[0].length; j++)
		{
		    //tc = cm.getRGB(sprite[i][j]);
		    tc = cm.getRGB(sprite[j][i]);
		    if(((getPixel(x+j, y+i))>>>24)<255)
			{
			    if(tc==color)
				setPixel(x+j, y+i, 
					 background.getPixelColor(x+j,y+i));
			    else
				setPixel(x+j, y+i,tc);
			}
		}
    }    

    public synchronized void drawSprite	
	(int x, int y, int[] sprite,int scanSize, int color, ColorModel cm)
    {
	int tc;
	setDensity(1);
	
	for(int i = 0; i < sprite.length; i++)
	    {
		tc = cm != null ? cm.getRGB(sprite[i]) : sprite[i];
		if(tc != color)
		    setPixel(x+(i%scanSize), y+((int)(i/scanSize)), tc);
	    }
    }

    public synchronized void drawSprite
	(int x, int y, int[] sprite, int scanSize, int color )
    {
	drawSprite(x, y, sprite, scanSize, color, null);
    }
    
    public synchronized void drawSpriteCl
	(int x, int y, int[] sprite,int scanSize, int color, ColorModel cm)
    {
	int tc;
	setDensity(1);
	
	for(int i = 0; i < sprite.length; i++)
	    {
		tc = cm.getRGB(sprite[i]);
		if(((getPixel
		     (x+(i%scanSize),
		      y+((int)(i/scanSize))))>>>24)<255)
		    {
			if(tc==color)
			    setPixel(x+(i%scanSize), 
				     y+((int)(i/scanSize)), 
				     background.getPixelColor(x+(i%scanSize),y+((int)(i/scanSize))));
			else
			    setPixel(x+(i%scanSize), y+((int)(i/scanSize)),tc);
		    }
	    }
    }    

    // temp? 
    public synchronized void drawSprite
	(int x, int y, int[][] sprite, int color, ColorModel cm, boolean trans)
    {
	int tc;
	setDensity(1);

	for(int i = 0; i < sprite.length; i++)
	    for(int j = 0; j < sprite[0].length; j++)
		{
		    tc = cm.getRGB(sprite[i][j]);
		    if(tc != color)
			setPixel(x+j, y+i, tc);
		    else
			setPixel(x+j, y+i, background.getPixelColor(x+j, y+i));
		    
		}
    }

    public synchronized void hideSprite
	(int x, int y, int[] sprite,int scanSize, int color)
    {
	setColor(null);
	setDensity(1);

	for(int i = 0; i < sprite.length; i++)
		if(sprite[i] != color)
		    setPixel(x+i/scanSize, y+((int)(i%scanSize)));;
    }


    public synchronized void hideSprite
	(int x, int y, int[][] sprite, int color)
    {
	setColor(null);
	setDensity(1);

	for(int i = 0; i < sprite.length; i++)
	    for(int j = 0; j < sprite[0].length; j++)
		if(sprite[i][j] != color)
		    setPixel(x+j, y+i);
    }

    public synchronized int[] getPixels()
    {
	return pixels;
    }

    public synchronized void setPixel( int x, int y, int c )
    {
	if(density < 1 && rand.nextFloat() > density)
	  return;
	
	if(!sandMode || getPixel(x,y) != sand) //do not draw if sand mode
	    if( x >= 0 && y >= 0 && x < width && y < height)
		{
		    pixels[y*width+x] = c;
		    if( directDraw )
			newPixels(x,y,1,1);
		}
    }

    public synchronized void setPixel( int x, int y )
    {
	if(!bk_color)
	    setPixel(x, y,((clipping<<24)|((color << 8) >>> 8)));
	   
	else
	    setPixel(x, y, ((clipping<<24) | 
			    ((background.getPixelColor(x, y) << 8) >>> 8)));
    }

    public synchronized void setPixel(int x, int y, Color c)
    {
	setPixel(x, y, c.getRGB());
    }

    public synchronized int getPixel(int x, int y)
    {
	if( x >= 0 && y >= 0 && x < width && y < height)
	    return pixels[y*width+x];
	else
	    return 0;
    }

    public synchronized MemoryImageSource getImageProducer()
    {
	return producer;
    }
    
    public void newPixels(int x, int y, int width, int height, boolean frame)
    {   
	producer.newPixels(x, y, width, height, frame);
    }

    public void newPixels(int x, int y, int width, int height)
    {   
	newPixels(x, y, width, height, false);
    }

    public void newPixels()
    {   
	producer.newPixels(0,0,0,0,true);
    }

    public synchronized void setColor(int c)
    {
	bk_color = false;
	color = c;
    }
    
    public synchronized void setDirectDraw(boolean bv)
    {
	directDraw = bv;
    }

    public synchronized void setColor(Color c)
    {
	if( c != null )
	    {
		color = c.getRGB();
		//Debug.println((color >>> 24)+ "");
		bk_color = false;
	    }
	else
	    bk_color = true;
    }

    public synchronized int getColor()
    {
	return color;
    }

    public synchronized void setDensity(float d)
    {
	density = d;
    }

    public synchronized float getDensity()
    {
	return density;
    }

    public synchronized void drawRect(int x, int y, int w, int h)
    {
	drawHorizontalLine(x, x+w, y);
	drawHorizontalLine(x, x+w, y+h);
	drawVerticalLine(x, y, y+h);
	drawVerticalLine(x+w, y, y+h);
    }

    public synchronized void fillRect(int x, int y, int w, int h)
    {
	for(int i = x; i < x+w; i++)
	    for(int j = y; j < y+h; j++)
		setPixel(i, j);
    }

    public synchronized void fillEllipse(int x, int y, int a, int b)
    {
	ellipse(x, y, a, b, true);
    }
    
    public synchronized void drawEllipse(int x, int y, int a, int b)
    {
	ellipse(x, y, a, b, false);
    }
    
    synchronized private void drawEllipsePoint
	(int x, int y, int xc, int yc, boolean fill)
    {
	if(fill)
	    {
		drawLine(x-xc, y+yc, x+xc, y+yc);
		drawLine(x-xc, y-yc, x+xc, y-yc);
	    }
	else
	    {
		setPixel(x+xc, y+yc);
		setPixel(x-xc, y+yc);
		setPixel(x+xc, y-yc);
		setPixel(x-xc, y-yc);
	    }
    }
    
    synchronized private void ellipse
	(int xc, int yc, int a, int b, boolean fill)
    {
	int a2, b2, ds, dt, dxt, dyt, xinc, yinc, x, y, t, s, e, ca, cd;
	a2 = a*a;
	b2 = b*b;
	ds = 4*a2;
	dt = 4*b2;

	dxt = (int)Math.round(a2/Math.sqrt(a2+b2));
	
	xinc = 1; yinc = -1;
	
	t = 0; s = -4 * a2 * b;
	e = -s/2 - 2 * b2 - a2;
	ca = -6 * b2;
	cd = ca - 4 * a2;
	x = 0; y = 0 - yinc * b;
	drawEllipsePoint(xc, yc, x, y, fill);
	
	for( int i = 0; i < dxt; i++ )
	    {
		x += xinc;
		if( e >= 0 )
		    {
			e = e + t + ca;
			t = t - dt;
		    }
		else
		    {
			y += yinc;
			e = e + t - s + cd;
			t = t - dt;
			s = s + ds;
		    }
		drawEllipsePoint(xc, yc, x, y, fill);
	    }

	dyt = y;
	e = e - t/2 - s/2 - b2 - a2;
	ca = -6 * a2;
	cd = ca - 4*b2;
	
	for( int i = 0; i < dyt; i++ )
	    {
		y += yinc;
		if( e <= 0 )
		    {
			e = e - s + ca;
			s = s + ds;
		    }
		else
		    {
			e = e - s + t + cd;
			t = t - dt;
			s = s + ds;
			x += xinc;
		    }
		drawEllipsePoint(xc, yc, x, y, fill);
	    }
    }
    
    public synchronized void fillCircle(int x, int y, int r)
    {
	circle(x, y, r, true, false);
    }
    
    public synchronized void drawCircle(int x, int y, int r)
    {
	circle(x, y, r, false, false);
    }

    public synchronized void fillGradientCircle(int x, int y, int r)
    {
	circle(x, y, r, true, true);
    }

    synchronized private void drawCirclePoint(int x, int y, int xc, int yc, 
					      int r, 
					      boolean fill, boolean gradient)
    {
	if(fill)
	    {
		if(!gradient)
		    {
			drawHorizontalLine(x-xc, x+xc, y+yc);
			drawHorizontalLine(x-xc, x+xc, y-yc);
			
			drawHorizontalLine(x-yc, x+yc, y+xc);
			drawHorizontalLine(x-yc, x+yc, y-xc);
		    }
		else
		    {
			drawHorizontalLineGradient(x-xc, x+xc, y+yc, 
						   x, y, r);
			drawHorizontalLineGradient(x-xc, x+xc, y-yc,
						   x, y, r);
			
			drawHorizontalLineGradient(x-yc, x+yc, y+xc, 
						   x, y, r);
			drawHorizontalLineGradient(x-yc, x+yc, y-xc,
						   x, y, r);
		    }
		    
	    }
	else
	    {
		setPixel(x+xc, y+yc);
		setPixel(x-xc, y+yc);
		setPixel(x+xc, y-yc);
		setPixel(x-xc, y-yc);

		setPixel(x+yc, y+xc);
		setPixel(x-yc, y+xc);
		setPixel(x+yc, y-xc);
		setPixel(x-yc, y-xc);
	    }
    }

    synchronized private void circle(int x, int y, int r, 
				     boolean fill, boolean gradient)
    {
	int d = 1-r, deltaE = 3, deltaSE = -2 * r + 5, xc = 0, yc = r;

	if( r == 0 ) return;

	drawCirclePoint(x, y, xc, yc, r, fill, gradient);
	while( yc > xc )
	    {
		if( d < 0 )
		    {
			d+=deltaE;
			deltaE+=2;
			deltaSE+=2;
		    }
		else
		    {
			d+=deltaSE;
			deltaE+=2;
			deltaSE+=4;
			yc--;
		    }
		xc++;
		drawCirclePoint(x, y, xc, yc, r, fill, gradient);
	    }
    }
    
    synchronized private void drawHorizontalLine(int x0, int x1, int y)
    {
	if ( x0 > x1 )
	    {
		int t = x0; x0 = x1; x1 = t;
	    }

	if( y >= 0 && y < height )
	    {
		for(int i = Math.max(x0, 0); i <= Math.min(x1, width-1); i++)
		    setPixel(i, y);
	    }
    }

    synchronized private void drawHorizontalLineGradient(int x0, int x1, int y,
							 int xc, int yc, int r)
    {
	int distance;

	if ( x0 > x1 )
	    {
		int t = x0; x0 = x1; x1 = t;
	    }

	if( y >= 0 && y < height )
	    {
		for(int i = Math.max(x0, 0); i <= Math.min(x1, width-1); i++)
		    {
			distance = (int)Math.sqrt((xc-i)*(xc-i)+(yc-y)*(yc-y));
			distance = r-(int)(distance*0.7);
			setPixel(i, y, 
				 scaleColor(color,(double)distance/(double)r));
		    }
	    }
    }

    synchronized private void drawVerticalLine(int x, int y0, int y1)
    {
	if ( y0 > y1 )
	    {
		int t = y0; y0 = y1; y1 = t;
	    }

	if( x >= 0 && x < width )
	    {
		for(int i = Math.max(y0, 0); i <= Math.min(y1, height-1); i++)
		    setPixel(x, i);
	    }
    }
    
    public synchronized void drawLine(int x1, int y1, int x2, int y2)
    {
	if( y1 == y2 )
	    {
		drawHorizontalLine(x1, x2, y1);
		return;
	    }
	if( x1 == x2 )
	    {
		drawVerticalLine(x1, y1, y2);
		return;
	    }

	int dx = x2-x1;
	int dy = y2-y1;
	int ix = Math.abs(dx);
	int iy = Math.abs(dy);
	int inc = Math.max(ix, iy);
	int plotx = x1, ploty = y1;
	boolean plot;
	int x = 0, y = 0;

	setPixel(plotx, ploty);
	
	for(int i = 0; i <= inc; i++)
	    {
		x+=ix;
		y+=iy;
		plot = false;

		if(x > inc)
		    {
			plot = true;
			x-=inc;
			if( dx > 0 )
			    plotx++;
			else
			    plotx--;
		    }

		if(y > inc)
		    {
			plot = true;
			y-=inc;
			if( dy > 0 )
			    ploty++;
			else
			    ploty--;
		    }
		setPixel(plotx, ploty);
	    }
    }

    public synchronized int getWidth()
    {
	return width;
    }

    public synchronized int getHeight()
    {
	return height;
    }

    public synchronized boolean isBackground(int x, int y)
    {
	if( x >= 0 && y >= 0 && x < width && y < height)
	    return getPixel(x, y) == background.getPixelColor(x, y);
	else
	    return false;
    }

    public synchronized boolean isGround(int x, int y)
    {
	if( x >= 0 && y >= 0 && x < width && y < height)
	    return getPixel(x, y) == sand;
	else
	    return false;
    }

    // this methods goes through a line and finds the first point which
    // is not a background. it doesn't really belong in here, may be..
    public synchronized Point intersectLine(int x1, int y1, int x2, int y2)
    {
	int dx = x2-x1;
	int dy = y2-y1;
	int ix = Math.abs(dx);
	int iy = Math.abs(dy);
	int inc = Math.max(ix, iy);
	int plotx = x1, ploty = y1;
	boolean plot;
	int x = 0, y = 0;

	if( !isBackground(plotx, ploty) ) return new Point(plotx, ploty);
	
	for(int i = 0; i <= inc; i++)
	    {
		x+=ix;
		y+=iy;
		plot = false;

		if(x > inc)
		    {
			plot = true;
			x-=inc;
			if( dx > 0 )
			    plotx++;
			else
			    plotx--;
		    }

		if(y > inc)
		    {
			plot = true;
			y-=inc;
			if( dy > 0 )
			    ploty++;
			else
			    ploty--;
		    }
		
		if( !isBackground(plotx, ploty) ) 
		    return new Point(plotx, ploty);
	    }
	return null;
    }

    // fill sprites with the pixels of color
    // color = 0 -- grab all colors
    public void getSprite(int x, int y, int[][] sprite)
    {
	getSprite(x, y, sprite, 0);
    }

    public void getSprite(int x, int y, int[][] sprite, int color)
    {
	int tc;

	for(int i = 0; i < sprite.length; i++)
	    for(int j = 0; j < sprite[0].length; j++)
		{
		    tc = getPixel(x+j, y+i);
		    if(color == 0 || tc == color) 
			sprite[i][j] = tc;
		    else
			sprite[i][j] = 0;
		}

    }

    public static int getRed(int color)
    {
	return ( 255 & (color >> 16) );
    }

    public static int getGreen(int color)
    {
	return ( 255 & (color >> 8) );
    }
    
    public static int getBlue(int color)
    {
	return ( 255 & color );
    }

    public static int getColor(int r, int g, int b)
    {
	r&=255; g&=255; b&=255;
	return (255 << 24) | (r << 16) | (g << 8) | b ;
    }

    public static int scaleColor(int color, double scale)
    {
	return getColor((int)(getRed(color)*scale),
			(int)(getGreen(color)*scale),
			(int)(getBlue(color)*scale));
    }
}

