package scorch;

/*
  Class:  Network
  Author: Mikhail Kruk
  Description: all the network interaction with the server; calls the 
  callback methods from the ScorchApplet
*/

import java.io.*;
import java.net.*;
import java.util.*;

import scorch.utility.*;
import scorch.Protocol;

public class Network implements Runnable
{
    private Socket acc;
    private Thread listener;
    private BufferedReader is;
    private final PrintWriter os;
    private final int pcount=0;
    private final ScorchApplet owner;
    
    private boolean listen;
    
    private int port;
    
    public Network(String host, int port, ScorchApplet owner) 
	throws IOException
    {
	this.owner = owner;
	
	acc = new Socket(host, port);
	acc.setSoTimeout(1000);
	is = new BufferedReader(new InputStreamReader(acc.getInputStream()));
	os = new PrintWriter(acc.getOutputStream(), true);
	Debug.println("Connected to "+host+" on "+port);
	
	listen = true;
	listener = new Thread(this, "network-thread");
	listener.start();
    }
    
    private void disconnect()
    {
	sendMessage(Protocol.quit);
	if(acc == null )
	    return;

        Debug.println("setting listen to false...");
	listen = false;

	try 
	    {
		acc.close();
		acc = null;

		if( is != null )
		    {
			is.close();
			is = null;
		    }
	    }
	catch(Throwable e) 
	    {
		System.err.println("disconnect: "+e);
		e.printStackTrace();
	    }
       
	listener = null;
		
    }
    
    synchronized public void sendMessage(String s)
    {
	if(os == null)
	    {
		Debug.println("sendMessage: failed to write to the network");
		return;
	    }
	os.print(s+"\r\n");
	os.flush();
	Debug.consolePrint("server <---| "+s);
    }

    private String getAnswer()
    {
	String msg = null;

	try
	    {
		/*while(listen && !is.ready() )
		    {
			try
			    {
				Thread.sleep(200);
			    }
			catch (Exception e)
			    {
				System.err.println(e);
			    }
			    }*/
		Debug.println("going to block, listen is "+listen);

		while( listen && msg == null )
		    {
			try
			    {
				msg = is.readLine();
			    }
			catch(SocketException e)
			    {
				if( listen )
				    owner.showDisconnect("Connection to the server lost");
				disconnect();
				return msg;
			    }
			catch(InterruptedIOException e)
			    {
				msg = null;
			    }
		    }
		Debug.println("read done, listen is "+listen);		
		Debug.consolePrint("server |---> "+msg);
		return msg;
	    }
	catch(IOException e) // now I don't think this can happen, 
	    {
		System.err.println("getAnswer: "+e);
		
		if( listen )
		    owner.showDisconnect("Connection to the server lost");
		disconnect();
		return msg;
	    }
    }
  
