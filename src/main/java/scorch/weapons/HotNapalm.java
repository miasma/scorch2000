package scorch.weapons;

/*
  Class:  Napalm
  Author: Mikhail Kruk
*/

public class HotNapalm extends Weapon
{
    public HotNapalm()
    {
	type = HotNapalm;
	price = 20000;
	argument = NapalmExplosion.HOT_NAPALM;
	explosionClass = "NapalmExplosion";
    }
}
