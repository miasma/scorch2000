/*
  Shutdown the server.  Stops listening on the port and disables access
  to the profile data base.  The games will be able to run to conclusion.
*/

package ScorchServer.ServerShell.commands;

import java.util.Vector;

import ScorchServer.ServerShell.ServerShell;

public class shutdown extends shellCommand
{
    public static String help = "Shut down the server. games finished without access to profile db.";

    public static void run(Vector<String> args, Object owner)
    {
	((ServerShell)owner).println("Shutting down server...");
	((ServerShell)owner).shutdownServer();
    }
}
