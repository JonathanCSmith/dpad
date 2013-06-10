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

import org.bouncycastle.crypto.modes.CFBBlockCipher;

import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 *
 * @author jonathansmith
 */
public abstract class Packet {
    
    public static final Map<Integer, Class<? extends Packet>> packetMap = new HashMap<Integer, Class<? extends Packet>> ();
    
    public Engine engine;
    public Session session;
    
    private int id;
    
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
    
    public int getID() {
        return this.id;
    }
    
    public final void setID(int id) {
        this.id = id;
    }
    
    public abstract int getNumberOfPayloads();
    
    public abstract int[] getPayloadSizes();
    
    public abstract ChannelBuffer writePayload(int payloadNumber, ChannelBuffer header, CFBBlockCipher encrypter);
    
    public abstract void parsePayload(int payloadNumber, byte[] bytes, CFBBlockCipher decrypter);
    
    public abstract void handleClientSide();
    
    public abstract void handleServerSide();
    
    public static Class<? extends Packet> getPacket(int id) {
        return packetMap.get(id);
    }
    
    public static void addPacket(Class<? extends Packet> packet) {
        int packetID = packetMap.size();
        try {
            packet.newInstance().setID(packetID);
            
        } catch (InstantiationException ex) {
            // TODO: Fix these
            DPADLogger.severe("Failed to assign packet id");
            DPADLogger.logStackTrace(ex);
        } catch (IllegalAccessException ex) {
            DPADLogger.severe("Failed to assign packet id");
            DPADLogger.logStackTrace(ex);
        }
        packetMap.put(packetID, packet);
    }
}
