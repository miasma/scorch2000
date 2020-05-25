/*
  Class : ServerThread

  Author: Alexander Rasin

  Description: ServerThread is a separate thread that is ran for each 
  connecting client.  This thread is represents the client on the server 
  side.  It accepts messages the client sends and executes actions 
  accordingly.  Each client has access to the game it is in (once the client 
  logged in succesfully)
  ServerThread is added to the game, has a reference to it's game (for 
  broadcasts) and can ask the server when the player is attempting to log
  in, to make sure login is valid.
*/

package ScorchServer;

import java.net.*;
import java.io.*;
import java.util.*;

import scorch.PlayerProfile;
import scorch.Protocol;

public class ServerThread extends Player implements Runnable {
    private Socket socket;
    private String hostName;

    //input and output streams
    private BufferedReader in = null;
    private PrintWriter out = null;
    private long pingtime, pongtime;
    private final static long PONGTIMEOUT = 60 * 1000;
    private final static long PINGTIME = 20 * 1000;
    private String password = null, resolution = "";
    private boolean isMaster = false;

    private boolean keep_running = true, made_turn = false;

    public ServerThread(Socket socket) {
        super(null);
        this.socket = socket;
        pingtime = pongtime = System.currentTimeMillis();
        Thread self = new Thread(this);
        self.start();
    }

    protected void makeMaster() {
        isMaster = true;
        //sendMessage("MASTER");
    }

    public void setReady(boolean val) {
        ready = val;
    }

    public boolean madeTurn() {
        //	System.out.println("RETURNING " + made_turn);
        return made_turn;
    }

    public boolean isMaster() {
        return isMaster;
    }

    //note that this method will try to resolve the host of the client
    //(unless it already has it).  So method will be used in the ScorchShell
    public String getHostName() {
        if (hostName == null)
            hostName = socket.getInetAddress().getHostName();

        return hostName;
    }

