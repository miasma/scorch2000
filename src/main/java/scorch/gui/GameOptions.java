package scorch.gui;

/*
  Class:  GameOptions
  Author: Mikhail Kruk

  Description: the dialog window presented to the master client to select
  both his player and general game options.
*/

import java.awt.*;
import java.awt.event.*;

import scorch.*;
import swindows.*;

public class GameOptions extends PlayersList implements ActionListener,
							FocusListener
{
    private final TextField gravity;
	private final TextField cash;
	private final TextField rounds;
    private int roundsVal = 5;
    private long cashVal = 0;
    private float gravityVal = Physics.EARTH_GRAVITY;
    private final Choice wind;
    private final TankSelection tankSelection;
    private final Checkbox hazards;
	private final Checkbox sounds;
	private final Checkbox lamermode;
    private final PlayerProfile profile;

    public GameOptions(String masterName, int maxpl, 
		       PlayerProfile profile, ScorchApplet owner)
    {
	this(masterName, maxpl, true, profile, owner);	
    }

    public GameOptions
	(String masterName, int maxpl, boolean master, PlayerProfile profile, 
	 ScorchApplet owner)
    {
	super("Game options", owner);

	// init dialog items
	gravity = new TextField(7);
	cash = new TextField(7);
	rounds = new TextField(7);
	hazards = new Checkbox("Nature hazards");
	sounds = new Checkbox("Sound effects");
	lamermode = new Checkbox("Lamer mode");
	tankSelection = new TankSelection(200,30);
	wind = new Choice();
	wind.addItem("Constant wind");
	wind.addItem("No wind");
	wind.addItem("Changing wind");
	wind.select(1);

	gravity.addActionListener(this);
	cash.addActionListener(this);
	rounds.addActionListener(this);
	gravity.addFocusListener(this);
	cash.addFocusListener(this);
	rounds.addFocusListener(this);

	// attempt to read settings from profile
	if( profile.getNumRounds() > 0 ) roundsVal = profile.getNumRounds();
	if( profile.getCash() > 0 ) cashVal = profile.getCash();
	if( profile.getGravity() > 0 ) gravityVal = profile.getGravity();
	if( profile.getWind() >= 0 ) wind.select(profile.getWind());
	if( profile.getTankType() >= 0 ) 
	    tankSelection.setSelected(profile.getTankType());
	hazards.setState(profile.getHazards());
	sounds.setState(profile.getSounds());
	lamermode.setState(false);

        this.profile = profile;

	Panel p0, p1, p2, p3, p2a, p2b, p2c, p4, p3a, p3b, p2d;

	// enable everything that is relevant to masters. Master is always true
	gravity.setEnabled(master);
	cash.setEnabled(master);
	rounds.setEnabled(master);
	hazards.setEnabled(false);
	lamermode.setEnabled(master);
	sounds.setEnabled(master);
       
	// init values
	gravity.setText(""+gravityVal);
	cash.setText(""+cashVal);
	rounds.setText(""+roundsVal);

	addPlayer(masterName);

       	p1 = new Panel();
	p1.setLayout(new BorderLayout());
	p1.add(new Label("Players:"), BorderLayout.NORTH);
	p1.add(players, BorderLayout.CENTER);
	
	p2 = new Panel();
	p2.setLayout(new GridLayout(5, 1));
	p2d = new Panel();
        p2d.setLayout(new FlowLayout(FlowLayout.RIGHT));
 	p2d.add(new Label("Number of rounds:"));
        p2d.add(rounds);
        p2.add(p2d);

	p2a = new Panel();
	p2a.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p2a.add(new Label("Gravity (m/sec^2):"));
	p2a.add(gravity);
	p2.add(p2a);
	
	p2b = new Panel();
	p2b.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p2b.add(new Label("Initial cash ($):"));
	p2b.add(cash);
	p2.add(p2b);
	
	p2.add(wind);

	p2c = new Panel();
	p2c.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
	//p2c.add(hazards);
	p2c.add(sounds);
	p2c.add(lamermode);
	p2.add(p2c);

	p3 = new Panel();
	p3.setLayout(new BorderLayout());
	p3b = new Panel();

	p3b.setLayout(new FlowLayout(FlowLayout.CENTER));
	p3b.add(new Label("Select your tank:"));
	p3b.add(tankSelection);

	p3a = new Panel();
	if( master ) 
	    {
		Button bt = new Button("Start Game");
		bt.addActionListener(this);
		p3a.add(bt);
		bt = new Button("Cancel Game");
		bt.addActionListener(this);
		p3a.add(bt);
	    }
	else
	    {
		Button bt = new Button("OK");
		bt.addActionListener(this);
		p3a.add(bt);
		bt = new Button("Cancel");
		bt.addActionListener(this);
		p3a.add(bt);
	    }

	p3.add(p3a, BorderLayout.SOUTH);
	p3.add(p3b, BorderLayout.CENTER);

	p0 = new Panel();
	p0.setLayout(new FlowLayout());
	p0.add(p1);
	p0.add(p2);

	p4 = new Panel();
	p4.setLayout(new BorderLayout());
	if( master )
	    {
		p4.add(new Label("You are the master of the game."), 
		       BorderLayout.NORTH);
		p4.add(new 
		       Label("Maximum number of players in the game is set to "+maxpl),
		       BorderLayout.CENTER);
		p4.add(new Label("Please select game options:"),
		       BorderLayout.SOUTH);
	    }
	else
	    {
		p4.add(new Label("You are connected as "+ masterName),
		       BorderLayout.NORTH);
		p4.add(new 
		       Label("Maximum number of players in the game is set to "+maxpl), BorderLayout.CENTER);
		p4.add(new Label("Please select player options:"),
		       BorderLayout.SOUTH);
	    }

	setLayout(new BorderLayout());
	add(p4, BorderLayout.NORTH);
	add(p0, BorderLayout.CENTER);
	add(p3, BorderLayout.SOUTH);
	validate();

	timeLeft = 180;
    }

    public void focusGained(FocusEvent evt) {}
    
    public void focusLost(FocusEvent evt) 
    {
	Object source = evt.getSource();

	if( source == gravity )
	    {
		try
		    {
			gravityVal = 
			    Float.parseFloat(gravity.getText());
		    }
		catch(NumberFormatException e)
		    {
		    }
		if( gravityVal < 0 ) gravityVal = 0;
		gravity.setText(""+gravityVal);
	    }
	if( source == cash )
	    {
		try
		    {
			cashVal = 
			    Long.parseLong(cash.getText());
		    }
		catch(NumberFormatException e)
		    {
		    }
		if( cashVal < 0 ) cashVal = 0;
		cash.setText(""+cashVal);
	    }
	if( source == rounds )
	    {
		try
		    {
			roundsVal = 
			    Integer.parseInt(rounds.getText());
		    }
		catch(NumberFormatException e)
		    {
		    }
		if( roundsVal < 1 ) roundsVal = 1;
		rounds.setText(""+roundsVal);
	    }
    }

    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();
	Object source = evt.getSource();
	
	if ( source == rounds ) 
	    {
		gravity.requestFocus();
		return;
	    }
	if ( source == gravity ) 
	    {
		cash.requestFocus();
		return;
	    }
	if ( source == cash ) 
	    {
		wind.requestFocus();
		return;
	    }
	
	if( cmd.equals("OK") )
	    {
		close();
		((ScorchApplet)owner).joinGame
		    (new PlayerSettings
			(tankSelection.getSelected(), sounds.getState()));
		return;
	    }
	if( cmd.equals("Cancel") )
	    {
		((ScorchApplet)owner).Quit();
		return;
	    }
	if( cmd.equals("Start Game") )
	    {
		if( players.getItemCount() < 2 )
		    {
			String[] b = {"OK"};
			MessageBox msg = new MessageBox
			    ("Error", "Can not start game without opponents", 
			     b, null, owner, this);
			msg.display();
			return;
		    }
		
		close();
				
		profile.setNumRounds(roundsVal);
		profile.setCash(cashVal);
		profile.setGravity(gravityVal);
		profile.setWind(wind.getSelectedIndex());
		profile.setTankType
		    (tankSelection.getSelected());
		profile.setHazards(hazards.getState());
		profile.setSounds(sounds.getState());
		profile.setLamerMode(lamermode.getState());

		((ScorchApplet)owner).setGameOptions(profile);
		return;
	    }
	if( cmd.equals("Cancel Game") )
	    {
		((ScorchApplet)owner).Quit();
		}
    }
}
