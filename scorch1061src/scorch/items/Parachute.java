package scorch.items;

/*
  Class:  Parachute
  Author: Mikhail Kruk

  Description: This class implements item "Parachute" which can saves tanks
  from falling damage
*/

import java.awt.Color;

public class Parachute extends Tracer
{
    private final static int w = Color.white.getRGB();
    public final static int[][] pIcon = {
	{0,0,0,0,0,w,w,w,w,0,0,0,0,0},
	{0,0,0,w,w,w,w,w,w,w,w,0,0,0},
	{0,0,w,w,w,w,w,w,w,w,w,w,0,0},
	{0,w,w,w,w,w,w,w,w,w,w,w,w,0},
	{w,w,w,w,w,w,w,w,w,w,w,w,w,w},
	{0,w,0,0,w,0,0,0,0,w,0,0,w,0},
	{0,0,w,0,0,w,0,0,w,0,0,w,0,0},
	{0,0,0,w,0,w,0,0,w,0,w,0,0,0},
	{0,0,0,0,w,0,w,w,0,w,0,0,0,0},
	{0,0,0,0,0,w,w,w,w,0,0,0,0,0},
    };

    public Parachute()
    {
	type = Parachute;
	price = 2000;
	autoDefenseAv = true;
    }
}
