package scorch.items;

/*
  Class:  Tracer
  Author: Mikhail Kruk
*/

import java.awt.*;
import java.awt.event.*;

import scorch.ScorchApplet;

public class Tracer extends Item
{
    public boolean active = false;

    public Tracer()
    {
	type = Tracer;
	price = 100;
    }

    public void decQuantity()
    {
	super.decQuantity();
	if( quantity == 0 ) active = false;
    }

    public ItemControl getControlPanel(ScorchApplet owner)
    {
	controlPanel = new TracerControl(this, owner);
	return controlPanel;
    }
}

class TracerControl extends ItemControl implements ItemListener
{
    public TracerControl(Tracer t, ScorchApplet owner)
    {
	super(t, owner);
	
	control = new Checkbox("Use", ((Tracer)item).active);
	((Checkbox)control).addItemListener(this);
	
	setLayout(new FlowLayout(FlowLayout.CENTER));
	add(control);
    }
   
    public void itemStateChanged(ItemEvent evt)
    {
	int t;
	
	if( (t = item.getQuantity()) > 0 && 
	    evt.getStateChange() == ItemEvent.SELECTED )
	    {
		owner.useItem(item.getType(), t);
		((Tracer)item).active = true;
	    }
	else
	    {
		owner.useItem(item.getType(), 0);
		((Tracer)item).active = false;
	    }
    }
}
