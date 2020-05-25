package scorch.items;

/*
  Class:  Shield
  Author: Mikhail Kruk
  Description: 
*/

import java.awt.*;
import java.awt.event.*;

import scorch.utility.*;
import scorch.ScorchApplet;

public class Shield extends Item
{
    public double damage;         // fraction of damage absorbed
    public int thickness;
    protected double maxStrength; // initial (max) strength of the shield

    protected double strength; // current strength of the shield
    
    public Shield()
    {
	type = Shield;
	price = 20000;
	maxStrength = 1;
	damage = 0.9;
	thickness = 1;
	autoDefenseAv = true;
    }

    public ItemControl getControlPanel(ScorchApplet owner)
    {
	controlPanel = new ShieldControl(this, owner);
	return controlPanel;
    }
    
    public double getStrength()
    {
	return strength;
    }
    
    public double getMaxStrength()
    {
	return maxStrength;
    }

    public void reset()
    {
	strength = maxStrength;
    }

    public double decStrength(double q)
    {
	Debug.println("Shield: "+type+" "+strength+" -> "+(strength-q));
	strength -= q;
	
	if( strength < .2 && strength > 0 )
	    strength = 0;
	return strength;
    }
}

class ShieldControl extends ItemControl implements ActionListener
{
    public ShieldControl(Shield s, ScorchApplet owner)
    {
	super(s, owner);

	control = new Button("Activate");
	((Button)control).addActionListener(this);
	
	setLayout(new FlowLayout());
	add(control);
    }
   
    public void actionPerformed(ActionEvent evt)
    {
	if( evt.getActionCommand().equals("Activate") && 
	    item.getQuantity() > 0 )
	    owner.useItem(item.getType(), -1);
    }
}
