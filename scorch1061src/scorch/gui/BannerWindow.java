package scorch.gui;

/*
  Class:  BannerWindow
  Author: Mikhail Kruk
*/

import java.awt.*;
import java.awt.event.*;
import java.net.*;

import scorch.*;
import swindows.*;

public class BannerWindow extends sWindow implements ActionListener
{
    private String address;

    public BannerWindow(ScorchApplet owner, String image, String address)
    {
	super(-1,-1,0,0,"Advertisment", owner);
   
	this.address = address;

	Button bCancel = new Button("Maybe later"), 
	    bVisit = new Button("Open the page");

	Panel pb = new Panel(), pt = new Panel(), 
	    pl = new Panel(new GridLayout(2,1));
	sPanel pbn = new sPanel(-1,-1);
	pb.setLayout(new FlowLayout(FlowLayout.CENTER));
	pb.add(bVisit);
	pb.add(bCancel);

	setLayout(new BorderLayout(0,0));
	pbn.add(new Banner(this, image, address));
	pt.add(pbn);
	add(pt, BorderLayout.NORTH); 
	pl.add(new Label("Please support our sponsors and us by clicking on "+
			 "the banner above", Label.CENTER));
	pl.add(new Label("Clicking banner will not terminate current game", 
			 Label.CENTER));
	add(pl, BorderLayout.CENTER);
	add(pb, BorderLayout.SOUTH);
	
	bCancel.addActionListener(this);
	bVisit.addActionListener(this);

	validate();
    }

    public void visit(String surl)
    {
	URL burl = null;
	try
	    {
		burl = new URL(surl);
	    }
	catch(MalformedURLException e) {}
	
	close();
	((ScorchApplet)owner).banner(burl);
    }

    public void actionPerformed(ActionEvent evt)
    {
	String cmd = evt.getActionCommand();

	if( cmd.equals("Open the page") )
	    visit(address);
	else
	    ((ScorchApplet)owner).banner(null);

	close();
	return;
    }
}

class Banner extends Canvas implements MouseListener
{
    private String bannerImage; // = "b1.gif";
    private String url; // = "http://www.commission-junction.com/track/track.dll?AID=538392&PID=542124&URL=http%3A%2F%2Fwww%2Etechsumer%2Ecom";
    private Image banner;

    private BannerWindow owner;

    Banner(BannerWindow owner, String image, String address)
    {
	super();
	this.owner = owner;
	this.bannerImage = image;
	this.url = address;
	
	MediaTracker tracker = new MediaTracker(this);
	banner = ScorchApplet.getImage(bannerImage);
	tracker.addImage(banner,0);
	
	try
	    {
		tracker.waitForAll();
	    }
	catch(InterruptedException e)
	    {
		System.err.println(e);
	    }
	setSize(banner.getWidth(this), banner.getHeight(this));
	setCursor(new Cursor(Cursor.HAND_CURSOR));

	addMouseListener(this);
    }
    
    public void mouseClicked(MouseEvent evt) 
    {
	owner.visit(url);
    }	

    public void mousePressed(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt) {}
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}

    public void paint(Graphics g)
    {
	g.drawImage(banner,0,0,this);
    }
}


