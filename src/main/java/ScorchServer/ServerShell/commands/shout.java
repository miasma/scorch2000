package ScorchServer.ServerShell.commands;

/*
  shout to all players in a game 
*/
import java.util.Vector;
import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import scorch.Protocol;

public class shout extends shellCommand
{
    static Game g;
    public static String help = "shout something to all player in a game.";
    
    //arguments are ignored here for now.
    public static void run(Vector<String> args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	StringBuilder message = new StringBuilder();

	if ( args.size() == 0 )
	    {
		shell.println("Usage: shout GameID [message]");
		return;
	    }
	
	try
	    {
		//yet to learn of a better way to check if string is number
		try 
		    {
			g = ScorchServer.findGameByID
			    ( Integer.parseInt
			      (args.elementAt(0)));
		    }
		catch( NumberFormatException e )
		    {
			g = null;
		    }
		
		for (int i = 1; i < args.size(); i++)
		    message.append(" ").append(args.elementAt(i));
		
		if (g == null)
		    shell.println("No such game: " + args.elementAt(0));
		else
		    g.broadcast(Protocol.say + Protocol.separator + 
				"(Message from Server):" + message);
	    }
	catch( Exception e )
	    {
		shell.println("say failed: " + e );
	    }
    }
}
