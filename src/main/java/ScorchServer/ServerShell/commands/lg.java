
package ScorchServer.ServerShell.commands;

/*
  List all current games
*/
import java.util.Vector;
import ScorchServer.ScorchServer;
import ScorchServer.ServerShell.ServerShell;

public class lg extends shellCommand
{
    public static String help = "To list the current games";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	((ServerShell)owner).print(ScorchServer.allGamesToString());
    }
}
