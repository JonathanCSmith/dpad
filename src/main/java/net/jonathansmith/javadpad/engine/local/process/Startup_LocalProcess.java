/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jonathansmith.javadpad.engine.local.process;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import net.jonathansmith.javadpad.database.experiment.Experiment;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.database.DatabaseConnection;
import net.jonathansmith.javadpad.database.batch.Batch;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.plugin.DPADPluginManager;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 * Note this should be client side in the future, will probably require
 * a major refactor :S
 * @author Jon
 */
public class Startup_LocalProcess extends RuntimeProcess {
    
    public enum State {
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
    
    public State getProgressState() {
        return this.state;
    }
    
    public void setAttemptConnection(String path) {
        this.state = State.CONNECTING;
        this.path = path;
    }
    
    private Configuration buildSessionConfiguration() {
        Configuration config = new Configuration();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:h2:file:" + this.path + "/JDPADDatabase");
        config.setProperty("hibernate.connection.username", "sa");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.current_session_context_class", "thread");
        
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
        return config;
    }
    
    public DatabaseConnection getDatabaseConnection() {
        return this.databaseConnection;
    }
    
    public DPADPluginManager getPluginManager() {
        return this.pluginManager;
    }
}
