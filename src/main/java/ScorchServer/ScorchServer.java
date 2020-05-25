/*
  Class:  ScorchServer

  Author: Alexander Rasin

  ScorchServer class is the class representing the actual server.  It's 
  purpose is to listen to client connections and once a connection is 
  established, it starts an instance of a ServerThread that will be 
  representing the client on the server side.
  ScorchServer also keeps track of the games in order to be able to
  run global checks for a user that has already in another game.  It
  also has access to class Disk that stores and looks up profiles - 
  games can call on the Server in order to lookup or write a profile of 
  a player.
  Once logged in succesfully, all the clients register on the server 
  that either assigns a client to existing game or makes a new one if 
  there are no spots available.
*/
  
package ScorchServer;

import java.net.*;
import java.util.Vector;
import java.io.IOException;
import scorch.PlayerProfile;
import scorch.Protocol;
import ScorchServer.ServerShell.*;

public class ScorchServer
{
    public static final String 
	version = "Scorched Earth 2000 Server, v1.271, 07/26/2000";

    private Socket accept = null;
    private ServerSocket s_socket = null;
    private int port;
    private static int game_count = 0;
    public static int potentialDesyncCount = 0;
    
    //all the games on the server
    private static Vector games = new Vector();
    //responsible for profile storage and lookup
    private static Disk disk = null;
    private static Vector topTen = new Vector();
    //can modify top ten to top 'any other number'
    private static int topX = 10;

    public static void main(String[] args)
    {
	disk = new Disk();
	ScorchServer ss = null;
	ServerShell shell = null;

	System.out.println(version+"\n");
	System.out.println
	    ("Copyright (C) 1999-2000 KAOS Software\n"+
	     "Scorched Earth 2000 comes with ABSOLUTELY NO WARRANTY;\nThis is"+
	     " free software, and you are welcome to redistribute it\nunder"+
	     " certain conditions. Please read COPYING for details.\n");
	
	try 
	    {
		//new Thread(new RemoteServerShell()).start();
		if (args.length > 0)
		    ss = new ScorchServer(Integer.parseInt(args[0]));
		else
		    ss = new ScorchServer(4242);

		shell = new ServerShell( ss );
		new Thread( shell ).start();
		ss.runServer();
	    }
	catch (Throwable t)
	    {
		System.out.println("ERROR starting server: "+t);
	    }
	System.exit( 0 );
    }
    
    public ScorchServer(int port)
    {
	this.port=port;
	//runServer();
    }
 
    //check if a player is allready participating in some game
    public static synchronized boolean alreadyPlaying(String name)
    {
	Game g = null;

	//see if the player is already logged into one of the games
	for (int i = 0; i < games.size(); i++)
	    {
		g = (Game)games.elementAt(i);
		if (g.alreadyPlaying(name))
		    return true;
	    }
	return false;
    }

    //lookup a player in the profile hashtable
    public static synchronized PlayerProfile lookupPlayer(String name)
    {
	return disk.getProfile(name);
    }
    
    //write the hashtable to disk
    //    public static synchronized void writeTable()
    //{
    //	disk.writeTable();
    //}
    
    //makes a new player in the profile database. 
    public static synchronized void newPlayer(PlayerProfile profile)
    {
	disk.add(profile);
	//writeTable();
    }
    
    //change user's profile (as requested by the user)
    public static synchronized void changeProfile(PlayerProfile profile)
    {
	//System.out.println("CHANGING " + profile);
	disk.change(profile);
	//System.out.println("RESULT " + lookupPlayer(profile.getName()));
	//writeTable();
    }
    
    //removes the user profile
    public static synchronized void deleteProfile(PlayerProfile profile)
    {
    
    }

    public static String allGamesToString()
    {
	String all = "";
	Game g = null;
	int multi_player = 0;
	
	for (int i = 0; i < games.size(); i++)
	    {
		g = (Game)games.elementAt(i);
		if (g.getHumanCount() > 1)  multi_player++;
		all = all + g + "\n";
	    }
	
	all += "Total of " + games.size() + " games, " + multi_player +
	    " multiplayer.\n\n";

	return all;
    }

