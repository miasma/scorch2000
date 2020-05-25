package scorch.weapons;

/*
Class:     NapalmExplosion
Author:    Alexander Rasin

Descition: Spreads a limited amount of flamable subtance
           over the surface of the playing field. Then this
           substance (napalm)burns for a short period of time 
*/

import java.awt.*;
import java.util.*;

import scorch.*;

import scorch.utility.Debug;

public class NapalmExplosion extends Explosion
{		
    private final int LIQUID = 0;
    private final int FIRE = 1;
    private final int CLEAR = 2;
    
    private final int black = Color.black.getRGB();
    private final int white = Color.white.getRGB();

    private final int screen = 2;

    public final static int NAPALM_COLOR = new Color(200, 200, 0).getRGB();
    public final static int NAPALM = 140;
    public final static int NAPALM_DURATION = 120;

    public final static int HOT_NAPALM_COLOR = new Color(254, 254,0).getRGB();
    public final static int HOT_NAPALM = 280;
    public final static int HOT_NAPALM_DURATION = 210;

    private int color = HOT_NAPALM_COLOR; //napalm color
    private int type = HOT_NAPALM;        //napalm type
    
    private int state = LIQUID;

    private boolean first = true;
    
    private int 
	pixelNum = HOT_NAPALM, 
	fireDuration = HOT_NAPALM_DURATION,
	burnoutDuration = 70,
	fireHeight = 150;
    //current level of napalm that is being expanded
    private int curLevel = -1;

    //two frames for the fire animation and a filter containing screen
    //shot of the napalm location
    private int[][][] frames;
    private int[] fireLevels;
    private int width, height, curFrame = 0;
    
    private NapalmLine[] napalmLines;
    private NapalmColorModel nfcm;

    private int um_y, //upper-most y
	lm_y, //lower-most y
	rm_x, //right-most x
	lm_x; //left-most x


    public NapalmExplosion()
    {
    }

    public NapalmExplosion(Bitmap bitmap, Random r, int color)
    {
	super( bitmap,0,0);
	//DEBUG
	setArgument(color);
	
	this.rand = r;
    }
		
    //implement Explodable
    public ExplosionInfo getExplosionInfo()
    {
	ExplosionInfo ei = new ExplosionInfo();  
	
	//extra boundaries in case sand cleared out on the border...
	ei.explosionArea = new Rectangle(lm_x - 1, 
					 rm_x-lm_x + 2,
					 um_y -1,
					 lm_y-um_y + 2);

	return ei;
    }
			
    public void setArgument(int arg)
    {
	type = arg;
    }

