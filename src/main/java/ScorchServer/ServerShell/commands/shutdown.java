/*
  Shutdown the server.  Stops listening on the port and disables access
  to the profile data base.  The games will be able to run to conclusion.
*/

package ScorchServer.ServerShell.commands;

import java.util.Vector;

import ScorchServer.ServerShell.ServerShell;

public class shutdown extends shellCommand {
    public String getHelp() {
        return "Shut down the server. games finished without access to profile db.";
    }

    public void run(Vector<String> args, ServerShell owner) {
        owner.println("Shutting down server...");
        owner.shutdownServer();
    }
}
