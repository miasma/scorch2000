package scorch;

/*
  Class:  PlayerSettings
  Author: Mikhail Kruk
  Description: player setting go in here
*/

public class PlayerSettings
{
    public final int tankType;
    public final boolean sounds;

    public PlayerSettings(int tt, boolean s)
    {
	tankType = tt;
	sounds = s;
    }

    public PlayerSettings(PlayerProfile profile)
    {
	this(profile.getTankType(), profile.getSounds());
    }

    public String toString()
    {
	return ""+tankType+Protocol.separator+sounds;
    }
}
