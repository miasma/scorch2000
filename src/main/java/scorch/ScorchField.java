package scorch;

/*
  Class:  ScorchField
  Author: Mikhail Kruk

  Description: the class that handles all the game stuff. This is the place
  where animation threads live, all the explosions, terrain generation goes in
  here. BTW terrain generation sucks, but on the otehr hand it looks kind of
  better than fractal one.
  Chat support is also here (for some reason yet unknown)
  Many, many things in this file must be fixed or redone from scratch
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import scorch.utility.*;
import scorch.backgrounds.*;
import scorch.weapons.*;

public final  class ScorchField extends Canvas 
    implements Runnable, 
	       MouseMotionListener,
	       FocusListener
{
    private Image backBuffer;
	private final Image evenMoreBackBuffer;
    private Graphics backBufferG;
    private final int width;
	private final int height;
    private final Bitmap bitmap;
    private final ScorchApplet scorchApplet;
    private Color groundColor;
    
    private Thread thread;
    private boolean sendEOT = false;
    private final Vector<Explodable> currentAnimations;

    private final ScorchChat chat;
    private final Tooltip tooltip;

    private ScorchPlayer player; // the player which fires
    private long earnedCash = 0; // cash and kills earned in this *round*
    private int kills = 0;

    private final Random rand;
    
    // TERRAIN GENERATION MUST BE REIMPLEMENTED
    // we neved got a chance to do it right. the code that generates
    // terrain was actually written to test the Bitmap engine and somehow
    // no one bothered to fix this...
    public ScorchField(int width, int height, Random rand, 
		       ScorchApplet scorchApplet)
    {
	super();

	this.width = width;
	this.height = height;
	this.scorchApplet = scorchApplet;
	this.rand = rand;

	Background bk = randomBackground();

	bitmap = new Bitmap(width, height, bk, rand);
	bitmap.setSandColor(groundColor.getRGB());

	evenMoreBackBuffer = createImage(bitmap.getImageProducer());

	Debug.startTimer();

	int gg = Math.round(3f/8f*(float)height);
	bitmap.setColor(null);
	bitmap.fillRect(0, height-gg, width, gg);
	bitmap.setColor(groundColor);
	bitmap.fillRect(0, 0, width, height-gg);

	Debug.stopTimer("initial fill");
	Debug.startTimer();

	bitmap.setColor(null);
	for(int i = 0; i < 20; i++)
	    {
		bitmap.fillEllipse
		    (Math.abs(rand.nextInt()) % width, 
		     Math.abs(rand.nextInt()) % height,
		     10+Math.abs(rand.nextInt()) % 90,
		     10+Math.abs(rand.nextInt()) % 60);
	    }
	bitmap.setDensity(1f);
	bitmap.setColor(groundColor);
	bitmap.fillEllipse(width/2, height, width, height/8);
	bitmap.setDensity(1f);

	Debug.stopTimer("ellipse fill");
	Debug.startTimer();
	
	drop(0, width);
	bitmap.newPixels(0, 0, width, height);
	Debug.stopTimer("drop");

	placeTanks();
	
	currentAnimations = new Vector<>();

	chat = new ScorchChat(this, bitmap.getBackground());
	tooltip = new Tooltip(this);

	addMouseMotionListener(this);
	addFocusListener(this);
    }
    
    private void hideTanks()
    {
	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		ScorchPlayer sp = scorchApplet.getPlayer(i);

		if(!sp.isAlive()) continue;
		sp.hideFrame(false, false);
	    }
    }
    
    private void dropTanks()
    {
	Debug.println("Starting to drop the tanks:", 10);
	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		ScorchPlayer sp = scorchApplet.getPlayer(i);
		
		if(!sp.isAlive()) continue;
		
		sp.setFalling(true);
		addAnimation(sp);
	    }
    }

    private void showTanks()
    {
	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		ScorchPlayer sp = scorchApplet.getPlayer(i);
		
		if(!sp.isAlive()) continue;

		sp.drawTank(true);
	    }
    }

    private void drawShields()
    {
	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		ScorchPlayer sp = scorchApplet.getPlayer(i);
		if( sp.isAlive() )
		    {
			sp.drawShield(backBufferG);

			if( Debug.dev || Debug.desyncTest)
			    {
				backBufferG.drawString
				    (""+(float)sp.getPowerLimit()/
				     (float) ScorchPlayer.maxPower *100,
				     sp.getX(), sp.getY()+2*sp.getHeight());
			    }
		    }
	    }
    }

    // randomly disperce players over the terrain. Also make each player object
    // aware of the scorchfield on which they live so that they can interact
    // with the environment
    private void placeTanks()
    {
	int[][] tank;
	int tankType;
	int numPlayers = scorchApplet.getPlayersNum();
	ScorchPlayer sp;
	Vector<Integer> positions = new Vector<>(numPlayers);
	int ct, t;
	
	for(int i = 0; i < numPlayers; i++)
	    positions.addElement(i);
	
	for(int i = 0; i < numPlayers; i++)
	    {
		t = Math.abs(rand.nextInt()) % positions.size();
		ct = positions.elementAt(t);
		positions.removeElementAt(t);
		
		sp = scorchApplet.getPlayer(ct);
		sp.onFieldInit(this, bitmap, ct);
		tankType = sp.getTankType();

			bitmap.setColor(null);
		for(int j = 1; j < height; j++)
		    {
			int count = 0, k;
			for(k = 0; k < Tanks.getTankWidth(tankType) &&
				count < Tanks.getTankWidth(tankType); k++) {
				int x = k+width/(numPlayers+1)*(i+1);
				if (!bitmap.isBackground
						(x, j)) {
					count++;
					bitmap.setPixel
							(x, j - 1);
				}
			}
			
			if(count >= Tanks.getTankWidth(tankType))
			    {
				sp.setPosition
				    (width/(numPlayers+1)*(i+1),
				     j-Tanks.getTankHeight(tankType));
				sp.drawNextFrame(true);

				j = height;
				}
		    }
	    }
    }

    private void drop(int startx, int endx)
    {
	new Dropper(bitmap, startx, endx);
    }

    public void update(Graphics g) 
    {
	paint(g);
    }
    
    public void paint(Graphics g) 
    { 	
	if(backBuffer == null) 
	    {
		backBuffer = createImage(width,height);
		backBufferG = backBuffer.getGraphics();
	    }

	backBufferG.drawImage(evenMoreBackBuffer, 0, 0, this);
	chat.paint(backBufferG, width);
	drawShields();
	
	tooltip.paint(backBufferG);

	g.drawImage(backBuffer, 0, 0, this);
    }
 
    public synchronized void run()
    {
	int i;

	if( thread == null ) return;

	do
	    {
		runCurrentAnimation();
		// now explode everyone who wants to explode
		for(i = 0; i < scorchApplet.getPlayersNum(); i++)
		    {
			ScorchPlayer sp = scorchApplet.getPlayer(i);
			if( sp.isDying() )
			    {
				addAnimation(sp);
				break;
			    }
		    }
	    }
	while( i < scorchApplet.getPlayersNum() ); // while someone explodes

	if( sendEOT )
	    {		
		if( player != null )
		    player.updateKills(earnedCash, kills);
		player = null; // just in case

		scorchApplet.sendEOT("run()");
	    }
	sendEOT = false;
	thread = null;
    }
    
    // runs an animation NOW
    // th specifies whether a new thread should be spawned or not
    public synchronized void runAnimationNow(Explodable animation)
    {
	sendEOT = false; // you never need to send EOT from such calls, I think
	
	addAnimation(animation);
	thread = Thread.currentThread();
	run();
    }

    private synchronized void addAnimation(Explodable animation)
    {
	currentAnimations.addElement(animation);
    }

    private synchronized void runCurrentAnimation()
    {
	int i;
	Explodable ca;
	
	if( scorchApplet.GalslaMode )
	    {
		while( thread != null )
		    {
			for(i = 0; i < scorchApplet.getPlayersNum(); i++)
			    scorchApplet.getPlayer(i).drawNextFrame(true);
			try
			    {
				Thread.sleep(60);
			    }
			catch(InterruptedException e){}
		    }
		return;
	    }

	while( currentAnimations.size() > 0 && thread != null &&
	       !scorchApplet.GalslaMode )
	    {
		i = 0;
		while( i < currentAnimations.size() )
		    {
			ca = currentAnimations.elementAt(i);

			if( ca.drawNextFrame(true) )
			    {
				bitmap.newPixels();
				i++;
			    }
			else
			    {
				bitmap.newPixels();
				currentAnimations.removeElementAt(i);
				processAnimation(ca);
			    }
		    }
		try
		    {
			Thread.sleep(40); // this shouldn't be constant?
		    }
		catch(InterruptedException e){}
	    }
    }
    
    // this method take care of stuff left after the animations ends
    private synchronized void processAnimation(Explodable ca)
    {
	int t;
	
	if( (ca instanceof GenericMissile) ||
	    (ca instanceof ScorchPlayer && !((ScorchPlayer)ca).isAlive()) )
	    {
		fireComplete(ca);
		return;
	    }
	if( ca instanceof ScorchPlayer &&  ((ScorchPlayer)ca).isAlive() )
	{
	    killTanks(null,null,false); // kills from falling
	}
    }
    
    private synchronized void fireComplete(Explodable expl)
    {
	ExplosionInfo ei = expl.getExplosionInfo();

	if( ei != null )
	    {	
		showTanks();
		
		if(ei.explosionArea != null)
		    {
			drop(ei.explosionArea.x, 
			     ei.explosionArea.x+ei.explosionArea.width);
			showTanks();
			bitmap.newPixels(ei.explosionArea.x, 0, 
					 ei.explosionArea.width+1, height);
		    }
		//if( ei.center != null ) TODO? maybe it's ok?
		killTanks(expl, player, false);
	    }
	// always drop tanks. I think this is needed only if someone used fuel
	// but there seems to be no easy way to tell, so drop.
	dropTanks(); 
    }

    public ScorchPlayer getTankAt(int x, int y)
    {
	ScorchPlayer sp;
	int tt;

	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		sp = scorchApplet.getPlayer(i);
		if( !sp.isAlive() ) continue;
		if( x > sp.getX() && x < sp.getX() + sp.getWidth() &&
		    y > sp.getY() && y < sp.getY() + sp.getHeight() )
		    return sp;
	    }
	return null;
    }

    //
    // checks if any of the players is in the range of any round explosion
    // works in two modes: 
    // * AI mode: used by AI to count number of victims of a possible shot
    // * normal mode: just kill all the victims
    //
    public int killTanks(Explodable expl, ScorchPlayer caller, 
			  boolean ai_mode)
    {
	ScorchPlayer sp;
	int counter = 0, cur_damage;
	boolean alive;
	
	//Debug.printThreads();

	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		sp = scorchApplet.getPlayer(i);

		// skip players which are dead or are about to explode
		if( !sp.isAlive() || sp.isDying() )
		    continue;

		if( expl != null )
		    cur_damage = expl.calculateDamage(sp);
		else
		    cur_damage = 0; // to drop tanks
		
		if( !ai_mode )
		    {
			alive = sp.decPowerLimit(cur_damage);

			if(!alive)
			    {
				if( sp != caller )
				    {
					// bonus if >1 kill with one shot
					earnedCash *= 1.3;

					earnedCash += sp.getBounty();
					kills++;
				    }
				else // killed himself
				    {
					earnedCash -= 10000; 
				    }
				
				sp.setExplosion(randomExplosion(sp));
			    }

			// give money for any damage done to other players
			if( sp != caller )
			    earnedCash += (10*cur_damage); 
		    }
		
		if( ai_mode && cur_damage > 0 )
		    {
			if(sp == caller)
			    counter-=ScorchPlayer.MAX_PLAYERS* ScorchPlayer.maxPower;
			else
			    counter+=expl.calculateDamage(sp);
		    }
	    }
	return counter;
    }

    public void stop()
    {
	if( thread != null ) 
	    thread = null;
    }
    
    public void start()
    {
	if( thread != null ) 
	    thread.start();
    }

    public synchronized void playerLeft(ScorchPlayer sp)
    {
	if( thread != null )
	    System.err.println("ScorchField.playerLeft(): internal error 0. "+
			       "please report to meshko@scorch2000.com");

	chat.addSystemMessage("Player "+sp.getName()+" left the game");

	if( sp.isAlive() )
	    {
		sp.setExplosion
		    (new SimpleExplosion(bitmap, SimpleExplosion.MISSILE));
		//sp.setFalling(true); 

		runAnimationNow(sp);

		//new Thread(this, "leftpl-thread");
		//thread.start();

		// we must wait here..
		/*try
		    {
			thread.join();
		    }
		catch(InterruptedException e)
		{}*/
	    }
    }

    public synchronized void massKill()
    {
	if( thread != null )
	   System.err.println("ScorchField.massKill(): internal error 0. please report to meshko@scorch2000.com");

	newSysMsg("MASSKILL by master");

	for(int i = 0; i < scorchApplet.getPlayersNum(); i++)
	    {
		ScorchPlayer sp = scorchApplet.getPlayer(i);
		
		if(!sp.isAlive()) continue;
		
		sp.setExplosion
		    (new SimpleExplosion(bitmap, SimpleExplosion.MISSILE));
		//sp.setFalling(true);
		addAnimation(sp);
	    }
	
	sendEOT = true;
	thread = Thread.currentThread(); //new Thread(this, "masskill-thread");
	run(); //thread.start();
    }

    // move this in the Background class? [todo]
    private Background randomBackground()
    {
	Color rc1, rc2;
	Color[] colors =
	{Color.black, new Color(0, 150, 0), 
	 new Color(254,0,0), new Color(0,0,254), 
	 new Color(254,254,254), new Color(0,254,254), 
	 new Color(254,254,0), new Color(34,23,65)};
	Background bk;

	rc1 = colors[Math.abs(rand.nextInt() % colors.length)];
	rc1 = new Color(rc1.getRed(), rc1.getGreen(), 
			Math.min(255, rc1.getBlue()+100));

	groundColor = 
	    colors[Math.abs(rand.nextInt() % colors.length)];
	groundColor = new Color
	    (Math.max(20, groundColor.getRed() - 150),
	     Math.max(20, groundColor.getGreen() - 150), 
	     Math.max(20, groundColor.getBlue() - 150));

	switch( Math.abs(rand.nextInt() % 3) )
	    {
	    case 0: 
		bk = new StarsBackground(width, height, rand);
		groundColor = new Color
		    (Math.min(255, groundColor.getRed() + 30),
		     Math.min(255, groundColor.getGreen() + 30), 
		     Math.min(255, groundColor.getBlue() + 30));
		break;
	    case 1:
		rc2 = colors
		    [Math.abs(rand.nextInt() % colors.length)];
		bk = new GradientBackground
		    (width, height, rc1, rc2, 127);
		break;
	    case 2:
		rc1 = new Color
		    (Math.max(20, rc1.getRed() - 50),
		     Math.max(20, rc1.getGreen() - 50),
		     Math.min(255, rc1.getBlue() + 30));
		bk = new PlainBackground(width, height, rc1);
		break;
	    default:
		bk = null;
	    }

	return bk;
    }

    // this is where each turn begins
    public synchronized void fire(ScorchPlayer sp, int weapon)
    {
	Explosion expl;
	GenericMissile msl;
	Physics physics;

	expl = sp.getWeaponExplosion(weapon);

	earnedCash = 0;
	kills = 0;
	this.player = sp;

	// hide shield so that missile can go thgrough
	//sp.drawShield(false, false); 

	int angle = player.getAngle();

	physics = new Physics(player.getTurretX(2.0),
			bitmap.getHeight()-player.getTurretY(2.0),
			      angle, player.getPower() / 8.0);

	Debug.log(player+" shoots:");
	Debug.log(""+physics);

	if( expl instanceof MIRVExplosion )
	    msl = new MIRVMissile(bitmap, physics, expl);
	else
	    msl = new RoundMissile(bitmap, physics, expl);

	// redraw shield. it's a bit of a hack: we hope that 
	// missile precalculated the rajectory beyond the shield
	// it's an almost reasonable assumption.
	//sp.drawShield(true, false); 

	if( sp.checkTracer() )
	    msl.setTracerColor(new Color(sp.getColor()));
	
	addAnimation(msl);
	
	sendEOT = true;
	thread = new Thread(this, "animation-thread");
	thread.start();
    }
  
    // adds a new chat messagte to the chat
    public void newChatMsg(String msg)
    {
	chat.addMessage(msg);
	if(msg.contains("Galsla"))
	    {
	       	chat.addSystemMessage("PRIVET GALKA!!!");
		scorchApplet.GalslaMode = true;
		thread = new Thread(this, "galsla-thread");
		thread.start();
	    }
    }
    
    // system message displayed through the chat
    public void newSysMsg(String msg)
    {
	chat.addSystemMessage(msg);
    }
    
    public void focusGained(FocusEvent e) 
    {
	transferFocus();
    }

    public void focusLost(FocusEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) 
    {
	ScorchPlayer sp = getTankAt(e.getX(), e.getY());
	if( sp != null )
	    tooltip.setText(sp.getToolTip(), e.getX(), e.getY());
	else
	    tooltip.setText(null, 0, 0);
    }

    // move this to the Explosion class? [todo]
    private Explosion randomExplosion(ScorchPlayer sp)
    {
	Explosion e = null;
	int expl_num = 7;
	int r = Math.abs(rand.nextInt()) % expl_num;
	int w = sp.getWidth(), h = sp.getHeight();

	switch( r )
	    {
	    case 0:
		e = new FireExplosion(bitmap, (int)(w*1.5), h*4);
		break;
	    case 1:
		e = new SimpleExplosion(bitmap, SimpleExplosion.MISSILE);
		break;
	    case 2:
		e = new SimpleExplosion(bitmap, SimpleExplosion.BABY_NUKE);
		break;
	    case 3:
		e = new SimpleExplosion(bitmap, SimpleExplosion.NUKE);
		break;
	    case 4:
		e = new SandExplosion(bitmap);
		break;
	    case 5:
		e = new LaserExplosion(bitmap, w);
		break;
	    case 6:
		e = new FunkyExplosion(bitmap, rand, 6);
		break;
	    default:
		System.err.println("ScorchField.getRanomExplosion(): error");
	    }

	return e;
    }

    public int getWidth()
    {
	return width;
    }
 
    public int getHeight()
    {
	return height;
    }
    
    /*private class starter implements Runnable
    {
	private Explodable animation;

	public starter(Explodable animation)
	{
	    this.animation = animation;
	    ScorchField.this.thread = new Thread(this, "starter thread");
	    ScorchField.this.thread.start();
	}
	
	public void run()
	{
	    ScorchField.this.addAnimation(animation);
	    ScorchField.this.run();
	}
	}*/
}

