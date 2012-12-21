/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.networking.packets.classicminecraft.extend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import net.mcforge.API.ClassicExtension;
import net.mcforge.iomodel.Player;
import net.mcforge.networking.IOClient;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

@ClassicExtension(extName = "ExtEntry")
public class ExtEntryPacket extends ExtendPacket {

    public ExtEntryPacket(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }

    public ExtEntryPacket(PacketManager packetManager) {
        this("ExtEntry", (byte)0x11, packetManager);
    }

    @Override
    public void WriteData(Player p, Server servers, Object... para) {
        String name = ((ClassicExtension)para[0]).extName();
        if (name.length() < 64)
            name += " ";
        ByteBuffer bf = ByteBuffer.allocate(69);
        bf.put(ID);
        try {
            bf.put(name.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(servers.getLoggerOutput());
        }
        bf.putInt(((ClassicExtension)para[0]).version());
        try {
            p.writeData(bf.array());
        } catch (IOException e) {
            e.printStackTrace(servers.getLoggerOutput());
        }
    }

    @Override
    public void Handle(byte[] message, Server server, IOClient player) {
        Player p = (player instanceof Player ? (Player)player : null);
        if (p == null)
            return;
        byte[] name = new byte[64];
        for (int i = 1; i < 64; i++)
            name[i] = message[i];
        try {
            ClassicExtension found = null;
            String extname = new String(name, "US-ASCII").trim();
            for (ClassicExtension c : server.getPluginHandler().getExtensions()) {
                if (c.extName().equals(extname)) {
                    found = c;
                    break;
                }
            }
            if (found == null)
                throw new ExtensionNotFoundException("The addon " + extname + " is not loaded!");
            p.addExtension(found);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(server.getLoggerOutput());
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace(server.getLoggerOutput());
        }
    }

}

