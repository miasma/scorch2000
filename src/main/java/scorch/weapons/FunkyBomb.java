package scorch.weapons;

/*
  Class:  FunkyBomb
  Author: Mikhail Kruk
*/

public class FunkyBomb extends Weapon
{
    public FunkyBomb()
    {
		super(FunkyExplosion::new);
	type = FunkyBomb;
	price = 30000;
	argument = 6;
    }
}