    public boolean drawNextFrame( boolean update )
    {
	int pixelsUsed = 0;

	if (first)
	    {
		if ( type == NAPALM )
		    {
			color = NAPALM_COLOR;
			pixelNum = NAPALM;
			fireDuration = NAPALM_DURATION;
		    }
		if ( type == HOT_NAPALM )
		    {
			color = HOT_NAPALM_COLOR;
			pixelNum = HOT_NAPALM;
			fireDuration = HOT_NAPALM_DURATION;
		    }

		//Debug.startTimer();
	    }

	//Debug.calcFPSRate( );
	bitmap.setColor( color );

	switch ( state )
	    {
	    case LIQUID:
		if (first)
		    {
			napalmLines = new NapalmLine[bitmap.getHeight()];
			for (int i = 0; i < bitmap.getHeight(); i++)
			    napalmLines[i] = null;
			
			um_y = -1;                   //upper-most y
			lm_y = -1;                   //lower-most y
			rm_x = -1;                   //right-most x
			lm_x = bitmap.getWidth() + 1;//left-most x
			curLevel = y;
			first = false;
			//in case the napalm falls on the floor, it has
			//a tendency to start 1 below the floor...
			if (curLevel == bitmap.getHeight())
			    curLevel--;
			napalmLines[curLevel] = 
			    new NapalmLine( x, curLevel, bitmap );
			pixelNum--;

			//napalm can go in the sand diagonally preventing
			//it from expanding.  clear out sand up,left,right
			bitmap.setColor( null );
			bitmap.setPixel( x - 1, curLevel );
			bitmap.setPixel( x + 1, curLevel );
			bitmap.setPixel( x, curLevel - 1 );
			bitmap.setColor( color );
		    }
		//speed up the expansion.
		for (int i = 0; i < 4 && pixelNum > 0; i++)
		    {
			pixelsUsed = napalmLines[curLevel].expand();
			
			//going up
			if (pixelsUsed == 0)
			    {
				curLevel--;
				if (napalmLines[curLevel]==null)
				    {
					if (canFill(x, curLevel))
					    {
						napalmLines[curLevel] = new NapalmLine(x, curLevel,bitmap);
						pixelNum--;
					    }
					//can't go up
					else
					    {
						fireDuration = fireDuration/10;
						pixelNum = 0;
					    }
				    }
			    }
			//going down, pixelsUsed is the x location.
			else if (pixelsUsed < 0)
			    {
				curLevel++;
				if (napalmLines[curLevel] == null)
				    napalmLines[curLevel] = 
					new NapalmLine
					    ( -pixelsUsed,curLevel, bitmap );
				else
				    napalmLines[curLevel].newPt( -pixelsUsed );
				
				pixelNum--;
			    }
			//expanding
			else
			    pixelNum -= pixelsUsed;
		    }

		if ( pixelNum < 1 )
		    {
			first = true;
			state = FIRE;
		    }
		break;
	    case FIRE:
		if ( first )
		    {
			clearNapalm( color );

			if (um_y > fireHeight)
			    um_y -= fireHeight;
			else 
			    {
				fireHeight = um_y;
				um_y = 0;
			    }

			width = rm_x - lm_x + 3;
			lm_x -= 1;

			height = lm_y - um_y + 1;

			frames = new int[3][height][width];
			//get the rectange with napalm from bitmap
			bitmap.getSprite(lm_x, um_y, frames[screen]);
			   
			nfcm = new NapalmColorModel( );
			first = false;
		    }

		initFrames (frames[0], frames[1], 
			    .10 + .18*(burnoutDuration > fireDuration ? 
				 (1.0 * 
				  (fireDuration > burnoutDuration/4 ? 
				   fireDuration : 0) / burnoutDuration) : 1 ), 
			    frames[screen]);
		
		fillCurFrame( frames[screen] );
		
		bitmap.setSandMode( true );
		bitmap.drawSprite( lm_x, um_y + 3 , frames[curFrame], 
				   nfcm.getRGB( black ), nfcm, 
				   true);
		drawUnderFire( frames[screen] );
		bitmap.setSandMode( false );
		//extra margin of + 3
		bitmap.newPixels( lm_x-1, um_y-1, width + 3, height + 3);

		fireDuration--;
		//Debug.pause(1000);
		
		if ( fireDuration < 1 )
		    state = CLEAR;
		break;
	    case CLEAR:		  
		bitmap.setColor( null );
		for (int i = 0; i < frames[screen].length; i++)
		    for (int j = 0; j < frames[screen][0].length; j++)
			{
			    if ( frames[screen][i][j] == color )
				bitmap.setPixel( lm_x + j, um_y + i );
			    else
				bitmap.setPixel( lm_x + j, um_y +i, frames[screen][i][j] );
			}
		bitmap.newPixels(lm_x, um_y, frames[screen][0].length,
				 frames[screen].length);
		return false;
		
	    default: System.out.println
			 ("NapalmExplosion: Warning: Invalid Case");
	    }
	return true;
    }

    private void drawUnderFire( int[][] filter )
    {
	for (int j = 0; j < filter[0].length; j++)
	    for (int i = fireLevels[j]; i < filter.length; i++)
		bitmap.setPixel( lm_x + j, um_y + i, filter[i][j]);
    }

    private void initFrames( int[] array1, int[] array2,  double density )
    {
	for (int i = 0; i < array1.length; i++)
	    {
		if ( rand.nextFloat() > 1 - density )
		    array1[i] = array2[i] = white;
		else
		    array1[i] = array2[i] = black;
	    }
    }

    private void initFrames( int[][] array1, int[][] array2,  double density,
			     int[][] filter)
    {
	if (fireLevels == null)
	    fireLevels = new int[filter[0].length];

	for (int i = 0; i < filter.length; i++)
	    for (int j = 0; j < filter[0].length; j++)
		{
		    if (filter[i][j] == color && 
			((i == filter.length - 1) || 
			 (filter[i+1][j] != color)))
			{
			    fireLevels[j] = i;
			    if ( rand.nextFloat() > 1 - density )
				array1[i][j] = array2[i][j] = white;
			    else
			    	array1[i][j] = array2[i][j] = black;
			}
		}
    }

