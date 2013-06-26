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
package net.jonathansmith.javadpad.common.network.packet;

/**
 *
 * @author Jon
 */
public interface LargePayloadPacket {
    
    public abstract boolean isPayloadLarge(int payloadNumber);
    
    public abstract double getLargePayloadSize(int payloadNumber);
    
    public abstract byte[] writeLargePayloadFragment(int payloadNumber, byte[] providedChunk, int chunkNumber);
    
    public abstract void finishWriting();
    
    public abstract void processLargePayloadFragment(int payloadNumber, byte[] providedChunk, int chunkNumber);
    
    public abstract void finishReading();

    public boolean isHealthy();
}
