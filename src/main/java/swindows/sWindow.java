package swindows;

/*
  Class:  sWindow
  Author: Mikhail Kruk
  Desciption: the class that represents the main part of Scorch GUI --
  scorchWindow.
*/

import java.awt.*;

public class sWindow extends sPanel 
{
    private int drag_x, drag_y;
    private boolean drag;
    private Image dragBuffer;

    protected String name; // windows name
    protected final Container owner; // each window has an owner
    
    private Component border;
    
    protected boolean doubleBorder = true; // border type

    // all the components are inserted into the mainPanel, not in the 
    // sWindow directly
    public final Container mainPanel;

    public sWindow(int x, int y, int w, int h, String name, 
		   Color bkColor, Color textColor, Container owner)
    {
	this(x, y, w, h, name, owner);
	this.bkColor = bkColor;
	this.textColor = textColor;
    }
    
    public sWindow(int x, int y, int w, int h, String name, Container owner)
    {
        super(x, y, w, h);
	this.name = name;
	this.owner = owner;

	setBackground(bkColor);

	this.mainPanel = new Panel();
	
	if(name != null)
	    {
		mainPanel.setLocation(wndBorder*3, 5*wndBorder + fontHeight);
		mainPanel.setSize(width-6*wndBorder, 
				  height-8*wndBorder - fontHeight);
	    }
	else
	    {
		mainPanel.setLocation(wndBorder*3, 3*wndBorder);
		mainPanel.setSize(width-6*wndBorder, 
				  height-6*wndBorder);
	    }

	super.setLayout(null);
	super.add(mainPanel);

	/*owner.addMouseMotionListener(this);
	  owner.addMouseListener(this);*/
	setVisible(false);
    }
    
    public void setDoubleBorder(boolean db)
    {
	doubleBorder = db;
    }
    
    public void display()
    {
	owner.add(this, 0);
	setVisible(false);
	place();
	validate();
	setVisible(true);
	requestFocus();
    }

    /*public void update(Graphics g) 
    {
	paint(g);
    }
    
    public void paint(Graphics g) 
    {
       if( drag )
	   {
	       if( dragBuffer == null )
		   {
		       dragBuffer = createImage(width, height);
		       //sPaint(dragBuffer.getGraphics());
		       dragBuffer.getGraphics().setColor(Color.white);
		       dragBuffer.getGraphics().drawRect(0,0,100,100);
		   }
	       //g.drawImage(dragBuffer, 0, 0, this);
	       g.setColor(Color.white);
	       g.drawRect(0,0,100,100);
	   }
       else
	   sPaint(g);
	   }*/

    public void paint(Graphics g)
    {
	if( !doubleBorder )
	    {
		super.paint(g);
		return;
	    }

	if( fm == null ) 
	    {
		fm = g.getFontMetrics();
		fontHeight = fm.getMaxAscent() + fm.getMaxDescent();
	    }
	
	g.setColor(bkColor);
	g.fill3DRect(0,0,width,height,true);

	if( name != null )
	    {
		g.setColor(bkColor);		
		g.fill3DRect(wndBorder, wndBorder, width-2*wndBorder, 
			     wndBorder+fontHeight, false);
		g.fill3DRect(wndBorder, 3*wndBorder + fontHeight, 
			     width-2*wndBorder, 
			     height-4*wndBorder - fontHeight, false);
		g.fill3DRect(wndBorder*2, 4*wndBorder + fontHeight,
			     width-4*wndBorder, 
			     height-6*wndBorder - fontHeight, true);
	
		g.drawString(name, (width - fm.stringWidth(name))/2, 
			     wndBorder+fm.getMaxAscent());
		mainPanel.setLocation(wndBorder*3, 5*wndBorder + fontHeight);
	    }
	else
	    {
		g.fill3DRect(wndBorder, wndBorder, 
			     width-2*wndBorder, 
			     height-2*wndBorder, false);
		g.fill3DRect(wndBorder*2, wndBorder*2,
			     width-4*wndBorder, 
			     height-4*wndBorder, true);
	    }
	g.setColor(textColor);
    }

    public void remove(Component c)
    {
	mainPanel.remove(c);
    }

    public void add(Component c, Object t)
    {
	mainPanel.add(c, t);
    }

