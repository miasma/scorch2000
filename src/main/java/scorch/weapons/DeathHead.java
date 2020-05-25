package scorch.weapons;

/*
  Class:  Death Head
  Author: Mikhail Kruk
*/

public class DeathHead extends Weapon
{
    public DeathHead()
    {
	type = DeathHead;
	price = 90000;
	argument = MIRVExplosion.DEATH_HEAD;
	explosionClass = "MIRVExplosion";
    }
}
