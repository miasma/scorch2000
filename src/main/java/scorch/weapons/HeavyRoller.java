package scorch.weapons;

/*
  Class:  HeavyRoller
  Author: Mikhail Kruk
*/

public class HeavyRoller extends Weapon
{
    public HeavyRoller()
    {
		super(RollerExplosion::new);
	type = HeavyRoller;
	price = 20000;
	argument = RollerExplosion.HEAVY_ROLLER;
    }
}
