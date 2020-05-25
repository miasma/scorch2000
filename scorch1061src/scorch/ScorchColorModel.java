/*
  Class:       ScorchColorModel
  Author       Alexander Rasin
  Desciption:  Color pallete predeccesor used in Weapons/Items(?). Translates
               index-based colors into RGB values.
*/
package scorch;

import java.awt.image.ColorModel;


public class ScorchColorModel extends ColorModel
{
    protected int r[] = new int[256];
    protected int g[] = new int[256];
    protected int b[] = new int[256];
    
    public ScorchColorModel( )
    {  
  	super( 32 );
    }
    
    public int getAlpha(int pixel)
    {
	return 255;
    }
    
    public int getRed(int pixel)
    {
	return r[pixel];			
    }
    
    public int getGreen(int pixel)
    {
	return g[pixel];			
    }
    
    public int getBlue(int pixel)
    {
	return b[pixel];			
    }
    
    /*    public int getRGB(int pixel)
    {
	return ((255 << 24) | (r[ ( pixel>>16 )&255 ] << 16) |
		(g[ ( pixel>>8 )&255 ] << 8) | (b[ pixel&255 ] ));
    }
    */

    public int getPixelSize()
    {
	return 32;
    }
}
