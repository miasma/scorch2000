/*
  gives information about a player.
*/
package ScorchServer.ServerShell.commands;

import java.util.Vector;

import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;

public class whois extends shellCommand {

    public String getHelp() {
        return "To get detailed description of the player (name/email/resolution etc.)";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell shell) {
        if (args.size() != 2 && args.size() != 1) {
            shell.println
                    ("Usage: whois {[GameID PlayerID] OR [PlayerName]}");
            return;
        }

        try {
            Player p;
            if (args.size() == 1)
                p = ScorchServer.findPlayerByName
                        (args.elementAt(0));
            else {
                Game g = ScorchServer.findGameByID
                        (Integer.parseInt
                                (args.elementAt(0)));

                p = g.findPlayerByID
                        (Integer.parseInt
                                (args.elementAt(1)));
            }

            if (p == null)
                shell.println("Player " + args.elementAt(0) +
                        " not currently playing");
            else
                shell.println(p + "\t\tfrom: " + p.getHostName());
        } catch (Exception e) {
            shell.println("whois failed " + e);
        }
    }
}

