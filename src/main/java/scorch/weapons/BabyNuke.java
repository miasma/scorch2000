package scorch.weapons;

/*
  Class:  BabyNuke
  Author: Mikhail Kruk
*/

public class BabyNuke extends Weapon
{
    public BabyNuke()
    {
		super(SimpleExplosion::new);
	type = BabyNuke;
	price = 20000;
	argument = SimpleExplosion.BABY_NUKE;
    }
}
