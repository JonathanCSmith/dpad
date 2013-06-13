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

import java.util.Collection;
import java.util.Iterator;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.session.NetworkThread;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.Session.NetworkThreadState;

/**
 *
 * @author Jon
 */
class OutgoingServerNetworkThread extends NetworkThread {
    
    public OutgoingServerNetworkThread(Engine eng, Session sess, String key) {
        super(eng, sess, key);
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
                    if (this.session.getState() != NetworkThreadState.RUNNING && priority != PacketPriority.CRITICAL) {
                        break;
                    }
                    
                    pending = this.packets.get(priority);
                    if (pending.isEmpty()) {
                        continue;
                    }
                    
                    
                    Iterator iter = pending.iterator();
                    packet = (Packet) iter.next();
                    iter.remove();
                    
                    if (this.session.getState() != NetworkThreadState.RUNNING) {
                        if (!(packet instanceof HandshakePacket) && !(packet instanceof EncryptionKeyRequestPacket) && !(packet instanceof EncryptionKeyResponsePacket)&& !(packet instanceof EncryptedSessionKeyPacket)) {
                            return;
                        }
                    }
                    
                    this.sendPacket(new PacketMessage(packet, priority));
                    break;
                }
            }
        }
    }
}
