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
package net.jonathansmith.javadpad.common.network.message;

import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;

/**
 *
 * @author Jon
 */
public class PacketMessage {
    
    private final Packet packet;
    private final PacketPriority priority;
    
    public PacketMessage(Packet p, PacketPriority priority) {
        this.packet = p;
        this.priority = priority;
    }
    
    public Packet getPacket() {
        return this.packet;
    }
    
    public PacketPriority getPriority() {
        return this.priority;
    }
}
