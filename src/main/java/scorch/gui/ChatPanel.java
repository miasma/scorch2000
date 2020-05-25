package scorch.gui;

/*
  Class:  ChatPanel
  Author: Mikhail Kruk

  Description: 
*/

import java.awt.*;

import scorch.*;
import swindows.*;

public class ChatPanel extends sWindow {

    public ChatPanel(int x, int y, int w, int h, ScorchApplet owner) {
        super(x, y, w, h, "Scorched Earth 2000 Chat", owner);

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        TextField chatMessage = new TextField();

        setLayout(new BorderLayout());
        add(chatArea, BorderLayout.CENTER);
        add(chatMessage, BorderLayout.SOUTH);
    }
}
