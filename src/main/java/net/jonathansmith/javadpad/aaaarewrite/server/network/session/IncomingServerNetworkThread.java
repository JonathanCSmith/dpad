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
package net.jonathansmith.javadpad.aaaarewrite.server.network.session;

import java.util.Collection;

import com.google.common.collect.Iterables;

import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.aaaarewrite.common.network.session.NetworkThread;
import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session;
import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session.State;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;

/**
 *
 * @author Jon
 */
class IncomingServerNetworkThread extends NetworkThread {

    public IncomingServerNetworkThread(Engine eng, Session sess) {
        super(eng, sess);
    }
    
    @Override
    public void run() {
        while (this.isRunning()) {
            if (this.packets.isEmpty()) {
                try {
                    Thread.sleep(100);
                }
                
                catch (InterruptedException ex) {
                    // TODO: Do I need to handle this? Why should it happen?
                }
            }
            
            else {
                Collection<Packet> pending;
                Packet packet;
                
                for (PacketPriority priority : PacketPriority.values()) {
                    if (this.session.getState() != State.RUNNING && priority == PacketPriority.HIGH) {
                        break;
                    }
                    
                    pending = this.packets.get(priority);
                    if (pending.isEmpty()) {
                        continue;
                    }
                    
                    packet = Iterables.get(pending, 0);
                    packet.setEngine(this.engine);
                    packet.setSession(this.session);
                    packet.handleServerSide();
                    break;
                }
            }
        }
    }
}