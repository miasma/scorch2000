/*
  Add kills/gain to a player profile.  To be used on players that are not
  currently logged in.
*/

package ScorchServer.ServerShell.commands;

import java.util.Vector;

import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import scorch.*;

public class addkg extends shellCommand {
    public String getHelp() {
        return "To add kills/gain to player's profile.";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell shell) {
        if (args.size() != 3) {
            shell.println("Usage: addkg UserName kills gain");
            return;
        }

        try {
            PlayerProfile p = ScorchServer.lookupPlayer(args.elementAt(0));

            if (p == null)
                shell.println("Player " + args.elementAt(0)
                        + " does not exist.\n");
            else {
                if (ScorchServer.alreadyPlaying(p.getName()))
                    shell.println("Can not modify profile for a logged in player");
                else {
                    p.setOverallKills
                            (p.getOverallKills() + Integer.parseInt
                                    (args.elementAt(1)));
                    p.setOverallGain
                            (p.getOverallGain() + Integer.parseInt
                                    (args.elementAt(2)));
                }
                ScorchServer.changeProfile(p);
            }
        } catch (Exception e) {
            shell.println("addkg failed: " + e);
        }
    }

}
