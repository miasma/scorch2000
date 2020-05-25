/*
  change player's password something to a player.
*/

package ScorchServer.ServerShell.commands;

import java.util.Vector;

import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import scorch.*;

public class passwd extends shellCommand {
    public String getHelp() {
        return "To reset a password for a user.";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell shell) {
        StringBuilder password = new StringBuilder();

        if (args.size() == 0) {
            shell.println("Usage: passwd PlayerName [password]");
            return;
        }

        try {
            PlayerProfile p = ScorchServer.findProfileByName
                    (args.elementAt(0));

            for (int i = 1; i < args.size(); i++)
                password.append(args.elementAt(i));

            if (p == null)
                shell.println("Player " + args.elementAt(0)
                        + " does not exist.\n");
            else {
                System.out.println("Password-" + password + "-");
                p.setPassword(password.toString());
                p.encrypt();
                ScorchServer.changeProfile(p);
            }
        } catch (Exception e) {
            shell.println("setPasswd failed: " + e);
        }
    }

}
