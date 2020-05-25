/*
  Class:  ItemControl
  Author: Mikhail Kruk

  Description: 
*/

package scorch.items;

import java.awt.*;

import scorch.ScorchApplet;

public abstract class ItemControl extends Container {
    protected final ScorchApplet owner;
    protected final Item item;
    protected Component control;

    public ItemControl(Item item, ScorchApplet owner) {
        super();

        this.item = item;
        this.owner = owner;
    }

    public void setEnabled(boolean v) {
        if (control != null)
            control.setEnabled(v);
        else
            System.err.println("ItemControl.setEnabled(): internal error");
    }
}
