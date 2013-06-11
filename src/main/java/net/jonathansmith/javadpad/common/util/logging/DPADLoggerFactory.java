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
    
    private final Map<Class, Logger> storedLoggers = new HashMap<Class, Logger> (); 
    
    public static DPADLoggerFactory getInstance() {
        if (instance == null) {
            instance = new DPADLoggerFactory();
        }
        
        return instance;
    }
    
    public Logger getLogger(Engine engine) {
        if (this.storedLoggers.containsKey(engine.getClass())) {
            return this.storedLoggers.get(engine.getClass());
        }
        
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(engine.getClass());
        logger.setLevel(Level.ALL);
        
        PatternLayout pattern = new PatternLayout("%d [%p|%c|%C{1}] %m%n");
        
        SwingAppender console = new SwingAppender(pattern, engine.getGUI());
        LevelRangeFilter consoleFilter = new LevelRangeFilter();
        if (engine.isDebug()) {
            consoleFilter.setLevelMin(Level.ALL);
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
        Logger universalLogger = LoggerFactory.getLogger(engine.getClass());
        this.storedLoggers.put(engine.getClass(), universalLogger);
        return universalLogger;
    }
}
