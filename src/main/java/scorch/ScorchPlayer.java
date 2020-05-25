package scorch;

/*
  Class:  ScorchPlayer
  Author: Mikhail Kruk
  
  Description: The class that represents every ScorchPlayer. Contains all
  the information about every player, its state, weapon, items, tank 
  condition and type; it is able to redraw itself and can explode.
*/

import java.util.*;
import java.awt.*;
import java.text.DecimalFormat; // for tooltip

import scorch.utility.*;
import scorch.weapons.*;
import scorch.items.*;

public class ScorchPlayer extends PhysicalObject implements Explodable
{
    public static final int MAX_PLAYERS = 8;
    public static final int startAngle = 30;
    //public static final int angle_step = 1; // don't change [broken desync]
    public static final int maxPower = 1000;
    public static final int minPower = (int)(0.2*maxPower);
    public static final int startPower = 300;

    private static long cashBounty = 40000;

    private int ID;
    private PlayerProfile profile;
    private int color = -1;
    private Weapon[] weapons;
    private Item[] items;
    private int parachutes = 0, tracers = 0;
    private Shield shield = null;
    
    protected ScorchField scorchField;
    protected int tankType;

    private Random rand;

    private long cash, earnedCash = 0;
    private int kills = 0;
    private int angle, power = startPower, powerLimit = maxPower;
    private int currentWeapon = Weapon.Missile;

    private int angle_change = 0;

    private boolean alive = true;
    private Explosion explosion, lastExplosion;
    private ExplosionInfo ei;

    // variable used for tank falling
    private boolean falling = false, exploding = false, 
	firstTurn = true, usingFuel = false;
    private static final int fallStep = 3, F_LEFT = 0, F_RIGHT = 1;
    private int 
	fallCount, 
	//fallHeight, 
	ox, oy,  // old position too hide previouse frame
	fallDirection = -1, // F_LEFT or F_RIGHT
	leftPos, rightPos; // falling margins for dirt dropping 

    protected ScorchApplet owner;

    public ScorchPlayer(int id, PlayerProfile profile, ScorchApplet owner)
    {
	super(null, null);
	this.owner = owner;
	this.profile = profile;
	this.ID = id;
	angle = startAngle;

	weapons = Weapon.loadWeapons(this);
	items = Item.loadItems(this);
    }

    // called by ScorchField when player is put on the field
    public void onFieldInit(ScorchField sf, Bitmap b, int color)
    {
	this.color = color;
	this.scorchField = sf;
	this.bitmap = b;
    }

    // called from ScorchApplet if lamer mode is enabled
    public void giveWeapons()
    {
	int i;
	for(i = 0; i < weapons.length; i++)
	    weapons[i].setQuantity(999);
	for(i = 0; i < items.length; i++)
	    items[i].setQuantity(999);
    }

    public void setRand(Random rand)
    {
	this.rand = rand;
    }

