package scorch.gui;

/*
  Class:  FuelBox
  Author: Mikhail Kruk

  Description: the window that popups when user uses the fuel item to move tank
*/

import java.awt.*;
import java.awt.event.*;

import scorch.*;
import scorch.items.*;
import swindows.*;

public class FuelBox extends sWindow implements ActionListener {
    private final Fuel fuel;
    private final ScorchPlayer player;

    private final Dimension od;

    protected final ScorchApplet applet;

    public FuelBox(ScorchApplet owner) {
        super(0, 0, 0, 0, "Fuel", owner);
        applet = owner;

        this.od = owner.getSize();

        this.player = owner.getMyPlayer();
        this.fuel = (Fuel) player.getItems()[Item.Fuel];

        sGauge fuelGauge = new
                sGauge(fuel.getQuantity(), fuel.getMaxQuantity(), owner);

        fuel.setGauge(fuelGauge);

        Button btnL, btnR, btnOK;

        Panel p = new Panel(new FlowLayout());
        btnL = new Button("<");
        btnR = new Button(">");
        btnOK = new Button("OK");
        p.add(btnL);
        p.add(btnOK);
        p.add(btnR);
        btnL.addActionListener(this);
        btnR.addActionListener(this);
        btnOK.addActionListener(this);

        setLayout(new BorderLayout());
        add(new Label("Use fuel to move your tank", Label.CENTER),
                BorderLayout.NORTH);
        add(fuelGauge, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        validate();
    }

    private void move(int dir) {
        if (fuel.getQuantity() <= 0) {
            //close();
            String[] b = {"OK"};
            String[] c = {null};
            MessageBox msg = new MessageBox
                    ("Message",
                            "You are out of fuel!",
                            b, c, owner, this);
            msg.display();
        }

        (applet).useItem(Item.Fuel, dir);

        adjustPos();
    }

    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "OK:":
                close();
                return;
            case "<":
                move(-1);
                return;
            case ">":
                move(1);
        }
    }

    public boolean handleEvent(Event evt) {
        if (evt.id == Event.KEY_PRESS) {
            if (evt.key == Event.ESCAPE) {
                close();
                return true;
            }
        }

        if (evt.id == Event.KEY_ACTION) {
            if (evt.key == Event.LEFT) {
                move(-1);
                return true;
            }
            if (evt.key == Event.RIGHT) {
                move(1);
                return true;
            }
        }

        return super.handleEvent(evt);
    }

    private void adjustPos() {
        int wx, wy;

        wx = (int) (player.getX() < od.width / 2 ?
                od.width * 3.0 / 4.0 - width / 2.0 :
                od.width / 4.0 - width / 2.0);
        wy = (int) (player.getY() < od.height / 2 ?
                od.height * 3.0 / 4.0 - height / 2.0 :
                od.height / 4.0 - height / 2.0);

        if (wx != x && wy != y) {
            setLocation(wx, wy);
            x = wx;
            y = wy;
        }
    }

    protected void place() {
        super.place();
        adjustPos();
    }

    public void close() {
        super.close();
        fuel.setGauge(null);

        // we want to synchronize on the player, because the animations are
        // run on it, and we don't want to return contro to UI before all
        // animations are finished
        synchronized ((applet).getMyPlayer()) {
            (applet).closeFuelWindow();
        }
    }
}
