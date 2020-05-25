package scorch.weapons;

/*
  Class:  Roller
  Author: Mikhail Kruk

  Description: the simple roller weapon
*/

import scorch.Bitmap;

public class Roller extends Weapon
{
    public Roller()
    {
	type = Roller;
	price = 13000;
	argument = RollerExplosion.ROLLER;
	explosionClass = "RollerExplosion";
    }
}
