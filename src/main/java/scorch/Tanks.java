package scorch;

/*
  Class:  Tanks
  Author: Mikhail Kruk
  Description: this class contains Tanks bitmaps (more to be added) as well
  as some functions for changing their colors and getting their dimensions
*/

import java.awt.Color;
import java.util.Arrays;

import scorch.Bitmap;

public class Tanks {
    private static final int tankColor = Bitmap.getColor(0, 180, 0);
    private static final int k = tankColor, b = Color.darkGray.getRGB(),
            t = Bitmap.getColor(0, 181, 0), w = Color.white.getRGB();

    private static final int[] tankColors = {
            Color.red.getRGB(), Color.yellow.getRGB(),
            Color.white.getRGB(), Bitmap.getColor(43, 112, 79),
            Color.cyan.getRGB(), Bitmap.getColor(0, 0, 150),
            Bitmap.getColor(0, 150, 0), Color.magenta.getRGB()
    };

    // tank array record format:
    // turret x 
    // turret y 
    // turret length
    // transparency color on the sprite
    // turret color (which will almost always be skipped)
    // left wheel
    // right wheel

    public static final int[][][] tanks = {
            {
                    {7, 4, 8, 0, t, 1, 1, 0, 0, 0, 0, 0, 0},
                    {t, t, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, t, t, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, t, t, k, k, k, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, k, k, k, 0, 0, 0, 0},
                    {0, 0, 0, k, k, k, k, k, k, k, k, 0, 0},
                    {0, 0, 0, k, k, k, k, k, k, k, k, 0, 0},
                    {0, k, k, k, k, k, k, k, k, k, k, k, 0},
                    {k, b, k, b, k, b, k, b, k, b, k, b, k},
                    {0, k, k, k, k, k, k, k, k, k, k, k, 0},
            },

            {
                    {7, 5, 9, 0, t, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, t, t, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, t, t, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, t, t, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, k, k, k, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, k, k, k, k, k, k, 0, 0, 0, 0, 0},
                    {0, 0, 0, k, k, k, k, k, k, k, k, 0, 0, 0, 0},
                    {0, 0, 0, k, k, k, k, k, k, k, k, 0, 0, 0, 0},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, k, 0},
                    {k, b, k, b, k, b, k, b, k, b, k, b, k, b, k},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, k, 0},
            },

            {
                    {7, 5, 7, 0, t, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, t, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, t, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, t, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, k, k, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, k, k, k, k, k, k, k, k, 0, 0, 0, 0},
                    {0, 0, 0, k, k, k, k, k, k, k, k, k, k, 0, 0, 0},
                    {0, 0, k, k, b, b, k, b, b, k, b, b, k, k, 0, 0},
                    {0, 0, k, k, k, k, k, k, k, k, k, k, k, k, 0, 0},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, k, k, 0},
                    {k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k},
            },

            {
                    {3, 3, 6, 0, t, 2, 2, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, t, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, t, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, t, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, k, k, 0, 0, 0, 0, k, k, k, 0, 0, 0},
                    {0, k, k, k, k, 0, 0, 0, k, 0, 0, k, 0, 0},
                    {0, k, k, k, k, 0, 0, 0, k, 0, 0, 0, k, 0},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, 0},
                    {k, k, k, k, k, k, k, k, k, k, k, k, k, k},
                    {k, k, k, k, k, k, k, k, k, k, k, k, k, k},
                    {0, k, b, k, 0, 0, 0, 0, 0, 0, k, b, k, 0},
                    {0, 0, k, 0, 0, 0, 0, 0, 0, 0, 0, k, 0, 0},
            },

            {
                    {11, 4, 9, 0, t, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, t, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, t, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, k, k, 0, 0, t, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, k, k, k, k, k, t, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, k, k, k, k, k, k, k, 0, 0, 0, 0},
                    {0, 0, k, k, k, k, k, 0, k, k, k, k, k, k, k, k, 0, 0, 0},
                    {0, 0, k, k, k, k, k, k, k, k, k, k, k, k, k, k, 0, 0, 0},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, 0},
                    {k, b, k, b, k, b, k, b, k, b, k, b, k, b, k, b, k, b, k},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, 0},
            },

            {
                    {8, 3, 6, 0, t, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, t, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, t, t, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, k, k, k, k, 0, t, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, k, k, k, k, k, k, k, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, k, k, k, k, k, k, k, k, k, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, k, k, k, k, k, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, k, k, k, k, k, k, k, k, k, k, k, 0, 0, 0},
                    {0, k, k, k, k, k, k, k, k, k, k, k, k, k, k, k, 0},
                    {k, k, b, b, b, b, b, b, b, b, b, b, b, b, b, k, k},
                    {k, b, k, b, b, k, b, b, k, b, b, k, b, b, k, b, k},
                    {0, 0, b, b, b, b, b, b, b, b, b, b, b, b, b, 0, 0},
            },
    };

    public static final int tankNum = tanks.length;

    public static int[][] getTank(int n, int c) {
        int tw = getTankWidth(n), th = getTankHeight(n);
        int[][] tank = new int[th][tw];
        int color = (c >= 0) ? tankColors[c] : tankColor;

        for (int i = 0; i < th; i++)
            for (int j = 0; j < tw; j++) {
                if (tanks[n][i][j] == tankColor)
                    tank[i][j] = color;
                else if (tanks[n][i][j] == t && c >= 0)
                    tank[i][j] = tank[0][3];
                else
                    tank[i][j] = tanks[n][i][j];
            }

        Arrays.fill(tank[0], tank[0][3]);

        return tank;
    }

    public static int getTurretX(int tt) {
        return tanks[tt][0][0];
    }

    public static int getTurretY(int tt) {
        return tanks[tt][0][1];
    }

    public static int getTurretL(int tt) {
        return tanks[tt][0][2];
    }

    public static int getLeftBase(int tt) {
        return tanks[tt][0][5];
    }

    public static int getRightBase(int tt) {
        return tanks[tt][0][6];
    }

    public static int getTankColor(int i) {
        return tankColors[i];
    }

    public static int[] getTankColors() {
        return tankColors;
    }

    public static int getTankWidth(int tt) {
        return tanks[tt][0].length;
    }

    public static int getTankHeight(int tt) {
        return tanks[tt].length;
    }

}
