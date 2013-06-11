/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jonathansmith.javadpad.toberefactored.engine.local.process;

import java.io.File;

import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.common.database.DataSet;
import net.jonathansmith.javadpad.common.database.DataType;
import net.jonathansmith.javadpad.common.database.Equipment;
import net.jonathansmith.javadpad.common.database.Experiment;
import net.jonathansmith.javadpad.common.database.User;
import net.jonathansmith.javadpad.server.database.DatabaseConnection;
import net.jonathansmith.javadpad.toberefactored.engine.common.process.RuntimeProcess;
import net.jonathansmith.javadpad.toberefactored.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.toberefactored.plugin.DPADPluginManager;
import net.jonathansmith.javadpad.toberefactored.util.logging.DPADLogger;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Note this should be client side in the future, will probably require
 * a major refactor :S
 * @author Jon
 */
public class Startup_LocalProcess extends RuntimeProcess {
    
    private enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTION_FAILURE,
        CONNECTED;
    }
    
    public boolean running = false;
    public String path = null;
    public State state = State.DISCONNECTED;
    
    private ServiceRegistry registry;
    private SessionFactory factory;
    
    private DatabaseConnection databaseConnection;
    private DPADPluginManager pluginManager;
    
    public Startup_LocalProcess(DPADLocalEngine parent) {
        super(parent);
    }

    @Override
    public void init() {
        this.running = true;
    }
    
    @SuppressWarnings({"SleepWhileInLoop", "CallToThreadDumpStack"})
    @Override
    public void run() {
        while (this.state == State.DISCONNECTED && this.running) {
            try {
                Thread.sleep(100);
                
            } catch (InterruptedException ex) {
                DPADLogger.severe("Database thread interrupted");
                DPADLogger.logStackTrace(ex);
                this.forceShutdown(true);
                return;
            }
        }
        
        if (!this.running) {
            this.forceShutdown(false);
            return;
        }
        
        Configuration config = this.buildSessionConfiguration();
        this.registry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
        
        try {
            // Connection
            this.state = State.CONNECTING;
            this.factory = config.buildSessionFactory(this.registry);
            this.factory.openSession();
            
        } catch (HibernateException ex) {
            this.state = State.CONNECTION_FAILURE;
            DPADLogger.severe("Connection to: " + this.path + " was rejected or unavailable");
            DPADLogger.logStackTrace(ex);
            this.forceShutdown(true);
            return;
        }
        
        this.databaseConnection = new DatabaseConnection(this.factory, this.registry);
        this.state = State.CONNECTED;
        
        // Load plugins...
        this.pluginManager = new DPADPluginManager();
        this.engine.setupEngine();
    }

    @Override
    public void forceShutdown(boolean error) {
        this.running = false;
        super.end(error);
    }
    
    public boolean isConnected() {
        return this.state == State.CONNECTED;
    }
    
    public void setAttemptConnection(String path) {
        this.state = State.CONNECTING;
        this.path = path;
    }
    
    private Configuration buildSessionConfiguration() {
        Configuration config = new Configuration();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:h2:file:" + this.path + "/JDPADDatabase;TRACE_LEVEL_FILE=4");
        config.setProperty("hibernate.connection.username", "sa");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.current_session_context_class", "thread");
        
        // Logging levels
        config.setProperty("org.hibernate.level", "INFO"); // Info
        config.setProperty("org.hibernate.hql.AST.level", "INFO"); // INFO
        config.setProperty("org.hibernate.SQL.level", "FINE"); // DEBUG
        config.setProperty("org.hibernate.type.level", "FINE");
        config.setProperty("org.hibernate.tool.hbm2ddl.level", "INFO");
        config.setProperty("org.hibernate.engine.level", "FINE");
        config.setProperty("org.hibernate.hql.level", "FINE");
        config.setProperty("org.hibernate.cache.level", "INFO");
        config.setProperty("org.hibernate.jdbc.level", "FINE");
        
        File file = new File(this.path + "/JDPADDatabase.h2.db");
        if (!file.exists()) {
            config.setProperty("hibernate.hbm2ddl.auto", "create");
            
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "validate");
        }
        
        config = this.addMappings(config);
        return config;
    }
    
    private Configuration addMappings(Configuration config) {
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Experiment.class);
        config.addAnnotatedClass(Batch.class);
        config.addAnnotatedClass(DataSet.class);
        config.addAnnotatedClass(DataType.class);
        config.addAnnotatedClass(Equipment.class);
        return config;
    }
    
    public DatabaseConnection getDatabaseConnection() {
        return this.databaseConnection;
    }
    
    public DPADPluginManager getPluginManager() {
        return this.pluginManager;
    }
}