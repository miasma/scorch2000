package scorch.gui;

/*
  Class:  StartGame
  Author: Mikhail Kruk
  Desciption: The window used to create a new game; allows adding AI players
  and setting the number of rounds
*/


import java.awt.*;
import java.awt.event.*;

import scorch.*;
import swindows.*;

public class StartGame extends sWindow implements ActionListener,
						  FocusListener
{
    private List players;
    private TextField maxPlayersText;
    private int maxPlayers;

    public StartGame(ScorchApplet owner)
    {
	super(-1,-1,0,0,"Create new game", owner);

	players = new List(ScorchPlayer.MAX_PLAYERS);
	maxPlayersText = new TextField(4);
	maxPlayersText.setText(""+ScorchPlayer.MAX_PLAYERS);
	maxPlayersText.addFocusListener(this);
	maxPlayersText.addActionListener(this);
	maxPlayers = ScorchPlayer.MAX_PLAYERS;

	Panel p0, p1, p2, p1a, p3;

	p0 = new Panel();
	p0.setLayout(new BorderLayout());
	p0.add(new Label("AI players:"), BorderLayout.NORTH);
	p0.add(players, BorderLayout.CENTER);

	p1a = new Panel();
	p1a.setLayout(new FlowLayout(FlowLayout.LEFT));
	p1a.add(new Label("Max # of players:"));
	p1a.add(maxPlayersText);

	p1 = new Panel();
	p1.setLayout(new GridLayout(2+AIPlayer.numAI,1,5,5));
	p1.add(p1a);

	Button bt;
	for(int i = 0; i < AIPlayer.numAI; i++)
	    {
		bt = new Button("Add "+AIPlayer.names[i]);
		bt.addActionListener(this);
		bt.setActionCommand(AIPlayer.names[i]);
		p1.add(bt);
	    }

	bt = new Button("Remove");
	bt.addActionListener(this);
	p1.add(bt);

	p2 = new Panel();
	p2.setLayout(new FlowLayout(FlowLayout.CENTER));
	bt = new Button("Create game");
	bt.addActionListener(this);
	p2.add(bt);
	bt = new Button("Cancel game");
	bt.addActionListener(this);
	p2.add(bt);
	
	p3 = new Panel();
	p3.setLayout(new FlowLayout(FlowLayout.CENTER));
	p3.add(new Label("Setup general game options:"));

	setLayout(new BorderLayout(5,5));
	add(p3, BorderLayout.NORTH);
	add(p0, BorderLayout.WEST);
	add(p1, BorderLayout.EAST);
	add(p2, BorderLayout.SOUTH);
	    
	validate();
    }

    public void focusGained(FocusEvent evt) {}
    
    public void focusLost(FocusEvent evt)
    {
	Object source = evt.getSource();

	if( source == maxPlayersText )
	    {
		try
		    {
			maxPlayers = 
			    Integer.parseInt(maxPlayersText.getText());
		    }
		catch(NumberFormatException e)
		    {
		    }
		if( maxPlayers < 2 )
		    maxPlayers = 2;
		if( maxPlayers > ScorchPlayer.MAX_PLAYERS )
		    maxPlayers = ScorchPlayer.MAX_PLAYERS;
		maxPlayersText.setText(maxPlayers+"");
			
		int c =  players.getItemCount();

		if( c > maxPlayers )
		    {
			for(int i = 0; i < c - maxPlayers+1; i++)
			    players.remove(c-i-1);
		    }
	    }
    }
	
    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();
	Object source = evt.getSource();

	if( source == maxPlayersText )
	    {
		transferFocus();
		return;
	    }

	if( cmd.equals("Remove") )
	    {
		int i = players.getSelectedIndex();
		if ( i >= 0 )
		    players.remove(i);
		return;
	    }
	
	if( cmd.equals("Cancel game") )
	    {
		((ScorchApplet)owner).Quit();
		return;
	    }
	
	if( cmd.equals("Create game") )
	    {
		((ScorchApplet)owner).createGame
		    (maxPlayers,players.getItems());
		close();
		return;
	    }
	
	// otherwise it's "Add <ai type>"
	if( players.getItemCount() < maxPlayers-1 )
	    {
		players.add(cmd);
	    }
    }
}
