package jonathansmith.dpad.server.network;

import java.net.SocketAddress;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.common.crypto.CryptographyManager;
import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.network.NetworkManager;
import jonathansmith.dpad.common.network.NetworkSession;
import jonathansmith.dpad.common.network.packet.play.RuntimeDisconnectPacket;

import jonathansmith.dpad.server.network.channel.ServerChannelInitialiser;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Server side network manager
 */
public class ServerNetworkManager extends NetworkManager {

    private final List<NetworkSession> sessions = Collections.synchronizedList(new ArrayList<NetworkSession>());

    private final ServerBootstrap serverBootstrap;
    private final KeyPair         serverKeyPair;

    public ServerNetworkManager(Engine engine, SocketAddress address, boolean isLocal) {
        super(engine, address, "Netty Server IO #%d", isLocal);

        this.serverBootstrap = new ServerBootstrap();
        this.serverKeyPair = CryptographyManager.createNewKeyPair();
    }

    public void addSession(ServerNetworkSession session) {
        this.sessions.add(session);
    }

    public KeyPair getKeyPair() {
        return this.serverKeyPair;
    }

    @Override
    public void run() {
        while (this.isAlive && !this.hasError) {
            synchronized (this.sessions) {
                Iterator<NetworkSession> iter = this.sessions.iterator();

                while (iter.hasNext()) {
                    final NetworkSession session = iter.next();

                    if (session.hasChannelInitialised() && !session.isChannelOpen()) {
                        iter.remove();

                        if (session.getExitMessage() != null) {
                            session.getNetworkProtocol().onDisconnect(session.getExitMessage());
                        }

                        else if (session.getNetworkProtocol() != null) {
                            session.getNetworkProtocol().onDisconnect("Disconnected");
                        }
                    }

                    else {
                        try {
                            session.processReceivedPackets();
                        }

                        catch (Exception ex) {
                            if (session.isLocalChannel()) {
                                this.engine.error("Error processing packets on local channel", ex);
                                this.shutdown(true);
                            }

                            else {
                                this.engine.warn("Failed to process inbound packet from: " + session.getSocketAddress(), ex);
                            }

                            session.scheduleOutboundPacket(new RuntimeDisconnectPacket("Internal server error"), new GenericFutureListener[]{new GenericFutureListener() {
                                @Override
                                public void operationComplete(Future f) {
                                    session.closeChannel("Internal Server Error");
                                }
                            }});

                            session.disableAutoRead();
                        }
                    }
                }
            }
        }

        for (final NetworkSession session : this.sessions) {
            if (!session.isLocalChannel() && session.isChannelOpen()) {
                session.scheduleOutboundPacket(new RuntimeDisconnectPacket("Server shutdown"), new GenericFutureListener[]{new GenericFutureListener() {
                    @Override
                    public void operationComplete(Future future) throws Exception {
                        session.shutdown(ServerNetworkManager.this.hasError);
                    }
                }});
            }
        }

        this.shutdown(false);
    }

    @Override
    public void buildBootstrap() {
        this.serverBootstrap.group(this.getEventLoopGroups());
        this.serverBootstrap.childHandler(new ServerChannelInitialiser(this.engine, this));

        if (this.isLocalConnection()) {
            this.serverBootstrap.channel(LocalServerChannel.class);
        }

        else {
            this.serverBootstrap.channel(NioServerSocketChannel.class);
        }

        this.setChannelFuture(this.serverBootstrap.localAddress(this.getSocketAddress()).bind());
        this.getChannelFuture().syncUninterruptibly();
    }
}