class Tooltip
{
    private static final Color hintColor = new Color(255,255,223);
  
    private String tip;
    private int x, y;
    private Graphics g;
    private final ScorchField owner;

    private FontMetrics fm = null;
    private int fontHeight;

    public Tooltip(ScorchField owner)
    {
	this.owner = owner;
    }

    public void setText(String ntip, int x, int y)
    {
	boolean update = 
	    ((ntip != null && !ntip.equals(tip)) ||
	     (ntip == null && tip != null));
	
	if( update )
	    {
		this.tip = ntip;
		this.x = x;
		this.y = y;
		owner.repaint();
	    }
    }

    public void paint(Graphics g)
    {
	int dx, dy, sw, ow = owner.getWidth(), oh = owner.getHeight();

	if( tip == null ) return;

	if( fm == null )
	    {
	    	fm = owner.getFontMetrics(owner.getFont());
		fontHeight = fm.getMaxAscent() + fm.getMaxDescent();
	    }
	
	sw = fm.stringWidth(tip)+6;
	if(x + sw > ow)
	    dx = ow-sw-1;
	else
	    dx = x;

	dy = y - fontHeight;
	    
	g.setColor(hintColor);
	g.fillRect(dx, dy, fm.stringWidth(tip)+6, fontHeight+4);
	g.setColor(Color.black);
	g.drawRect(dx, dy, fm.stringWidth(tip)+6, fontHeight+4);
	g.drawString(tip, dx+3, dy+2+fm.getMaxAscent());
    }  
}
