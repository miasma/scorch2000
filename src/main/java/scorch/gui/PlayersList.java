package scorch.gui;

/*
  Class:  PlayersList
  Author: Mikhail Kruk

  Description: extension of the PlayersLister interface that stores 
  players in the List java GUI element
*/

import java.awt.*;

import scorch.*;
import swindows.*;

public class PlayersList extends sWindow implements PlayersLister, Runnable
{
    // max [displayed] length of username 
    protected static final int MAX_LENGTH = 20; 

    protected int timeLeft = 0;
    protected Thread thread;

    protected final List players;

    public PlayersList(String name, ScorchApplet owner)
    {
	super(-1,-1,0,0,name, owner);
	
	players = new List(ScorchPlayer.MAX_PLAYERS);
    }

    public void addPlayer(String name)
    {
	players.add(name.substring(0,Math.min(MAX_LENGTH,name.length())));
    }

    public void removePlayer(String name)
    {
	players.remove(name.substring(0,Math.min(MAX_LENGTH,name.length())));
    }

    public void display()
    {
	super.display();
	
	thread = new Thread(this);
	thread.start();
    }

    public void close()
    {
	super.close();
	thread = null;
    }

    public void run()
    {
	while(timeLeft > 0 && thread != null)
	    {
		((ScorchApplet)owner).setTimerLabel(timeLeft);
		try
		    {
			Thread.sleep(1000);
		    }
		catch(InterruptedException e) {}
		timeLeft--;
	    }
	if( timeLeft <= 0 )
	    ((ScorchApplet)owner).showDisconnect("You have timed out");
	else
	    ((ScorchApplet)owner).setTimerLabel(-1); // hide the label
    }
}

