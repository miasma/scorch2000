/*
  Class : Game

  Author: Alexander Rasin

  Game class represents a single game hosted on the scorch server.  It has 
  a list of all players, game options as well as a seed that is used by all
  clients to assure turn consistency (i.e. all clients use same random 
  number generator).
  The purpose of this class is to notify players whose turn it is, broadcast 
  move of a player to the rest of game participants and notify players when 
  one of them leaves the game.
*/

package ScorchServer;

import java.util.*;
import Scorch.Protocol;

public class Game
{
    private Vector players;        //the list of participating players
    private Vector dead_pl;        //the list of players dead in this round
    private Vector players_left;   //the list of players who left this round
    private int turn = 0;                 //who's turn it is right now
    private Player turn_order[] = null;   //the order in which players go.
    private int num_rounds, total_rounds;
    private int id;
    //player count is initially one because a check for available spots
    //only occurs when a game already exists. i.e. a new game is not
    //checked and the player_count is not incremented for the master
    private int player_count = 1;
    //next player id
    private int player_id = 0;
    private long start_time = -1;

    //can_join is a flag that is set to false when the master has started
    //the game.  initialized set to true when all player options are received
    //game over is a flag that is set than the game is left by master
    private boolean can_join = false, initialized = false, gameover = false;
    
    //check if master has already sent mass kill. at the end of the turn 
    //it will be broadcasted.
    private boolean massKill = false;

    public static final int MAX_PLAYERS = 8;
    private int max_players = MAX_PLAYERS; 
    private String gameoptions = null;     //the game options (not parsed here)
    private String resolution = null;      //the game resolution

    //seed shared by all players for synchronous random generator
    private long seed;
    
    //DEBUG
    private String check = null, checkSentBy = null;
    private boolean lostSync = false;
    private int human_count = 0;

    public Game(int id, String res)
    {
	this(id);
	resolution = res;
    }

    public Game(int id)
    {
	this.id = id;
	players = new Vector();
	dead_pl = new Vector();
	players_left = new Vector();
	can_join = false;
	seed = System.currentTimeMillis();
    }

    public int getID()
    {
	return id;
    }
     
    public long getSeed()
    {
	return seed;
    }

    public String getResolution()
    {
	return resolution;
    }

    public int getHumanCount()
    {
	return human_count;
    }

    public String getAllHumanPlayersString()
    {
	String result = "";
	Player p;
	
	for (int i = 0; i < dead_pl.size() + players.size(); i++)
	    {
		p = (Player) ((i >= players.size()) ? 
			      dead_pl.elementAt(i-players.size()) :
			      players.elementAt(i));

		if (p instanceof ServerThread)
		    result += p + "\t\tfrom: " + p.getHostName();
	    }
	return result;
    }

    //DEBUG
    public void checkSync(String msg, String name)
    {
	if (check == null)
	    check = msg;
	else
	    if (!check.equals(msg))
		{
		    broadcast(Protocol.shout + Protocol.separator + "Client " + name + " has lost sync. It is recommended that you restart the game now");
		    broadcast(Protocol.shout + Protocol.separator + "Please report this problem to meshko@scorch2000.com");
		    if (!lostSync)
			{
			    System.out.println("Game " + id + 
				    " lost sync, log is being generated");
			    Disk.recordGame(this, checkSentBy +" " + check, 
					          name + " " + msg);
			    lostSync = true;
			    broadcast(Protocol.requestlog);
			}
		}
	checkSentBy = name;
    }

    //this does not have to be synchronized because in any game 
    //there is only one master that sets the options.
    public void setOptions(String opt)
    {
	StringTokenizer st = new StringTokenizer(opt, ""+Protocol.separator);
	st.nextToken();  //skip command
	st.nextToken();  //skip gravity
	//get the number of rounds
	total_rounds = num_rounds = Integer.parseInt(st.nextToken());  
	gameoptions = opt;
    }

