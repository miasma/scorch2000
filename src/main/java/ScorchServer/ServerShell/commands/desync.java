
package ScorchServer.ServerShell.commands;

/*
  List all current games
*/
import java.util.Vector;
import ScorchServer.ScorchServer;
import ScorchServer.ServerShell.ServerShell;

public class desync extends shellCommand
{
    public static String help = "To print current number of desyncs.";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	((ServerShell)owner).println("Observed number of desyncs so far: " +
				     ScorchServer.getDesyncCount() +" out of " 
				     + ScorchServer.potentialDesyncCount 
				     + " opportunities.\n");
    }
}
