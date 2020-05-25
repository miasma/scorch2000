package scorch.weapons;

/*
  Class:  Missile
  Author: Mikhail Kruk
*/

public class Missile extends Weapon
{
    public Missile()
    {
        super(SimpleExplosion::new);
	type = Missile;
	price = 0;          // price 0 means that this item can't be bought
	quantity = 999;
	argument = SimpleExplosion.MISSILE;
    }

    // infinite ammo!
    public void decQuantity()
    {
    }
}