    //check if all the options (each player and game) have been recieved
    //and send the options out, if all of them are recieved.
    public synchronized void checkInitialize()
    {
	if (initialized)
	    return;

	for (int i = 0; i < players.size(); i++)
	    {
		Player st = (Player)players.elementAt(i);
		if (st.getOptions() == null)
		    return;
	    }
	if (gameoptions == null) 
	    return;
	
	//if we got that far, the game is initialized
	initialized = true;

	for (int i = 0; i < players.size(); i++)
	    broadcast(((Player)players.elementAt(i)).getOptions());

	broadcast(gameoptions + Protocol.separator + getSeed());
    }

    //check if all the clients are ready to make the next turn
    //send out the MAKETURN command notifying whose turn has come
    public synchronized void checkTurn()
    {
	Player st;

	//last player left, there is no one left (either dead or alive)
	if ((players.size() == 0 && dead_pl.size() == 0))// || massKill)
	    return;

	//check if all the players who are still alive in this round 
	//are ready for the next turn (returns if at least one isn't)
	for (int i = 0; i < players.size(); i++)
	    {
		st = (Player)players.elementAt(i);
		//System.out.println(" status " + st.isReady());
		if (!st.isReady())
		    return;
	    }

	//check if all the players who are already dead in this round are
	//ready for the next turn (the dead players still see everything)
	for (int i = 0; i < dead_pl.size(); i++)
	    {
		st = (Player)dead_pl.elementAt(i);
                //System.out.println(" status " + st.isReady());
		if (!st.isReady())
		    return;
	    }

	for (int i = 0; i < players_left.size(); i++)
	    broadcast(Protocol.playerleft + Protocol.separator
		      + ((Player)players_left.elementAt(i)).getID());

	players_left.removeAllElements();

	//if we have one (or zero) living players left it's time to start 
	//a new round.  Unless, of course, there are no dead players left
	//either. That means the player is by himself in the game.
	if (players.size() < 2)
	    {
		if ((dead_pl.size() == 0) && (players.size() == 1))
		    ((Player)players.elementAt(0)).dropPlayer
			("you are the only player left.");
		else
		    endRound();
		return;
	    }

	//move the turn variable until we find the next living player
	while (! players.contains(turn_order[(++turn) % turn_order.length]));
	//System.out.println("SKIP " + turn_order[turn % turn_order.length]);

	turn = turn % turn_order.length;

	if (massKill)
	    {
		//broadcast the mass kill message to all clients.
		broadcast(Protocol.masskill);
		massKill = false;
		//DEBUG
		check = null;
	    }
	else
	    //tell clients who makes the next turn
	    {
		broadcast(Protocol.maketurn + Protocol.separator + 
			  turn_order[turn].getID());
		check = null;
	    }
	
	//new turn started, set the ready flags to false
	setReadyFlags(false);
    }

    //set the ready flag of players accordingly.
    //when all players are ready (done drawing) the next turn can start
    private void setReadyFlags(boolean value)
    {
	for (int i = 0; i < players.size(); i++)
	    ((Player)players.elementAt(i)).setReady(value);

	for (int i = 0; i < dead_pl.size(); i++)
	    ((Player)dead_pl.elementAt(i)).setReady(value);
    }

    //see if a player is already participating in the game
    public boolean alreadyPlaying(String name)
    {
	for (int i = 0; i < players.size(); i++)
	    {
		Player st = (Player)players.elementAt(i);
		if (st.getName().equals(name))
		    return true;
	    }
	return false;
    }

    // The game is over 
    public void gameOver(String reason)
    {
	int i = 0;
	if ( start_time > 0 )
	    System.out.println("GAME " +id+ " OVER, total time " + timeToString
			       (System.currentTimeMillis() - start_time));
	else
	    System.out.println("GAME " +id+" OVER before having begun(sp?)");

	gameover = true;

	//return to the regular vector of players (from the dead_pl vector)
	//and disconnect all the players that are still in the game
	resurrectPlayers();
	while (players.size() > 0)
	    {
		Player st = (Player)players.elementAt(0);
		if (st != null)
		    {
			player_count--;
			st.dropPlayer(reason);
		    }
	    }
	ScorchServer.removeGame(this);
    }

