/*
  give the list of current commands with short description
*/

package ScorchServer.ServerShell.commands;

import java.util.*;

import ScorchServer.ServerShell.ServerShell;

public class help extends shellCommand {
    public String getHelp() {
        return "To get this help message.";
    }

    //arguments are ignored here for now.
    public void run(Vector<String> args, ServerShell shell) {
        for (shellCommand cmd: shell.commands) shell.println(cmd.getHelp());
    }
}
