package scorch.weapons;

/*
  Class:  Funky Nuke
  Author: Mikhail Kruk
*/

public class FunkyNuke extends Weapon
{
    public FunkyNuke()
    {
		super(FunkyExplosion::new);
	type = FunkyNuke;
	price = 50000;
	argument = 10;
    }
}
