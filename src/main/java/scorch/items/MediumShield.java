package scorch.items;

/*
  Class:  Shield
  Author: Mikhail Kruk
*/

import java.awt.*;
import java.awt.event.*;

import scorch.utility.*;
import scorch.ScorchApplet;

public class MediumShield extends Shield
{
    public MediumShield()
    {
	type = MediumShield;
	maxStrength = 2;
	price = 27000;
	damage = 0.95;
	thickness = 3;
    }

}
