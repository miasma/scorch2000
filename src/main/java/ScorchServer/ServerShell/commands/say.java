package ScorchServer.ServerShell.commands;

/*
  Say something to a player.
*/

import java.util.Vector;

import ScorchServer.*;
import ScorchServer.ServerShell.ServerShell;
import scorch.Protocol;

public class say extends shellCommand {
    public String getHelp() {
        return "to say something to a player in any game.";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell shell) {
        StringBuilder message = new StringBuilder();
        int from = 2;

        if (args.size() == 0) {
            shell.println("Usage: say {[GameID PlayerID] OR [PlayerName]} [message]");
            return;
        }

        try {
            //yet to learn of a better way to check if string is number
            Player p;
            try {
                Game g = ScorchServer.findGameByID
                        (Integer.parseInt
                                (args.elementAt(0)));

                p = g.findPlayerByID
                        (Integer.parseInt
                                (args.elementAt(1)));
            } catch (NumberFormatException e) {
                p = ScorchServer.findPlayerByName
                        (args.elementAt(0));
                from = 1;
            }

            for (int i = from; i < args.size(); i++)
                message.append(" ").append(args.elementAt(i));

            if (p == null)
                shell.println("Player not found");
            else
                p.sendMessage(Protocol.say + Protocol.separator +
                        "(Message from Server):" + message);
        } catch (Exception e) {
            shell.println("say failed: " + e);
        }
    }

}
