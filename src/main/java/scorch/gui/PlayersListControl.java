package scorch.gui;

/*
  Class: PlayersListControl
  Author: Mikhail Kruk

  Description: the GUI element that provides the toolbar widget which lists
  current players of the game, shows thair status etc
*/

import java.awt.*;
import java.util.Vector;

import scorch.*;
import swindows.*;

public class PlayersListControl extends sWindow implements PlayersLister
{
    private final PlayersPanel pp;
    private final ScorchPlayer myself;

    public PlayersListControl
	(Vector<ScorchPlayer> players, ScorchPlayer myself, sWindow owner)
    {
        super(-1,-1,0,0,null,owner);

	this.myself = myself;
	
	setDoubleBorder(false);
	
	setLayout(new BorderLayout(0,0));
	pp = new PlayersPanel(players, this);
	add(pp, BorderLayout.CENTER);
	validate();
	width = height = -1;
    }

    public void paint(Graphics g)
    {
	if( width == -1 && height == -1 )
	    {
		Dimension d = getSize();
		width = d.width; height = d.height;

		mainPanel.setLocation(2*wndBorder, 2*wndBorder);
		mainPanel.setSize
		    (width-4*wndBorder, height-4*wndBorder);
		mainPanel.validate();
	    }
	super.paint(g);
    }
    
    public void addPlayer(String player)
    {
	pp.update();
    }

    public void removePlayer(String player)
    {
	pp.update();
    }

    public void highlight(ScorchPlayer sp)
    {
	pp.highlight(sp);
    }

    public ScorchPlayer getMyself()
    {
	return myself;
    }
}

class PlayersPanel extends Panel
{
    private Image backBuffer = null;
    private final Vector<ScorchPlayer> players;
    private int fontHeight;
    private Graphics backBufferG;
    private final PlayersListControl owner;

    private ScorchPlayer highlighted;

    public PlayersPanel(Vector<ScorchPlayer> players, PlayersListControl owner)
    {
	this.players = players;
	this.owner = owner;
    }
    
    public void update()
    {
	drawNames();
	repaint();
    }
    
    public void paint(Graphics g)
    {
	if( backBuffer == null )
	    {
		Dimension d = getSize();

		backBuffer = createImage(d.width, d.height);
		backBufferG = backBuffer.getGraphics();

		fontHeight = owner.getFontHeight();
		drawNames();
	    }

	g.drawImage(backBuffer, 0, 0, this);
    }

    void highlight(ScorchPlayer sp)
    {
	highlighted = sp;
	update();
    }

    private void drawNames()
    {	
	String t;

	if( backBuffer == null )
	    return;

	Dimension d = getSize();
	
	backBufferG.setColor(Color.gray);
	backBufferG.fillRect(0,0,d.width, d.height);
	
	int vs = (d.height-2* sPanel.wndBorder) / ScorchPlayer.MAX_PLAYERS;
	if( vs > fontHeight ) //+ owner.wndBorder )
	    vs = fontHeight ; //+ owner.wndBorder;

	for(int i = 0; i < players.size(); i++)
	    {
		ScorchPlayer sp = players.elementAt(i);
		if( !sp.isAlive() )
		    backBufferG.setColor(Color.black);
		else
		    backBufferG.setColor(new Color(sp.getColor()));
		
		t = sp.getName();
		t = t.substring(0, Math.min(12, t.length()));
		backBufferG.drawString(t, 2* sPanel.wndBorder, (i+1)* vs);
		
		if( sp == owner.getMyself() )
		    {
			int lineWidth = 
			    (backBufferG.getFontMetrics()).stringWidth(t);
			
			backBufferG.setColor(Color.white);
			backBufferG.drawLine
			    (2* sPanel.wndBorder, (i+1)* vs+1,
			     2* sPanel.wndBorder +lineWidth, (i+1)* vs+1);
		    }

		if( highlighted == sp )
		    {
			backBufferG.setColor(Color.white);
			backBufferG.drawRect
			    (2* sPanel.wndBorder -2, i * vs + 2,
			     d.width + 2-4* sPanel.wndBorder, vs);
			backBufferG.setColor(Color.lightGray);
			backBufferG.drawLine(2* sPanel.wndBorder -2,
					     i * vs + 2 + vs, 
					     d.width -2* sPanel.wndBorder,
					     i * vs + 2 + vs);
			backBufferG.drawLine(d.width -2* sPanel.wndBorder,
					     i * vs + 2,
					     d.width -2* sPanel.wndBorder,
					     i * vs + 2 + vs);
		    }
	    }
    }
}