    private void fillCurFrame( int[][] filter )
    {
	if (curFrame == 0) 
	    curFrame = 1;
	else
	    curFrame = 0;
	
	for (int i = 0; i < frames[curFrame].length - 1; i++)
	    for (int j = 1; j < frames[curFrame][0].length - 1; j++)
		    frames[curFrame][i][j] = 
			( i == frames[curFrame].length - 2 ? 
			  getAveragePixel( frames[(curFrame + 1) % 2],
					   j, i+1, true ) :
			  getAveragePixel( frames[(curFrame + 1) % 2],
					   j, i+1, false ) ); 
    }

    private int getAveragePixel( int[][] array, int x, int y, boolean bottom )
    {
	int r, g, b;
	int div;

	b = /*(255 & (array[y][x])) + FIRE STORM DEBUG*/
	    (255 & (array[y-1][x])) + (255 & (array[y-1][x-1])) + 
	    (255 & (array[y][x-1])) + (255 & (array[y][x+1])) + 
	    (255 & (array[y-1][x+1])) + (bottom ? 0 : 
					 (255 & (array[y+1][x-1]))+ 
					 (255 & (array[y+1][x])) + 
					 (255 & (array[y+1][x+1])));
	if (bottom)
	    div = 5;
	else
	    div = 8;

	b /= div;
	r = g = b;

	return Bitmap.getColor(r, g, b);
    }

    private void clearNapalm( int color )
    {
	int pair[];

	bitmap.setColor( color );

	for (int i = 0; i < napalmLines.length; i++)
	    if (napalmLines[i] != null)
		{
		    if (um_y == -1) um_y = i;
		    if (lm_y < i)   lm_y = i;
		    pair = napalmLines[i].clearNapalm( );
		    if (lm_x > pair[0])  lm_x = pair[0]; 
		    if (rm_x < pair[1])  rm_x = pair[1]; 
		}
	bitmap.newPixels(lm_x, lm_y, rm_x-lm_x, um_y-lm_y);
    }

    private boolean canFill(int x, int y)
    {
	return bitmap.isBackground(x, y);
    }
    
    public int calculateDamage( ScorchPlayer sp )
    {
	int[][] filter = frames[screen];
	int center_x = sp.getX() + sp.getWidth()/2, 
	    center_y = sp.getY() + sp.getHeight()/2, damage = 0, dist = 0;
	

	for (int i = 0; i < filter.length; i++)
	    for (int j = 0; j < filter[0].length; j++)
		if (filter[i][j] == color)
		    {
			dist = (int)(Math.pow(center_x - (j + lm_x), 2) + 
			    Math.pow(center_y - (um_y + i), 2));
			
			if (dist <= 800)
			    damage += Math.pow((800 - dist), 2);
		    }
	damage /= (type == HOT_NAPALM ? 50000 : 80000);
	return damage;
    }
}

class NapalmLine
{
    Bitmap bmp = null;
    Vector pts_pairs = new Vector();
    private int lm, rm, y;

    public NapalmLine( int x, int y_coord, Bitmap bitmap )
    {
	int pair[] = new int[2]; 
	this.y = y_coord;
	this.bmp = bitmap;
	pair[0] = pair[1] = lm = rm = x;
	pts_pairs.addElement(pair);
	//DEBUG
	//bmp.setColor( new Color (254, 254, 0));
	bmp.setPixel(x,y);
	bmp.newPixels(x, y, 1, 1);
    }


    public int expand()
    {
	int[] pair;
	int pixels, expansion = 0, count;
 
	//check if 'liquid' can flow to a lower level at one of the boundaries
	for (int i = 0; i < pts_pairs.size(); i++)
	    {
		pair = (int[])pts_pairs.elementAt(i);
		if ( bmp.isBackground( pair[0], y + 1) )
		    return -pair[0];
		if ( bmp.isBackground( pair[1], y + 1) )
		    return -pair[1];
	    }
	count = 0;
	pixels = 0;
	//while no pixels could be expanded (don't want to leave this method
	//without extending any liquid - animation will stop. Keep trying all
	//the posibilities on the same level
	while ( (pixels == 0) && (count < pts_pairs.size()) )
	    {
		pair = (int[])pts_pairs.elementAt(expansion);//i
		expansion = (++expansion) % pts_pairs.size();
		count++;
		if ( bmp.isBackground( pair[0] - 1, y ) )
		    {
			bmp.setPixel( --pair[0], y );
			bmp.newPixels(pair[0], y, 1, 1);
			pixels++;
		    }
		if ( bmp.isBackground( pair[1] + 1, y ) )
		    {
			bmp.setPixel( ++pair[1], y );
			bmp.newPixels(pair[1], y, 1, 1);
			pixels++;
		    }
		checkBdry( pair );
	    }
	
	return pixels;
    }
    
