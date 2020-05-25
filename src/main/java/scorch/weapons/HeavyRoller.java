package scorch.weapons;

/*
  Class:  HeavyRoller
  Author: Mikhail Kruk
*/

public class HeavyRoller extends Weapon
{
    public HeavyRoller()
    {
	type = HeavyRoller;
	price = 20000;
	argument = RollerExplosion.HEAVY_ROLLER;
	explosionClass = "RollerExplosion";
    }
}
