package scorch.gui;

/*
  Class:  GameControl
  Author: Mikhail Kruk

  Desciption: 
*/

import java.awt.*;
import java.util.Random;

import scorch.*;

public class GameControl extends Panel
{
    public GameControl(int number, GamesPanel owner)
    {
	super();
	setBackground(Color.red);
	setLayout(new BorderLayout(0,0));
	Panel p1 = new Panel(new FlowLayout(FlowLayout.CENTER,0,0));
	p1.add(new GameIcon());
	add(p1, BorderLayout.CENTER);
	add(new Label("Game #"+number+" by Boneovantor", Label.CENTER), 
	    BorderLayout.SOUTH);
    }

    /*public void paint(Graphics g)
    {
	width = getSize().width;
	height = getSize().height;	
	super.paint(g);
	}*/
}

class GameIcon extends Canvas
{
    private static String[] iconNames = {"Images/gicon1.gif",
					 "Images/gicon2.gif"};
    private static Image[] icons;

    private Image icon;

    GameIcon()
    {
	super();
	if( icons == null )
	    loadIcons();
	
	Random r = new Random();
	icon = icons[(int)(Math.random()*Integer.MAX_VALUE) % icons.length];
	setSize(icon.getWidth(this), icon.getHeight(this));
    }
    
    public void paint(Graphics g)
    {
	g.drawImage(icon,0,0,this);
    }

    private void loadIcons()
    {
	icons = new Image[iconNames.length];
	MediaTracker tracker = new MediaTracker(this);
	
	for(int i = 0; i < iconNames.length; i++)
	    {
		icons[i] = ScorchApplet.getImage(iconNames[i]);
		tracker.addImage(icons[i],i);
	    }
	try
	    {
		tracker.waitForAll();
	    }
	catch(InterruptedException e)
	    {
		System.err.println(e);
	    }
    }
}
