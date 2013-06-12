/*
 * Copyright (C) 2013 Jon
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
package net.jonathansmith.javadpad.common.network.packet.user;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.User;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.session.Session;

import org.apache.commons.lang3.SerializationUtils;


/**
 * 
 * @author Jon
 */
public class UsersResponsePacket extends Packet {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private List<User> users;
    private byte[][] serializedUsers;
    
    public UsersResponsePacket() {
        super();
    }
    
    public UsersResponsePacket(Engine engine, Session session, List<User> list) {
        super(engine, session);
        this.users = list;
        
        this.serializeUsers();
    }
    
    private void serializeUsers() {
        User user;
        byte[][] temps = new byte[this.users.size()][];
        for (int i = 0; i < this.users.size(); i++) {
            user = this.users.get(i);
            byte[] data = SerializationUtils.serialize(user);
            temps[i] = data;
        }
        
        this.serializedUsers = temps;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        if (lock.compareAndSet(false, true)) {
            id = newID;
        }
    }

    @Override
    public int getNumberOfPayloads() {
        return this.users.size();
    }

    @Override
    public int[] getPayloadSizes() {
        int[] sizes = new int[this.users.size()];
        
        for (int i = 0; i < this.serializedUsers.length; i++) {
            sizes[i] = this.serializedUsers[i].length;
        }
        
        return sizes;
    }

    @Override
    public byte[] writePayload(int payloadNumber, int providedSize) {
        return this.serializedUsers[payloadNumber];
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        User user = (User) SerializationUtils.deserialize(bytes);
        this.users.set(payloadNumber, user);
    }

    @Override
    public void handleClientSide() {
        this.session.addArrivedDataset("Users", this.users);
    }

    @Override
    public void handleServerSide() {}

    @Override
    public String toString() {
        return this.users.size() + " users packaged in User response packet";
    }
}
