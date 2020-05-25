package scorch.weapons;

/*
  Class:  BabyRoller
  Author: Mikhail Kruk
*/

public class BabyRoller extends Weapon
{
    public BabyRoller()
    {
		super(RollerExplosion::new);
	type = BabyRoller;
	price = 7000;
	argument = RollerExplosion.BABY_ROLLER;
    }
}
