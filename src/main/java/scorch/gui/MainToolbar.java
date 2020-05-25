package scorch.gui;

/*
  Class:  MainToolbar
  Author: Mikhail Kruk

  Description: the main GUI class for the interaction with the user
  all the controls live here, also this class handles all the keyboard 
  input and then forwards it to appropriate destinations
*/

import java.awt.*;
import java.util.Vector;

import scorch.*;
import scorch.weapons.*;
import swindows.*;

public class MainToolbar extends sWindow 
{
    private final Label power;
	private final Label angle;
	private final Label ammo;
    private final PlayersListControl pl;
    private final Button up;
	private final Button down;
	private final Button left;
	private final Button right;
	private final Button fire;
    private final Button inventory;
	private final Button system;
    private final Choice weapon;
    private final ScorchPlayer myPlayer;
    private final SystemMenu sMenu;
    private Inventory wndInventory;

    public MainToolbar(Vector<ScorchPlayer> players, ScorchApplet owner)
    {
	super(owner.getGameWidth(), 0,
	      owner.getWidth()-owner.getGameWidth(), owner.getHeight(),
	      "SCORCH 2000", owner);

	this.myPlayer = owner.getMyPlayer();

	//addKeyListener(this);

	sMenu = new SystemMenu(owner);

	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints constraints;
	
	setLayout(layout);

	pl = new PlayersListControl(players, myPlayer, this);
	add(pl); 
	pl.setVisible(true);
	//pl.addKeyListener(this);
	constraints = makeConstraints
	    (0,0,1,8,GridBagConstraints.BOTH,
	     GridBagConstraints.CENTER, new Insets(0,5,0,5));
	constraints.weightx = 1000;
	constraints.weighty = 1000;
	layout.setConstraints(pl, constraints);
	
	system = new Button("System");
	//system.addKeyListener(this);
	add(system);
	constraints = makeConstraints
	    (0,9,1,1,GridBagConstraints.HORIZONTAL,
	     GridBagConstraints.CENTER,  new Insets(2,5,2,5));
	layout.setConstraints(system, constraints);


	inventory = new Button("Inventory");
	//inventory.addKeyListener(this);
	add(inventory);
	constraints = makeConstraints
	    (0,10,1,1,GridBagConstraints.HORIZONTAL, 
	     GridBagConstraints.CENTER,  new Insets(2,5,2,5));
	layout.setConstraints(inventory, constraints);

	weapon = new Choice();
	//weapon.addKeyListener(this);
	
	Weapon[] weapons = myPlayer.getWeapons();
	Weapon w;
		for (Weapon value : weapons) {
			w = value;
			if (w.getQuantity() > 0)
				weapon.addItem(w.getName());
		}

	add(weapon);
	constraints = makeConstraints
	    (0,11,1,1,GridBagConstraints.NONE, 
	     GridBagConstraints.CENTER, new Insets(2,5,2,5));
        layout.setConstraints(weapon, constraints);
	
	Panel pData = new Panel();
	power = new Label(myPlayer.getPower()+"", Label.LEFT);
	angle = new Label(myPlayer.getAngle()+"", Label.LEFT);
	ammo = new Label(""+myPlayer.getWeaponAmmo(), Label.LEFT);
	power.setForeground(Color.red);
	angle.setForeground(Color.red);
	ammo.setForeground(Color.red);
	pData.setLayout(new GridLayout(3,2,0,0));
	pData.add(new Label("Ammo:"));
	pData.add(ammo);
	pData.add(new Label("Power:"));
	pData.add(power);
	pData.add(new Label("Angle:"));
	pData.add(angle);
	add(pData);
	constraints = makeConstraints
	    (0,13,1,2,GridBagConstraints.HORIZONTAL,
	     GridBagConstraints.WEST, new Insets(0,5,0,5));
	layout.setConstraints(pData, constraints);

	Panel keys = new Panel();
	keys.setLayout(new GridLayout(2, 3, 5, 5));
	up = new Button("+");
	//up.addKeyListener(this);
	down = new Button("-");
	//down.addKeyListener(this);
	left = new Button("<");
	//left.addKeyListener(this);
	right = new Button(">");
	//right.addKeyListener(this);
	fire = new Button("fire");
	//fire.addKeyListener(this);
	keys.add(new Label(""));
	keys.add(up);
	keys.add(new Label(""));
	keys.add(left);
	keys.add(down);
	keys.add(right);
	
	add(keys);
	constraints = makeConstraints
	    (0,17,1,1,GridBagConstraints.HORIZONTAL,
	     GridBagConstraints.WEST, new Insets(2,5,2,5));
	layout.setConstraints(keys, constraints);
	
	add(fire);	
	constraints = makeConstraints
	    (0,18,1,1,GridBagConstraints.HORIZONTAL,
	     GridBagConstraints.WEST, new Insets(2,5,2,5));
	layout.setConstraints(fire, constraints);	

	updateWeapon();
	
	validate();
    }

