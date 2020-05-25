package scorch.gui;

/*
  Class:  UsersPanel
  Author: Mikhail Kruk
  Description: 
*/

import java.awt.*;

import scorch.*;
import swindows.*;

public class UsersPanel extends sWindow
{

    public UsersPanel(int x, int y, int w, int h, ScorchApplet owner)
    {
	super(x,y,w,h,"Users", owner);

	setLayout(new BorderLayout());
	add(new List(), BorderLayout.CENTER);


        Button btnCreate = new Button("Create Game");
        Button btnWhois = new Button("Get user info");
        Button btnHelp = new Button("Help");
        Button btnLeave = new Button("Leave Scorch");

	Panel btnPanel = new Panel(new GridLayout(4, 1, 0, 5));
	btnPanel.add(btnCreate);
	btnPanel.add(btnWhois);
	btnPanel.add(btnHelp);
	btnPanel.add(btnLeave);
	add(btnPanel, BorderLayout.SOUTH);
    }
}