    synchronized public boolean drawNextFrame(boolean update)
    {
	int s, e;

	if(!alive) return false;

	if( owner.GalslaMode )
	    {
		hideFrame(true, false);
		
		if(Math.random() > 0.5) 
		    x += (int)(Math.random()*4.0);
		else
		    x -= (int)(Math.random()*4.0);
		if(Math.random() > 0.51) 
		    y += (int)(Math.random()*4.0);
		else
		    y -= (int)(Math.random()*4.0);

		incAngle(10);

		if( x < 0 ) x = 0;
		if( y < 15 ) y = 15;
		if( x > bitmap.getWidth() ) x = bitmap.getWidth();
		if( y > bitmap.getHeight() ) y = bitmap.getHeight();

		drawTank(true);

		return false;
	    }

	while( falling ) // the tank is falling after explosion
	    {
		fallCount++; 
		int w = getWidth(); // tank dimensions
		int h = getHeight();
		// where tank's base actually begins 
		// (if it's not whole bottom line of the sprite)
		int leftBase = Tanks.getLeftBase(tankType),
		    rightBase = Tanks.getRightBase(tankType);

		s = -1; e = -1;
		// check what is under the tank, s(outh), e(ast)
		for(int i = leftBase; i < w-rightBase; i++)
		    if(!bitmap.isBackground(x+i, y+h))
			{
			    if( s == -1 ) s = i;
			    e = i;
			}

		if( s < 0 && e < 0 ) // nothing underneath: just fall
		    {
			y++;

			if(fallCount % fallStep == 0 || parachuteOpen())
			    {
				drawFallStep();
				return true;
			    }
			continue;
		    }
		
		// falling is simplified if engine is started (using fuel)
		// this means tansk stop fallign as soon as it hits something
		if( usingFuel ) 
		    {
			setUsingFuel(false);
			if(fallCount > 0 )
			    fallingDamage();
			else
			    setFalling(false);

			return false;
		    }

		// slide to the left or to the right if there is no walls
		for(int i = 0; i < h && (s < 0 || e < 0); i++)
		    {
			if(!bitmap.isBackground(x-1,y+i))
			    {
				s = 0;
				if( e < 0 ) e = s;
			    }
			if(!bitmap.isBackground(x+w,y+i))
			    {
				e = w-1;
				if( s < 0 ) s = e;
			    }
		    }

		// decide which way to slide
		if( s > w / 2 && (fallDirection == F_LEFT || 
				  fallDirection == -1))
		    {
			x--;
			fallDirection = F_LEFT;
			if( x < leftPos ) leftPos = x;
			if(fallCount % fallStep == 0 || parachuteOpen()) 
			    {
				drawFallStep();
				return true;
			    }
			continue;
		    }
		if( e < w / 2 && (fallDirection == F_RIGHT ||
				  fallDirection == -1))
		     {
			 x++;
			 fallDirection = F_RIGHT;
			 if( x > rightPos ) rightPos = x;
			 if(fallCount % fallStep == 0 || parachuteOpen()) 
			     {
				 drawFallStep();
				 return true;
			     }
			 continue;
		     }
	    
		// we are stable (end of falling)
		
		if( fallCount > 0 )
		    {
			fallingDamage();
						
			// the dirt that was on top of this tank has to fall
			new Dropper(bitmap, leftPos-w, rightPos+2*w);
			drawTank(false);
			bitmap.newPixels(leftPos-w, 0, 
					 rightPos-leftPos+3*w, 
					 bitmap.getHeight());
		    }
		else
		    setFalling(false);

		if( explosion != null )
		    {
			explosion.setPosition
			    (getX()+getWidth()/2, getY()+getHeight()/2);
		    }
		return false;
	    }

	if( explosion != null )
	    {
		exploding = true;
		boolean t = explosion.drawNextFrame(update);
		if( !t ) 
		    {
			ei = explosion.getExplosionInfo();
			setExplosion(null);
			setAlive(false);
			owner.sendTankDead(getID());
		    }
		return t;
	    }

	drawTank(true);
	return false;
    }

    synchronized private void drawFallStep()
    {
	if( !alive || bitmap == null ) return; // hm..
	
	if( ox > 0 && oy > 0 )
	    {
		int tx, ty;
		tx = x; ty = y;
		x = ox; y = oy;
		hideFrame(false, false);
		
		x = tx; y = ty;
	    }
	
	drawTank(false);
	int tl = Math.max(getTurretL(), Parachute.pIcon.length);
	bitmap.newPixels(Math.min(x,ox)-tl, oy-tl, 
			 getWidth()+2*tl, getHeight()+2*tl);
	ox = x; oy = y;
    }

    // that's old-style drawShield method which doesn't affect Bitmap.
    public void drawShield(Graphics g)
    {
	//System.err.println("depricated method ScorchPlayer.drawShield()");
	if( shield == null || !alive )
	    return;
	
	int radius = (int)(2*Math.max(getWidth(), getHeight()));
	int i = Math.max(0, (int)(255*shield.getStrength()/
				  shield.getMaxStrength()));
	
	g.setColor(new Color(i,i,i));
	for(i = 0; i < shield.thickness; i++)
	    g.drawOval(x+getWidth()/2 - (radius+i)/2, 
		       y+getHeight()/2 - (radius+i)/2, radius+i, radius+i);
    }

