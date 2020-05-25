package scorch;

/*
  Interface:  Explodable
  Author:     Nathan Roslavker
  Desciption: This interface indicates that the implementing class
              is either an explosion or contains an explosion. 
              Explosion, Player, and GenericMissile implement this
              interface (also see ExplosionInfo).
*/

import scorch.weapons.ExplosionInfo;

public interface Explodable extends FrameShow {
    ExplosionInfo getExplosionInfo();

    int calculateDamage(ScorchPlayer sp);
}