    public Component add(Component c, int t)
    {
	return mainPanel.add(c, t);
    }
    
    public Component add(Component c)
    {
	return mainPanel.add(c);
    }

    public void setLayout(LayoutManager l)
    {
	if(mainPanel != null)
	    mainPanel.setLayout(l);
    }

    public int getWidth()
    {
	return width;
    }

    public int getHeight()
    {
	return height;
    }

    public int getFontHeight()
    {
	return fontHeight;
    }

    protected void place()
    {
        if(width == 0 && height == 0)
	    {
		Dimension ps;
		mainPanel.validate();
		ps = mainPanel.getPreferredSize();
		mainPanel.setSize(ps);

		width = ps.width+6*wndBorder;
		height = ps.height+8*wndBorder + fontHeight;		
	    }
	
	setSize(width, height);
	if( x == -1 && y == -1 ) center();
	setLocation(x, y);
    }
    
    protected void center()
    {
	Dimension d = owner.getSize();
	x = (d.width-width)/2;
	y = (d.height-height)/2;
	/*setLocation(x, y);
	  validate();*/
    } 

    protected GridBagConstraints makeConstraints
	(int gridx, int gridy, int gridw, int gridh, int fill, 
	 int wx, int wy, int a, Insets i, int sx, int sy)
    {
	GridBagConstraints c = new GridBagConstraints();
	
	c.gridx = gridx; c.gridy = gridy;
	c.gridwidth = gridw; c.gridheight = gridh;
	c.fill = fill;
	c.weightx = wx;
	c.weighty = wy;
	c.anchor = a;
	c.ipadx = sx; c.ipady = sy;
	c.insets = i;
	
	return c;
    }

    protected GridBagConstraints makeConstraints
	(int gridx, int gridy, int gridw, int gridh, int fill,int a, Insets i) 
    {
	return makeConstraints
	    (gridx, gridy, gridw, gridh, fill, 1, 1, a, i, 0, 0);
    }

    protected GridBagConstraints makeConstraints
	(int gridx, int gridy, int gridw, int gridh, int fill,
	 int a, int sx, int sy)
    {
	return makeConstraints
	    (gridx, gridy, gridw, gridh, fill, 1, 1, a, 
	     new Insets(0,0,0,0), sx, sy);
    }
    
    public void close()
    {
	setVisible(false);
	owner.remove(this);
    }

    /*public boolean handleEvent(Event evt)
    {
	if( evt.id == Event.MOUSE_DOWN )
	    {
		if(evt.x > wndBorder && evt.x < width-wndBorder && 
		   evt.y > wndBorder && evt.y < fontHeight + wndBorder )
		    {
			drag_x = evt.x; drag_y = evt.y;
			drag = true;
			border = new sBorder(x,y,width,height,owner);
			//setVisible(false);
			dragBuffer = null;
			return true;
		    }
		else
		    return super.handleEvent(evt);
	    }
	if( evt.id == Event.MOUSE_UP && drag )
	    {
		x += (evt.x-drag_x);
		y += (evt.y-drag_y);
		setLocation(x, y);
		owner.remove(border);
		border = null;

		drag = false;
		return true;
	    }
	if( (evt.id == Event.MOUSE_DRAG )//|| evt.id == Event.MOUSE_EXIT ) 
	    && drag )
	    {
		if( evt.x != drag_x || evt.y != drag_y )
		    {
			x += (evt.x-drag_x);
			y += (evt.y-drag_y);
			border.setLocation(x, y);
			validate();
			drag_x = evt.x; drag_y = evt.y;
		    }
		return true;
	    }
	return super.handleEvent(evt);
    }
    */
}

/*class sBorder extends Component
    {
    private int w, h;

    public sBorder(int x, int y, int w, int h, Container owner)
    {
	super();
	owner.add(this);

	setLocation(x,y);
	setSize(w, h);
	this.w = w;
	this.h = h;
    }

    public void update(Graphics g)
    {
	paint(g);
    }

    public void paint(Graphics g)
    {
	g.setColor(Color.black);
	g.drawRect(0,0,w,h);
	g.setColor(Color.white);
	g.drawRect(1,1,w-2,h-2);
	g.setColor(Color.black);
	g.drawRect(2,2,w-4,h-4);
    }
}
*/
