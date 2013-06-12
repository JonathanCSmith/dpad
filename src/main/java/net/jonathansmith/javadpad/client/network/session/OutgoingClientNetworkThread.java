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
package net.jonathansmith.javadpad.client.network.session;

import java.util.Collection;
import java.util.Iterator;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.session.NetworkThread;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.Session.NetworkThreadState;

/**
 *
 * @author Jon
 */
public class OutgoingClientNetworkThread extends NetworkThread {
    
    public OutgoingClientNetworkThread(Engine eng, Session sess) {
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
                    if (this.session.getState() != NetworkThreadState.RUNNING && priority == PacketPriority.HIGH) {
                        break;
                    }
                    
                    pending = this.packets.get(priority);
                    if (pending.isEmpty()) {
                        continue;
                    }
                    
                    Iterator iter = pending.iterator();
                    packet = (Packet) iter.next();
                    iter.remove();
                    
                    this.sendPacket(new PacketMessage(packet, priority));
                    break;
                }
            }
        }
    }
}
