package scorch;

/*
  Class:  AIPlayer
  Author: Mikhail Kruk
  
  Description: 
  This class implements all types of AIPlayers. For now all three types 
  have the same strategy and differ only in some constants which are 
  supposed to determine how good AI player is. 
*/

import scorch.weapons.*;
import scorch.utility.Debug;

public class AIPlayer extends ScorchPlayer implements Runnable {
    public static final String[] names = {"Shooter", "Cyborg", "Killer"};
    public static final int numAI = names.length;

    private static final long[] bounty = {10000, 20000, 30000};
    private static final int[] accuracy = {5, 4, 2}; // lower is better
    private static final double[] rfactor = {3.0, 2.0, 1.5};

    private int type; // ai type, index in the above arrays

    public AIPlayer(int id, PlayerProfile profile, ScorchApplet owner) {
        super(id, profile, owner);

        String name = profile.getName();
        for (type = 0; type < numAI; type++)
            if (name.equals(names[type]))
                break;

        if (type >= numAI)
            System.err.println("AIPlayer: internal error, unknown ai type: " +
                    name);
    }

    // override this to make AI tanks look different
    public void setTankType(int ignored) {
        tankType = type;
    }

    public long getBounty() {
        return bounty[type];
    }

    public int getAccuracy() {
        return accuracy[type];
    }

    public double getRadiusFactor() {
        return rfactor[type];
    }

    public void makeTurn() {
        if (isFirstTurn())
            autoDefense();
        else
            aimedFire();
    }

    private void aimedFire() {
        Thread thread = new Thread(this, "ai-aimer-thread");
        thread.start();
    }

    public void buyAmmunition() {
        //System.out.println(getName()+" buying ammo for "+getCash()); // TODO
    }

    private void autoDefense() {
        if (useAutoDefense()) {
            //System.out.println(getName()+" using AutoDefense"); // TODO
        }
        owner.sendEOT();
    }

    public void run() {
        aim();
        owner.aiFire(this);
    }

    // the algorythm for AI aiming which just tries to shoot at different
    // angles with different power until it finds a target
    // Here is the list of improvements which *must* be done to AI:
    // TODO: different weapons
    //       normal strength selection
    //       try to fire in direction of opponent even if can't hit directly
    private void aim() {
        RoundMissile missile;
        ExplosionInfo ei;
        int startAngle = getAngle(), startPower = getPower();
        int curAngle, curPower, accuracy;
        double radiusFactor;

        accuracy = getAccuracy();
        radiusFactor = getRadiusFactor();

        curAngle = getAngle();
        do {
            // power increment depens on accuracy,
            // accuracy is angle increment. confused? good.
            curPower = incPower(10 * accuracy);
            Debug.println("ai is aiming, power is: " + curPower + " angle: " +
                    getAngle() + " startPower: " + startPower);

            if (Math.abs(curPower - startPower) < 10 * accuracy) {
                curPower = startPower;
                curAngle = incAngle(accuracy);
                if (Math.abs(curAngle - startAngle) < accuracy)
                    curAngle = startAngle;
            }

            // make a fake missile to see were it lands
            int angle = getAngle();

            missile = new RoundMissile
                    (bitmap,
                            new Physics(getTurretX(2.0),
                                    bitmap.getHeight() - getTurretY(2.0),
                                    angle, getPower() / 8.0),
                            new SimpleExplosion
                                    (bitmap, (int) (SimpleExplosion.MISSILE * radiusFactor)));

            // to determine if there was an explosion at all
            ei = missile.getExplosionInfo();

            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
            }
        }
        while ((ei == null ||
                scorchField.killTanks(missile, this, true) <= 0) &&
                (curAngle != startAngle || curPower != startPower));
    }
}
