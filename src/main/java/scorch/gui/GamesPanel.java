package scorch.gui;

/*
  Class:  GamesPanel
  Author: Mikhail Kruk
  Description: 
*/

import java.awt.*;

import scorch.*;
import swindows.*;

public class GamesPanel extends sWindow
{
    final Panel gamesList;

    public GamesPanel(int x, int y, int w, int h, ScorchApplet owner)
    {
	super(x,y,w,h,"Games", owner);

	ScrollPane scrollPane = new ScrollPane();
	gamesList = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
	scrollPane.add(gamesList);
	setLayout(new BorderLayout());
	add(scrollPane,BorderLayout.CENTER);
	for(int i = 0; i < 10; i++)
	    {
		Panel t = new GameControl(i, this);
		gamesList.add(t);
	    }
    }
}
