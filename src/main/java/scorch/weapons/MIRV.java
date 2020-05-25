package scorch.weapons;

/*
  Class:  MIRV
  Author: Mikhail Kruk
*/

public class MIRV extends Weapon
{
    public MIRV()
    {
	type = MIRV;
	price = 35000;
	argument = MIRVExplosion.MIRV;
	explosionClass = "MIRVExplosion";
    }
}