    public void drawShield(boolean show, boolean update)
    {
	// if called from ScorchField, or if falling cancel
	if( true || shield == null || (falling && show) )
	    return; 

	int radius = (int)(Math.max(getWidth(), getHeight()));
	int i = Math.max(0, (int)(255*shield.getStrength()/
				  shield.getMaxStrength()));
	int xc = x+getWidth()/2, yc = y+getHeight()/2;
	
	bitmap.setSandMode(true);
	if( show ) 
	    bitmap.setColor(new Color(i,i,i));
	else
	    bitmap.setColor(null);

	for(i = 0; i < shield.thickness; i++)
	    bitmap.drawEllipse(xc, yc, radius+i, radius+i);
	
	bitmap.setSandMode(false);

	if( update ) 
	    bitmap.newPixels
		(xc-radius-shield.thickness,
		 yc-radius-shield.thickness, 
		 (radius+shield.thickness)*2, 
		 (radius+shield.thickness)*2);
    }

    synchronized public void drawTank(boolean update)
    {	
	if(!alive || bitmap == null)
	    return;

	bitmap.drawSprite(x, y, Tanks.getTank(tankType, color), 0);
	drawParachute(true);

	bitmap.setColor(getColor());
	drawTurret(update);

	if( shield != null && !falling )
	    drawShield(true, update);
	else
	    if( update ) bitmap.newPixels(x, y, getWidth(), getHeight());
    }

    synchronized private void drawParachute(boolean show)
    {
	int mx, my;

	if( parachuteOpen() )
	    {
		mx = x + (int)((getWidth() - Parachute.pIcon[0].length)/2);
		my = getTurretSY() - Parachute.pIcon.length;
		if( show )
		    bitmap.drawSprite(mx, my, Parachute.pIcon, 0);
		else
		    bitmap.hideSprite(mx, my, Parachute.pIcon, 0);
	    }
    }

    synchronized private void drawTurret(boolean update)
    {
	int x1 = x+Tanks.getTurretX(tankType), 
	    x2 = getTurretX(1), y1 = y+Tanks.getTurretY(tankType), 
	    y2 = getTurretY(1);

	bitmap.drawLine(x1, y1, x2, y2);
	if( update )
	    bitmap.newPixels(Math.min(x1, x2), Math.min(y1, y2),
			     Math.abs(x1-x2)+1, Math.abs(y1-y2)+1);
    }

    synchronized public void hideFrame(boolean update, boolean turretOnly)
    {
	bitmap.setColor(null);
	drawTurret(update);

	if( turretOnly )
	    return;

	bitmap.hideSprite(x, y, Tanks.getTank(tankType, color), 0);
	
	drawParachute(false);
	if( shield != null )
	    drawShield(false, update);
	else
	    if( update ) bitmap.newPixels(x, y, getWidth(), getHeight());
    }

    synchronized public int getTurretSX()
    {
	return x+Tanks.getTurretX(tankType);
    }

    synchronized public int getTurretSY()
    {
	return y+Tanks.getTurretY(tankType);
    }

    synchronized public int getTurretX(double q)
    {
	int length = getTurretL();
	double rangle = (double)angle * Math.PI / 180.0;
	return x + Tanks.getTurretX(tankType) +
	    (int)((double)length*q*Math.cos(rangle));
    }

    synchronized public int getTurretY(double q)
    {
	int length = getTurretL();
	double rangle = (double)angle * Math.PI / 180.0;
	return y + Tanks.getTurretY(tankType) - 
	    (int)((double)length*q*Math.sin(rangle));
    }

    synchronized public int getTurretL()
    {
	return Tanks.getTurretL(tankType);
    }

    private boolean parachuteOpen()
    {
	return (fallCount > 5 && parachutes > 0 && !isDying());
    }
   
    public String getName()
    {
	return profile.getName();
    }

    public long getCash()
    {
	return cash;
    }

    public long getBounty()
    {
	return cashBounty;
    }
    
    public long getEarnedCash()
    {
	return earnedCash;
    }
    
    public void setEarnedCash(long ec)
    {
	earnedCash = ec;
    }

    public void setCash(long nc)
    {
	cash = nc;
    }

