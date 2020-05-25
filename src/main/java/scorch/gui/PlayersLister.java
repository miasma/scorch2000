package scorch.gui;

/*
  Class:  PlayersLister
  Author: Mikhail Kruk

  Description: an interface which provides basic functionallity of an object
  that contains the list of the players
*/

public interface PlayersLister {
    void addPlayer(String name);

    void removePlayer(String name);
}
