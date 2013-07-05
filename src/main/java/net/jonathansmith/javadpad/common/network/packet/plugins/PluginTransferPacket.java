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
package net.jonathansmith.javadpad.common.network.packet.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.concurrent.atomic.AtomicBoolean;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.events.plugin.PluginArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LargePayloadPacket;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.session.Session;

import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Jon
 */
public class PluginTransferPacket extends LockedPacket implements LargePayloadPacket {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private PluginRecord plugin;
    private String path;
    private File file;
    private byte[] serializedData;
    private FileInputStream fis = null;
    private FileOutputStream fos = null;
    private boolean hasErrored = false;
    
    public PluginTransferPacket() {
        super();
    }
    
    public PluginTransferPacket(Engine engine, Session session, PluginRecord record, String path) {
        super(engine, session);
        this.plugin = record;
        this.serializeData();
        this.path = path;
        this.file = new File(path);
    }
    
    private void serializeData() {
        if (this.plugin == null) {
            return;
        }
        
        this.serializedData = SerializationUtils.serialize(this.plugin);
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
    public int getNumberOfLockedPayloads() {
        return 2;
    }
    
    @Override
    public boolean isPayloadLarge(int payloadNumber) {
        return payloadNumber == 0 ? false : true;
    }

    @Override
    public int getLockedPayloadSize(int payloadNumber) {
        return payloadNumber == 0 ? this.serializedData.length : 0;
    }

    @Override
    public double getLargePayloadSize(int payloadNumber) {
        if (this.file == null || !this.file.exists()) {
            return 0;
        }
        
        return this.file.length();
    }

    @Override
    public byte[] writeLockedPayload(int payloadNumber) {
        return payloadNumber == 0 ? this.serializedData : null;
    }

    @Override
    public byte[] writeLargePayloadFragment(int payloadNumber, byte[] providedChunk, int chunkNumber) {
        if (this.hasErrored) {
            return null;
        }
        
        try {
            if (this.fis == null) {
                this.fis = new FileInputStream(this.file);
            }
            
            this.fis.read(providedChunk, 8192 * chunkNumber, 8192 * chunkNumber + providedChunk.length);
        } 
        
        catch (Exception ex) {
            this.engine.warn("Error when attempting to send file packet", ex);
            this.hasErrored = true;
            return null;
        }
        
        return providedChunk;
    }
    
    @Override
    public void finishWriting() {
        try {
            this.fis.close();
        } 
        catch (IOException ex) {
            this.engine.warn("Error closing stream", ex);
        }
    }

    @Override
    public void parseLockedPayload(int payloadNumber, byte[] bytes) {
        if (payloadNumber == 0) {
            this.plugin = (PluginRecord) SerializationUtils.deserialize(bytes);
        }
    }

    @Override
    public void processLargePayloadFragment(int payloadNumber, byte[] providedChunk, int chunkNumber) {
        if (this.hasErrored) {
            return;
        }
        
        try {
            if (this.fos == null) {
                this.path = this.engine.getFileSystem().getUpdateDirectory().getAbsolutePath() + "//" + this.plugin.getName() + ".jar";
                this.fos = new FileOutputStream(this.path);
            }
            
            this.fos.write(providedChunk, 8192 * chunkNumber, 8192 * chunkNumber + providedChunk.length);
        } 
        
        catch (Exception ex) {
            this.engine.warn("Error when attempting to send file packet", ex);
            this.hasErrored = true;
        }
    }

    @Override
    public void finishReading() {
        try {
            this.fos.close();
        } 
        
        catch (IOException ex) {
            this.engine.warn("Error closing stream", ex);
        }
    }
    
    @Override
    public void handleClientSide() {
        this.engine.getPluginManager().addPluginFile(new File(this.path));
        
        this.engine.getEventThread().post(new PluginArriveEvent());
    }

    @Override
    public void handleServerSide() {
        this.engine.getPluginManager().addPluginFile(new File(this.path));
    }
    
    @Override
    public boolean isHealthy() {
        return !this.hasErrored;
    }

    @Override
    public String toString() {
        return "Plugin packet with plugin: " + this.plugin.getName();
    }
}