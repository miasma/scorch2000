package scorch.weapons;

/*
  Class:  Napalm
  Author: Mikhail Kruk
*/

public class Napalm extends Weapon
{
    public Napalm()
    {
		super(NapalmExplosion::new);
	type = Napalm;
	price = 10000;
	argument = NapalmExplosion.NAPALM;
    }
}
