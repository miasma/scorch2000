/*
  Class:  ScorchChat
  Author: Mikhail Kruk

  Description: Class responsible for drawing chat messages over the 
  ScorchField
*/

package scorch;

import java.awt.*;

import scorch.backgrounds.*;

class ScorchChat implements Runnable {
    private static final String SysMsgPrefix = "*System* ";
    private static final int MARGIN = 5;
    private static final int DELAY = 7500; // ms
    private static final int MAX_LINES = 8;

    private final String[] chat = new String[MAX_LINES];
    private final Color chatColor;
    private FontMetrics fm = null;
    private int fontHeight = -1, curChatLine = 0;

    private Thread thread;
    private final ScorchField owner;

    public ScorchChat(ScorchField owner, Background bk) {
        this.owner = owner;

        int c1 = Math.min(bk.getPixelColor(0, 0), bk.getPixelColor(0, 1));

        // take complementary color to be the color of chat lines
        chatColor = new Color(255 - Bitmap.getRed(c1),
                255 - Bitmap.getGreen(c1),
                255 - Bitmap.getBlue(c1));
    }

    public void paint(Graphics g, int width) {
        if (fm == null) {
            fm = g.getFontMetrics();
            fontHeight = fm.getMaxAscent() + fm.getMaxDescent();
        }

        g.setColor(chatColor);

        String ws;
        int wi = Physics.getWind() * 8;
        if (wi > 0)
            ws = wi + " ->";
        else
            ws = "<- " + (-wi);
        if (wi != 0)
            g.drawString(ws, width - 2 * fm.stringWidth(ws), (int) (fontHeight * 1.2));

        for (int i = curChatLine - 1; i >= 0; i--) {
            if (chat[i].startsWith(SysMsgPrefix)) {
                g.drawString
                        (chat[i],
                                (width -
                                        fm.stringWidth(chat[i])) / 2,
                                fontHeight * (i + 1));
            } else {
                g.drawString(chat[i], MARGIN, fontHeight * (i + 1));
            }
        }
    }

    public synchronized void addMessage(String msg) {
        boolean done = false;
        String next_msg;
        int screenWidth, new_length, new_width;
        int[] font_width;

        // get the maximum allowed string length. note that I've used
        // 4 margins insread of 2 for extra safety since we do not
        // calculate the exact widths and rely on characters being of the
        // (approx) same size
        screenWidth = owner.getWidth() - 4 * MARGIN;

        next_msg = msg.replace(Protocol.separator, ' '); // what is this for?;

        do {
            msg = next_msg;
            if (fm != null &&
                    (fm.stringWidth(msg)) > screenWidth) {
                font_width = fm.getWidths();
                new_width = 0;
                new_length = 0;
                while (new_width < screenWidth) {
                    new_width +=
                            font_width[(int) msg.charAt(new_length)];
                    new_length++;
                }

                if (new_length < msg.length()) {
                    next_msg = msg.substring(new_length);
                    msg = msg.substring(0, new_length);
                } else {
                    // IMHO we can't get here, but we do somehow.
                    done = true;
                }
            } else
                done = true;

            if (curChatLine == MAX_LINES)
                dropLastLine();

            chat[curChatLine++] = msg;
            owner.repaint();
        }
        while (!done);

        if (thread == null) {
            thread = new Thread(this, "chat-thread");
            thread.start();
        }
    }

    public synchronized void addSystemMessage(String msg) {
        addMessage(SysMsgPrefix + msg);
    }

    private synchronized void dropLastLine() {
        System.arraycopy(chat, 1, chat, 0, MAX_LINES - 1);
        curChatLine--;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                System.err.println(e);
            }

            dropLastLine();
            owner.repaint();

            if (curChatLine == 0) {
                thread = null;
                return;
            }
        }
    }
}
