package scorch.items;

/*
  Class:  Battery
  Author: Mikhail Kruk
  Description: Batteries are items which restore player's power
*/

import java.awt.*;
import java.awt.event.*;

import scorch.ScorchApplet;

public class Battery extends Item {
    public static final int power = 100;

    public Battery() {
        type = Battery;
        price = 4500;
    }

    public ItemControl getControlPanel(ScorchApplet owner) {
        controlPanel = new BatteryControl(this, owner);
        return controlPanel;
    }
}

class BatteryControl extends ItemControl implements ActionListener {
    public BatteryControl(Battery b, ScorchApplet owner) {
        super(b, owner);

        Button button = new Button("Install");
        control = button;
        button.addActionListener(this);

        setLayout(new FlowLayout());
        add(control);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("Install")) {
            if (item.getQuantity() > 0)
                owner.useItem(Item.Battery, Battery.power);
        }

    }
}
