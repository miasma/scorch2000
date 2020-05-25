package swindows;

/*
  Class:  sGauge
  Author: Mikhail Kruk

  Desciption: gauge
*/

import java.awt.*;
import java.awt.event.*;

public class sGauge extends sPanel
{
    private double percent;
    private int value, max;

    public sGauge(int value, int max, Container owner)
    {
	super(0,0,-1,-1);

	this.percent = (double)value/max;
	this.value = value;
	this.max = max;
    }

    // when value displayed by gauge is changed, call this method
    public void updateValue(int val)
    {
	value = val;
	if( percent == (double)value/max ) return;
	percent = (double)value/max;
	repaint();
    }

    public void update(Graphics g)
    {
	int w;
	
	super.update(g);
	w = (int)((width-2-2*wndBorder)*percent);

	if( w > 0 )
	    {
		g.setColor(Color.red);
		g.fill3DRect(wndBorder+1,wndBorder+1, 
			     w, height-2*wndBorder-2, true);
	    }

	g.setColor(Color.white);
	g.drawString(value+"",
		     (width-2*wndBorder-fm.stringWidth(value+""))/2,
		     (height+fm.getMaxAscent())/2-1);
    }

    public Dimension getPreferredSize()
    {
	return new Dimension(0,(int)(fontHeight*1.8));
    }
}
