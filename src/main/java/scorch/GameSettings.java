package scorch;

/*
  Class:  GameSettings
  Author: Mikhail Kruk

  Description: game settings are stored in this class before being sent 
  to the server as well as after they are recieved from server
*/


public class GameSettings
{
    public static final int NO_WIND = 1, CONST_WIND = 0, CHANGING_WIND = 2;

    public final float gravity;
    public final int maxRounds;
    public final boolean hazards;
    public final int wind;
    public final long initialCash;
    public final boolean lamerMode;

    public GameSettings(float g, int m, boolean h, int w, long c, boolean lm)
    {
	gravity = g;
	maxRounds = m;
	hazards = h;
	wind = w;
	initialCash = c;
	lamerMode = lm;
    }

    public GameSettings(PlayerProfile profile)
    {
	this(profile.getGravity(), profile.getNumRounds(), 
	     profile.getHazards(), profile.getWind(), profile.getCash(),
	     profile.getLamerMode());
    }

    public String toString()
    {
	return ""+gravity+Protocol.separator+maxRounds+Protocol.separator+
	    hazards+Protocol.separator+wind+Protocol.separator+initialCash+
	    Protocol.separator+lamerMode;
    }  
}
