package swindows;

/*
  Class:  sPanel
  Author: Mikhail Kruk
  Desciption: the root of scorchWindows package; basic java panel functionality
  plus scorch look and feel
*/

import java.awt.*;

public class sPanel extends Panel
{
    public static final int wndBorder = 3;

    protected int width, height, x, y;
    protected FontMetrics fm = null;
    protected int fontHeight;
    
    protected Color bkColor = Color.lightGray,  // default window color
	textColor = Color.black;
    
    public sPanel(int width, int height)
    {
        this(0, 0, width, height);
    }

    public sPanel(int x, int y, int width, int height)
    {
	super();
	this.x = x;
	this.y = y;
        this.width = width;
        this.height = height;

	setFont(new Font("Dialog", Font.BOLD, 12));
	fm = getFontMetrics(getFont());
	fontHeight = fm.getMaxAscent() + fm.getMaxDescent();
    }

    public void paint(Graphics g)
    {
	update(g);
    }

    public void update(Graphics g)
    {
	if( width == -1 && height == -1 )
	    {
		width = getSize().width;
		height = getSize().height;	
	    }
	
	g.setColor(bkColor);
	g.fill3DRect(0,0,width,height,true);

	g.fill3DRect(wndBorder, wndBorder, 
		     width-2*wndBorder, 
		     height-2*wndBorder, false);
    }
}
