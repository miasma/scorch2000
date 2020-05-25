package scorch.gui;

/*
  Class:  AutoDefenseWnd
  Author: Mikhail Kruk

  Desciption: similar to inventory, but used for autodefense only
*/

import java.awt.*;
import java.awt.event.*;

import scorch.*;
import scorch.items.*;

public class AutoDefenseWnd extends Inventory {
    public AutoDefenseWnd(ScorchApplet owner) {
        super(owner, "Auto Defense System");
    }

    protected Panel preparePanel(Item item) {
        if (item.autoDefense())
            return super.preparePanel(item);
        else
            return null;
    }

    public void actionPerformed(ActionEvent evt) {
        (applet).sendEOT("AutoDefense");
        close();
    }
}