    public void setMaxPlayers(int max)
    {
	max_players = max;
	can_join = true;
    }

    public synchronized void gameStarted()
    {
	//int human_count = 0;
	Player pl = null;
	can_join = false;

	start_time = System.currentTimeMillis();

	turn_order = new Player[players.size()];
	
	//fill in the ids of players (in random order) to the turn_order array
	for (int i = 1; i <= players.size(); i++)
	    {
		pl = (Player)(players.elementAt( (i*37) % players.size() ));
		turn_order[i-1] = pl;
		if (pl instanceof ServerThread)
		    human_count++;
	    }
	
	if (human_count > 1)
	    ScorchServer.potentialDesyncCount++;
    }

    public void massKill(Player p)
    {
	//special case of master sending the masskill during his/her
	//own turn. broadcast the masskill immideately
	if (p == turn_order[turn] && !((ServerThread)p).madeTurn())
	    broadcast(Protocol.masskill);
	else
	    massKill = true;

	//DEBUG
	check = null;
    }
    
    public synchronized boolean reserveSpot(String player_resolution)
    {
	//	System.out.println(" called with " + player_resolution + " compare to " + this.resolution);

	if (this.resolution.equals(player_resolution))
	    return reserveSpot();
	else
	    return false;
    }

    public synchronized boolean reserveSpot()
    {
	if (can_join && (player_count < max_players))
	    {
		player_count++;
		return true;
	    }

	return false;
    }

    public synchronized void playerDied(int dead_player)
    {
	Player st;

	//introduce player lookup by id??
	for (int i = 0; i < players.size(); i++)
	    {
		st = (Player)players.elementAt(i);
		if (st.getID() == dead_player)
		    {
			players.removeElement(st);
			dead_pl.addElement(st);
			checkTurn();
			return;
		    }
	    }
    }
    
    //return the players from the 'dead' vector to the regular list.
    private void resurrectPlayers()
    {
	Object st;

	while (dead_pl.size() > 0)
	    {
		st = dead_pl.elementAt(0);
		dead_pl.removeElement(st);
		players.addElement(st);
	    }
    }

    //end the previous round and signal starting of the new one.
    private synchronized void endRound()
    {
	num_rounds--;

	//all the dead players come alive. the new round has started
	resurrectPlayers();

	//DEBUG
	check = null;

	if (num_rounds == 0)
	    broadcast(Protocol.endofgame);
	else
	    broadcast(Protocol.endofround);
	//new turn will be starting then the new round is ready
	massKill = false;
	setReadyFlags(false);
    }

    //remove a player from the game.
    public synchronized void leave(Player player)
    {
	if (players.contains(player))
	    players.removeElement(player);
	else
	    if (dead_pl.contains(player))
		dead_pl.removeElement(player);
	else
	    {
		//this should never happen. if a player has a reference
		//to a game object, he should be in that game.
		System.err.println("Game: No such player!");
		this.gameOver(" of an internal error.");
	    }

	//if the players can still join, the leave of the player
	//is broadcasted immideately, otherwise stored for next round
	if (can_join)
	    broadcast(Protocol.playerleft + Protocol.separator 
		      + player.getID());
	else
	    if (!players_left.contains(player))
		players_left.addElement(player);

	if (player instanceof ServerThread)
	    System.out.println
		(" Player " + player.getName() + " left the game #"+id);
	player_count--;
	
	if (gameover) return;
	
	//if the master left, the game is officially over.
	if ((player instanceof ServerThread) && 
	    (((ServerThread)player).isMaster()))
	    {
		//System.out.println("The master has left the game");
		this.gameOver("the master left the game.");
	    }
	else
	//if there are no players left in the game (dead or alive)
	//remove the game from the list of registered games in the server
	if (players.size() + dead_pl.size() == 0)
	    {
		//System.out.println("Game " +id +" :NO MORE PLAYERS");
		ScorchServer.removeGame(this);
	    }
	else
	    {
		//check if all options have been set and it's time to 
		//start the game (by sending all game options to clients)
		checkInitialize();
		//see if everybody done drawing the last turn and it's
		//time to make a new one.
                if (turn_order != null && turn_order[turn] == player)
		    setReadyFlags(true);
		checkTurn();
		
		//if we have one player left the game cannot continue and the 
		//single left client is disconnected.
		//note that if can join is true, players are still gathering
		if ( !can_join && (dead_pl.size() + players.size()) == 1)
		    ((Player)players.elementAt(0)).dropPlayer
			("you are the only player left.");
		else
		    //if there is 1 or 0 living players - end the round
		    if (!can_join && players.size() < 2)
			endRound();
	    }
    }
    
