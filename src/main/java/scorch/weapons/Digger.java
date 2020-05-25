package scorch.weapons;

/*
  Class:  BabyRoller
  Author: Mikhail Kruk
*/

public class Digger extends Weapon
{
    public Digger()
    {
	type = Digger;
	price = 4000;
	argument = DiggerExplosion.DIGGER;
	explosionClass = "DiggerExplosion";
    }
}
