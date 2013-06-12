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
package net.jonathansmith.javadpad.common.network.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.common.database.User;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;

/**
 *
 * @author Jon
 */
public abstract class Session {
    
    public enum NetworkThreadState {
        EXCHANGING_HANDSHAKE,
        EXCHANGING_AUTHENTICATION,
        RUNNING;
    }
    
    public final Channel channel;
    public final Engine engine;
    
    private final Random random = new Random();
    private final String id = Long.toString(random.nextLong(), 16).trim();
    
    public NetworkThread incoming;
    public NetworkThread outgoing;
    public Map<String, List> arrivedSessionData = new HashMap<String, List> ();
    
    private NetworkThreadState state = NetworkThreadState.EXCHANGING_HANDSHAKE;
    private User user;
    private Experiment experiment;
    private Batch batch;
    
    public Session(Engine eng, Channel channel) {
        this.engine = eng;
        this.channel = channel;
    }

    public String getSessionID() {
        return this.id;
    }
    
    public NetworkThreadState getState() {
        return this.state;
    }
    
    public void incrementState() {
        if (this.state == NetworkThreadState.RUNNING) {
            return;
        }
        
        int currentState = this.state.ordinal();
        this.state = NetworkThreadState.values()[currentState + 1];
        
        if (this.state == NetworkThreadState.RUNNING) {
            this.engine.info("Session: " + this.getSessionID() + " has been authenticated for full interaction");
        }
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Experiment getExperiment() {
        return this.experiment;
    }
    
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }
    
    public Batch getBatch() {
        return this.batch;
    }
    
    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public void addPacketToSend(PacketPriority priority, Packet p) {
        this.outgoing.addPacket(priority, p);
    }

    public void addPacketToReceive(PacketPriority priority, Packet p) {
        this.incoming.addPacket(priority, p);
    }
    
    public void sendPacketMessage(PacketMessage pm) {
        this.channel.write(pm);
    }
    
    public void addArrivedDataset(String handle, List data) {
        if (this.arrivedSessionData.containsKey(handle)) {
            this.arrivedSessionData.remove(handle);
        }
        
        this.arrivedSessionData.put(handle, data);
    }
    
    public void start() {
        this.incoming.start();
        this.outgoing.start();
    }
    
    public abstract void shutdown(boolean force);

    public abstract void disconnect();
    
    public abstract void dispose();
}
