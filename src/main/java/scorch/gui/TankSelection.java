package scorch.gui;

/*
  Class:  TankSelection
  Author: Mikhail Kruk

  Desciption: TankSelection ui component used to select tank type for each 
  player
*/

import java.awt.*;

import scorch.*;
import scorch.backgrounds.PlainBackground;
import swindows.*;

public class TankSelection extends sPanel {
    final TankSelectionControl control;

    public TankSelection(int w, int h) {
        super(w, h);
        control = new TankSelectionControl(w, h);
        add(control);
    }

    public int getSelected() {
        return control.getSelected();
    }

    public void setSelected(int s) {
        control.setSelected(s);
    }

    public void paint(Graphics g) {
        width = getSize().width;
        height = getSize().height;
        super.paint(g);
    }
}

class TankSelectionControl extends Panel {
    private static final int tankBorder = 3;

    private Bitmap bitmap = null;
    private final int width;
    private final int height;
    private Image backBuffer;
    private int selectedTank = 0;

    public TankSelectionControl(int w, int h) {
        setLayout(null);
        setSize(w, h);
        width = w;
        height = h;
    }

    public void paint(Graphics g) {
        update(g);
    }

    public boolean handleEvent(Event evt) {
        int i, x, y;

        if (bitmap == null)
            return false;

        if (evt.id == Event.KEY_ACTION &&
                (evt.key == Event.LEFT || evt.key == Event.RIGHT)) {
            drawBorder(selectedTank, false);
            if (evt.key == Event.LEFT)
                selectedTank--;
            else
                selectedTank++;
            if (selectedTank < 0) selectedTank = Tanks.tankNum - 1;
            if (selectedTank > Tanks.tankNum - 1) selectedTank = 0;
            drawBorder(selectedTank, true);
        }

        if (evt.id == Event.MOUSE_DOWN || evt.id == Event.MOUSE_UP ||
                evt.id == Event.MOUSE_DRAG) {
            x = evt.x;

            i = ((x - (width / (Tanks.tankNum + 1)) / 2) /
                    (width / (Tanks.tankNum + 1)));

            if (i < 0 || i >= Tanks.tankNum)
                return true;

            if (Math.abs((i + 1) * (width / (Tanks.tankNum + 1)) - x) <=
                    (Tanks.getTankWidth(i) / 2 + tankBorder)
                    && (i != selectedTank)) {
                drawBorder(selectedTank, false);
                selectedTank = i;
                drawBorder(selectedTank, true);
            }
        }

        return super.handleEvent(evt);
    }

    public void update(Graphics g) {
        if (bitmap == null) {
            bitmap =
                    new Bitmap(width, height, new PlainBackground
                            (width, height, Color.darkGray));
            backBuffer = createImage(bitmap.getImageProducer());
            redraw();
        }

        g.drawImage(backBuffer, 0, 0, this);
    }

    private void redraw() {
        bitmap.setColor(null);
        bitmap.fillRect(0, 0, width, height);

        for (int i = 0; i < Tanks.tankNum; i++) {
            if (i == selectedTank) {
                drawBorder(i, true);
            }

            bitmap.drawSprite
                    (width / (Tanks.tankNum + 1) * (i + 1) -
                                    Tanks.getTankWidth(i) / 2,
                            (height - Tanks.getTankHeight(i)) / 2,
                            Tanks.getTank(i, -1), 0);
        }
        bitmap.newPixels(0, 0, width, height, true);
    }

    private void drawBorder(int tank, boolean show) {
        int tw = Tanks.getTankWidth(tank),
                th = Tanks.getTankHeight(tank),
                bx = width / (Tanks.tankNum + 1) * (tank + 1) - tw / 2 - tankBorder,
                by = (height - th) / 2 - tankBorder;

        if (show)
            bitmap.setColor(Color.lightGray);
        else
            bitmap.setColor(null);

        bitmap.drawRect(bx, by, tw + 2 * tankBorder, th + 2 * tankBorder);

        if (show)
            bitmap.setColor(Color.gray);
        else
            bitmap.setColor(null);

        bitmap.drawLine(bx, by + th + 2 * tankBorder,
                bx + tw + 2 * tankBorder, by + th + 2 * tankBorder);
        bitmap.drawLine(bx + tw + 2 * tankBorder, by, bx + tw + 2 * tankBorder,
                by + th + 2 * tankBorder);
        bitmap.newPixels(bx, by, tw + 2 * tankBorder + 1, th + 2 * tankBorder + 1, true);
    }

    public int getSelected() {
        return selectedTank;
    }

    public void setSelected(int s) {
        selectedTank = s;
    }

}
