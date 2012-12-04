package net.mcforge.iomodel.bot;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.mcforge.groups.Group;
import net.mcforge.server.Server;
import net.mcforge.world.Level;

public class PlayerBot extends Bot {

    private String name;
    private List<Waypoint> packet_queue = Collections.synchronizedList(new LinkedList<Waypoint>());
    
    public PlayerBot(Server server, Level level, String name) {
        super(server, level);
        this.name = name;
    }

    @Override
    public void tick() {
    }

    @Override
    public void sendMessage(String message) { }

    @Override
    public Group getGroup() {
        return Group.getDefault();
    }

    @Override
    public String getName() {
        return name;
    }

}
