package scorch.gui;

/*
  Class:  UsersPanel
  Author: Mikhail Kruk
  Description: 
*/

import java.awt.*;

import scorch.*;
import scorch.utility.*;
import swindows.*;

public class UsersPanel extends sWindow
{
    private Button btnCreate, btnWhois, btnHelp, btnLeave;

    public UsersPanel(int x, int y, int w, int h, ScorchApplet owner)
    {
	super(x,y,w,h,"Users", owner);

	setLayout(new BorderLayout());
	add(new List(), BorderLayout.CENTER);


	btnCreate = new Button("Create Game");
	btnWhois = new Button("Get user info");
	btnHelp = new Button("Help");
	btnLeave = new Button("Leave Scorch");

	Panel btnPanel = new Panel(new GridLayout(4, 1, 0, 5));
	btnPanel.add(btnCreate);
	btnPanel.add(btnWhois);
	btnPanel.add(btnHelp);
	btnPanel.add(btnLeave);
	add(btnPanel, BorderLayout.SOUTH);
    }
}
