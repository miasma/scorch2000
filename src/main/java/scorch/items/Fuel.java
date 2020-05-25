package scorch.items;

/*
  Class:  Fuel
  Author: Mikhail Kruk

  Description: fuel is the item which allows the player to move his(her)
  tank to get a better shot at the opponents
*/

import java.awt.*;
import java.awt.event.*;

import swindows.sGauge;
import scorch.ScorchApplet;

public class Fuel extends Item
{
    private static int tankVolume; // how much fuel per a tank bought

    private sGauge gauge = null;

    public Fuel()
    {
	type = Fuel;
	price = 10000;
	quantity = 0;
	maxQuantity = 1000;
	bundle = 100;
    }

    public void setGauge(sGauge gauge)
    {
	this.gauge = gauge;
    }

    public void setQuantity(int q)
    {
	super.setQuantity(q);
	if( gauge != null )
	    gauge.updateValue(quantity);
    }	    

    public ItemControl getControlPanel(ScorchApplet owner)
    {
	controlPanel = new FuelControl(this, owner);
	return controlPanel;
    }
}

class FuelControl extends ItemControl implements ActionListener
{
    public FuelControl(Fuel f, ScorchApplet owner)
    {
	super(f, owner);

	control = new Button("Activate");
	((Button)control).addActionListener(this);
	
	setLayout(new FlowLayout());
	add(control);
    }
   
    public void actionPerformed(ActionEvent evt)
    {
	if( evt.getActionCommand().equals("Activate") && 
	    item.getQuantity() > 0 )
	    owner.showFuelWindow((Fuel)item);
    }
}
