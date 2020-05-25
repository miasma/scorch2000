/*
  boots a player from a game.
*/

package ScorchServer.ServerShell.commands;

import java.util.Vector;
import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;

public class boot extends shellCommand
{
    static Game g;
    static Player p;

    public static String help = "To boot a person with PlayerID from game GameID.";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	String message = "";
	
	if (args.size() < 2)
	    {
		shell.println("Usage: boot GameID PlayerID [reason]");
		return;
	    }
	
	try
	    {
		g = ScorchServer.findGameByID
		    ( Integer.parseInt
		      ( args.elementAt(0).toString())); 

		p = g.findPlayerByID
		    ( Integer.parseInt
		      ( args.elementAt(1).toString())); 

		if (args.size() == 2)
		    p.dropPlayer("one of the developers didn't like you very much");
		else
		    {
		        for (int i = 2; i < args.size(); i++)
			    message = message + " " + args.elementAt(i);
			p.dropPlayer(message);
		    }
	    }
	catch( Exception e )
	    {
		shell.println("Boot: failed to boot player "+e);
	    }
    }
}

