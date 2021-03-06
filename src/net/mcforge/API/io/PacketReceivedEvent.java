/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.API.io;

import java.io.InputStream;

import net.mcforge.API.Cancelable;
import net.mcforge.API.EventList;
import net.mcforge.networking.IOClient;
import net.mcforge.server.Server;

public class PacketReceivedEvent extends IOEvent implements Cancelable {
    private InputStream reader;
    private byte opcode;
    private boolean _cancel;
    private static EventList events = new EventList();
    public PacketReceivedEvent(IOClient client, Server server, InputStream reader, byte opcode) {
        super(client, server);
        this.reader = reader;
        this.opcode = opcode;
    }

    @Override
    public boolean isCancelled() {
        return _cancel;
    }

    @Override
    public void setCancel(boolean cancel) {
        this._cancel = cancel;
    }

    @Override
    public EventList getEvents() {
        return events;
    }
    /**
     * Get a list of registered listeners
     * @return The list of listeners
     */
    public static EventList getEventList() {
        return events;
    }
    /**
     * Get a DataInputStream to read the bytes being sent by the client
     * @return A DataInputStream
     */
    public InputStream getReader() {
        return reader;
    }
    /**
     * Get the op code the client sent
     * The op code is the first byte of the byte array, this usually
     * indicates what type of message it is.
     * @return The first byte or op code
     */
    public byte getOpCode() {
        return opcode;
    }

}

