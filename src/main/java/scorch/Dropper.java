package scorch;

/*
  Class: Dropper
  Author: Mikhail Kruk
  Description: class that drops the ground from the air;
  it'd be good to make a separate animation out of this class so that
  falling ground would be animated (now it falls instantly).
  Current dropping algorithm is not suited for that though.
*/

public class Dropper
{
    private Bitmap bitmap;
    private int groundColor;
    private int startx, endx, width, height;

    public Dropper(Bitmap bitmap, int startx, int endx)
    {
	this.bitmap = bitmap;

	this.width = bitmap.getWidth();
	this.height = bitmap.getHeight();

	this.startx = Math.max(0, startx);
	this.endx = Math.min(width, endx);

	this.groundColor = bitmap.getSandColor();
	
	run();
    }

    public void run()
    {
	int lowerBound, upperBound, thickness, j;

	for(int i = startx; i <= endx; i++)
	    {
		j = 0; 
		while( j < height )
		    {
			lowerBound = height;
			upperBound = j;
			thickness = 0;
			for(; j < height; j++)
			    {
				if( bitmap.isGround(i, j) )
				    {
					thickness++;
				    }
				else
				    {
					if( !bitmap.isBackground(i, j) )
					    {
						lowerBound = j++;
						break;
					    }
				    }
			    }
			
			if( thickness > 0 )
			    {
				bitmap.setColor(groundColor);
				bitmap.drawLine(i, lowerBound-thickness, 
						i, lowerBound-1);

				if( upperBound < lowerBound-thickness-1 )
				    {
					bitmap.setColor(null);
					bitmap.drawLine
					    (i, upperBound, i, 
					     lowerBound-thickness-1);
				    }
			    }
		    }
	    }
    }
}
