package scorch.gui;

/*
  Class:  PlayersLister
  Author: Mikhail Kruk

  Description: an interface which provides basic functionallity of an object
  that contains the list of the players
*/

import java.awt.*;

public interface PlayersLister
{
    public void addPlayer(String name);
    public void removePlayer(String name);
}
