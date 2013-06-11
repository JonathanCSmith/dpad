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
package net.jonathansmith.javadpad.server.network.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.Engine;

/**
 *
 * @author Jon
 */
public class SessionRegistry {

    public final ConcurrentMap<ServerSession, Boolean> sessions = new ConcurrentHashMap<ServerSession, Boolean> ();
    public final Engine engine;
    
    public SessionRegistry(Engine eng) {
        this.engine = eng;
    }
    
    public Session addAndGetNewSession(Channel c) {
        int id = this.sessions.size();
        ServerSession session = new ServerSession(this.engine, c);
        this.sessions.put(session, true);
        return session;
    }

    public void remove(ServerSession session) {
        if (this.sessions.containsKey(session)) {
            this.sessions.remove(session);
        }
    }
}
