package scorch.weapons;

/*
  Class:  BabyDigger
  Author: Mikhail Kruk
*/

public class BabyDigger extends Weapon
{
    public BabyDigger()
    {
    	super(DiggerExplosion::new);
	type = BabyDigger;
	price = 2000;
	argument = DiggerExplosion.BABY_DIGGER;
    }
}
