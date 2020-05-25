package swindows;

/*
  Class:  sScrollPanel
  Author: Mikhail Kruk
  Desciption: all the GUI elements needed to build a scrollable table
  with horizontal entries
*/

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class sScrollPanel extends sPanel
{
    private final int rows;  // number of visible rows
	private int start_idx;     // the first rows displayed in panel (currently)
	private final int total_rows;    // total number of rows in the panel
    private Panel mainPanel;
    private final Panel wp;  // weapper panel around mainPanel
    private final Vector<Panel> panels;
    private Container owner;

    public sScrollPanel(Container owner, int width, int height, 
			Vector<Panel> panels, int rows)
    {
        super(width, height);

	this.total_rows = panels.size();
	this.rows = Math.min(rows, total_rows);
	this.panels = panels;

	start_idx = 0;

	wp = new Panel(new BorderLayout(0, 0));
	wp.setBackground(Color.gray);

	rebuild();

	// do we need a scroll bar?
	if( rows < total_rows )
	    {
            sScrollBar sb = new sScrollBar(this);
		wp.add(sb, BorderLayout.EAST);
	    }

	add(wp);
    }

    private void rebuild()
    {
	Panel np = new Panel(new GridLayout(rows, 1, 0, 0)), tp;
	np.setBackground(Color.gray);
	
	for(int i = start_idx; i < start_idx+rows; i++)
	    {
		tp = panels.elementAt(i);
		np.add(tp);
		np.validate();
	    }
	
	if(mainPanel != null)
	    wp.remove(mainPanel);
	
	mainPanel = np;

	wp.add(mainPanel, BorderLayout.CENTER);
	wp.validate();
	//mainPanel.validate();
    }

    public void setIndex(int index)
    {
	if( start_idx == index ||
	    (start_idx == 0 && index < 0) ||
	    (index + rows >= total_rows && start_idx == total_rows - rows) )
	    return;
	
	start_idx = index;

	if( start_idx < 0 ) 
	    start_idx = 0;
	else
	    if( start_idx + rows >= total_rows ) 
		start_idx = total_rows - rows;
	
	rebuild(); // really changed
    }

    public void buttonPress(int type)
    {
	if( type == sScrollButton.SCROLL_UP )
	    {
		if( start_idx > 0 )
		    setIndex(start_idx-1);
		return;
	    }
	if( type == sScrollButton.SCROLL_DOWN )
	    {
		if( start_idx + rows < total_rows )
		    setIndex(start_idx+1);
		return;
	    }
	System.err.println("sScrollPanel.buttonPress(): unknown button type: "+
			   type);
    }

    public boolean handleEvent(Event evt)
    {
	int new_idx = start_idx;

	if( evt.id == Event.KEY_ACTION )
	    {
		switch(evt.key)
		    {
		    case Event.PGUP: 
			new_idx-=(rows);
			break;
		    case Event.PGDN:
			new_idx+=(rows);
			break;
		    case Event.UP:
			new_idx--;
			break;
		    case Event.DOWN:
			new_idx++;
			break;
		    }
		setIndex(new_idx);
	    }
	return super.handleEvent(evt);
    }
}

