package net.mcforge.networking.packets.minecraft.extend;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.Player;
import net.mcforge.networking.IOClient;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.networking.packets.PacketType;
import net.mcforge.server.Server;

public class ClickDistancePacket extends ExtendPacket {

	public ClickDistancePacket(String name, byte ID, PacketManager parent,
			PacketType packetType) {
		super(name, ID, parent, packetType);
	}
	
	public ClickDistancePacket(PacketManager parent) {
		super("ClickDistance", (byte)0x20, parent, PacketType.Server_to_Client);
	}

	@Override
	public void Handle(byte[] message, Server server, IOClient player) { }

	@Override
	public void Write(IOClient client, Server servers) {
		Write(client, servers, 12); //TODO Test if this is default..?
	}

	@Override
	public void WriteData(Player p, Server servers, Object... para) {
		ByteBuffer bf = ByteBuffer.allocate(2);
		bf.put(ID);
		bf.putShort(Short.parseShort(para[0].toString()));
		try {
			p.WriteData(bf.array());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}