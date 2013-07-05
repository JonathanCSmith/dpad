/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.server;

import java.io.File;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Level;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import net.jonathansmith.javadpad.DPAD;
import net.jonathansmith.javadpad.DPAD.Platform;
import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.records.AnalyserDataset;
import net.jonathansmith.javadpad.common.database.records.AnalyserPluginRecord;
import net.jonathansmith.javadpad.common.database.records.DataType;
import net.jonathansmith.javadpad.common.database.records.Equipment;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.database.records.LoaderDataset;
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;
import net.jonathansmith.javadpad.common.database.records.Sample;
import net.jonathansmith.javadpad.common.database.records.Template;
import net.jonathansmith.javadpad.common.database.records.TimeCourseData;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.gui.TabbedGUI;
import net.jonathansmith.javadpad.common.network.protocol.CommonPipelineFactory;
import net.jonathansmith.javadpad.common.util.filesystem.FileSystem;
import net.jonathansmith.javadpad.common.util.logging.DPADLoggerFactory;
import net.jonathansmith.javadpad.common.util.threads.NamedThreadFactory;
import net.jonathansmith.javadpad.server.database.connection.DatabaseConnection;
import net.jonathansmith.javadpad.server.database.recordaccess.DatabaseManager;
import net.jonathansmith.javadpad.server.gui.ServerGUI;
import net.jonathansmith.javadpad.server.network.session.SessionRegistry;

/**
 *
 * @author jonathansmith
 */
public class Server extends Engine {
    
    private final ChannelGroup channelGroup;
    private final SessionRegistry sessionRegistry;
    
    private ServerBootstrap bootstrap;
    private DatabaseManager databaseManager;
    
    public Server(DPAD main, String host, int port) {
        super(main, Platform.SERVER, host, port);
        
        this.setGUI((TabbedGUI) new ServerGUI(this));
        this.setFileSystem(new FileSystem(this));
        
        // Netty setup
        this.channelGroup = new DefaultChannelGroup();
        this.sessionRegistry = new SessionRegistry(this);
        this.bootstrap = new ServerBootstrap();
    }
    
    public ChannelGroup getChannelGroup() {
        return this.channelGroup;
    }
    
    public SessionRegistry getSessionRegistry() {
        return this.sessionRegistry;
    }
    
    public DatabaseConnection getSessionConnection() {
        return this.databaseManager.getConnection();
    }

    @Override
    public void init() {
        super.init();
        if (this.errored) {
            this.isAlive = false;
            return;
        }
        
        this.setCompleteLogger(DPADLoggerFactory.getInstance().getLogger(this));
        
        // TODO: Config?
        // TODO: Default properties
        
        this.info("Beginning database initialisation");
        
        // Add our appenders to existing loggers
        DPADLoggerFactory.getInstance().getLogger(this, "org.jboss.logging", Level.WARN);
        DPADLoggerFactory.getInstance().getLogger(this, "org.jboss.netty", Level.INFO);
        DPADLoggerFactory.getInstance().getLogger(this, "org.hibernate", Level.WARN);
        
        Configuration config = this.buildSessionConfiguration();
        boolean isNew = config.getProperty("hibernate.hbm2ddl.auto").contentEquals("create");
        
        ServiceRegistry registry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
        
        SessionFactory sessionFactory;
        try {
            sessionFactory = config.buildSessionFactory(registry);
            sessionFactory.openSession();

            if (isNew) {
                this.warn("");
                this.warn("==========================================================");
                this.warn("Retrying database creation for a second time as it is new!");
                this.warn("==========================================================");
                this.warn("");
                
                sessionFactory.close();
                sessionFactory = config.buildSessionFactory(registry);
                sessionFactory.openSession();
            }
        }

        catch (HibernateException ex) {
            this.forceShutdown("Connection to: " + this.getFileSystem().getDatabaseDirectory() + " was rejected or unavailable", ex);
            return;
        }
        
        this.databaseManager = new DatabaseManager(sessionFactory);
        
        this.info("Beginning network initialisation");
        
        ChannelFactory factory;
        SocketAddress address;
        if (!this.hostName.contentEquals("local")) {
            ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Boss", true));
            ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("DPAD - Server - Worker", true));
            factory = new NioServerSocketChannelFactory(boss, worker);
            address = new InetSocketAddress(this.portNumber);
        }
        
