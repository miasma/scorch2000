package scorch.gui;

/*
  Class:  BootBox
  Author: Mikhail Kruk

  Description: the window that popups when masters want to boot someone 
  from the game
*/

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import scorch.*;
import swindows.*;

public class BootBox extends sWindow implements ActionListener, KeyListener
{

	public BootBox(ScorchApplet owner)
    {
	super(-1,-1,0,0,"Boot player", owner);

		Choice players = new Choice();
	players.addKeyListener(this);

	Vector<ScorchPlayer> plrs = owner.getPlayers();
	ScorchPlayer sp;
	for(int i = 0; i < plrs.size(); i++)
	    {
		sp = plrs.elementAt(i);
		if( !(sp instanceof AIPlayer) )
		    players.addItem(sp.getName());
	    }

	Panel pb = new Panel();

	pb.setLayout(new FlowLayout(FlowLayout.CENTER));

	Button tb = new Button("Boot");
	tb.addActionListener(this);
	pb.add(tb);
	tb = new Button("Cancel");
	tb.addActionListener(this);
	pb.add(tb);
	
	addKeyListener(this);

	Panel up = new Panel(new FlowLayout(FlowLayout.CENTER));
	up.add(new Label("Boot player: "));
	up.add(players);

	setLayout(new BorderLayout());
	add(up, BorderLayout.CENTER);
	//add(new Panel(), BorderLayout.NORTH); 
	add(pb, BorderLayout.SOUTH);
	
	validate();
    }

    public void keyPressed(KeyEvent evt)
    {
	if( evt.getKeyCode() == KeyEvent.VK_ESCAPE )
	    close();
    }
    public void keyReleased(KeyEvent evt) {}
    public void keyTyped(KeyEvent evt) {}

    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();

	if( "Boot".equals(cmd) )
	    {
		//owner.bootPlayer(id?);
		close();
		return;
	    }
	if( "Cancel".equals(cmd) )
	    {
		close();
        }
    }
}