    public void updateWeapon()
    {
	weapon.select(Weapon.getName(myPlayer.getWeapon()));
	ammo.setText(""+myPlayer.getWeaponAmmo());
    }
    /*
    public void keyPressed(KeyEvent evt)
    {
	if( !isEnabled() ) return;
	
	char key = evt.getKeyChar();
	int code = evt.getKeyCode();

	if( !evt.isActionKey() )
	    {
		if( (key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z') ||
		    (key >= '!' && key <= '@') )
		    {
			((ScorchApplet)owner).showChat(key);
			return;
		    }
	    }

	int scale = 1;
	if ( evt.isControlDown() ) scale = 5;

	if( code == KeyEvent.VK_F1 )
	    {
		((ScorchApplet)owner).displayReference();
		return;
	    }

	if( code == KeyEvent.VK_F2 )
	    {
		StatsWindow sw = new StatsWindow
		    (StatsWindow.IG,(ScorchApplet)owner);
		sw.display();
		return;
	    }

	if( code == KeyEvent.VK_F3 && !((ScorchApplet)owner).isGuest() )
	    {
		NewUser nu = 
		    new NewUser		
			(((ScorchApplet)owner).getMyPlayer().getName(), 
			 (ScorchApplet)owner, 
			 ((ScorchApplet)owner).getMyPlayer().
			 getProfile());
		nu.display();
		return;
	    }
	
	if( code == KeyEvent.VK_F4 )
	    {
		MessageBox msg;
		
		if( ((ScorchApplet)owner).isMaster() )
		    {
			String b[] = {"Yes", "Cancel"};
			String c[] = {"massKill", null};
			msg = new MessageBox
			    ("Confirmation", 
			     "Are you sure you want to kill everybody and start a new round?",
			     b, c, owner, this);
		    }
		else
		    {
			String b[] = {"OK"};
			String c[] = {null};
			msg = new MessageBox
			    ("Message", 
			     "You have to be the master of the game to masskill",
			     b, c, owner, this);
		    }
		msg.display();
		return;
	    }
	
	if( code == KeyEvent.VK_F5 && 
	    (wndInventory == null || !wndInventory.isVisible()) &&
	    inventory.isEnabled())
	    {
		wndInventory = new Inventory((ScorchApplet)owner);
		wndInventory.display();
		return;
	    }
	
	if( code == KeyEvent.VK_F10 && !sMenu.isVisible() )
	    {
		sMenu.display(); 
		return;
	    }

	// ignore all other events if it is not our turn
	if( !fire.isEnabled() ) 
	    return;

	if( code == KeyEvent.VK_LEFT)
	    {
		updateAngleLabel(((ScorchApplet)owner).changeAngle(scale));
		return;
	    }
	if( code == KeyEvent.VK_RIGHT )
	    {
		updateAngleLabel(((ScorchApplet)owner).changeAngle(-scale));
		return;
	    }
	if( code == KeyEvent.VK_HOME)
	    {
		updateAngleLabel(((ScorchApplet)owner).changeAngle(10));
		return;
	    }
	if( code == KeyEvent.VK_END )
	    {
		updateAngleLabel(((ScorchApplet)owner).changeAngle(-10));
		return;
	    }
	if( code == KeyEvent.VK_UP || key == '+' )
	    {
		updatePowerLabel(((ScorchApplet)owner).changePower(scale));
		return;
	    }
	if( code == KeyEvent.VK_DOWN || key == '-' )
	    {
		updatePowerLabel(((ScorchApplet)owner).changePower(-scale));
		return;
	    }
	if( code == KeyEvent.VK_PAGE_UP )
	    {
		updatePowerLabel(((ScorchApplet)owner).changePower(10));
		return;
	    }
	if( code == KeyEvent.VK_PAGE_DOWN )
	    {
		updatePowerLabel(((ScorchApplet)owner).changePower(-10));
		return;
	    }
	if( key == ' ' )
	    {
		enableKeys(false);
		((ScorchApplet)owner).fire();
		if(myPlayer.getWeaponAmmo() <= 0)
		    {
			weapon.remove(weapon.getSelectedIndex());
			myPlayer.setWeapon(0);
		    }
		ammo.setText(""+myPlayer.getWeaponAmmo());
		return;
	    }
    }
    
    public void keyReleased(KeyEvent evt) {}
    public void keyTyped(KeyEvent evt) {}

    public void actionPerformed(ActionEvent evt)
    {
	if( !isEnabled() ) return;

	String cmd = evt.getActionCommand();
    }    */

