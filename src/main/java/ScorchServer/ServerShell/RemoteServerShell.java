/*
  The Remote Scorch Server Shell
*/
/*
  Remote Server Shell.  Listens to connections and spawns regular shells
  connected to the socket's streams
*/

package ScorchServer.ServerShell;

import java.io.*;
import java.net.*;

public class RemoteServerShell implements Runnable
{
    public final static int port = 4243;
    ServerSocket ss = null;
    Socket accept = null;
    BufferedReader in = null;
    PrintWriter out = null;
    ServerShell shell = null;
    
    public void run()
    {
	try
	    {
		//maximum of 2 connections in a queue
		ServerSocket ss = new ServerSocket(port, 2);
		
		while (true)
		    {
			System.out.println("RShell: accepting on port "+ port);
			accept = ss.accept();

			initStreams();
			
			if (accept.getInetAddress().getHostName().equals("localhost"))
			    new Thread(new ServerShell(in, out, accept))
				.start();
			else
			    {
				out.println("\nOnly connections from local host are currently accepted. \nSecurity to be implemented... ");
				disconnect();
			    }
			Thread.yield();
		    }
	    }
	catch (Exception e)
	    {
		System.err.println("SERVER: listening failed "+ e);
		System.exit(-1);
	    }
    }

    //initialize input/output streams for connection
    private void initStreams()
    {
	try 
	    {
		in = new BufferedReader(new InputStreamReader
		    (accept.getInputStream()));
		//setting autoflush to true... wonder if it works
		out = new PrintWriter(accept.getOutputStream(), true);
	    }
	catch (IOException e)
	    {
		System.err.println("RSShell: failed to initialize streams "+e);
		System.exit(1);
	    }
    }

    private void disconnect()
    {
	if (accept == null)
	    return;
	try 
	    {
		in.close();
		out.close();
		accept.close();
	    }
	catch (IOException e) { System.err.println(e); }
	accept = null;
    }
}