    public void newPt( int x )
    {
	int pair[] = new int[2]; 
	pair[0] = pair[1] = x;
	//DEBUG
	//bmp.setColor( new Color( 254, 254, 0 ));
	bmp.setPixel(x, y);
	bmp.newPixels(x, y, 1, 1);
	checkBdry(pair);
	
	pts_pairs.addElement(pair);
    } 

    // returns the left and right boundaries of the napalm on this line
    public int[] clearNapalm( )
    {
	int[] pair = null;
	for (int i = 0; i < pts_pairs.size(); i++)
	    {
		 pair = (int[])pts_pairs.elementAt(i);
		 bmp.drawLine(pair[0], y, pair[1], y);
	    }
	pair[0] = lm;  pair[1] = rm;
	return pair;
    }

    private void checkBdry(int[] pair)
    {
	if (pair[0] < lm)  lm = pair[0];
	if (pair[1] > rm)  rm = pair[1];
    }
}

/*
  Class:       NapalmColorModel
  Author       Alexander Rasin
  Desciption:  Color pallete used in animating Napalm fire. Translates
               index-based colors into RGB values.
*/

class NapalmColorModel extends ScorchColorModel
{
    //colors in the palette, positions of the colors from 0 to 255
    //fill in the arrays with smooth transition between them.
    private final static int blackRange = 5;
    private final static int blueRange = 12;
    private final static int redRange = 25;
    private final static int orangeRange = 40;
    private final static int yellowRange = 70;
    private final static int whiteRange = 110;
    
    public NapalmColorModel( )
    {  
  	super();
	
	make_gradient(0, Color.black.getRed(), 
		      Color.black.getGreen(),
		      Color.black.getBlue(), blackRange, 
		      Color.black.getRed(),
		      Color.black.getGreen(), 
		      Color.black.getBlue());
	make_gradient(blackRange, Color.black.getRed(), 
		      Color.black.getGreen(),
		      Color.black.getBlue(), blueRange, 
		      Color.blue.getRed(),
		      Color.blue.getGreen(), 
		      Color.blue.getBlue()/7);
	make_gradient(blueRange, Color.blue.getRed(), 
		      Color.blue.getGreen(),
		      Color.blue.getBlue()/7, redRange, 
		      Color.red.getRed(),
		      Color.red.getGreen(), 
		      Color.red.getBlue());
	make_gradient(redRange, Color.red.getRed(), 
		      Color.red.getGreen(),
		      Color.red.getBlue(), orangeRange, 
		      Color.orange.getRed(),
		      Color.orange.getGreen(), 
		      Color.orange.getBlue());
	make_gradient(orangeRange, Color.orange.getRed(), 
		      Color.orange.getGreen(),
		      Color.orange.getBlue(), yellowRange, 
		      Color.yellow.getRed(),
		      Color.yellow.getGreen(), 
		      Color.yellow.getBlue());
	make_gradient(yellowRange, Color.yellow.getRed(), 
		      Color.yellow.getGreen(),
		      Color.yellow.getBlue(), whiteRange, 
		      Color.white.getRed(),
		      Color.white.getGreen(), 
		      Color.white.getBlue());
    }
    
    private void make_gradient (int start_index, int red_s, int green_s,
				int blue_s, int end_index, int red_e,
				int green_e, int blue_e)
    {
	//Produces smooth gradients from RGB_start to RGB_end
	//(on the variable's names s = start and e = end
	int index, max = (int)(end_index - start_index);
	
	float red_inc, green_inc, blue_inc;
	
	//Set the two starting values
	r[start_index] = red_s;
	g[start_index] = green_s;  
	b[start_index] = blue_s;
	r[end_index] = red_e;  
	g[end_index] = green_e;  
	b[end_index] = blue_e;
	
	//Compute the RGB increments
	red_inc = (red_e - red_s) / ((float) (max));
	green_inc = (green_e - green_s) / ((float) (max));
	blue_inc = (blue_e - blue_s) / ((float) (max));
	//Set middle colors
	for (index = 1; index < max; index++)
	    {
		r[(start_index + index)] = (byte)(red_s + red_inc * index);
		g[(start_index + index)] = (byte)(green_s + green_inc * index);
		b[(start_index + index)] = (byte)(blue_s + blue_inc * index);
	    }
    }
    
    public int getRGB(int pixel)
    {
	return ((255 << 24) | (r[ ( pixel>>16 )&255 ] << 16) |
		(g[ ( pixel>>8 )&255 ] << 8) | (b[ pixel&255 ] ));
    }
}
