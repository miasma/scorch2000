package scorch.gui;

/*
  Class:  ShopWindow
  Author: Mikhail Kruk
  Desciption: the ScorchShop window which allows users to buy items and 
  weapons for the next rounds
*/

import java.awt.*;
import java.awt.event.*;

import scorch.*;
import scorch.weapons.*;
import scorch.items.*;
import swindows.*;

public class ShopWindow extends sWindow implements ActionListener,
						   KeyListener
{
    private final Button finish;
	private final Button cancel;
	private final Button leave;
    private final ScorchPlayer myPlayer;
    private long cash;
    private final Label lCash;
    private final ShopItem[] shopItems;

    public ShopWindow(int w, int h, ScorchApplet owner)
    {
	super(-1,-1,w,h,"Scorched Earth Shop", owner);
   
	myPlayer = owner.getMyPlayer();
	Weapon[] weapons = myPlayer.getWeapons();
	Item[] items = myPlayer.getItems();
	int count = 0;
	
	if( weapons != null ) count += weapons.length;
	if( items != null ) count += items.length;

	cash = myPlayer.getCash();

	Panel itemsPanel = new Panel(new GridLayout(count,1,0,4)),
	    mainPanel = new Panel(new BorderLayout(0,0));
		
	shopItems = new ShopItem[weapons.length+items.length];
	
	loadShopItems(itemsPanel, 0, weapons);
	loadShopItems(itemsPanel, weapons.length, items);

	Panel legend = new Panel(new GridLayout(1,5));

	legend.add(new Label("item", Label.CENTER));
	legend.add(new Label("price", Label.CENTER));
	legend.add(new Label("amount", Label.CENTER));
	legend.add(new Label("order", Label.CENTER));
	legend.add(new Label(""));

        ScrollPane scrollPane = new ScrollPane();
	
	mainPanel.add(legend, BorderLayout.NORTH);
	mainPanel.add(itemsPanel, BorderLayout.CENTER);
	
	scrollPane.add(mainPanel);

	finish = new Button("Confirm order");
	cancel = new Button("Cancel order");
	leave = new Button("Leave the game");

	finish.addActionListener(this);
	cancel.addActionListener(this);
	leave.addActionListener(this);

	Panel bp = new Panel();
	bp.add(finish);
	bp.add(cancel);
	bp.add(leave);

	lCash = new Label("", Label.CENTER);
	updateCash(0);

	setLayout(new BorderLayout(0,0));
	add(lCash, BorderLayout.NORTH);
	add(scrollPane, BorderLayout.CENTER);
	add(bp, BorderLayout.SOUTH);

	addKeyListener(this);
    }
    
    // load shopItems panels corresponding to the items array
    // into to the Panel pItems and also make a copy [reference] to them
    // from the list shopItems
    private void loadShopItems(Panel pItems, int offset, Item[] items)
    {
	for(int i = 0; items != null && i < items.length; i++)
	    {
		ShopItem si = new ShopItem(this, items[i]);
		pItems.add(si);
		shopItems[offset+i] = si;
	    }
    }
    
    // change amount of cash and update the label in the window
    public void updateCash(long inc)
    {
	cash += inc;
	lCash.setText("Please buy weapons and items for the next round. "+
		      "You have $"+cash+" left");

		for (ShopItem shopItem : shopItems) shopItem.updateButtons();
    }

    public void display()
    {
	super.display();
	center();
    }

    public void actionPerformed(ActionEvent evt)
    {
	Object source = evt.getSource();

	if( source == finish )
	    {
		confirm();
		return;
	    }
	if( source == cancel )
	    {
		close();
		((ScorchApplet)owner).startGame();
	    }
	if( source == leave )
	    {
		close();
		((ScorchApplet)owner).Quit();
		}
    }

    private void confirm()
    {
	close();
		for (ShopItem shopItem : shopItems) shopItem.confirmOrder();
	myPlayer.setCash(cash);
	((ScorchApplet)owner).startGame();
    }

    public void keyPressed(KeyEvent evt)
    {
	if( evt.getKeyCode() == KeyEvent.VK_ESCAPE )
	    {
		close();
		((ScorchApplet)owner).startGame();
	    }

	if( evt.getKeyCode() == KeyEvent.VK_ENTER )
	    {
		confirm();
		((ScorchApplet)owner).startGame();
	    }
    }

    public void keyReleased(KeyEvent evt) {}
    public void keyTyped(KeyEvent evt) {}
    
    public boolean hasCash(long price)
    {
	return cash >= price;
    }
}

class ShopItem extends Panel implements ActionListener
{
    private final long price;
    private int order = 0;
	private final int maxOrder;
    private final Item item;
    private final Label lOrder;
    private final Button add;
	private final Button remove;
    private final ShopWindow owner;

    public ShopItem(ShopWindow owner, Item item)
    {
	super(new GridLayout(1,5));

	this.item = item;
	this.owner = owner;
        String name = item.getName();
	this.price = item.getPrice();
	this.maxOrder = item.getMaxOrder();

	add(new Label(name, Label.CENTER));
	Panel t2 = new Panel(new FlowLayout(FlowLayout.CENTER,4,0));

	add = new Button("add");
	remove = new Button("remove");
	
	add.addActionListener(this);
	remove.addActionListener(this);

	t2.add(add);
	t2.add(remove);
	
	lOrder = new Label("0", Label.CENTER);
        Label lPrice = new Label(price + "", Label.CENTER);
        Label lQuantity = item.getQuantityLabel();
	add(lPrice);
	add(lQuantity);
	add(lOrder);
	add(t2);
    }

    // make sure that only affordable items are enabled
    // also disable items the max capacity for which is reached
    public void updateButtons()
    {
	add.setEnabled( price > 0 && 
			owner.hasCash(price) && 
			(order <= maxOrder-item.getBundle() || 
			 maxOrder == -1));
	remove.setEnabled( price > 0 && order > 0 );
    }

    public void confirmOrder()
    {
	item.incQuantity(order);
    }
    
    public void actionPerformed(ActionEvent evt)
    {
	Object source = evt.getSource();

	if( source == add && add.isEnabled() )
	    {
		order+=item.getBundle();
		owner.updateCash(-price);
		lOrder.setText(""+order);
		return;
	    }
	if( source == remove && remove.isEnabled() )
	    {
		order-=item.getBundle();
		owner.updateCash(price);
		lOrder.setText(""+order);
		}
    }
}
