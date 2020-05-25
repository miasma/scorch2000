/*
  give the list of current commands with short description
*/

package ScorchServer.ServerShell.commands;

import java.util.*;

import ScorchServer.ServerShell.ServerShell;

public class help extends shellCommand
{
    public static String help = "To get this help message.";

    //arguments are ignored here for now.
    public static void run(Vector<String> args, Object owner)
    {
	ServerShell shell = (ServerShell)owner;
	String[] help = shell.getCommandHelp();

        for (String s : help) shell.println(s);
    }
}
