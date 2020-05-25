package ScorchServer;

import scorch.PlayerProfile;
import scorch.Protocol;

/*
  Class : AIPlayer

  Author: Alexander Rasin

  This is a class for AI player.  Since there is no separate client for AI
  player (AI players are stored on the master client), it does not implement
  send message.
  It also overrides the unsetReady since in AI client ready is defaulted to
  be true (no separate client, no actions to draw).
*/
public class AIPlayer extends Player {
    public AIPlayer(String type) {
        super(type);
        this.ready = true;

        this.profile = new PlayerProfile(name, "*", "");
    }

    public synchronized void setGame(Game g, int pl_id) {
        this.myGame = g;
        id = pl_id;
        ploptions = Protocol.setplayeroptions + Protocol.separator +
                id + Protocol.separator + 0;
    }

    public void dropPlayer(String reason) {
        myGame.leave(this);
    }

    public String getHostName() {
        return "On the master's client";
    }

    //AI is always ready as it does not draw (has no separate client)
    public void setReady(boolean val) {
        //System.out.println("in overreaden message " + ready);
    }
}

