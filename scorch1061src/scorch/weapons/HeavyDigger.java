package scorch.weapons;

/*
  Class:  HeavyDigger
  Author: Mikhail Kruk
*/

public class HeavyDigger extends Weapon
{
    public HeavyDigger()
    {
	type = HeavyDigger;
	price = 6000;
	argument = DiggerExplosion.HEAVY_DIGGER;
	explosionClass = "DiggerExplosion";
    }
}
