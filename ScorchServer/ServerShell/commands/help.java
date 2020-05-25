/*
  give the list of current commands with short description
*/

package ScorchServer.ServerShell.commands;

import java.util.*;
import ScorchServer.ScorchServer;
import ScorchServer.ServerShell.ServerShell;

public class help extends shellCommand
{
    public static String help = "To get this help message.";

    //arguments are ignored here for now.
    public static void run(Vector args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	String help[] = shell.getCommandHelp();

	for (int i = 0; i < help.length; i++ )
	    shell.println(help[i]);
    }
}
