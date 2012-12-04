package net.mcforge.iomodel.bot;

import net.mcforge.API.CommandExecutor;
import net.mcforge.server.Server;
import net.mcforge.server.Tick;
import net.mcforge.world.Level;

public abstract class Bot implements Tick, CommandExecutor {
    
    private Server server;
    
    private Level level;
    
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
}