    //broadcast a message to every client (dead and alive) in the game
    public synchronized void broadcast(String msg)
    {
	Player st;
	
	for (int i = 0; i < players.size(); i++)
	    {
		st = (Player)players.elementAt(i);
		if (st != null)
		    st.sendMessage(msg);
	    }
	for (int i = 0; i < dead_pl.size(); i++)
	    {
		st = (Player)dead_pl.elementAt(i);
		if (st != null)
		    st.sendMessage(msg);
	    }
    }

    public synchronized void addPlayer(Player player, String command)
    {
	players.addElement(player);
	//first set the game and notify the player
	player.setGame(this, player_id++);

	//System.out.println("ADDING " + player.getProfile());

	broadcast(command + Protocol.separator + player.getProfile() 
		  + Protocol.separator + player.getID());

	//notify you about the players that logged in before you
	for (int i = 0; i < players.size(); i++)
	    {
		Player pl = ((Player)players.elementAt(i));
		//if this is not you, you need to know about him/her
		if (pl.getID() != player.getID())
		    player.sendMessage
			(Protocol.loggedin+Protocol.separator+pl.getProfile() 
			 + Protocol.separator + pl.getID());
	    }
    }

    //don't think it really has to be synchronized
    public synchronized Player findPlayerByID(int index)
    {
	Player p;

	for (int i = 0; i < dead_pl.size() + players.size(); i++)
	    {
		p = (Player) ((i >= players.size()) ? 
			      dead_pl.elementAt(i-players.size()) :
			      players.elementAt(i));

		if (p.getID() == index)
		    return p;
	    }
	return null;
    }

    //don't think it really has to be synchronized
    public synchronized Player findPlayerByName(String name)
    {
	Player p;

	for (int i = 0; i < dead_pl.size() + players.size(); i++)
	    {
		p = (Player) ((i >= players.size()) ? 
			      dead_pl.elementAt(i-players.size()) :
			      players.elementAt(i));

		if (p.getName().equals(name))
		    return p;
	    }
	return null;
    }

    private String timeToString( long time )
    {
	time /= 1000;

	return ""+ time / 3600 + "h. "+ (time % 3600)/60 + 
	    "m. " + (time % 60) + "s.";
    }
    
    public String toString()
    {
	String humans = "", AIs = "";
	Player p;
	long lapsed = System.currentTimeMillis() - start_time;

	for (int i = 0; i < players.size() + dead_pl.size(); i++)
	    {
		p = ( (i < players.size()) ? (Player)players.elementAt(i) :
		      (Player)dead_pl.elementAt(i-players.size()));
		if (p instanceof AIPlayer)
		    AIs += p.getName(players) + "("+p.getID()+ ")  ";
		//human player
		else
		    humans += /*(((ServerThread)p).isMaster() ? "*" : "")+*/ 
			p.getName(players) + "("+p.getID()+")  ";
	    }  

	return ( "Game #"+id+ " Round #" + (total_rounds-num_rounds) + 
		 ((turn_order == null) ? " (starting) ": 
		 " (in progress " + timeToString( lapsed ) + ")" )+ 
		 "\n     People   : "+  humans +
		 "\n     Computers: "+ AIs +"\n");
    }
}