        else {
            factory = new DefaultLocalServerChannelFactory();
            address = new LocalAddress(this.portNumber);
        }
        
        this.bootstrap.setFactory(factory);
        this.bootstrap.setOption("child.tcpNoDelay", true);
        this.bootstrap.setOption("child.keepAlive", true);
        
        ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this);
        this.bootstrap.setPipelineFactory(pipelineFactory);
        
        Channel acceptor = this.bootstrap.bind(address);
        if (!acceptor.isBound()) {
            this.bootstrap.releaseExternalResources();
            this.error("Server failed to bind port: " + this.portNumber + " is there already a server running?");
            this.isAlive = false;
            this.errored = true;
            return;
        }
        
        this.info("Server bound to port: " + this.portNumber);
        this.channelGroup.add(acceptor);
        this.isAlive = true;
    }

    @Override
    public void run() {
        while (this.isAlive && !this.errored) {
            try {
                Thread.sleep(100);
            } 
            
            catch (InterruptedException ex) {
                this.forceShutdown("Main thread interrupted", ex);
            }
        }
        
//        if (this.errored) {
//            this.main.setErrored("Error in server main thread", null);
//        }
        
        this.finish();
    }
    
    public void finish() {
        // Hibernate, bonecp shutdown TODO: verify this is all
        this.databaseManager.closeAll();
        
        // Netty shutdown
        this.channelGroup.close().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();
        this.warn("Server stopped");
        
        // Allow main runtime preservation if possible
        this.main.removeTab(this.gui);
    }

    @Override
    public void saveAndShutdown() {
        // TODO: worker threads shutdown
        this.getEventThread().shutdown(false);
        this.getPluginManager().shutdown(false);
        this.sessionRegistry.shutdownSessions(false);
        
        this.info("Shuttdown called on: " + this.platform.toString());
        this.isAlive = false;
    }

    @Override
    public void forceShutdown(String cause, Throwable ex) {
        // TODO: worker threads force shutdown
        this.getEventThread().shutdown(true);
        this.getPluginManager().shutdown(true);
        this.sessionRegistry.shutdownSessions(true);
        
        this.error(cause, ex);
        this.isAlive = false;
        this.errored = true;
    }
    
    private Configuration buildSessionConfiguration() {
        Configuration config = new Configuration();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:h2:file:" + this.getFileSystem().getDatabaseDirectory() + "/JDPADDatabase");
        
        config.setProperty("connection.provider.provider_class", "com.jolbox.bonecp.provider.BoneCPConnectionProvider");
        config.setProperty("bonecp.partitionCount", "3");
        config.setProperty("bonecp.maxConnectionsPerPartition", "15");
        config.setProperty("bonecp.minConnectionsPerPartition", "2");
        config.setProperty("bonecp.acquireIncrement", "3");
        
        config.setProperty("hibernate.connection.username", "sa");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.current_session_context_class", "thread");
        
        File file = new File(this.getFileSystem().getDatabaseDirectory() + "/JDPADDatabase.h2.db");
        if (!file.exists()) {
            config.setProperty("hibernate.hbm2ddl.auto", "create");
            
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "validate");
        }
        
        config = this.addMappings(config);
        return config;
    }
    
    private Configuration addMappings(Configuration config) {
        config.addAnnotatedClass(AnalyserDataset.class);
        config.addAnnotatedClass(AnalyserPluginRecord.class);
        config.addAnnotatedClass(DataType.class);
        config.addAnnotatedClass(Equipment.class);
        config.addAnnotatedClass(Experiment.class);
        config.addAnnotatedClass(LoaderDataset.class);
        config.addAnnotatedClass(LoaderPluginRecord.class);
        config.addAnnotatedClass(Sample.class);
        config.addAnnotatedClass(Template.class);
        config.addAnnotatedClass(TimeCourseData.class);
        config.addAnnotatedClass(User.class);
        return config;
    }
}
