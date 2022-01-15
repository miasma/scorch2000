package scorch.weapons;

/*
  Class:  Weapon
  Author: Mikhail Kruk
  Description: the class which is inherited by all the scorch weapons
  used to store explosions, prices, names etc
  Refer to README.weapon for brief descirption of weapon adding procedure
*/

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import scorch.*;
import scorch.items.*;

public abstract class Weapon extends Item {
    public static final int Missile = 0,
            BabyNuke = 1, Nuke = 2,
            SandBomb = 3, BabyRoller = 4, Roller = 5, HeavyRoller = 6,
            BabyDigger = 7, Digger = 8, HeavyDigger = 9,
            FunkyBomb = 10, FunkyNuke = 11,
            Napalm = 12, HotNapalm = 13, MIRV = 14, DeathHead = 15;

    private static final String[] names = {
            "Missile", "Baby Nuke", "Nuke",
            "Sand Bomb",
            "Baby Roller", "Roller", "Heavy Roller",
            "Baby Digger", "Digger", "Heavy Digger",
            "Funky Bomb", "Funky Nuke",
            "Napalm", "Hot Napalm",
            "MIRV", "Death Head"
    };

    static java.util.List<Weapon> generateWeapons() {
        return Arrays.asList(
                new Missile(),
                new BabyNuke(),
                new Nuke(),
                new SandBomb(),
                new BabyRoller(),
                new Roller(),
                new HeavyRoller(),
                new BabyDigger(),
                new Digger(),
                new HeavyDigger(),
                new FunkyBomb(),
                new FunkyNuke(),
                new Napalm(),
                new HotNapalm(),
                new MIRV(),
                new DeathHead()
        );
    }

    protected final Supplier<Explosion> explosionClass;
    protected int argument = -1;

    // get name of this instance of weapon
    public String getName() {
        return names[type];
    }

    // get weapon name by type
    public static String getName(int type) {
        return names[type];
    }

    // get type of the weapons buy its name
    public static int getType(String name) {
        for (int i = 0; i < names.length; i++)
            if (names[i].equals(name))
                return i;
        return -1;
    }

    public Weapon(Supplier<Explosion> explosionClass) {
        this.explosionClass = explosionClass;
    }

    public ItemControl getControlPanel(ScorchApplet owner) {
        controlPanel = new WeaponControl(type, owner);
        return controlPanel;
    }

    public Explosion produceExplosion(Bitmap bitmap, Random rand) {
        try {
            // for Netscape again
            Explosion expl = explosionClass.get();

            expl.setBitmap(bitmap);
            //expl.setPosition(0,0);
            expl.setRand(rand);
            expl.setArgument(argument);
            return expl;
        } catch (Exception e) {
            System.err.println("produceExplosion: " + e);
        }

        return null;
    }

    // produce an array of all avaiable weapons. relies on the list of 
    // class names names[]. For now name of the weapons == name of the class
    // (spaces in names are omitted)
    public static Weapon[] loadWeapons(ScorchPlayer sp) {
        java.util.List<Weapon> wList = generateWeapons();
        for (Weapon w : wList) w.scorchPlayer = sp;

        return wList.toArray(new Weapon[]{});
    }
}

// this class is a panel displayed in the inventory window to control the
// weapon. For most of them (all?) it should be just "select" button
class WeaponControl extends ItemControl implements ActionListener {
    protected final int type;

    public WeaponControl(int type, ScorchApplet owner) {
        super(null, owner);
        this.type = type;

        Button button = new Button("Select");
        control = button;
        button.addActionListener(this);

        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(control);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("Select")) {
            owner.selectWeapon(type);
        }
    }
}
