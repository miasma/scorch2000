package scorch.gui;

/*
  Class:  ChatBox
  Author: Mikhail Kruk

  Description: the window that popups when user starts typing some text
*/

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import scorch.*;
import swindows.*;

public class ChatBox extends sWindow implements ActionListener, KeyListener
{
    private final TextField message;
    private final char ch;
    private final Choice rcpt;

    private static final String BROADCAST = "Everybody";

    public ChatBox(char c, ScorchApplet owner)
    {
	super(-1,-1,0,0,"Chat", owner);

	this.ch = c;

	rcpt = new Choice();
	rcpt.addKeyListener(this);
	rcpt.add(BROADCAST);
	Vector<ScorchPlayer> plrs = owner.getPlayers();
	for(int i = 0; i < plrs.size(); i++)
	    rcpt.addItem(plrs.elementAt(i).getName());

	Panel pb = new Panel();

	pb.setLayout(new FlowLayout(FlowLayout.CENTER));

	Button tb = new Button("Say it");
	tb.addActionListener(this);
	pb.add(tb);
	tb = new Button("Cancel");
	tb.addActionListener(this);
	pb.add(tb);
	
	addKeyListener(this);

	message = new TextField(40);
	message.addKeyListener(this);

	Panel up = new Panel(new FlowLayout(FlowLayout.CENTER));
	up.add(new Label("Say"));
	up.add(message);
	up.add(new Label("to"));
	up.add(rcpt);

	setLayout(new BorderLayout());
	add(up, BorderLayout.CENTER);
	add(new Panel(), BorderLayout.NORTH); 
	add(pb, BorderLayout.SOUTH);
	
	validate();
    }

    public void keyPressed(KeyEvent evt)
    {
	if( evt.getSource() == rcpt )
	    message.requestFocus();

	if( evt.getKeyCode() == KeyEvent.VK_ESCAPE )
	    closeChat(false);
	else
	    if( evt.getKeyCode() == KeyEvent.VK_ENTER )
		closeChat(true);
    }
    public void keyReleased(KeyEvent evt) {}
    public void keyTyped(KeyEvent evt) {}

    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();

	if( "Say it".equals(cmd) )
	    {
		closeChat(true);
		return;
	    }
	if( "Cancel".equals(cmd) )
	    {
		closeChat(false);
        }
    }

    void closeChat(boolean say)
    {
	close();
	((ScorchApplet)owner).closeChat(say, rcpt.getSelectedIndex());
    }

    public void display()
    {
	super.display();
	message.setText(ch+"");
	message.setCaretPosition((message.getText()).length());
	message.requestFocus();
    }

    public String getMessage()
    {
	return message.getText();
    }
}
