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
import net.jonathansmith.javadpad.server.database.user.UserManager;

/**
 *
 * @author Jon
 */
public class UsersRequestPacket extends Packet {

    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    public UsersRequestPacket() {
        super();
    }
    
    public UsersRequestPacket(Engine engine, Session session) {
        super(engine, session);
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
        return 0;
    }

    @Override
    public int[] getPayloadSizes() {
        return null;
    }

    @Override
    public byte[] writePayload(int payloadNumber, int providedSize) {
        return null;
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {}

    @Override
    public void handleClientSide() {}

    @Override
    public void handleServerSide() {
        List<User> users = UserManager.getInstance().loadAll();
        Packet p = new UsersResponsePacket(this.engine, this.session, users);
    }

    @Override
    public String toString() {
        return "User request packet";
    }
}
