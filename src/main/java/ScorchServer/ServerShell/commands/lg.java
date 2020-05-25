
package ScorchServer.ServerShell.commands;

/*
  List all current games
*/

import java.util.Vector;

import ScorchServer.ScorchServer;
import ScorchServer.ServerShell.ServerShell;

public class lg extends shellCommand {
    public String getHelp() {
        return "To list the current games";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell owner) {
        owner.print(ScorchServer.allGamesToString());
    }
}