    public void incCash(long nc)
    {
	cash += nc;
	earnedCash += nc;
    }

    public int getKills()
    {
	return kills;
    }
    
    private void incKills(int k)
    {
	kills+=k;
    }

    public int getID()
    {
	return ID;
    }

    public void setTankType(int type)
    {
	tankType = type;
    }

    public int getTankType()
    {
	return tankType;
    }

    /*public void setColor(int color)
    {
	this.color = color;
	}*/
   
    public int getAngle()
    {
	return angle;
    }

    public void setPower(int new_power)
    {
	power = new_power;
	
	if( power < 0 ) power = powerLimit;
	if( power > powerLimit ) power = 0;
    }

    public int incPower( int inc )
    {
	setPower(power + inc);

	return power;
    }

    // increment turret angle by inc degrees
    public int incAngle( int inc )
    {
	angle_change += inc;
	angle += inc;
	
	/*if(Math.abs(angle_change) > angle_step || 
	   angle < 0 || angle >= 180 || inc == 0)
	   {*/
	        angle -= angle_change;
		bitmap.setColor(null);
		drawTurret(true);
		//hideFrame(false, false);
		angle += angle_change;
		//angle_change = angle_step*2;
		/*}*/

	if( angle < 0) angle += 180;
	if( angle >= 180 ) angle %= 180;
	
	if(/*Math.abs(angle_change) > angle_step &&*/ !owner.GalslaMode) 
	    {
		// we need to draw tank, not just turret
		// because while hiding turret we might hide part of tank...
		drawTank(true); 
		bitmap.newPixels();
		angle_change = 0;
		owner.sendUpdate(this);
	    }
	
	//int tl = getTurretL();
	//bitmap.newPixels(x-tl, y-tl, getWidth()+2*tl, getHeight()+2*tl, true);

	return angle;
    }
    
    public void setAngle(int new_angle)
    {
	hideFrame(false, false);
	angle = new_angle;
	drawTank(false);

	int tl = getTurretL();
	bitmap.newPixels(x-tl, y-tl, getWidth()+2*tl, getHeight()+2*tl);
	angle_change = 0;
    }
    
    // calculate damage from the falling
    // for now as simple as 1 point for any fall step... 
    // must be changed, probably
    // if the parachute is open, no damage is inflicted
    private void fallingDamage()
    {
	drawParachute(false); // hide parachute if needed
	// dispose of the used parachute
	if( parachuteOpen() ) 
	    {
		parachutes--;
		((Parachute)items[Item.Parachute]).
		    decQuantity();
	    }
	else
	    {
		powerLimit -= fallCount;
		checkPower();
	    }

	// reset fall count after drawParachute but before 
	// setFalling() since setFalling() redraws tank (?)
	fallCount = -1; 
	setFalling(false);
    }

    // check that power is below powerlimit and update label if necessary
    private void checkPower()
    {
	if( power > powerLimit )
	    {
		power = powerLimit;
		owner.updatePowerLabel(this);
	    }
    }

    // decrease power limit by q*maxpower
    // return true if player is still alive after that
    public boolean decPowerLimit(int dec)
    {
	double d;

	if( shield != null )
	    {
		// if there is a shield make the damage //two times smaller
		// and then apply part of it to the shield and part of it
		// to the player.
		double q = (shield.damage*dec)/(maxPower);

		if( (d = shield.decStrength(q)) <= 0 )
		    {
			dec *= (1-shield.damage-d); // increase dec by d
			deactivateShield(); // no more shield
		    }
		else
		    dec *= (1-shield.damage); 
	    }

	powerLimit -= dec;
	
	Debug.println("new power limit for "+getName()+ " is "+powerLimit);
	
	if( powerLimit < minPower)	    
	    return false;
	else 
	    {
		checkPower(); 
		return true;
	    }
    }

    public int getPower()
    {
	return power;
    }

    public int getPowerLimit()
    {
	return powerLimit;
    }

    public void incPowerLimit(int inc)
    {
	powerLimit += inc;
	if( powerLimit < 0 ) powerLimit = 0;
	if( powerLimit > maxPower ) powerLimit = maxPower;
    }

