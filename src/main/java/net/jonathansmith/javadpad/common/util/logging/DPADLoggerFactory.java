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
package net.jonathansmith.javadpad.common.util.logging;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.varia.LevelRangeFilter;

import net.jonathansmith.javadpad.common.Engine;

/**
 *
 * @author Jon
 */
public class DPADLoggerFactory {
    
    public static DPADLoggerFactory instance;
    
    private final Map<String, Logger> storedLoggers = new HashMap<String, Logger> (); 
    
    public static DPADLoggerFactory getInstance() {
        if (instance == null) {
            instance = new DPADLoggerFactory();
        }
        
        return instance;
    }
    
    public Logger getLogger(Engine engine) {
        return this.getLogger(engine, engine.getClass().toString());
    }
    
    public Logger getLogger(Engine engine, String name) {
        return this.getLogger(engine, name, Level.ALL);
    }
    
    // If the logger does not exist, it will create one and force it to adopt our
    // logging strategy
    public Logger getLogger(Engine engine, String name, Level level) {
        if (this.storedLoggers.containsKey(name)) {
            return this.storedLoggers.get(name);
        }
        
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
        logger.setLevel(level);
        
        PatternLayout pattern = new PatternLayout("%d [%p|%c|%C{1}] %m%n");
        
        SwingAppender console = new SwingAppender(pattern, (LogDisplay) engine.getGUI());
        LevelRangeFilter consoleFilter = new LevelRangeFilter();
        if (engine.isDebug()) {
            consoleFilter.setLevelMin(level);
            consoleFilter.setLevelMax(Level.FATAL);
        }
        
        else {
            consoleFilter.setLevelMin(Level.INFO);
            consoleFilter.setLevelMax(Level.WARN);
        }
        
        console.addFilter(null);
        logger.addAppender(console);
        
        try {
            FileAppender fileAppender = new DailyRollingFileAppender(pattern, engine.getFileSystem().getLogDirectory().getAbsolutePath().concat("/log.log"), "'.'yyyy-MM-dd");
            LevelRangeFilter fileFilter = new LevelRangeFilter();
            fileFilter.setLevelMin(Level.WARN);
            fileFilter.setLevelMax(Level.FATAL);
            fileAppender.addFilter(fileFilter);
            fileAppender.setAppend(true);
            fileAppender.activateOptions();
            logger.addAppender(fileAppender);
        } 
        
        catch (IOException ex) {
            logger.error("Couldn not create FileAppender for error logging!");
        }
        
        // Hopefully this is the same logger!
        Logger universalLogger = LoggerFactory.getLogger(name);
        this.storedLoggers.put(name, universalLogger);
        return universalLogger;
    }
}
