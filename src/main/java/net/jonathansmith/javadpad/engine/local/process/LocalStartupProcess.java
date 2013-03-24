/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jonathansmith.javadpad.engine.local.process;

import java.io.File;

import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.engine.database.DatabaseConnection;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import net.jonathansmith.javadpad.engine.database.entry.ExperimentEntry;

/**
 * Note this should be client side in the future, will probably require
 * a major refactor :S
 * @author Jon
 */
public class LocalStartupProcess extends RuntimeProcess {
    
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
    
    public LocalStartupProcess(DPADLocalEngine parent) {
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
                this.engine.logger.severe("Database thread interrupted");
                ex.printStackTrace();
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
            this.engine.logger.severe("Connection to: " + this.path + " was rejected or unavailable");
            ex.printStackTrace();
            this.forceShutdown(true);
            return;
        }
        
        DatabaseConnection databaseConnection = new DatabaseConnection(this.factory, this.registry);
        this.state = State.CONNECTED;
        this.engine.setDatabaseConnection(databaseConnection);
        
        // Load plugins...
        
        this.forceShutdown(false);
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
        config.addAnnotatedClass(ExperimentEntry.class);
        return config;
    }
}
