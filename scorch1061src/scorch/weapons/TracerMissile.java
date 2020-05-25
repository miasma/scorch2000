package scorch.weapons;

/*
  Class:  TracerMissile
  Author: Mikhail Kruk

  Desciption: missile which leaves a trace. The tracer is actuallby build 
  into the generic missile, so this class just enables it
*/

import scorch.*;

public class TracerMissile extends GenericMissile
{
    private static int[][] funkyParticle = {{w}}; // fake missile image

    public TracerMissile(Bitmap bitmap, Physics physics, Explosion explosion)
    {
	super(bitmap, physics, funkyParticle, explosion);
	tracer = java.awt.Color.yellow;
    }
}
