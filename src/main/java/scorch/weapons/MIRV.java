package scorch.weapons;

/*
  Class:  MIRV
  Author: Mikhail Kruk
*/

public class MIRV extends Weapon
{
    public MIRV()
    {
		super(MIRVExplosion::new);
	type = MIRV;
	price = 35000;
	argument = MIRVExplosion.MIRV;
    }
}
