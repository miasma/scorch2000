package scorch.weapons;

/*
  Class:  Nuke
  Author: Mikhail Kruk
*/

public class Nuke extends Weapon
{
    public Nuke()
    {
		super(SimpleExplosion::new);
	type = Nuke;
	price = 40000;
	argument = SimpleExplosion.NUKE;
    }
}
