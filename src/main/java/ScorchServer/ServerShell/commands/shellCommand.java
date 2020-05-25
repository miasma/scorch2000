package ScorchServer.ServerShell.commands;

/* 
   class inherited by all commands.
*/

import ScorchServer.ServerShell.ServerShell;

import java.util.Vector;


public abstract class shellCommand {
    abstract public String getHelp();

    abstract public void run(Vector<String> args, ServerShell owner);
}
