package ScorchServer.ServerShell.commands;

/*
  Say something to a player.
*/
import java.util.Vector;
import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import Scorch.Protocol;

public class say extends shellCommand
{
    static Game g;
    static Player p;
    public static String help = "to say something to a player in any game.";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	String message = "";
	int from = 2;

	if ( args.size() == 0 )
	    {
		shell.println("Usage: say {[GameID PlayerID] OR [PlayerName]} [message]");
		return;
	    }
	
	try
	    {
		//yet to learn of a better way to check if string is number
		try 
		    {
			g = ScorchServer.findGameByID
			    ( Integer.parseInt
			      ( args.elementAt(0).toString())); 
			
			p = g.findPlayerByID
			    ( Integer.parseInt
			      ( args.elementAt(1).toString())); 
		    }
		catch( NumberFormatException e )
		    {
			p = ScorchServer.findPlayerByName
			    ((String)args.elementAt(0));
			from = 1;
		    }

		for (int i = from; i < args.size(); i++)
		    message = message + " " + args.elementAt(i);
		
		if (p == null)
		    shell.println("Player not found");
		else
		    p.sendMessage(Protocol.say + Protocol.separator + 
				  "(Message from Server):" + message);
	    }
	catch( Exception e )
	    {
		shell.println("say failed: " + e );
	    }
    }

}