    public boolean handleEvent(Event evt)
    {
	if( !isEnabled() )
	    return super.handleEvent(evt);

		if( evt.id == Event.KEY_PRESS &&
	    ((evt.key >= 'A' && evt.key <= 'Z') || 
	     (evt.key >= 'a' && evt.key <= 'z') ||
	     (evt.key >= '!' && evt.key <= '@') ) )
	    {
		((ScorchApplet)owner).showChat((char)evt.key);
		return true;
	    }

	if( evt.id == Event.ACTION_EVENT )
	    {
		if( evt.target == weapon )
		    {
			((ScorchApplet)owner).setWeapon
			    (Weapon.getType(weapon.getSelectedItem()));
			ammo.setText(""+myPlayer.getWeaponAmmo());
			return true;
		    }
		if( evt.target == left )
		    {
			updateAngleLabel(((ScorchApplet)owner).changeAngle(3));
			return true;
		    }
		if( evt.target == right )
		    {
			updateAngleLabel
			    (((ScorchApplet)owner).changeAngle(-3));
			return true;
		    }
		if( evt.target == up )
                    {
			updatePowerLabel(((ScorchApplet)owner).changePower(5));
                        return true;
                    }
                if( evt.target == down )
                    {
			updatePowerLabel
			    (((ScorchApplet)owner).changePower(-5));
                        return true;
                    }
		if( evt.target == fire && fire.isEnabled() )
                    {
			enableKeys(false);
			((ScorchApplet)owner).fire();
			if(myPlayer.getWeaponAmmo() <= 0)
			    {
				weapon.remove(weapon.getSelectedIndex());
				myPlayer.setWeapon(0);
			    }
			ammo.setText(""+myPlayer.getWeaponAmmo());
			return true;
                    }
		if( evt.target == system && !sMenu.isVisible() )
		    {
			sMenu.display(); 
			return true;
		    }
		if( evt.target == inventory && 
		    (wndInventory == null || !wndInventory.isVisible()) &&
		    inventory.isEnabled() )
		    {
			//if( wndInventory == null )
			    wndInventory = new Inventory((ScorchApplet)owner);
			wndInventory.display();
			return true;
		    }

	    }

	if( evt.id == Event.KEY_ACTION || evt.id == Event.KEY_PRESS )
	    {
		int scale = 1;
		if ( evt.controlDown() ) scale = 5;

		if( evt.key == Event.F1 )
		    {
			((ScorchApplet)owner).displayReference();
			return true;
		    }

		if( evt.key == Event.F2 )
		    {
			StatsWindow sw =
			    new StatsWindow
				(StatsWindow.IG,(ScorchApplet)owner);
			sw.display();
			return true;
		    }

		if( evt.key == Event.F3 && !((ScorchApplet)owner).isGuest() )
		    {
			NewUser nu = 
			    new NewUser		
			       (((ScorchApplet)owner).getMyPlayer().getName(), 
				(ScorchApplet)owner, 
				((ScorchApplet)owner).getMyPlayer().
				getProfile());
			nu.display();
			return true;
		    }

		if( evt.key == Event.F4 )
		    {
			MessageBox msg;

			if( ((ScorchApplet)owner).isMaster() )
			    {
				String[] b = {"Yes", "Cancel"};
				String[] c = {"massKill", null};
				msg = new MessageBox
				    ("Confirmation", 
				     "Are you sure you want to kill everybody and start a new round?",
				     b, c, owner, this);
			    }
			else
			     {
				String[] b = {"OK"};
				String[] c = {null};
				msg = new MessageBox
				    ("Message", 
				     "You have to be the master of the game to masskill",
				     b, c, owner, this);
			    }
			msg.display();
			return true;
		    }

		if( evt.key == Event.F5 && 
		    (wndInventory == null || !wndInventory.isVisible()) &&
		    inventory.isEnabled())
		    {
			//if( wndInventory == null )
			    wndInventory = new Inventory((ScorchApplet)owner);
			wndInventory.display();
			return true;
		    }

		if( evt.key == Event.F8 )
		    {
			if( ((ScorchApplet)owner).isMaster() )
			    {
				BootBox bb = new BootBox((ScorchApplet)owner);
				bb.display();
			    }
			else
			    {
				MessageBox msg;
				String[] b = {"OK"};
				String[] c = {null};
				msg = new MessageBox
				    ("Message", 
				     "You have to be the master of the game to boot players",
				     b, c, owner, this);
				msg.display();
			    }
			return true;
		    }

		if( evt.key == Event.F10 && !sMenu.isVisible() )
		    {
			sMenu.display(); 
			return true;
		    }

		// ignore all other events if it is not our turn
		if( !fire.isEnabled() ) 
		    return super.handleEvent(evt);

		if( evt.key == Event.LEFT)
		    {
			updateAngleLabel
			    (((ScorchApplet)owner).changeAngle(scale));
			return true;
		    }
		if( evt.key == Event.RIGHT )
		    {
			updateAngleLabel
			    (((ScorchApplet)owner).changeAngle(-scale));
			return true;
		    }
		if( evt.key == Event.HOME)
		    {
			updateAngleLabel
			    (((ScorchApplet)owner).changeAngle(10));
			return true;
		    }
		if( evt.key == Event.END )
		    {
			updateAngleLabel
			    (((ScorchApplet)owner).changeAngle(-10));
			return true;
		    }
		if( evt.key == Event.UP || evt.key == '+' )
                    {
			updatePowerLabel
			    (((ScorchApplet)owner).changePower(scale));
                        return true;
                    }
                if( evt.key == Event.DOWN || evt.key == '-' )
                    {
			updatePowerLabel
                            (((ScorchApplet)owner).changePower(-scale));
                        return true;
                    }
		if( evt.key == Event.PGUP )
                    {
			updatePowerLabel
			    (((ScorchApplet)owner).changePower(10));
                        return true;
                    }
                if( evt.key == Event.PGDN )
                    {
			updatePowerLabel
                            (((ScorchApplet)owner).changePower(-10));
                        return true;
                    }
		if( evt.key == ' ' && fire.isEnabled() )
                    {
			enableKeys(false);
			((ScorchApplet)owner).fire();
			if(myPlayer.getWeaponAmmo() <= 0)
			    {
				weapon.remove(weapon.getSelectedIndex());
				myPlayer.setWeapon(0);
			    }
			ammo.setText(""+myPlayer.getWeaponAmmo());
			return true;
		    }
	    }

	return super.handleEvent(evt);
    }

    public PlayersLister getPlayersList()
    {
	return pl;
    }

    public void enableKeys(boolean enable)
    {
	if(!enable)
	    transferFocus();

	up.setEnabled(enable);
	down.setEnabled(enable);
	left.setEnabled(enable);
	right.setEnabled(enable);
	fire.setEnabled(enable);
	inventory.setEnabled(enable);
	weapon.setEnabled(enable);

	if( wndInventory != null )
	    wndInventory.close();
    }
    
    public boolean isBarEnabled()
    {
	return up.isEnabled();
    }

    public void hideMenu()
    {
	sMenu.close();
    }

    public void updatePowerLabel(int nv)
    {
	power.setText(nv+"");
    }

    public void updateAngleLabel(int nv)
    {
	angle.setText(nv+"");
    }
}