    public int getWeapon()
    {
	return currentWeapon;
    }

    public Weapon[] getWeapons()
    {
	return weapons;
    }

    public Item[] getItems()
    {
	return items;
    }

    public int getWidth()
    {
	return Tanks.getTankWidth(tankType);
    }

    public int getHeight()
    {
	return Tanks.getTankHeight(tankType);
    }

    public int getColor()
    {
	return Tanks.getTankColor(color);
    }

    public PlayerProfile getProfile()
    {
	return profile;
    }

    public synchronized void setFalling(boolean fall)
    {
	if( falling == fall || exploding ) 
	    return; // if already falling or exploding -- ignore

	this.falling = fall;

	if( fall )
	    {
		ox = x;
		oy = y;
		fallCount = -1;
		//fallHeight = 0;
		fallDirection = -1;
		leftPos = x;
		rightPos = x;
		//hideFrame(true, false);
	    }
	else
	    drawFallStep();
    }

    public synchronized boolean isUsingFuel()
    {
	return usingFuel;
    }

    public synchronized void setUsingFuel(boolean fuel)
    {
	usingFuel = fuel;
    }
    
    public void resetAll()
    {
	firstTurn = true;
	setAlive(true);
	//setKills(0);
	//setEarnedCash(0);
	//deactivateShield();
	setExplosion(null); lastExplosion = null;

	Parachute p = (Parachute)items[Item.Parachute];
	p.active = false; parachutes = 0;

	Tracer t = (Tracer)items[Item.Tracer];
	t.active = false; tracers = 0;

	powerLimit = maxPower;
	power = startPower;
    }

    private void setAlive(boolean alive)
    {
	this.alive = alive;
	if( alive ) 
	    {
		setFalling(false);
		exploding = false;
	    }
	else
	    hideFrame(true, false);
    }

    public boolean isAlive()
    {
	return alive;
    }

    // if explosion is already set this tank is about to die.
    public boolean isDying()
    {
	return explosion != null;
    }

    public void setExplosion(Explosion expl)
    {
	if( expl != null )
	    expl.setPosition(getX()+getWidth()/2, getY()+getHeight()/2);
	else
	    lastExplosion = explosion;

	explosion = expl;
    }
    
    // calculate the damage caused by [this] instance of scorchplayer done
    // to the sp
    public int calculateDamage(ScorchPlayer sp)
    {
	if( lastExplosion != null )
	    return lastExplosion.calculateDamage(sp);
	else
	    return 0;
    }

    public ExplosionInfo getExplosionInfo()
    {
	if( explosion == null )
	    {
		ExplosionInfo r = ei; // reset EI to null and return it
		ei = null;
		return r;
	    }
	else
	    return explosion.getExplosionInfo();
    }

    public void useItem(int type, int arg)
    {
	switch( type )
	    {
	    case Item.Shield:
	    case Item.MediumShield:
	    case Item.HeavyShield:
		activateShield(type);
		break;
	    case Item.Parachute:
		activateParachute(arg);
		break;
	    case Item.Tracer:
		activateTracer(arg);
		break;
	    case Item.Battery:
		activateBattery(arg);
		break;
	    case Item.Fuel:
		activateFuel(arg);
		break;
	    default:
		System.err.println("ScorchPlayer.useItem(): illegal item "+
				   type);
	    }
    }

    private void activateShield(int type)
    {
	if( shield != null ) 
	    return;

	shield = (Shield)items[type];

	shield.decQuantity();

	shield.reset();
	drawTank(true);
	bitmap.newPixels();
    }

    // this should be private, but due to some debug issues...
    public void activateParachute(int num)
    {
	Parachute p = ((Parachute)items[Item.Parachute]);

	parachutes = num;

	// set local copy of parachute to the current state
	if( num > 0 && !p.active ) 
	    {
		p.active = true;
		p.setQuantity(num);
	    }
    }
    
    private void activateTracer(int num)
    {
	Tracer t = ((Tracer)items[Item.Tracer]);

	tracers = num;

	if( num > 0 && !t.active ) 
	    {
		t.active = true;
		t.setQuantity(num);
	    }
    }

