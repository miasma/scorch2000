/*
  The Scorch Server Shell (console)
*/

package ScorchServer.ServerShell;

import java.io.*;
import java.util.*;
import java.lang.reflect.Method;
import java.net.Socket;
import ScorchServer.ScorchServer;

public class ServerShell implements Runnable
{
    private static String prompt = "ServerShell-> ";
    private static String separators = " ";
    private Class argType[] = new Class[2];

    private String commandNames[] = { "lg", "boot", "help", "say", "whois",
				      "desync", "passwd", "shout", "addkg",
				      "shutdown"};
    private String commandHelp[] = new String[commandNames.length];
    private Hashtable commandsHash = new Hashtable(),
	classHash = null;//new Hashtable();

    private ScorchServer owner = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private Socket socket = null;
    
    
    public ServerShell(ScorchServer ss)
    {
	super();
	
	this.owner = ss;
    	//take over standart input and output
	in = new BufferedReader(new InputStreamReader(System.in));
	out = new PrintWriter(System.out, true);
    }
    
    //alternative constructor to use for spawning by RemoteServerShell
    public ServerShell(BufferedReader i, PrintWriter o, Socket s)
    {
	super();
	
	this.socket = s;
	this.in = i;
	this.out = o;
    }

    //provides common interface for output of all commands
    public void print(String msg)
    {
	out.print(msg);
	out.flush();
    }

    //provides common interface for output of all commands
    public void println(String msg)
    {
	out.println(msg);
    }

    private boolean executeCommand(String cmd)
    {
	Object[] args = new Object[2];
	String command = null;
	Vector v = new Vector();
	Method commandMethod = null;

	if (cmd == null) return true;

	StringTokenizer st = new StringTokenizer(cmd, separators);
	
	if (st.hasMoreTokens())
	    command = st.nextToken();

	if (command == null)
	    return true;

	if ("quit".equals(command))
	    return false;

	//get the rest of arguments
	while(st.hasMoreTokens())
	    v.addElement(st.nextToken());

	args[0] = v;
	args[1] = this;

	commandMethod = (Method)commandsHash.get(command);

	try
	    {
		if (commandMethod == null)
		    println(" Command " +command +" not found.\n");
		else
		    commandMethod.invoke(null, args);
	    }
	catch (Exception e) 
	    { 
		println("invoke failed: "+e); 
	    }

	return true;
    }

    private void loadCommands()
    {	
	Class commandClass;
	try {
	    argType[0] = Class.forName("java.util.Vector");
	    argType[1] = Class.forName("java.lang.Object");
	}
	catch (Exception e)
	    { 
		println(""+e);
		System.exit(1);
	    }

	print("Loading commands ");

	for (int i = 0; i < commandNames.length; i++)
	    {
		try 
		    {
			commandClass = Class.forName
			    ("ScorchServer.ServerShell.commands."
			     +commandNames[i]);
			commandHelp[i] = commandNames[i] + "\t\t" + 
			    (String)commandClass.getField("help").get(null)
			    +"\n";
			commandsHash.put(commandNames[i], 
					 commandClass.getMethod
					 ("run", argType));
			print(". ");
		    }
		catch( Exception e )
		    {
			println(" Command " + commandNames[i] 
				+ " not found.\n" + e);
		    }
	    }
	println(" Done.\n");
    }

    /*    protected void initStreams()
    {
	//take over standart input.
	in = new BufferedReader(new InputStreamReader(System.in));
    }
    */
    public void run()
    {
	//load the available commands into the hashtable
	loadCommands();
	
	print(prompt);
	try {
	    
	    while ( this.executeCommand( in.readLine() ) )
		print(prompt); 

	} catch (Exception e) {
	    System.err.println("Shell error: " + e);
	}
	
	println("\nExiting... Goodbye");
	disconnect();
    }

    public String[] getCommandHelp()
    {
	return commandHelp;
    }

    private void disconnect()
    {
	try
	    {
		in.close();
		out.close();
		if (socket != null)
		    socket.close();
	    }
	catch (IOException e)
	    {
		System.err.println("Shell: disconnect failed " + e);
	    }
    }

    public void shutdownServer()
    {
	owner.shutdown();
    }
    /*
    public static void main(String[] args)
    {
	new Thread(new ServerShell()).start();
    }
    */
}
