package swindows;

/*
  Class:  MessageBox
  Author: Mikhail Kruk
  Description: generic tool for providing small dialog boxes with a few
  buttons; callbacks can be supplied to the constructor and associated with
  buttons. 
*/

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

public class MessageBox extends sWindow implements ActionListener {
    protected final String[] buttons;
    protected final String[] callbacks;
    protected final Object[] args;
    protected Container peer;
    protected final int textAlign;

    public MessageBox(String title, String message,
                      String[] buttons, String[] callbacks,
                      Container owner, Container peer) {
        this(title, message, buttons, callbacks, owner);
        this.peer = peer;

        peer.setEnabled(false);
    }

    public MessageBox(String title, String message,
                      String[] buttons, String[] callbacks,
                      Container owner) {
        this(title, message, buttons, callbacks, null,
                Label.CENTER, owner);
    }

    public MessageBox(String title, String message,
                      String[] buttons, String[] callbacks,
                      int textAlign,
                      Container owner) {
        this(title, message, buttons, callbacks, null,
                textAlign, owner);
    }

    public MessageBox(String title, String message,
                      String[] buttons, String[] callbacks, Object[] args,
                      Container owner) {
        this(title, message, buttons, callbacks, args, Label.CENTER, owner);
    }

    public MessageBox(String title, String message,
                      String[] buttons, String[] callbacks, Object[] args,
                      int textAlign,
                      Container owner) {
        super(-1, -1, 0, 0, title, owner);

        this.buttons = buttons;
        this.callbacks = callbacks;
        this.args = args;
        this.textAlign = textAlign;

        Panel pb = new Panel();
        pb.setLayout(new FlowLayout(FlowLayout.CENTER));

        for (String button : buttons) {
            Button tb = new Button(button);
            pb.add(tb);
            tb.addActionListener(this);
        }

        /* build the text (multiline) panel */
        Panel pl = new Panel();
        StringTokenizer st = new StringTokenizer(message, "" + '\n');
        pl.setLayout(new GridLayout(st.countTokens(), 1, 0, 0));
        while (st.hasMoreTokens())
            pl.add(new Label(st.nextToken(), textAlign));

        // this will put text into a nice 3d box. May become option one day
        //sPanel spl = new sPanel(-1,-1);
        //spl.add(pl);

        setLayout(new BorderLayout(0, 0));
        add(pl, BorderLayout.CENTER);
        if (buttons.length > 0)
            add(pb, BorderLayout.SOUTH);

        validate();
    }

    public void close() {
        // if the box was modal, enable other windows
        if (peer != null)
            peer.setEnabled(true);
        super.close();
    }

    public void actionPerformed(ActionEvent evt) {
        Method m;
        String cmd = evt.getActionCommand();

        if (peer != null)
            peer.setEnabled(true);

        if (callbacks == null) {
            close();
            return;
        }
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].equals(cmd)) {
                close();
                if (callbacks[i] == null) return;
                try {
                    Class<?>[] type;
                    Object[] arg;
                    if (args == null || args[i] == null) {
                        type = new Class[0];
                        arg = new Object[0];
                    } else {
                        type = new Class[1];
                        arg = new Object[1];
                        type[0] = args[i].getClass();
                        arg[0] = args[i];
                    }
                    m = owner.getClass().getMethod
                            (callbacks[i], type);
                    m.invoke(owner, arg);
                } catch (Throwable t) {
                    System.err.println("MessageBox: " + t);
                    System.err.println("method: " + callbacks[i]);
                }
                return;
            }
        }
    }
}
