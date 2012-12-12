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

    public int[] getNextPos(int currx, int curry, int currz) {
        int min = 0;
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    try {
                        int index = level.posToInt(currx + xx, curry + yy, currz + zz);
                        if (index == -1)
                            continue;
                        int cx = level.posToInt(currx + xx, curry, currz);
                        int cy = level.posToInt(currx, curry + yy, currz);
                        int cz = level.posToInt(currx, curry, currz + zz);
                        if (xx == 0 && yy == 0 && zz == 0)
                            continue;
                        if (grid[index] == -1)
                            continue;
                        if (xx != 0 && yy != 0 && grid[cx] == -1 && grid[cy] == -1)
                            continue;
                        if (xx != 0 && zz != 0 && grid[cx] == -1 && grid[cz] == -1)
                            continue;
                        if (yy != 0 && zz != 0 && grid[cy] == -1 && grid[cz] == -1)
                            continue;
                        if (grid[index] <= grid[min] || min == 0)
                            min = index;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Whoops :3");
                    }
                }
            }
        }

        return IntToPos(min, level);
    }
    
    public boolean nextMoveValid(int oldx, int oldy, int oldz, int startx, int starty, int startz) {
        if (level.getTile(oldx, oldy, oldz).getVisibleBlock() == 0)
            return level.getTile(startx, starty - 1, startz).getVisibleBlock() != 0;
        return true;
    }

    public ArrayList<Vector3> getMoves(int startx, int starty, int startz) {
        int oldx = startx;
        int oldy = starty;
        int oldz = startz;
        boolean triedagain = false;
        int tries = 100;
        ArrayList<Vector3> temp = new ArrayList<Vector3>();
        while ((startx != x || starty != y || startz != z) && tries > 0) {
            int[] pos = getNextPos(startx, starty, startz);
            Vector3 newpos = new Vector3(pos[0], pos[1], pos[2]);
            if (newpos.getX() == oldx && newpos.getY() == oldy && newpos.getZ() == z && triedagain)
                break;
            if (temp.contains(newpos) || nextMoveValid(startx, starty, startz, pos[0], pos[1], pos[2])) {
                int i = level.posToInt(pos[0], pos[1], pos[2]);
                grid[i] = -1;
                startx = oldx;
                starty = oldy;
                startz = oldz;
                temp.clear();
                triedagain = true;
                tries--;
                continue;
            }
            try {
                int x1 = level.posToInt(newpos.getX() + 1, newpos.getY(), newpos.getZ());
                int x2 = level.posToInt(newpos.getX() - 1, newpos.getY(), newpos.getZ());
                
                int y1 = level.posToInt(newpos.getX(), newpos.getY() + 1, newpos.getZ());
                int y2 = level.posToInt(newpos.getX(), newpos.getY() - 1, newpos.getZ());
                
                int z1 = level.posToInt(newpos.getX(), newpos.getY(), newpos.getZ() + 1);
                int z2 = level.posToInt(newpos.getX(), newpos.getY(), newpos.getZ() - 1);
                if ((grid[x1] == -1 && grid[x2] == -1) || (grid[y1] == -1 && grid[y2] == -1) || (grid[z1] == -1 && grid[z2] == -1) || (newpos.getX() == oldx && newpos.getY() == oldy && newpos.getZ() == oldz))
                    grid[level.posToInt(pos[0], pos[1], pos[2])] = -1;
            }
            catch (Exception e) { }
            temp.add(newpos);
            startx = pos[0];
            starty = pos[1];
            startz = pos[2];
        }
        System.out.println("Done!");
        return temp;
    }

    public Vector3 getNextMove(int currentx, int currenty, int currentz) {
        recalculateGrid();
        getMoves(currentx, currenty, currentz);
        int[] pos = getNextPos(currentx, currenty, currentz);
        return new Vector3(pos[0], pos[1], pos[2]);
    }

    public boolean hasNextMove(int currentx, int currenty, int currentz) {
        return currentx != x || currenty != y || currentz != z;
    }

    public void dispose() {
        grid = null;
    }

    public void recalculateGrid() {
        for (int i = 0; i < grid.length; i++) {
            if (grid[i] == -1)
                continue;
            int[] pos = IntToPos(i, level);
            if (level.getTile(pos[0], pos[1], pos[2]).canWalkThrough() && level.getTile(pos[0], pos[1] + 1, pos[2]).canWalkThrough()) {
                grid[i] = Math.abs(pos[0] - x) + Math.abs(pos[2] - z) + Math.abs(pos[1] - z);
            }
            else
                grid[i] = -1;
        }
    }

    public static int[] calculateGrid(Level level, int x, int y, int z) {
        int[] grid = new int[level.getLength()];
        for (int i = 0; i < grid.length; i++) {
            int[] pos = IntToPos(i, level);
            if (level.getTile(pos[0], pos[1], pos[2]).canWalkThrough()) {
                grid[i] = Math.abs(pos[0] - x) + Math.abs(pos[2] - z);
                if (level.getTile(pos[0], pos[1] - 1, pos[2]).canWalkThrough())
                    grid[i] += Math.abs(pos[1] - y) + (getDepth(pos[0], pos[1], pos[2], level) * 2);
                if (!level.getTile(pos[0] + 1, pos[1], pos[2]).canWalkThrough() || !level.getTile(pos[0] - 1, pos[1], pos[2]).canWalkThrough() || !level.getTile(pos[0], pos[1], pos[2] + 1).canWalkThrough() || !level.getTile(pos[0], pos[1], pos[2]).canWalkThrough())
                    grid[i] += 2;
                if (!level.getTile(pos[0], pos[1] + 1, pos[2]).canWalkThrough())
                    grid[i] = -1;
            }
            else
                grid[i] = -1;
        }
        return grid;
    }

    private static int getDepth(int x, int y, int z, Level level) {
        int i = 0;
        while (level.getTile(x, y - 1, z).canWalkThrough()) {
            y--;
            i++;
        }
        return i;
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
