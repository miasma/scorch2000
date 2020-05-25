package scorch.gui;

/*
  Class:  StatsWindow
  Author: Mikhail Kruk

  Desciption: Shows current game stats of all players
  TODO: make it into 4 different classes with one super class!
*/

import java.awt.*;
import java.util.Vector;

import scorch.*;
import scorch.utility.*;
import swindows.*;

public class StatsWindow extends sWindow
{
    public static final int 
	EOG = 0, // show end of game statistics
	EOR = 1, // end of round
	IG = 2,  // in game
	TT = 3;  // top ten

    private int type;

    public StatsWindow(int type, ScorchApplet owner)
    {
	this(type, owner.getPlayers(), owner);
    }
    
    public StatsWindow(int type, Vector players, ScorchApplet owner)
    {
	super(-1,-1,0,0,"Players Statistics", owner);

	switch( type )
	    {
	    case IG:
	    case EOR:
		name = "Players Statistics for Round #"+
		    owner.getRoundCount()+" out of "+owner.getMaxRounds();
		break;
	    case TT:
		name = "Top Ten Players";
		break;
	    case EOG:
		name = "Game Over";
		break;
	    }
		
        this.type = type;

	Panel p, b = new Panel(new FlowLayout(FlowLayout.CENTER));
	
	p = buildPlayersTable(players);

	Panel legend = new Panel(new GridLayout(1,5));

	legend.add(new Label("Player Name", Label.CENTER));
	if(type != TT)
	    {
		legend.add(new Label("Kills", Label.CENTER));
		legend.add(new Label("Gain", Label.CENTER));
	    }
	legend.add(new Label("Overall Kills", Label.CENTER));
	legend.add(new Label("Overall Gain", Label.CENTER));

	if( type != EOR )
	    {
		b.add(new Button("OK"));
	    }
	else
	    {
		b.add(new Button("Play next round"));
		b.add(new Button("Go shopping"));
		b.add(new Button("Leave the game"));
	    }

	sPanel spnl = new sPanel(-1,-1);
	Panel p2 = new Panel();
	p2.setBackground(Color.gray);
	p2.setLayout(new BorderLayout(5,5));
	p2.add(legend, BorderLayout.NORTH);
	p2.add(p, BorderLayout.CENTER);
	spnl.add(p2);

	setLayout(new BorderLayout(0,0));
	//add(legend, BorderLayout.NORTH);
	//add(p, BorderLayout.CENTER);
	add(spnl,BorderLayout.CENTER);
	add(b, BorderLayout.SOUTH);
    }

    public void close()
    {
	super.close();
	transferFocus();
    }
    
    public boolean handleEvent(Event evt)
    {
	if( evt.id == Event.ACTION_EVENT )
	    {
		if( evt.arg.equals("OK") )
		    {
			close();
                        if( type == EOG ) 
  			   ((ScorchApplet)owner).Quit();
			return true;
		    }
		if( evt.arg.equals("Play next round") )
		    {
			close();
			((ScorchApplet)owner).startGame();
			return true;
		    }
		if( evt.arg.equals("Go shopping") )
		    {
			close();
			((ScorchApplet)owner).shop();
			return true;
		    }
		if( evt.arg.equals("Leave the game") )
		    {
			close();
			((ScorchApplet)owner).Quit();
			return true;
		    }
	    }
	if( evt.id == Event.KEY_PRESS && evt.key == Event.ESCAPE )
	    {
		close();
		switch( type )
		    {
		    case EOG : 
			((ScorchApplet)owner).Quit();
			break;
		    case IG  :
		    case TT  :
			break;
		    case EOR :
			((ScorchApplet)owner).startGame();
			break;
		    default:
			System.err.println
			    ("internal error in StatsWindow.java, wrong type");
		    }
		
		return true;
	    }
	
	return false;
    }

    private Panel buildPlayersTable(Vector players)
    {
	String ts;
	PlayerProfile spp;
	ScorchPlayer sp;
	Panel pp, 
	    rPanel = 
	    new Panel(new GridLayout
		(Math.max(players.size(), ScorchPlayer.MAX_PLAYERS),1,0,0));

	int[] index;
	long[] cash;
	
	cash = new long[players.size()];
	for(int i = 0; i < players.size(); i++)
	    {
		if( type == TT )
		    {
			spp = (PlayerProfile)players.elementAt(i);
			cash[i] = spp.getOverallGain();
		    }
		else
		    {
			sp = (ScorchPlayer)players.elementAt(i);
			cash[i] = sp.getEarnedCash();
		    }
	    }

	try
	    {
		index = QSort.sort(cash);
	    }
	catch(Exception e)
	    {
		System.err.println("and of course sort failed: "+e);
		return null;
	    }

	for(int i = 0; i < players.size(); i++)
	    {
		sp = null;
		if( type == TT )
		    spp = (PlayerProfile)players.elementAt(index[i]);
		else
		    {
			sp = (ScorchPlayer)players.elementAt(index[i]);
			spp = sp.getProfile();
		    }
			   
		pp = new Panel(new GridLayout(1,5));

		ts = spp.getName();
		ts = ts.substring(0, Math.min(20, ts.length()));
		pp.add(new Label(ts, Label.CENTER));
		if( type != TT )
		    {
			pp.add(new Label(""+sp.getKills(), Label.CENTER));
			pp.add(new Label(""+sp.getEarnedCash(), Label.CENTER));
		    }
		pp.add(new Label(""+spp.getOverallKills(), Label.CENTER));
		pp.add(new Label(""+spp.getOverallGain(), Label.CENTER));
		rPanel.add(pp);
	    }

	return rPanel;
    }
}
