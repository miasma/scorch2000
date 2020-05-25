/*
  Class:  Player

  Author: Alexander Rasin

  This is a general abstract class that represents a player.  Both AIPlayer
  and ServerThread (which is a human player) extend it.
  All players have reference to their game, id options and know if 
  the client on the other side is down drawing last action.
*/

package ScorchServer;

import scorch.PlayerProfile;
import java.util.Vector;

abstract public class Player
{
    protected Game myGame = null;
    protected String name = null, ploptions = null, jvm = "";
    protected PlayerProfile profile;
    protected int id = -1, type = -1;
    protected boolean ready = false;


    //get player profile
    public PlayerProfile getProfile()
    {
	return profile;
    }

    public void dropPlayer(String reason)
    {
	//System.out.println("AI? HAS BEEN DROPPED");
    }

    public boolean isReady()
    {
	return ready;
    }
    
    abstract public void setReady(boolean val);

    abstract public String getHostName();
    
    public String getOptions()
    {
	return ploptions;
    }

    public void sendMessage(String msg)
    {
    }
    
    public synchronized void setGame(Game g, int pl_id)
    {
	this.myGame = g;
	id = pl_id;

	//this is a master (id=0) can only be human player
	if (id == 0)
	    ((ServerThread)this).makeMaster();
    }

    public int getID()
    {
	return id;
    }

    public String getName()
    {
	return name;
    }
    
    /* 
       Get the name and modify it to reflect if the player is
       dead or alive.  Get's the vector of the living players
    */
    public String getName(Vector<Player> the_living)
    {
	return "["+(profile.isGuest() ? "g" : "") + (isReady() ? "+" : "" ) + 
	           (the_living.contains(this) ? "" : "^") + "]"+name;
    }

    public String toString()
    {
	return "\nPlayer: " + name + "\t\tkills: " + 
	    profile.getOverallKills() + " gain: " + 
	    profile.getOverallGain() + "\n\t\t\temail: " + profile.getEmail() +
	    "\n\t\t\tJVM/OS: "+jvm+"\n\t\t\tresolution: " + 
	    myGame.getResolution() +"\n";
    }
}