    //ping the client if it has been idle too long.
    //disconnect if timeout has been reached
    private void checkPing() {
        long now = System.currentTimeMillis();
        if (now - pongtime > PONGTIMEOUT) {
            System.out.println("Player " + name + " id " + id + " timed out");
            //CHANGE THIS... PROTOCOL
            dropPlayer("your connection timed out.");
        } else if (now - pingtime > PINGTIME) {
            sendMessage(Protocol.ping);
            pingtime = now;
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    private String getMessage() {
        try {
            while (keep_running && !in.ready()) {
                checkPing();
                Thread.sleep(500);
            }
            //the thread is already disconnected
            if (!keep_running)
                return null;

            return in.readLine();
        } catch (Exception e) {
            System.err.println("SERVER: reading from " + in + " failed " + e);
            dropPlayer(null);
        }

        return null;
    }

    //drop a player from a game. reason == null means that the player quit
    public void dropPlayer(String reason) {
        if (reason != null)
            sendMessage(Protocol.disconnect + Protocol.separator +
                    "You've been disconnected because " + reason);

        if (myGame != null) {
            //actually leave the game. the player is removed first so
            //that the broadcasts only go to other players
            myGame.leave(this);
        }

        if (myGame != null) {
            myGame.checkTurn();
            myGame = null;
        }

        disconnect();
    }

    private synchronized void disconnect() {
        if (socket == null)
            return;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        socket = null;
        keep_running = false;
    }

    //see if the message received is a valid login.  The player cannot
    //join a game unless s/he provides a valid login.  This method parses
    //the input and reads the profile from server if applicable.
    private boolean logIn(String msg) {
        boolean loggedin = false;
        StringTokenizer st;

        password = "";

        st = new StringTokenizer(msg, "" + Protocol.separator);

        if (st.countTokens() < 2)
            return false;

        if (((msg = st.nextToken()).equals(Protocol.login)) ||
                (msg.equals(Protocol.newplayer))) {
            if (!msg.equals(Protocol.newplayer)) {
                if (st.hasMoreTokens())
                    name = st.nextToken();
                if (st.hasMoreTokens())
                    password = st.nextToken();
                //game resolution.
                if (st.hasMoreTokens())
                    resolution = st.nextToken();
            }

            profile = ScorchServer.lookupPlayer(name);

            //System.out.println(" request " + msg + " found " + profile);

            if (msg.equals(Protocol.newplayer) && (profile == null)) {
			/*	if (st.hasMoreTokens())
			    profile = new PlayerProfile
				(name, password, st.nextToken());
			else
			    profile = new PlayerProfile(name, password,"");
			*/
                profile = new PlayerProfile(st);
                name = profile.getName();
                password = profile.getPassword();
                while (st.hasMoreTokens())
                    resolution += st.nextToken();

                if (ScorchServer.lookupPlayer(name) != null)
                    profile = new PlayerProfile(null, "", "");
                else
                    //create a new player and write his profile
                    //to disk
                    ScorchServer.newPlayer(profile);
            } else
                //new user command failed
                if (msg.equals(Protocol.newplayer))
                    profile = new PlayerProfile(null, null);

            //guest login. password = desired user name
            if (name.equals(Protocol.guest)) {
                if (ScorchServer.lookupPlayer(password) == null) {
                    profile = new PlayerProfile(name, password, "");
                    name = password;
                    password = Protocol.guest;
                } else
                    //guest
                    profile = new PlayerProfile(null, null);
            }

            if ((profile != null) && (profile.getName() == null)) {
                sendMessage(Protocol.loginfailed + Protocol.separator +
                        name + Protocol.separator +
                        Protocol.usernametaken);
                loggedin = false;
            } else if (ScorchServer.alreadyPlaying(name)) {
                //System.out.println(ScorchServer.alreadyPlaying(name));
                sendMessage(Protocol.loginfailed + Protocol.separator +
                        name + Protocol.separator +
                        Protocol.alreadyloggedin);
                loggedin = false;
            } else if (profile == null) {
                sendMessage(Protocol.loginfailed + Protocol.separator +
                        name + Protocol.separator +
                        Protocol.wrongusername);
                loggedin = false;
            } else if ((password == null || !password.equals(profile.getPassword()))) {
                sendMessage(Protocol.loginfailed + Protocol.separator +
                        name + Protocol.separator +
                        Protocol.wrongpassword);
                loggedin = false;
            } else
                loggedin = true;
        }
        return loggedin;
    }

    //initialize input/output streams for connection
    private void initStreams() {
        try {
            in = new BufferedReader(new InputStreamReader
                    (socket.getInputStream()));
            //setting autoflush to true... wonder if it works
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("ServerThread: connection failed " + e);
            System.exit(1);
        }
    }

    //the run method of the thread.  It continuously listends to client 
    //messages and responds/performs appropriate action.
    public void run() {
        String msg, command;
        StringTokenizer st;
        boolean loggedin = false;

        initStreams();

        do {
            //wait for a successful login command (or a quit command)
            msg = getMessage();

            if (msg == null || msg.equals(Protocol.quit)) {
                disconnect();
                return;
            }
        }
        while (!logIn(msg));

        System.out.println(" User name " + name + " logged in.");

        myGame = ScorchServer.register(this, resolution);
        checkPing();

        while (keep_running && ((msg = getMessage()) != null)) {
            pingtime = pongtime = System.currentTimeMillis();

            st = new StringTokenizer(msg, Protocol.separator + "");

            //no command (empty string?)
            if (st.countTokens() == 0)
                continue;

            //the client command
            command = st.nextToken();

            //answer to ping can be ignored
            if (command.equals(Protocol.pong))
                continue;

            //shout and setgameoptions needs to be simply broadcasted
            if (command.equals(Protocol.shout)) {
                System.out.println(msg);
                myGame.broadcast(msg);
                continue;
            }
            if (command.equals(Protocol.jvminfo)) {
                jvm = st.nextToken();
                continue;
            }
            //memorize game options (send them then the game starts)
            if (command.equals(Protocol.setgameoptions) && isMaster()) {
                myGame.gameStarted();
                myGame.setOptions(msg);

                //check if all the options neccesary have been
                //received and the clients can be initialized
                myGame.checkInitialize();
                continue;
            }
            if (command.equals(Protocol.endofturn)) {
                ready = true;
                made_turn = false;
                //if all players are ready for the next turn,
                //notify clients of who goes next

                //DEBUG
                myGame.checkSync(msg, name);

                myGame.checkTurn();
                continue;
            }
            if (command.equals(Protocol.clientlog)) {
                //DEBUG?
                Disk.recordClientLog(msg, id);
                continue;
            }
            //memorize the player options. will broadcast them later
            if (command.equals(Protocol.setplayeroptions)) {
                ploptions = msg;
                //check if all the options neccesary have been
                //received and the clients can be initialized
                myGame.checkInitialize();
                continue;
            }
            //player used a weapon in his turn, broadcast but note the fact
            if (command.equals(Protocol.useweapon)) {
                made_turn = true;

                myGame.broadcast(msg);
                continue;
            }
            //player says something to a player from his game.
            if (command.equals(Protocol.say)) {
                int plid = Integer.parseInt(st.nextToken());
                String said = st.nextToken();
                Player p;

                p = myGame.findPlayerByID(plid);
                if (p != null) {
                    p.sendMessage
                            (Protocol.say + Protocol.separator + said);
                    if (id != plid)
                        sendMessage
                                (Protocol.say + Protocol.separator + said);
                } else
                    sendMessage(Protocol.say + Protocol.separator +
                            "Message not delivered, player left the game (" + said + ")");

                continue;
            }
            //the user requested to change his/her profile
            if (command.equals(Protocol.changeprofile)) {
                //make a new profile (and cut out the command
                //that goes in front of string msg

                profile = new PlayerProfile
                        (msg.substring(Protocol.changeprofile.length() + 1
                        ));

                if (profile.getPassword().equals("*"))
                    profile.setPassword(password);
                else
                    password = profile.getPassword();

                ScorchServer.changeProfile(profile);
                continue;
            }
            //a player died. the master client will notify us.
            if (command.equals(Protocol.playerdead)) {
                if (isMaster)
                    myGame.playerDied
                            ((Integer.parseInt(st.nextToken())));
                continue;
            }
            //master performs a mass kill. done to finish a round
            if (command.equals(Protocol.masskill)) {
                if (isMaster)
                    myGame.massKill(this);
                continue;
            }
            //maximum players in the game
            if (command.equals(Protocol.setmaxplayers)) {
                if (st.hasMoreTokens())
                    myGame.setMaxPlayers
                            (Integer.parseInt(st.nextToken()));
                continue;
            }
            //add ai player
            if (command.equals(Protocol.addaiplayer)) {
                if (!st.hasMoreTokens())
                    sendMessage(Protocol.commandfailed);
                else {
                    //client checks number of AI players added
                    myGame.reserveSpot();
                    //add new AI player of type that was recieved
                    myGame.addPlayer(new AIPlayer(st.nextToken()),
                            Protocol.ailoggedin);
                }
                continue;
            }

            if (command.equals(Protocol.topten)) {
                sendMessage(ScorchServer.getTopTen());
                continue;
            }

            if (command.equals(Protocol.quit)) {
                dropPlayer(null);
                continue;
            }

            //this is a temporary - unknown messages are broadcasted
            //for debug purposes.
            myGame.broadcast(msg);
        }
        disconnect();
    }
}

