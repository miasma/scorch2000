package scorch;

/*
  Class:  ChatScreen
  Author: Mikhail Kruk
  Description: 
*/

import java.awt.*;

import scorch.utility.*;
import scorch.gui.*;

public class ChatScreen extends Panel
{
    private static String[] commands = 
    {"/exit", "/create", "/join", "/me", "/msg", "/observe", "/whois",
     "/version", "/ping"};

    private static final double 
	chatSize = 0.8, 
	gamesSize = 0.4,
	controlsSize = 0.4;

    private ChatPanel chatPanel;
    private GamesPanel gamesPanel;
    private UsersPanel usersPanel;
    
    public ChatScreen(int width, int height, ScorchApplet owner)
    {
	super();

	setLayout(null);

	chatPanel = new ChatPanel
	    (0, (int)(height*gamesSize), (int)(width*chatSize), 
	     (int)((1-gamesSize)*height), owner);
	gamesPanel = new GamesPanel
	    (0, 0, (int)(width*chatSize), (int)(height*gamesSize), owner);
	usersPanel = new UsersPanel
	    ((int)(width*chatSize),0,(int)((1-chatSize)*width),height,owner);

	chatPanel.display();
	gamesPanel.display();
	usersPanel.display();
    }
}
