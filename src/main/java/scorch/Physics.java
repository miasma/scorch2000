package scorch;

// CLASS: Physics
// AUTHOR: Ramya Ramesh
// PURPOSE: This class manages the estimation of the projectile
// trajectories, Winds, and Hazard conditions.

//
// Changes by Alex Rasin and Mikhail Kruk
//

public class Physics {
    /////////////////////////////////
    // Constants

    // Default Earths gravity
    public static final float EARTH_GRAVITY = 9.8f;
    public static final int MAX_WIND = 10;
    /////////////////////////////////

    /////////////////////////////////
    // Class member variables 

    static private float gravity = EARTH_GRAVITY;  // Gravity value
    static private int windVelocity; // Constant Wind Velocity

    // Step Size
    private final double stepSize;

    // Angle and speed of projectile launch
    private final int Angle;
    private final double Speed;
    private final double VelX0;
    private final double VelY0;
    // Starting X and Y locations of projectile
    private final int startX;
    private final int startY;
    // Current X and Y positions
    private int X, Y;
    // Current step in trajectory
    private int StepN;
    ///////////////////////////////////


    //////////////////////////////////// 
    // Methods of Physics class

    // Constructor
    public Physics(int x0, int y0, int angl, double power) {
        Angle = angl; // Set angle of launch
        Speed = power; // set initial speed
        startX = x0; // set start X location
        startY = y0; // set start Y location

        X = x0;
        Y = y0;

        // Horizontal and vertical components of velocity
        double angle = (double) Angle * Math.PI / 180.00;

        VelX0 = Speed * Math.cos(angle);
        VelY0 = Speed * Math.sin(angle);

        // Set stepSize
	/*stepSize = Math.abs(4/VelX0) * Math.sqrt(Math.abs(VelX0)/62.0);
	
	if (Angle == 90 || Speed == 0)
	    stepSize = 0.08;
	else
	    if (Angle > 80 && Angle < 100)
		stepSize = stepSize * 
		    (((Math.abs(90.0 - (double)Angle)-5.0)/2.0)+5) / 10.0;
	*/
        stepSize = 0.1;

        // Initialize Step to 0
        StepN = 0;
    }

    // Set gravity to arbitrary value
    public static void setGravity(float grav) {
        gravity = grav;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getAngle() {
        return Angle;
    }

    // Set wind to one of three values: NO_WIND, CONSTANT_WIND or
    // CHANGING_WIND
    public static void setWind(int NewWind) {
        windVelocity = NewWind;
    }

    public static int getWind() {
        return windVelocity;
    }


    // Calculate trajectory for specified Steps.
    // Assumes that the vectors Xpos[] and Ypos[] have already been allocated
    // Note stepSize determines jump size and may need to be set properly
    public void CalcTrajectory(int[] Xpos, int[] Ypos, int steps) {
        // ERROR Condition if steps is <= 0
        if (steps <= 0)
            return;

        double deltax, deltay;

        // Iterate through each step
        for (int ii = StepN; ii < StepN + steps; ++ii) {

            // Compute X and Y incremenets according to equations
            deltax = (windVelocity + VelX0) * ii * stepSize;
            deltay = ii * stepSize * (VelY0 - 0.5 * gravity * ii * stepSize);

            Xpos[ii - StepN] = (int) (startX + deltax);
            Ypos[ii - StepN] = (int) (startY + deltay);
        }
        // Update the StepN position
        StepN += steps;

        // Store current location
        X = Xpos[steps - 1];
        Y = Ypos[steps - 1];

    }

    public double getHSpeed() {
        return VelX0;
    }

    public String toString() {
        return "x: " + X + " y: " + Y + " power: " + (Speed * 8) + " angle: " + Angle;
    }
}
