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
package net.jonathansmith.javadpad.common.network.packet;

import java.util.HashMap;
import java.util.Map;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.session.Session;

/**
 *
 * @author jonathansmith
 */
public abstract class Packet {
    
    public static final Map<Integer, Class<? extends Packet>> packetMap = new HashMap<Integer, Class<? extends Packet>> ();
    
    public Engine engine;
    public Session session;
    
    private boolean forceUnencrypted = false;
    
    public Packet() {
        this(null, null);
    }
    
    public Packet(Engine engine, Session session) {
        this.engine = engine;
        this.session = session;
    }
    
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    public boolean getIsUnencrypted() {
        return this.forceUnencrypted;
    }
    
    public void forceUnencrypted() {
        this.forceUnencrypted = true;
    }
    
    public abstract int getID();
    
    public abstract void setID(int newID);
    
    public abstract int getNumberOfPayloads();
    
    public abstract int getPayloadSize(int payloadNumber);
    
    public abstract byte[] writePayload(int payloadNumber, int providedSize);
    
    public abstract void parsePayload(int payloadNumber, byte[] bytes);
    
    public abstract void handleClientSide();
    
    public abstract void handleServerSide();
    
    @Override
    public abstract String toString();
    
    public static Class<? extends Packet> getPacket(int id) {
        if (!packetMap.containsKey(id)) {
            return packetMap.get(0);
        }
        
        return packetMap.get(id);
    }
    
    public static void addPacket(Class<? extends Packet> packet) {
        int packetID = packetMap.size();
        try {
            if (!packetMap.containsValue(packet)) {
                packet.newInstance().setID(packetID);
                packetMap.put(packetID, packet);
            }
            
            else {
                throw new RuntimeException("Packet registration failure!");
            }
            
        } catch (Exception ex) {
            System.out.println("Failed to add packet");
            ex.printStackTrace();
        }
    }
}