class sScrollButton extends Component implements MouseListener,
						 MouseMotionListener
{
    public static final int SCROLL_UP = 0;
    public static final int SCROLL_DOWN = 1;

    private static final int btnSize = 17;

    private final int type;
    private boolean bpressed = false,  // button pressed
	mpressed = false;              // mouse button pressed
    private final sScrollPanel owner;
    
    private static final Color d = Color.darkGray, w = Color.white, 
	l = Color.lightGray, n = null;
    private static final Color[][] btnUP = {
	{n,n,n,n,n,w,n,n,n,n,n},
	{n,n,n,n,w,l,d,n,n,n,n},
	{n,n,n,n,w,l,d,n,n,n,n},
	{n,n,n,w,l,l,l,d,n,n,n},
	{n,n,n,w,l,l,l,d,n,n,n},
	{n,n,w,l,l,l,l,l,d,n,n},
	{n,n,w,l,l,l,l,l,d,n,n},
	{n,w,l,l,l,l,l,l,l,d,n},
	{n,w,l,l,l,l,l,l,l,d,n},
	{w,l,l,l,l,l,l,l,l,l,d},
	{d,d,d,d,d,d,d,d,d,d,d} };

     private static final Color[][] btnDN = {
	{w,w,w,w,w,w,w,w,w,w,w},
	{w,l,l,l,l,l,l,l,l,l,d},
	{n,w,l,l,l,l,l,l,l,d,n},
	{n,w,l,l,l,l,l,l,l,d,n},
	{n,n,w,l,l,l,l,l,d,n,n},
	{n,n,w,l,l,l,l,l,d,n,n},
	{n,n,n,w,l,l,l,d,n,n,n},
	{n,n,n,w,l,l,l,d,n,n,n},
	{n,n,n,n,w,l,d,n,n,n,n},
	{n,n,n,n,w,l,d,n,n,n,n},
	{n,n,n,n,n,d,n,n,n,n,n} };

    private static final Color[][][] btns = {btnUP, btnDN};

    public sScrollButton(int type, sScrollPanel owner)
    {
	super();
	
	this.type = type;
	this.owner = owner;

	addMouseListener(this);
	addMouseMotionListener(this);
    }
    
    public Dimension getPreferredSize()
    {
	return new Dimension(btnSize,btnSize);
    }

    public void paint(Graphics g)
    {
	Dimension dim = getSize();
	int width = dim.width, height = dim.height;
	Color tc;

	int sx = (width - btns[type][0].length)/2,
	    sy = (height - btns[type].length)/2;
	
	g.setColor(Color.lightGray);
	g.fill3DRect(0, 0, width, height, true);

	for(int i = 0; i < btns[type][0].length; i++)
	    for(int j = 0; j < btns[type].length; j++)
		{
		    tc = btns[type][j][i];
		    if(tc != null)
			{	
			    if( bpressed )
				{
				    if( tc == d ) 
					tc = w;
				    else
					if( tc == w ) 
					    tc = d;
				}
			    g.setColor(tc);
			    g.fillRect(sx+i,sy+j,1,1);
			}
		}
    }

    
    public void mouseClicked(MouseEvent evt) 
    {
    }

    public void mouseEntered(MouseEvent evt)
    {
	/*
	// check coordinates to fix netscape bug. 
	if( mpressed && evt.getX() >= 0 && evt.getY() >= 0 )
	    {
		System.err.println(evt.getPoint());
		bpressed = true;
		repaint();
		}*/
    }

    public void mouseExited(MouseEvent evt)
    {
	if( bpressed )
	    {
		bpressed = false;
		repaint();
	    }
    }

    public void mouseDragged(MouseEvent evt)
    {
	int x = evt.getX(), y = evt.getY();
	
	if( bpressed && 
	    ( x < 0 || x >= btnSize || y < 0 || y >= btnSize ) )
	    {
		bpressed = false;
		repaint();
		return;
	    }

	if( !bpressed &&
	    ( x >= 0 && x < btnSize && y >= 0 && y < btnSize ) )
	    {
		bpressed = true;
		repaint();
        }
    }
    
    public void mouseMoved(MouseEvent evt)
    {
    }

    public void mousePressed(MouseEvent evt)
    {
	bpressed = true;
	mpressed = true;
	repaint();
    }
    
    public void mouseReleased(MouseEvent evt)
    {
	if( bpressed )
	    {
		bpressed = false;
		repaint();
		owner.buttonPress(type);
	    }
	mpressed = false;
    }
}

class sScrollBar extends Container
{

    public sScrollBar(sScrollPanel owner)
    {
	super();
	setLayout(new BorderLayout(0,0));

        sScrollButton btnUP = new sScrollButton(sScrollButton.SCROLL_UP, owner);
        sScrollButton btnDN = new sScrollButton(sScrollButton.SCROLL_DOWN, owner);
	add(btnUP, BorderLayout.NORTH);
	add(btnDN, BorderLayout.SOUTH);
	add(new sSeparator(), BorderLayout.WEST);
	add(new sSeparator(), BorderLayout.EAST);	

    }
}

class sSeparator extends sPanel
{
    public sSeparator()
    {
	super(-1,-1);
    }
    
    public Dimension getPreferredSize()
    {
	Dimension r = super.getPreferredSize();
	r.width = wndBorder;
	return r;
    }
    
    public void paint(Graphics g)
    {
	Dimension d = getSize();
	int w = d.width, h = d.height;
	
	g.setColor(Color.lightGray);
	g.fill3DRect(0,0,wndBorder,h,true);
    }
}
