/*
  gives information about a player.
*/
package ScorchServer.ServerShell.commands;

import java.util.Vector;
import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;

public class whois extends shellCommand
{
    static Game g;
    static Player p;

    public static String help = "To get detailed description of the player (name/email/resolution etc.)";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	String message = null;
	
	if (args.size() != 2 && args.size() != 1)
	    {
		shell.println
		    ("Usage: whois {[GameID PlayerID] OR [PlayerName]}");
		return;
	    }
	
	try
	    {
		if (args.size() == 1)
		    p = ScorchServer.findPlayerByName
			((String)args.elementAt(0));
		else
		    {
			g = ScorchServer.findGameByID
			    ( Integer.parseInt
			      ( args.elementAt(0).toString())); 
			
			p = g.findPlayerByID
			    ( Integer.parseInt
			      ( args.elementAt(1).toString())); 
		    }

		if (p == null)
		    shell.println("Player " + args.elementAt(0) +
				  " not currently playing");
		else
		    shell.println(p + "\t\tfrom: " + p.getHostName());
	    }
	catch( Exception e )
	    {
		shell.println("whois failed " + e);
	    }
    }
}

