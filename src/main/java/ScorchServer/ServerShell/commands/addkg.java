/*
  Add kills/gain to a player profile.  To be used on players that are not
  currently logged in.
*/

package ScorchServer.ServerShell.commands;

import java.util.Vector;
import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import scorch.*;

public class addkg extends shellCommand
{
    static Game g;
    static PlayerProfile p;
    public static String help = "To add kills/gain to player's profile.";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	String password = "";

	if ( args.size() != 3 )
	    {
		shell.println("Usage: addkg UserName kills gain");
		return;
	    }
	
	try
	    {
		p = ScorchServer.lookupPlayer( (String)args.elementAt(0) );
		
		if (p == null)
		    shell.println("Player " + (String)args.elementAt(0)
				  +" does not exist.\n");
		else
		    {
			if (ScorchServer.alreadyPlaying( p.getName() ))
			    shell.println("Can not modify profile for a logged in player");
			else
			    {
				p.setOverallKills
				    ( p.getOverallKills() + Integer.parseInt
				      ( args.elementAt(1).toString() ) );
				p.setOverallGain
				    ( p.getOverallGain() + Integer.parseInt
				      ( args.elementAt(2).toString() ) );
			    }
			ScorchServer.changeProfile(p);
		    }
	    }
	catch( Exception e )
	    {
		shell.println("addkg failed: " + e );
	    }
    }

}
