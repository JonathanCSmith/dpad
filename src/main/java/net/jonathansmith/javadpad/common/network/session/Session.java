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

import java.util.Random;

import org.jboss.netty.channel.Channel;

import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.common.database.User;
import net.jonathansmith.javadpad.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.Engine;

/**
 *
 * @author Jon
 */
public abstract class Session {
    
    public enum State {
        EXCHANGING_HANDSHAKE,
        EXCHANGING_AUTHENTICATION,
        RUNNING;
    }
    
    public final Channel channel;
    public final Engine engine;
    
    private final Random random = new Random();
    private final String id = Long.toString(random.nextLong(), 16).trim();
    
    private State state = State.EXCHANGING_HANDSHAKE;
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
    
    public State getState() {
        return this.state;
    }
    
    public void incrementState() {
        int currentState = this.state.ordinal();
        this.state = State.values()[currentState + 1];
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

    public abstract void addPacketToSend(PacketPriority priority, Packet p);
    
    public abstract void addPacketToReceive(PacketPriority priority, Packet p);
    
    public void sendPacketMessage(PacketMessage pm) {
        this.channel.write(pm);
    }

    public abstract void disconnect();
    
    public abstract void dispose();
}