    public void run()
    {
	String s;
	for(s = getAnswer(); 
	    listen && s != null;  //&& s.length() != 0; 
	    s = getAnswer())
	    {
		int pos = s.indexOf(Protocol.separator);
		String command = s.substring(0,(pos == -1 ? s.length() : pos));
		String rest = (pos == - 1 ? "" : s.substring(pos+1));
		StringTokenizer st = 
		    new StringTokenizer(rest, Protocol.separator+"");

		if(command.equals(Protocol.ping))
		    {
			sendMessage(Protocol.pong);
			continue;
		    }

		if( owner.GalslaMode )
		    continue;

		/*try
		    {
			Thread.currentThread().sleep(1000);
		    }
		catch(InterruptedException e)
		{}*/
		
		// here we've made a stupid protocol design decision
		// the player id comes after profile, so profile structure
		// is fixed. Now to add fields to the profile, we have
		// to make parser a bit obscure
		if(command.equals(Protocol.loggedin) || 
		   command.equals(Protocol.ailoggedin))
		    {
			PlayerProfile profile;
			
			profile = new PlayerProfile(rest);
			
			// find the last token and hope it is player ID
			while(st.countTokens() > 1)
			    st.nextToken();
			
			owner.loggedIn
			    (Integer.parseInt(st.nextToken()), profile, 
			     command.equals(Protocol.ailoggedin));
			continue;
		    }
		if(command.equals(Protocol.loginfailed))
		    {
			owner.errorLogin(st.nextToken(), st.nextToken());
			continue;
		    }
		if(command.equals(Protocol.playerleft))
		    {
			owner.playerLeft(Integer.parseInt(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.gameoptions))
		    {
			owner.recieveGameSettings
			    (new GameSettings
				(Float.parseFloat(st.nextToken()),
				 Integer.parseInt(st.nextToken()),
						Boolean.parseBoolean(st.nextToken()),
				 Integer.parseInt(st.nextToken()),
				 Long.parseLong(st.nextToken()),
						Boolean.parseBoolean(st.nextToken())),
			     Long.parseLong(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.shout) || 
		   command.equals(Protocol.say))
		    {
			owner.chatMsg(rest);
			continue;
		    }
		if(command.equals(Protocol.setplayeroptions))
		    {
			owner.recievePlayerOptions
			    (Integer.parseInt(st.nextToken()),
						Integer.parseInt(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.disconnect))
		    {
			owner.showDisconnect(rest);
			continue;
		    }
		if(command.equals(Protocol.update))
		    {
			owner.recieveUpdate(Integer.parseInt(st.nextToken()),
					    Integer.parseInt(st.nextToken()),
					    Integer.parseInt(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.useweapon))
		    {
			owner.fire(Integer.parseInt(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.useitem))
		    {
			owner.recieveUseItem(Integer.parseInt(st.nextToken()),
				      Integer.parseInt(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.maketurn))
		    {
			owner.makeTurn(Integer.parseInt(st.nextToken()));
			continue;
		    }
		if(command.equals(Protocol.endofround))
		    {
			owner.recieveEOR();
			continue;
		    }
		if(command.equals(Protocol.endofgame))
		    {
			owner.recieveEOG();
			continue;
		    }
		if(command.equals(Protocol.masskill))
		    {
			owner.recieveMassKill();
			continue;
		    }
		if(command.equals(Protocol.topten))
		    {
			Vector<PlayerProfile> profiles = new Vector<>();
			while(st.hasMoreTokens())
			    profiles.addElement(new PlayerProfile(st));
			owner.showTopTen(profiles);
			continue;
		    }
		if(command.equals(Protocol.requestlog))
		    {
			sendMessage
			    (Protocol.clientlog + Protocol.separator + 
			     Debug.getLog());
			continue;
		    }
		System.err.println("Network: unhandled command: " + command);
	    }

	if( listen )
	    owner.showDisconnect("Connection to the server lost");
	disconnect();

        if( is != null )
	    {
		try 
		    {
			is.close();
			is = null;
		    }
		catch(IOException e)
		    {
			System.err.println("Exception whilte trying to close socket: "+e);
		    }
	    }
	Debug.println("Disconnected from server; last message: "+s);
	Debug.println("listen was: "+listen);
    }

    /*public void sendLogin(String name, String password)
    {
	sendMessage(Protocol.login + Protocol.separator + name + 
		    Protocol.separator + password);
		    }*/
    
    public void sendLogin(PlayerProfile profile, String options)
    {
	profile.encrypt();
	sendMessage(Protocol.login + Protocol.separator + 
		    profile.makeLoginString() + 
		    Protocol.separator + options);
	sendMessage(Protocol.jvminfo+Protocol.separator+ScorchApplet.JVM);
    }

    /*public void sendNewUser(String name, String password, String email)
    {
	sendMessage(Protocol.newplayer + Protocol.separator + name + 
		    Protocol.separator + password +Protocol.separator + email);
		    }*/

    public void sendNewUser(PlayerProfile profile, String options)
    {
	profile.encrypt();
	sendMessage(Protocol.newplayer + Protocol.separator + profile +
		    Protocol.separator + options);
	sendMessage(Protocol.jvminfo+Protocol.separator+ScorchApplet.JVM);
    }

    public void sendMaxPlayers(int maxPlayers)
    {
	sendMessage(Protocol.setmaxplayers + Protocol.separator + maxPlayers);
    }
    
    public void sendAIPlayer(String player)
    {
	sendMessage(Protocol.addaiplayer + Protocol.separator + player);
    }

    public void sendPlayerSettings(int id, PlayerSettings pSettings)
    {
	sendMessage(Protocol.setplayeroptions+Protocol.separator+
		    id + Protocol.separator + pSettings);
    }

    public void sendGameSettings(GameSettings gSettings)
    {
	sendMessage(Protocol.setgameoptions+Protocol.separator+gSettings);
    }

    public void sendShout(String msg, String player, int idx)
    {
	ScorchPlayer sp;

	if( idx < 0 )
	    sendMessage(Protocol.shout + Protocol.separator + 
			"<" + player + ">" + " " + msg);
	else
	    {
		sp = owner.getPlayer(idx);
		int pid = sp.getID();
		sendMessage(Protocol.say + Protocol.separator +
			    pid + Protocol.separator + "[" + player +" => "+
			    sp.getName()+ "]" + " " + msg);
	    }
    }

    public void sendUpdate(int id, int power, int angle)
    {
	//Debug.printStack();
	sendMessage(Protocol.update + Protocol.separator + id +
		    Protocol.separator + power +
		    Protocol.separator + angle);
    }

    public void sendEOT(String dt)
    {
	sendMessage(Protocol.endofturn+Protocol.separator+dt);
    }

    public void sendUseWeapon(int w_id)
    {
	sendMessage(Protocol.useweapon + Protocol.separator + w_id);
    }

    public void sendUseItem(int i_id, int param)
    {
	sendMessage(Protocol.useitem + Protocol.separator + i_id + 
		    Protocol.separator + param);
    }

    public void sendTankDead(int id)
    {
	sendMessage(Protocol.playerdead + Protocol.separator + id);
    }

    public void sendMassKill()
    {
	sendMessage(Protocol.masskill);
    }

    public void sendUpdateUser(PlayerProfile profile, boolean encrypt)
    {
	if( encrypt ) profile.encrypt();
	sendMessage(Protocol.changeprofile+Protocol.separator+profile);
    }

    public void sendTopTen()
    {
	sendMessage(Protocol.topten);
    }

    public void quit()
    {
	Debug.clearLog();

	disconnect();
    }
}
