package scorch.gui;

/*
  Class:  ScorchFrame
  Author: Mikhail Kruk

  Description: The holder for the scorch game field. Provides border and
  resizes it correctly
*/

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import scorch.*;
import scorch.utility.*;
import swindows.*;

public class ScorchFrame extends sWindow implements FocusListener
{
    private ScorchField scorch;

    public ScorchFrame(int w, int h, Random rand, ScorchApplet owner)
    {
	super(0,0,w,h,null, owner);

	scorch = new ScorchField(w-6*wndBorder, h-6*wndBorder, rand, owner);
	Debug.println("scorch craeted");

	addFocusListener(this);

	setLayout(null);
	scorch.setLocation(0,0);
	scorch.setSize(w-6*wndBorder, h-6*wndBorder);
	add(scorch, 0);
	
	validate();
    }

    public ScorchField getScorch()
    {
	return scorch;
    }
 
    public void focusGained(FocusEvent e) 
    {
	transferFocus();
    }

    public void focusLost(FocusEvent e) {}

}
