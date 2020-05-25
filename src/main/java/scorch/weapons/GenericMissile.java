package scorch.weapons;

import java.awt.*;

import scorch.*;

/*
  Class: GenericMissile
  Author: Nathan Roslavker
  Fixes: Mikhail Kruk
  
  Description:
  Encapulates basic missile behavior, such as trajecotry
  calculation and collision detection, and explosion upon collision. 
*/

public class GenericMissile extends PhysicalObject implements Explodable {
    private static int sndSHOT = -1;

    protected final static int b = Color.black.getRGB();
    protected final static int w = Color.white.getRGB();

    protected final Explosion explosion;

    protected final int width;
    protected final int height;
    protected int prev_x = -1, prev_y = -1;
    protected Color tracer = null;
    protected ExplosionInfo IE = null;
    protected int frameNum = 0;
    protected final int[][] missile;

    protected final int[] xt;
    protected final int[] yt; // trajectory arrays
    protected int step = 0;
    private final int numSteps = 50;

    //private state variables
    protected final static int EXPLOSION = 1, MISSILE = 0, END = 2;
    protected int state;

    public GenericMissile(Bitmap bitmap, Physics physics,
                          int[][] sprite, Explosion explosion) {
        super(bitmap, physics);

        height = sprite.length;
        width = sprite[0].length;

        setPosition
                (physics.getStartX(), bitmap.getHeight() - physics.getStartY());

        this.missile = sprite;
        this.explosion = explosion;

        xt = new int[numSteps];
        yt = new int[numSteps];

        physics.CalcTrajectory(xt, yt, numSteps);


        state = MISSILE;
    }

    //This function returns an approximate point where
    //the collision occured
    //if the return value is null, there was no collision
    protected Point calcCollision(int x, int y) {
        if (prev_x == -1 || prev_y == -1) return null; // first call
        if (y < 0 || prev_y < 0) return null; // out of the screen
        return bitmap.intersectLine(prev_x, prev_y, x, y);
    }

    public int calculateDamage(ScorchPlayer sp) {
        if (state == END)
            return explosion.calculateDamage(sp);
        else
            return 0;
    }

    public ExplosionInfo getExplosionInfo() {
        if (state == END)
            return explosion.getExplosionInfo();
        else {
            ExplosionInfo ie = null;
            Point p = null; //lcCollision(xt[0],bitmap.getHeight()-yt[0]);
            int i;

            do {
                for (i = 0; i < numSteps &&
                        xt[i] < bitmap.getWidth() &&
                        xt[i] >= 0 &&
                        p == null; i++) {
                    if ((bitmap.getHeight() - yt[i]) >= 0)
                        p = calcCollision
                                (xt[i], bitmap.getHeight() - yt[i]);
                    prev_x = xt[i];
                    prev_y = bitmap.getHeight() - yt[i];
                }
                if (i == numSteps)
                    physics.CalcTrajectory(xt, yt, numSteps);
            }
            while (i == numSteps);

            if (p != null) {
                // prepare for calculateDamage()
                ie = new ExplosionInfo();
                x = p.x;
                y = p.y;
                explosion.setPosition(x, y);
                state = END;
            }
            return ie;
        }
    }

    public boolean isExploding() {
        return (state == EXPLOSION);
    }

    public void drawFrame(boolean update) {
        if (!isExploding()) {
            if (tracer == null) {
                bitmap.drawSprite(x - width / 2, y - height / 2, missile, 0);
                if (update)
                    bitmap.newPixels
                            (x - width / 2, y - height / 2, width, height);
                bitmap.hideSprite(x - width / 2, y - height / 2, missile, 0);
            }
        } else
            explosion.drawFrame(update);
    }

    public void hideFrame() {
        if (!isExploding()) {
            if (tracer == null) {
                bitmap.newPixels(x - width / 2, y - height / 2,
                        width, height);
            }
        } else
            explosion.hideFrame();
    }

    protected void initExplosion(int ex, int ey) {
        if (explosion instanceof Directional)
            ((Directional) explosion).setSpeed(physics.getHSpeed());

        hideFrame();
        explosion.setPosition(ex, ey);
        setPosition(ex, ey);
        state = EXPLOSION;
    }

    public boolean drawNextFrame(boolean update) {
        Point p;

        switch (state) {
            case MISSILE:
                if (frameNum++ > 0) {
                    if (update) hideFrame();

                    if (tracer != null && frameNum > 1) {
                        bitmap.setDirectDraw(true);
                        bitmap.setColor(tracer);
                        bitmap.drawLine(x, y, prev_x, prev_y);
                        bitmap.setDirectDraw(false);
				
				/*if( update )
				    bitmap.newPixels(Math.min(x, prev_x)-2,
						     Math.min(y, prev_y)-2,
						     Math.abs(prev_x-x)+4, 
						     Math.abs(prev_y-y)+4);*/
                        bitmap.setColor(null);
                        bitmap.drawLine(x, y, prev_x, prev_y);
                    }
                } else {
                    startSound(sndSHOT);
                }

                if (step >= numSteps) {
                    step = 0;
                    physics.CalcTrajectory(xt, yt, numSteps);
                }

                prev_x = x;
                prev_y = y;

                x = xt[step];
                y = bitmap.getHeight() - yt[step];

                while (true) {
                    while (step < numSteps &&
                            Math.abs(xt[step] - x) < 2 &&
                            Math.abs(yt[step] - (bitmap.getHeight() - y)) < 2)
                        step++;

                    if (step == numSteps) {
                        step = 0;
                        physics.CalcTrajectory(xt, yt, numSteps);
                    } else
                        break;
                }

                // in bounds?
                if (x >= 0 && x < bitmap.getWidth()) {
                    if (y >= 0) {
                        if ((p = calcCollision(x, y)) != null) {
                            initExplosion(p.x, p.y);
                            return true;
                        }

                        drawFrame(update);
                    }
                    step++;
                    return true;
                } else {
                    return false; // missile left the screen
                }
            case EXPLOSION:
                if (explosion.drawNextFrame(update))
                    return true;
                else
                    state = END;
            case END:
                return false;
            default:
                System.err.println("GenericMissile: invalid state");
                return false;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        prev_x = this.x;
        prev_y = this.y;
    }

    public void setTracerColor(Color tc) {
        tracer = tc;
    }

    public Color getTracerColor() {
        return tracer;
    }

    public static void loadSounds(ScorchApplet owner) {
        sndSHOT =
                addSound(owner.getAudioClip(owner.getCodeBase(), "Sound/shot.au"));
    }
}
