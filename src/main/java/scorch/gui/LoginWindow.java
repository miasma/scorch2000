package scorch.gui;

/*
  Class:  LoginWindow
  Author: Mikhail Kruk

  Description: the initial login dialog window with the ability to 
  go to the new user creating window or to log in as guest user
*/

import java.awt.*;
import java.awt.event.*;

import scorch.*;
import swindows.*;

public class LoginWindow extends sWindow implements ActionListener
{
    private TextField username, password, gpassword;

    public LoginWindow(String name, ScorchApplet owner)
    {
	super(-1,-1,0,0,"Welcome to Scorched Earth 2000!", owner);
   
	Button bLogin = new Button("Log in"),
	    bGuest = new Button("Guest"),
	    bNew = new Button("New player"),
	    bLeave = new Button("Leave");

	username = new TextField(15);
	password = new TextField(15);
	gpassword = new TextField(15);

	username.setText(name);
	password.setEchoChar('*');
	password.setEnabled(true);
	gpassword.setEchoChar('*');
	gpassword.setEnabled(true);

	Panel p1, p2, p3, p1a, p1b, p1c;
    
	p1a = new Panel();
	p1a.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p1a.add(new Label("User name:"));
	p1a.add(username);

	p1b = new Panel();
	p1b.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p1b.add(new Label("Password:"));
	p1b.add(password);

	p1c = new Panel();
	p1c.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p1c.add(new Label("Game Password:"));
	p1c.add(gpassword);

	p1 = new Panel();
	p1.setLayout(new GridLayout(3,1));
	p1.add(p1a);
	p1.add(p1b);
	p1.add(p1c);

	p2 = new Panel();
	p2.setLayout(new FlowLayout(FlowLayout.CENTER));
	p2.add(p1);

	p3 = new Panel();
	p3.setLayout(new FlowLayout(FlowLayout.CENTER));
	p3.add(bLogin);
	p3.add(bNew);
	p3.add(bGuest);
	p3.add(bLeave);
	
	setLayout(new BorderLayout(10,10));
	add(p2, BorderLayout.CENTER); 
	add(p3, BorderLayout.SOUTH);
	
	bLogin.addActionListener(this);
	bNew.addActionListener(this);
	bGuest.addActionListener(this);
	bLeave.addActionListener(this);

	validate();
    }

    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();
	
	if( cmd.equals("Leave") )
	    {
		((ScorchApplet)owner).Quit();
		return;
	    }
	if( cmd.equals("New player") )
	    {
		close();
		((ScorchApplet)owner).newUser(username.getText());
		return;
	    }
	
	if( username.getText().equals("") )
	    {
		String b[] = {"OK"};
		MessageBox msg = new MessageBox
		    ("Error", "You must provide a user name",
		     b, null, owner, this);
		msg.display();
		return;
	    }
		
	if( cmd.equals("Log in") )
	    {
		if( username.getText().equals(Protocol.guest) )
		    {
			String b[] = {"OK"};
			MessageBox msg = new MessageBox
			    ("Error", 
			     "Username "+Protocol.guest+" is reserved",
			     b, null, owner, this);
			msg.display();
			return;
		    }
			
		((ScorchApplet)owner).LogIn
		    (new PlayerProfile(username.getText(),
				       password.getText()),
		     gpassword.getText());
		close();
		return;
	    }
	
	if( cmd.equals("Guest") )
	    {
		if( username.getText().equals(Protocol.guest) )
		    {
			String b[] = {"OK"};
			MessageBox msg = new MessageBox
			    ("Error", 
			     "Username "+Protocol.guest+" is reserved",
			     b, null, owner, this);
			msg.display();
			return;
		    }

		((ScorchApplet)owner).LogIn
		    (new PlayerProfile(Protocol.guest, 
				       username.getText()),
		     gpassword.getText());
		close();
		return;
	    }
	return;
    }

    public void close()
    {
	password.setText("");
	password.setEchoChar((char)0);
	gpassword.setText("");
	gpassword.setEchoChar((char)0);
	super.close();
    }
    
    public void display()
    {
	super.display();
	if( username.getText().equals("") )
	    username.requestFocus();
	else
	    password.requestFocus();
    }
}
