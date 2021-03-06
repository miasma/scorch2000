package scorch.gui;

/*
  Class:  Inventory
  Author: Mikhail Kruk

  Desciption: Shows the list of weapons and items and allows to select 
  and activate them
*/

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import scorch.*;
import scorch.weapons.*;
import scorch.items.*;
import swindows.*;

public class Inventory extends sWindow implements ActionListener, KeyListener {
    protected final ScorchPlayer myPlayer;

    protected final ScorchApplet applet;

    public Inventory(ScorchApplet owner) {
        this(owner, "Inventory");
    }

    public Inventory(ScorchApplet owner, String title) {
        super(-1, -1, 0, 0, title, owner);
        applet = owner;

        myPlayer = owner.getMyPlayer();

        Vector<Panel> panels = new Vector<>();

        loadPanels(panels);

        Panel b = new Panel(new FlowLayout(FlowLayout.CENTER));
        Button bOK = new Button("OK");
        b.add(bOK);

        bOK.addActionListener(this);
        addKeyListener(this);

        // take max number of rows we can display
        // assume that Inventory box should be <= 70% of the applet
        Panel t = new Panel(new FlowLayout());
        t.add(panels.elementAt(0));
        t.setVisible(false);
        owner.add(t);
        t.validate();

        int rows = (int) (owner.getHeight() * 0.7) /
                panels.elementAt(0).getSize().height;

        owner.remove(t);
        t.removeAll();

        sScrollPanel scp = new sScrollPanel(this, -1, -1, panels, rows);

        setLayout(new BorderLayout(0, 0));
        //add(spnl,BorderLayout.CENTER);
        add(scp, BorderLayout.CENTER);
        add(b, BorderLayout.SOUTH);
    }

    protected void loadPanels(Vector<Panel> panels) {
        Weapon[] weapons = myPlayer.getWeapons();
        Item[] items = myPlayer.getItems();

        loadPanels(panels, weapons);
        loadPanels(panels, items);
    }

    // build a list of panels which correspond to items vector
    protected void loadPanels(Vector<Panel> panels, Item[] items) {
        Panel pp;

        for (int i = 0; items != null && i < items.length; i++) {
            pp = preparePanel(items[i]);
            if (pp != null)
                panels.addElement(pp);
        }
    }

    // prepare panel to be inserted into the inventory
    // if item should not be inserted, return null
    protected Panel preparePanel(Item item) {
        Panel pp;

        if (item.getQuantity() == 0)
            return null;

        pp = new Panel(new GridLayout(1, 3, 0, 0));

        pp.add(new Label(item.getName(), Label.CENTER));
        pp.add(item.getQuantityLabel());
        pp.add(item.getControlPanel(applet));

        return pp;
    }

    public void close() {
        super.close();
        transferFocus();
    }

    public void actionPerformed(ActionEvent evt) {
        close();
    }

    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
            close();
    }

    public void keyReleased(KeyEvent evt) {
    }

    public void keyTyped(KeyEvent evt) {
    }

}
