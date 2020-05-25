package scorch.gui;

/*
  Class:  NewUser
  Author: Mikhail Kruk
  Description: the dialog box used for creating new user profiles
  and for editing user profiles
*/

import java.awt.*;
import java.awt.event.*;

import scorch.*;
import swindows.*;

public class NewUser extends sWindow implements ActionListener
{
    private final TextField username;
	private final TextField password1;
	private final TextField password2;
	private final TextField email;
    private final PlayerProfile profile;

    public NewUser(String name, ScorchApplet owner)
    {
	this(name, owner, null);
    }

    public NewUser(String name, ScorchApplet owner, PlayerProfile profile)
    {
	super(-1,-1,0,0,
	      profile == null 
	      ? "Creating new user profile" : "Edit user profile"
	      , owner);

	this.profile = profile;

	username = new TextField(15);
	password1 = new TextField(15);
	password2 = new TextField(15);
	email = new TextField(15);

	username.setText(name);
	password1.setEchoChar('*');
	password2.setEchoChar('*');
	password1.setEditable(true);
	password2.setEditable(true);

	if( profile != null )
	    {
		username.setEnabled(false);
		email.setText(profile.getEmail());
	    }

	Panel p1, p3, p1a, p1b, p1c, p1d;
    
	p1a = new Panel();
	p1a.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p1a.add(new Label("User name:"));
	p1a.add(username);

	p1b = new Panel();
	p1b.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p1b.add(new Label("Password:"));
	p1b.add(password1);

	p1c = new Panel();
	p1c.setLayout(new FlowLayout(FlowLayout.RIGHT));
	p1c.add(new Label("Confirm:"));
	p1c.add(password2);

	p1d = new Panel();
	p1d.setLayout(new FlowLayout( FlowLayout.RIGHT));
	p1d.add(new Label("E-mail address:"));
	p1d.add(email);

	p1 = new Panel();
	p1.setLayout(new GridLayout(4,1));
	p1.add(p1a);
	p1.add(p1b);	
	p1.add(p1c);
	p1.add(p1d);

	p3 = new Panel();
	p3.setLayout(new FlowLayout(FlowLayout.CENTER));
	
	Button tb = new Button("OK");
	tb.addActionListener(this);
	p3.add(tb);
	tb = new Button("Cancel");
	tb.addActionListener(this);
	p3.add(tb);
	
	setLayout(new BorderLayout());
	add(p1, BorderLayout.CENTER); 
	add(p3, BorderLayout.SOUTH);
	
	validate();
    }
    
    public void display()
    {
	super.display();
	if( username.getText().equals("") )
	    username.requestFocus();
	else
	    password1.requestFocus();
    }

    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();

	if( cmd.equals("Cancel") )
	    {
		close();
		if(profile == null)
		    ((ScorchApplet)owner).loginWindow
			(username.getText());
		return;
	    }
	
	if( username.getText().equals("") )
	    {
		String[] b = {"OK"};
		MessageBox msg = new MessageBox
		    ("Error", "You must provide a user name", 
		     b, null, owner, this);
		msg.display();
		return;
	    }
	if( username.getText().equals(Protocol.guest) )
	    {
		String[] b = {"OK"};
		MessageBox msg = new MessageBox
		    ("Error", 
		     "Username "+Protocol.guest+" is reserved", 
		     b, null, owner, this);
		msg.display();
		return;
	    }
	if( !password1.getText().equals(password2.getText()) )
	    {
		String[] b = {"OK"};
		MessageBox msg = new MessageBox
		    ("Error", "Passwords do not match. Please reenter",
		     b, null, owner, this);
		password1.setText("");
		password2.setText("");
		msg.display();
		return;
	    }
	if( password1.getText().equals("") && profile == null )
	    {
		String[] b = {"OK"};
		MessageBox msg = new MessageBox
		    ("Error", "You must enter a password", 
		     b, null, owner, this);
		msg.display();
		return;
	    }
	if( cmd.equals("OK") )
	    {
		if( profile == null )
		    ((ScorchApplet)owner).sendNewUser
			(new PlayerProfile(username.getText(),
					   password1.getText(),
					   email.getText()));
		else
		    {
			profile.setEmail(email.getText());
			String p = password1.getText();
			if( !p.equals("") ) 
			    {
				profile.setPassword(p);
				((ScorchApplet)owner).updateUser(profile,true);
			    }
			else
			    ((ScorchApplet)owner).updateUser(profile,false);
		    }
		
		close();
		}
    }
    public void close()
    {
	// Linux appletviewer bug fix (almost works)
	password1.setText("");
	password2.setText("");
	password1.setEchoChar((char)0);
	password2.setEchoChar((char)0);
	super.close();
	//owner.requestFocus();
    }
} 
