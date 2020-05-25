package scorch.weapons;

/*
  Class:  BabyDigger
  Author: Mikhail Kruk
*/

public class BabyDigger extends Weapon
{
    public BabyDigger()
    {
	type = BabyDigger;
	price = 2000;
	explosionClass = "DiggerExplosion";
	argument = DiggerExplosion.BABY_DIGGER;
    }
}
