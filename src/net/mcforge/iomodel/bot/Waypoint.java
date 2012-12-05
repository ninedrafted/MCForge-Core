package net.mcforge.iomodel.bot;

import java.util.ArrayList;

import net.mcforge.world.Level;

public class Waypoint {
    private int[] grid;
    private Level level;
    private int x, y, z;
    
    public Waypoint(Level level, int x, int y, int z, int[] grid) {
        this.grid = grid;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Waypoint(Level level, int x, int y, int z) {
        this(level, x, y, z, calculateGrid(level, x, y, z));
    }
    
    public int[] getNextPos(int currentx, int currenty, int currentz) {
        int x1 = currentx + 1, x2 = currentx - 1, x3 = currentx;
        int y1 = currenty + 1, y2 = currenty - 1, y3 = currenty;
        int z1 = currentz + 1, z2 = currentz - 1, z3 = currentz;
        
        int up1 = level.posToInt(x1, y3, z3), down1 = level.posToInt(x2, y3, z3);
        int left1 = level.posToInt(x3, y3, z1), right1 = level.posToInt(x3, y3, z2);
        int yup = level.posToInt(x3, y1, z3), ydown = level.posToInt(x3, y2, z3);
        int yupfoward = level.posToInt(x1, y1, z3), yupback = level.posToInt(x2, y1, z3);
        int yupleft = level.posToInt(x3, y1, z1), yupright = level.posToInt(x3, y1, z2);
        int ydownfoward = level.posToInt(x1, y2, z3), ydownback = level.posToInt(x2, y2, z3);
        int ydownleft = level.posToInt(x3, y2, z1), ydownright = level.posToInt(x3, y2, z2);
        
        int min = up1;
        if (up1 == -1)
            min = down1;
        if (down1 != -1 && ((grid[down1] < grid[min] && grid[down1] != -1) || (grid[min] == -1 && grid[down1] != -1)))
            min = down1;
        if (left1 != -1 && ((grid[left1] < grid[min] && grid[left1] != -1) || (grid[min] == -1 && grid[left1] != -1)))
            min = left1;
        if (right1 != -1 && ((grid[right1] < grid[min] && grid[right1] != -1) || (grid[min] == -1 && grid[right1] != -1)))
            min = right1;
        if (yup != -1 && ((grid[yup] < grid[min] && grid[yup] != -1) || (grid[min] == -1 && grid[yup] != -1)))
            min = yup;
        if (ydown != -1 && ((grid[ydown] < grid[min] && grid[ydown] != -1) || (grid[min] == -1 && grid[ydown] != -1)))
            min = ydown;
        if (yupfoward != -1 && ((grid[yupfoward] < grid[min] && grid[yupfoward] != -1) || (grid[min] == -1 && grid[yupfoward] != -1)))
            min = yupfoward;
        if (yupback != -1 && ((grid[yupback] < grid[min] && grid[yupback] != -1) || (grid[min] == -1 && grid[yupback] != -1)))
            min = yupback;
        if (yupleft != -1 && ((grid[yupleft] < grid[min] && grid[yupleft] != -1) || (grid[min] == -1 && grid[yupleft] != -1)))
            min = yupleft;
        if (yupright != -1 && ((grid[yupright] < grid[min] && grid[yupright] != -1) || (grid[min] == -1 && grid[yupright] != -1)))
            min = yupright;
        if (ydownleft != -1 && ((grid[ydownleft] < grid[min] && grid[ydownleft] != -1) || (grid[min] == -1 && grid[ydownleft] != -1)))
            min = ydownleft;
        if (ydownright != -1 && ((grid[ydownright] < grid[min] && grid[ydownright] != -1) || (grid[min] == -1 && grid[ydownright] != -1)))
            min = ydownright;
        if (ydownback != -1 && ((grid[ydownback] < grid[min] && grid[ydownback] != -1) || (grid[min] == -1 && grid[ydownback] != -1)))
            min = ydownback;
        if (ydownfoward != -1 && ((grid[ydownfoward] < grid[min] && grid[ydownfoward] != -1) || (grid[min] == -1 && grid[ydownfoward] != -1)))
            min = ydownback;
        
        return IntToPos(min, level);
    }
    
    public ArrayList<Vector3> getMoves(int startx, int starty, int startz) {
        ArrayList<Vector3> temp = new ArrayList<Vector3>();
        while (startx != x || starty != y || startz != z) {
            int[] pos = getNextPos(startx, starty, startz);
            Vector3 newpos = new Vector3(pos[0], pos[1], pos[2]);
            if (temp.contains(newpos)) {
                System.out.println(newpos.getX() + ":" + newpos.getY() + ":" + newpos.getZ() + " ALREADY EXISTS!");
                System.out.println("The end is " + x + ":" + y + ":" + z);
                break;
            }
            temp.add(newpos);
            startx = pos[0];
            starty = pos[1];
            startz = pos[2];
      }
        return temp;
    }
    
    public void dispose() {
        grid = null;
    }
    
    public static int[] calculateGrid(Level level, int x, int y, int z) {
        int[] grid = new int[level.getLength()];
        for (int i = 0; i < grid.length; i++) {
            int[] pos = IntToPos(i, level);
            if (level.getTile(pos[0], pos[1], pos[2]).canWalkThrough()) {
                grid[i] = Math.abs(pos[0] - x) + Math.abs(pos[1] - y) + Math.abs(pos[2] - z);
                if (level.getTile(pos[0], pos[1] - 1, pos[2]).canWalkThrough())
                    grid[i]++;;
                if (level.getTile(pos[0], pos[1] + 1, pos[2]).canWalkThrough())
                    grid[i]--;
            }
            else
                grid[i] = -1;
        }
        return grid;
    }
    
    private static int[] IntToPos(int index, final Level level) {
        final int width = level.width, depth = level.depth;
        int[] toreturn = new int[3];
        toreturn[1] = (index / width / depth);
        index -= toreturn[1]*width*depth;
        toreturn[2] = (index/width);
        index -= toreturn[2]*width;
        toreturn[0] = index;
        return toreturn;
    }
}