    public static int getDesyncCount()
    {
	return Disk.getDesyncCount();
    }

    public static int getGameCount()
    {
	return game_count;
    }

    public static Game findGameByID(int index)
    {
	Game g;

	for (int i = 0; i < games.size(); i++)
	    {
		g = ((Game)games.elementAt(i));
		if (g.getID() == index)
		    return g;
	    }

	return null;
    }

    public static PlayerProfile findProfileByName( String name )
    {
	return disk.findProfileByName( name );
    }

    public static Player findPlayerByName( String name )
    {
	Player pl = null;

	for (int i = 0; i < games.size(); i++)
	    {
		pl = ((Game)games.elementAt(i)).findPlayerByName(name);
		if (pl != null)
		    return pl;
	    }

	return null;
    }

    public static void insertTopTen(PlayerProfile profile)
    {
	int position = 0;

	//the profile is already in the top list
	//	if (topTen.contains(profile))
	//    return;

	for (int i = 0; i < topTen.size(); i++)
	    if (profile.getName().equals
		(((PlayerProfile)topTen.elementAt(i)).getName()))
		topTen.removeElementAt(i);

	while (position<topX && position < topTen.size())
	    {
		if (profile.getOverallGain() > 
		    ((PlayerProfile)topTen.elementAt
		     (position)).getOverallGain())
		    break;
		
		position++;
	    }

	if (position == topTen.size() && !(position<topX) )
	    return;

	//System.out.println("Inserting " + profile.getName() + " at " + position);
	topTen.insertElementAt(profile, position);

	//if we are over the size of hall of fame, remove the last player
	if (topTen.size() > topX)
	    topTen.removeElement(topTen.lastElement());
    }

    public static String getTopTen()
    {
	String result = Protocol.topten + Protocol.separator;

	for (int i = 0; i < topTen.size(); i++)
	    result = result + ((PlayerProfile) topTen.elementAt(i)) +
		Protocol.separator;
	
	return result;
    }

    //register a logged in player. essentially adds a player to a game he 
    //will participate in. if there is no available game - make a new one
    //player can only participate in the game that matches his resolution
    public static Game register(ServerThread player, String resolution)
    {
	Game g = null;
	
	//find an emtpy game and add the player to it.
	for (int i = 0; i < games.size(); i++)
	    {	    
		g = (Game)games.elementAt(i);
		if (g.reserveSpot(resolution))
		    {
			g.addPlayer(player, Protocol.loggedin);
			return g;
		    }
	    }
	
	//no available games. make a new one and add the player to it
	g = new Game(game_count++, resolution);
	games.addElement(g);
	g.addPlayer(player, Protocol.loggedin);
	return g;
    }

    public static void removeGame( Game g )
    {
	games.removeElement(g);
    }

    public static void broadcast( String msg )
    {
	for (int i = 0; i < games.size(); i++)
	    ((Game)games.elementAt(i)).broadcast( msg );
    }

    //stop listening for connection
    //and disable disk access
    public void shutdown()
    {
	broadcast( Protocol.say + Protocol.separator + "Server is shuting "+
		   "down.  You may continue playing, but all profile "+
		   "modifications (such as changing password) will be lost");
	broadcast( Protocol.say + Protocol.separator + "If you notice any " +
		   "strange behavior in the game please report to " +
		   "alexr@scorch2000.com");
	try
	    {
		s_socket.close();
	    }
	catch (IOException e)
	    {
		System.out.println("ScorchServer: error shutting down " + e);
	    }
	disk.shutdown();
    }

    public void runServer()
    {
	ServerThread st = null;
	try
	    {
		//maximum of 5 connections in a queue
		s_socket = new ServerSocket(port, 5);
		
		while (true)
		    {
			//System.out.println("SERVER: accepting on port "+port);
			accept = s_socket.accept();

			st = new ServerThread(accept);

			Thread.yield();
		    }
	    }
	catch (Exception e)
	    {
		//System.err.println("SERVER: listening failed "+ e);
		//System.exit(-1);
	    }

	try 
	    {
		while( games.size() > 0 )
		    Thread.currentThread().sleep( 1000 );
	    }
	catch (Exception e)
	    {}
    }
}
