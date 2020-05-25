package scorch.weapons;

/*
  Class:  Funky Nuke
  Author: Mikhail Kruk
*/

import java.util.Random;

public class FunkyNuke extends Weapon
{
    public FunkyNuke()
    {
	type = FunkyNuke;
	price = 50000;
	argument = 10;
	explosionClass = "FunkyExplosion";
    }
}
