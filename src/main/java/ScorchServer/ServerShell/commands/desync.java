
package ScorchServer.ServerShell.commands;

/*
  List all current games
*/

import java.util.Vector;

import ScorchServer.ScorchServer;
import ScorchServer.ServerShell.ServerShell;

public class desync extends shellCommand {
    public String getHelp() {
        return "To print current number of desyncs.";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell owner) {
        owner.println("Observed number of desyncs so far: " +
                ScorchServer.getDesyncCount() + " out of "
                + ScorchServer.potentialDesyncCount
                + " opportunities.\n");
    }
}