    // used by ScorchApplet to show AutoDefense window when appropriate
    public boolean isFirstTurn()
    {
	if(!firstTurn) return false; 

	firstTurn = false;
	return true;
    }

    // checks if there is an autodefense available and 
    // decreases the number of ads left and return the result of the check
    public boolean useAutoDefense()
    {
	AutoDefense ad = (AutoDefense)items[Item.AutoDefense];
	boolean need = false; // AutoDefense needed? (i.e. are there any items)
	Item it;
	
	if( ad.getQuantity() > 0 )
	    {
		for(int i = 0; i < items.length && !need; i++)
		    {
			it = items[i];
			need = (it.getQuantity() > 0) && 
			    (it instanceof Parachute || it instanceof Shield);
		    }

		if( need )
		    {
			ad.decQuantity();
			return true;
		    }
	    }
	
	return false;
    }

    private void activateBattery(int power)
    {
	Battery battery = (Battery)items[Item.Battery];

	incPowerLimit(power);

	battery.decQuantity();
    }

    // dir = -1 for left, 1 for right
    private void activateFuel(final int dir)
    {
	final Fuel fuel = (Fuel)items[Item.Fuel];
	
	class fuelMove implements Runnable
	    {
		public fuelMove()
		{
		    Thread thread = new Thread(this, "fuel-thread");
		    thread.start();
		}
		
		public void run()
		{
		    int tx, ty;
	    
		    synchronized ( ScorchPlayer.this )
			{
			    ty = getY()+getHeight()-1;
			    tx = getX();
			    if ( dir == 1 )
				tx+=getWidth();
			    else
				tx-=1;
			    
			    // for now control quantity only for the 
			    // the local player
			    if( !bitmap.isBackground(tx+dir, ty-1) ||
				(owner.getMyPlayer() == ScorchPlayer.this && 
				 fuel.getQuantity() <= 0) )
				return;
			    
			    fuel.decQuantity();

			    if ( dir == 1 )
				tx-=Tanks.getRightBase(tankType);
			    else
				tx+=Tanks.getLeftBase(tankType);

			    hideFrame(true, false);
			    if( !bitmap.isBackground(tx, ty) )
				y--;
			    
			    x+=dir;
			    
			    setFalling(true);
			    setUsingFuel(true);
			    scorchField.runAnimationNow(ScorchPlayer.this);
			}
		}
	    }
	new fuelMove();
    }

    private void deactivateShield()
    {
	drawShield(false, true); // hide it
	shield = null;
	//drawTank(true);
    }

    public boolean checkTracer()
    {
	if( tracers > 0 )
	    {
		tracers--;
		((Tracer)items[Item.Tracer]).decQuantity();
		return true;
	    }
	else
	    return false;
    }
    
    public void setWeapon(int weapon_n)
    {
	currentWeapon = weapons[weapon_n].getType();
    }

    public void decWeapon()
    {
	weapons[currentWeapon].decQuantity();
    }

    public int getWeaponAmmo()
    {
	return weapons[currentWeapon].getQuantity();
    }
    
    public Explosion getWeaponExplosion(int weapon)
    {
	Explosion e = weapons[weapon].produceExplosion(bitmap, rand);
	return e;
    }

    public String toString()
    {
	String res = "name: "+getName()+" angle: "+getAngle()+" power: "+
	    getPower()+" powerLimit: "+ powerLimit;
	
	return res;
    }

    // return string to be displayed in tool tip
    public String getToolTip()
    {
	String res;
	double pv = ((double)powerLimit / maxPower*100);
	DecimalFormat nf = new DecimalFormat();
	nf.setMaximumFractionDigits(2);

	res = getName() + " " + nf.format(pv) + "%";
	if( shield != null ) 
	    {
		double sv = shield.getStrength()/shield.getMaxStrength()*100;
		res += " Shield "+ nf.format(sv) + "%";
	    }

	return res;
    }

    public void updateKills(long earnedCash, int kills)
    {
	profile.incOverallGain(earnedCash);
	profile.incOverallKills(kills);
	incCash(earnedCash);
	incKills(kills);
    }
}
