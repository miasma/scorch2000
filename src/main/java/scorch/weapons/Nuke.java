package scorch.weapons;

/*
  Class:  Nuke
  Author: Mikhail Kruk
*/

public class Nuke extends Weapon
{
    public Nuke()
    {
	type = Nuke;
	price = 40000;
	argument = SimpleExplosion.NUKE;
	explosionClass = "SimpleExplosion";
    }
}
