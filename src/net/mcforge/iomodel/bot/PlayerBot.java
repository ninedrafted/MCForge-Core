package net.mcforge.iomodel.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.mcforge.groups.Group;
import net.mcforge.iomodel.Player;
import net.mcforge.networking.packets.minecraft.GlobalPosUpdate;
import net.mcforge.networking.packets.minecraft.TP;
import net.mcforge.server.Server;
import net.mcforge.world.Block;
import net.mcforge.world.Level;

public class PlayerBot extends Bot {

    private String name;
    private Waypoint dest;
    private byte ID;
    private Thread run;
    private ArrayList<Vector3> moves = new ArrayList<Vector3>();
    public int index, oldx, oldy, oldz;
    
    public byte yaw, pitch;
    //private List<Waypoint> packet_queue = Collections.synchronizedList(new LinkedList<Waypoint>());
    public byte oldyaw;
    public byte oldpitch;
    
    public PlayerBot(Server server, Level level, String name) {
        super(server, level);
        this.name = name;
        ID = getFreeID();
        run = new Run();
    }

    @Override
    public void tick() {
        
    }
    
    public void spawn() {
        for (Player p : getServer().players) {
            p.spawnBot(this);
        }
        run.start();
    }
    
    public void despawn() {
        
    }
    
    public void dispose() {
        dest = null;
        moves.clear();
        despawn();
    }
    
    @Override
    public void setX(short x) {
        oldx = getX();
        this.x = x;
    }
    
    @Override
    public void setY(short y) {
        oldy = getY();
        this.y = y;
    }
    
    @Override
    public void setZ(short z) {
        oldz = getZ();
        this.z = z;
    }
    
    private byte getFreeID() {
        boolean found = true;
        byte toreturn = 0;
        for (int i = 0; i < 255; i++) {
            found = true;
            for (Player p : getServer().players) {
                if (p.getID() == i) {
                    found = false;
                    break;
                }
            }
            if (found) {
                toreturn = (byte)i;
                break;
            }
        }
        return toreturn;
    }
    
    public byte getID() {
        return ID;
    }
    
    public void updatePos() throws IOException {
        TP t = (TP)(getServer().getPacketManager().getPacket("TP"));
        GlobalPosUpdate gps = (GlobalPosUpdate)(getServer().getPacketManager().getPacket("GlobalPosUpdate"));
        byte[] tosend;
        if (Math.abs(getX() - oldx) >= 127 || Math.abs(getY() - oldy) >= 127 || Math.abs(getZ() - oldz) >= 127)
            tosend = t.toSend(this);
        else
            tosend = gps.toSend(this);
        synchronized (getServer().players){ 
            for (Player p : getServer().players) {
                if (p.getLevel() == getLevel())
                    p.writeData(tosend);
            }
        }
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

    private class Run extends Thread {
        
        @Override
        public void run() {
            while (true) {
                if (dest == null) {
                    final Random RANDOM = new Random();
                    int desx = 0, desy = 0, desz = 0;
                    while (!getLevel().getTile(desx, desy, desz).canWalkThrough()) {
                        desx = RANDOM.nextInt(getLevel().width);
                        desy = RANDOM.nextInt(getLevel().height);
                        desz = RANDOM.nextInt(getLevel().depth);
                    }
                    while (getLevel().getTile(desx, desy - 1, desz).canWalkThrough())
                        desy--;
                    desy++;
                    System.out.println("Desx: " + desx + " Desy: " + desy + " Desz: " + desz);
                    dest = new Waypoint(getLevel(), desx, desy, desz);
                    moves = dest.getMoves(getX() / 32, getY() / 32, getZ() / 32);
                    Player.GlobalBlockChange(desx, desy, desz, Block.getBlock("White"), getLevel(), getServer());
                }
                if (index < moves.size()) {
                    int y = moves.get(index).getY();
                    while (getLevel().getTile(moves.get(index).getX(), y, moves.get(index).getZ()).canWalkThrough())
                        y--;
                    y++;
                    setX((short)((0.5 + moves.get(index).getX()) * 32));
                    setY((short)((y + 1.5) * 32));
                    setZ((short)((moves.get(index).getZ() + 0.5) * 32));
                    try {
                        updatePos();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    index++;
                    ArrayList<Vector3> temp = dest.getMoves(getX() / 32, getY() / 32, getZ() / 32);
                    if (!temp.equals(moves)) {
                        moves = temp;
                        index = 0;
                    }
                }
                else {
                    moves.clear();
                    dest = null;
                    index = 0;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
