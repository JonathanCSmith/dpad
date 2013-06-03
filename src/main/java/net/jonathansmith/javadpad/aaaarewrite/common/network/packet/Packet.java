/*
 * Copyright (C) 2013 jonathansmith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonathansmith.javadpad.aaaarewrite.common.network.packet;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 * @author jonathansmith
 */
public abstract class Packet {
    
    public static final Map<Integer, Packet> packetMap = new HashMap<Integer, Packet> ();
    
    private int id;
    
    public final void setID(int id) {
        this.id = id;
    }
    
    public abstract ChannelBuffer writeHeader(boolean upstream);
    
    public abstract ChannelBuffer writePayload(boolean upstream, ChannelBuffer header);
    
    public static Packet getPacket(int id) {
        return packetMap.get(id);
    }
    
    public static void addPacket(Packet packet) {
        int packetID = packetMap.size();
        packet.setID(packetID);
        packetMap.put(packetID, packet);
    }
}
