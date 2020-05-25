package scorch.items;

/*
  Class: AutoDefense
  Author: Mikhail Kruk

  Description: Auto Defense gives player a chance to activate shield etc
  before the round begins.
*/

import java.awt.*;

import scorch.ScorchApplet;

public class AutoDefense extends Item
{
    public AutoDefense()
    {
	type = AutoDefense;
	price = 5000;
    }

    public ItemControl getControlPanel(ScorchApplet owner)
    {
	controlPanel = new AutoDefenseControl(this, owner);
	return controlPanel;
    }
}

class AutoDefenseControl extends ItemControl
{
    public AutoDefenseControl(Item ad, ScorchApplet owner)
    {
	super(ad, owner);
	
	setLayout(new FlowLayout());
	control = new Label("active");
	add(control);
    }
}
