package scorch;

/*
  Class:      PhysicalObject.
  Author:     Nathan Roslavker

  Desciption: A base class for any animated object that purports to
  have real world physical properties. An instance of 
  Physics class, passed as a paremeter, is used to calculate
  the trajecotry.
*/

abstract public class PhysicalObject extends Audible {
    protected int x;
    protected int y;
    protected final int weight;
    protected Bitmap bitmap;
    protected Physics physics;

    public PhysicalObject(Bitmap bitmap, Physics physics) {
        this.bitmap = bitmap;
        this.physics = physics;

        x = y = weight = 0;

        if (physics != null)
            setPosition(physics.getStartX(), physics.getStartY());
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPhysics(Physics physics) {
        this.physics = physics;
    }

    // these two methods might want to be synchronized
    // but there is a problem with making them synch: when mouse is moved,
    // coordinates of players are used, and it may interfere with animations
    // synrchronized on player
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
