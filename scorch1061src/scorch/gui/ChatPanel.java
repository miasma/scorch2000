package scorch.gui;

/*
  Class:  ChatPanel
  Author: Mikhail Kruk

  Description: 
*/

import java.awt.*;

import scorch.*;
import scorch.utility.*;
import swindows.*;

public class ChatPanel extends sWindow
{
    private TextArea chatArea;
    private TextField chatMessage;

    public ChatPanel(int x, int y, int w, int h, ScorchApplet owner)
    {
	super(x,y,w,h,"Scorched Earth 2000 Chat", owner);

	chatArea = new TextArea();
	chatArea.setEditable(false);
	chatMessage = new TextField();

	setLayout(new BorderLayout());
	add(chatArea, BorderLayout.CENTER);
	add(chatMessage, BorderLayout.SOUTH);
    }
}
