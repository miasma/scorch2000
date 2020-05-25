package ScorchServer.ServerShell.commands;

/*
  shout to all players in a game 
*/

import java.util.Vector;

import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import scorch.Protocol;

public class shout extends shellCommand {
    public String getHelp() {
        return "shout something to all player in a game.";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell shell) {
        StringBuilder message = new StringBuilder();

        if (args.size() == 0) {
            shell.println("Usage: shout GameID [message]");
            return;
        }

        try {
            //yet to learn of a better way to check if string is number
            try {
                Game g;
                g = ScorchServer.findGameByID
                        (Integer.parseInt
                                (args.elementAt(0)));
                for (int i = 1; i < args.size(); i++)
                    message.append(" ").append(args.elementAt(i));

                g.broadcast(Protocol.say + Protocol.separator +
                        "(Message from Server):" + message);
            } catch (NumberFormatException e) {
                shell.println("No such game: " + args.elementAt(0));
            }

        } catch (Exception e) {
            shell.println("say failed: " + e);
        }
    }
}
