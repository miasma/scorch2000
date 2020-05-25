package scorch.weapons;

/*
  Class:  Napalm
  Author: Mikhail Kruk
*/

public class Napalm extends Weapon
{
    public Napalm()
    {
	type = Napalm;
	price = 10000;
	argument = NapalmExplosion.NAPALM;
	explosionClass = "NapalmExplosion";
    }
}
