package scorch.gui;

/*
  Class:  SystemMenu
  Author: Mikhail Kruk

  Description: this class provides system menu GUI functionality
*/

import java.awt.*;

import scorch.ScorchApplet;
import swindows.*;

public class SystemMenu extends sWindow {
    private final Button mkill;

    protected final ScorchApplet applet;

    public SystemMenu(ScorchApplet owner) {
        super(-1, -1, 0, 0, "System Menu", owner);
        applet = owner;

        Panel p = new Panel();
        p.setLayout(new GridLayout(9, 1, 0, 5));
        p.add(new Button("Statistics"));
        Button topTen = new Button("Top 10 players");
        topTen.setEnabled(true);
        p.add(topTen);
        mkill = new Button("Mass kill");
        mkill.setEnabled(owner.isMaster());
        p.add(mkill);
        Button editProfile = new Button("Edit profile");
        editProfile.setEnabled(!owner.isGuest());
        p.add(editProfile);
        Button deleteProfile = new Button("Delete profile");
        deleteProfile.setEnabled(false && !owner.isGuest());
        p.add(deleteProfile);
        p.add(new Button("About Scorch"));
        p.add(new Button("On-line help"));
        p.add(new Button("Leave Scorch"));
        p.add(new Button("Close this menu"));

        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(p);

        validate();
    }

    public boolean handleEvent(Event evt) {
        if (evt.id == Event.KEY_PRESS) {
            if (evt.key == Event.ESCAPE) {
                close();
                return true;
            } else
                return super.handleEvent(evt);
        }

        if (evt.id == Event.ACTION_EVENT) {
            if (evt.arg.equals("On-line help")) {
                //close();
                (applet).showHelp();
                return true;
            }
            if (evt.arg.equals("Edit profile")) {
                //close();
                NewUser nu =
                        new NewUser
                                ((applet).getMyPlayer().getName(),
                                        applet,
                                        (applet).getMyPlayer().
                                                getProfile());
                nu.display();
                return true;
            }
            if (evt.arg.equals("Statistics")) {
                //close();
                StatsWindow sw =
                        new StatsWindow(StatsWindow.IG, applet);
                sw.display();
                return true;
            }
            if (evt.arg.equals("Top 10 players")) {
                //close();
                (applet).requestTopTen();
                return true;
            }
            if (evt.arg.equals("About Scorch")) {
                //close();
                AboutBox ab = new AboutBox(applet);
                ab.display();
                return true;
            }
            if (evt.arg.equals("Leave Scorch")) {
                (applet).Quit();
                return true;
            }
            if (evt.arg.equals("Close this menu")) {
                close();
                return true;
            }
            if (evt.arg.equals("Mass kill")) {
                close();

                String[] b = {"Yes", "Cancel"};
                String[] c = {"massKill", null};
                MessageBox msg = new MessageBox
                        ("Confirmation", "Are you sure you want to kill everybody and start a new round?",
                                b, c, owner, this);
                msg.display();
            }
        }
        return super.handleEvent(evt);
    }

    public void display() {
        if ((applet).isMassKilled())
            mkill.setEnabled(false);
        super.display();
    }
}
