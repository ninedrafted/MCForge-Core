/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.networking.packets.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.mcforge.API.io.PacketPrepareEvent;
import net.mcforge.iomodel.Player;
import net.mcforge.iomodel.bot.PlayerBot;
import net.mcforge.networking.IOClient;
import net.mcforge.networking.packets.Packet;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class SpawnPlayer extends Packet {

    public SpawnPlayer(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }

    public SpawnPlayer(PacketManager pm) {
        super("Spawn Player", (byte)0x07, pm);
    }

    @Override
    public void Handle(byte[] message, Server server, IOClient player) {
        // TODO Auto-generated method stub

    }

    @Override
    public void Write(IOClient p, Server server, Object...parma) {
        PacketPrepareEvent event = new PacketPrepareEvent(p, this, server);
        server.getEventSystem().callEvent(event);
        if (event.isCancelled())
            return;
        Player player;
        if (p instanceof Player) {
            player = (Player)p;
        }
        else
            return;
        try {
            byte pID, yaw, pitch;
            String name;
            short x, y, z;
            if (parma[0] instanceof Player) {
                Player spawn = (Player)parma[0];
                pID = (spawn == player) ? (byte)0xFF : spawn.getID();
                yaw = spawn.yaw;
                pitch = spawn.pitch;
                name = (spawn.isShowingPrefix() ? spawn.getDisplayName() : (spawn.isUsingCustomNick() && spawn.getCustomName().startsWith("&") ? "" : spawn.getDisplayColor().toString()) + (spawn.isUsingCustomNick() ? spawn.getCustomName() : spawn.username));
                x = spawn.getX();
                y = spawn.getY();
                z = spawn.getZ();
            }
            else if (parma[0] instanceof PlayerBot) {
                PlayerBot spawn = (PlayerBot)parma[0];
                pID = spawn.getID();
                yaw = spawn.yaw;
                pitch = spawn.pitch;
                name = spawn.getName();
                x = spawn.getX();
                y = spawn.getY();
                z = spawn.getZ();
            }
            else
                return;
            byte[] send = new byte[74];
            send[0] = ID;
            send[1] = pID; 
            while (name.length() < 64)
                name += " ";
            byte[] nameb = name.getBytes("US-ASCII");
            System.arraycopy(nameb, 0, send, 2, 64);
            System.arraycopy(HTNO(x), 0, send, 66, 2);
            System.arraycopy(HTNO(y), 0, send, 68, 2);
            System.arraycopy(HTNO(z), 0, send, 70, 2);
            send[72] = yaw;
            send[73] = pitch;
            player.writeData(send);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] HTNO(short x) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(x);
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public void Write(IOClient player, Server servers) {
    }

}

