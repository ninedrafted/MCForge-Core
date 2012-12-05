package net.mcforge.iomodel.bot;

import net.mcforge.API.CommandExecutor;
import net.mcforge.server.Server;
import net.mcforge.server.Tick;
import net.mcforge.world.Level;

public abstract class Bot implements Tick, CommandExecutor {
    
    private Server server;
    
    private Level level;
    
    protected short x, y, z;
    
    public Bot(Server server, Level level) {
        this.level = level;
        this.server = server;
    }
    
    public Server getServer() {
        return server;
    }
    
    public Level getLevel() {
        return level;
    }

    /**
     * @return the x
     */
    public short getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(short x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public short getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(short y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public short getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(short z) {
        this.z = z;
    }
}
