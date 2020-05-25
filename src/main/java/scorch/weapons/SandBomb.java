package scorch.weapons;

/*
  Class:  SandBomb
  Author: Mikhail Kruk
*/

public class SandBomb extends Weapon
{
    public SandBomb()
    {
		super(SandExplosion::new);
	type = SandBomb;
	price = 5000;
	argument = SandExplosion.MAX_HEIGHT;
    }
}
