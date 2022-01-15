package scorch.items;

/*
  Class:  Item
  Author: Mikhail Kruk
  
  Description: the super class of all the items (shield, fuel etc)
*/

import java.util.*;
import java.awt.*;

import scorch.*;

public abstract class Item {

    public static final int Shield = 0, MediumShield = 1, HeavyShield = 2,
            Parachute = 3, Battery = 4, Tracer = 5, AutoDefense = 6, Fuel = 7;

    private static final String[] names = {
            "Shield", "Medium Shield", "Heavy Shield",
            "Parachute", "Battery", "Tracer", "Auto Defense", "Fuel"
    };

    static java.util.List<Item> generateItems() {
        return Arrays.asList(
                new Shield(),
                new MediumShield(),
                new HeavyShield(),
                new Parachute(),
                new Battery(),
                new Tracer(),
                new AutoDefense(),
                new Fuel()
        );
    }

    protected long price = 0; // 0 means that this item can't be bought
    protected int bundle = 1; // the number of items you buy at once
    protected int maxQuantity = 0; // 0 means unlimited
    protected int type = -1;

    protected int quantity;

    // whether to make this item availble through the autodefense
    protected boolean autoDefenseAv = false;

    protected Label lQuantity = null;

    protected ItemControl controlPanel = null;
    protected ScorchPlayer scorchPlayer;

    public Item() {
        quantity = 0;
    }

    public String getName() {
        return names[type];
    }

    public int getType() {
        return type;
    }

    public long getPrice() {
        return price;
    }

    public int getBundle() {
        return bundle;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    // return -1 if there is no limit, or max # that the user can carry
    public int getMaxOrder() {
        return maxQuantity == 0 ? -1 : maxQuantity - quantity;
    }

    public void incQuantity(int inc) {
        setQuantity(quantity + inc);
    }

    public void decQuantity() {
        setQuantity(quantity - 1);
    }

    public void setQuantity(int q) {
        if (q > maxQuantity && maxQuantity > 0) {
            System.err.println("maxQuantity excceded in Item.setQuantity");
            return;
        }

        quantity = q;
        if (lQuantity == null)
            lQuantity = new Label(quantity + "", Label.CENTER);
        else
            lQuantity.setText(quantity + "");

        if (controlPanel != null) {
            controlPanel.setEnabled(quantity > 0);
            controlPanel.validate();
        }
    }

    public Label getQuantityLabel() {
        if (lQuantity == null)    // if label not created yet create it now
            setQuantity(quantity);

        return lQuantity;
    }

    public boolean autoDefense() {
        return autoDefenseAv;
    }

    // create a vector with all available items
    public static Item[] loadItems(ScorchPlayer sp) {
        java.util.List<Item> iList = generateItems();
        for (Item i : iList) i.scorchPlayer = sp;

        return iList.toArray(new Item[]{});
    }

    // each item and weapons must create control panel which
    // will be then displayed in the Invetory box, save it in
    // the controlPanel field and return it
    abstract public ItemControl getControlPanel(ScorchApplet owner);

    public static String trimSpaces(String pName, String fullName) {
        StringBuilder name = new StringBuilder(pName);

        StringTokenizer st = new StringTokenizer(fullName, " ");
        while (st.hasMoreTokens()) name.append(st.nextToken());

        return name.toString();
    }
}
