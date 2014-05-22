package jonathansmith.dpad.common.engine.util.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;

import org.slf4j.Logger;

import jonathansmith.dpad.common.engine.Engine;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 23/03/14.
 * <p/>
 * Logger factory for all engines to acquire their loggers
 */
public class LoggerFactory {

    private static LoggerFactory instance;

    private final Map<String, Logger> storedLoggers = new HashMap<String, Logger>();

    public static LoggerFactory getInstance() {
        if (instance == null) {
            instance = new LoggerFactory();
        }

        return instance;
    }

    public Logger getLogger(Engine engine, LoggingLevel levels) {
        return this.getLogger(engine, engine.getClass().toString(), levels);
    }

    public Logger getLogger(Engine engine, String key, LoggingLevel levels) {
        if (this.storedLoggers.containsKey(key)) {
            return this.storedLoggers.get(key);
        }

        final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(key);
        logger.setLevel(Level.ALL);

        PatternLayout pattern = new PatternLayout("||" + engine.getPlatform() + "|| %d [%p|%c|%C{1}] %m%n");
        SwingAppender appender = new SwingAppender(pattern, engine.getDisplayTab());

        LevelRangeFilter levelFilter = new LevelRangeFilter();
        if (DPAD.getInstance().isVerboseLogging()) {
            levelFilter.setLevelMin(levels.getVerboseConsoleMin());
            levelFilter.setLevelMax(levels.getVerboseConsoleMax());
        }

        else {
            levelFilter.setLevelMin(levels.getStandardConsoleMin());
            levelFilter.setLevelMax(levels.getStandardConsoleMax());
        }

        appender.addFilter(levelFilter);
        logger.addAppender(appender);

        try {
            String path = engine.getFileSystem().getLogDirectory().getAbsolutePath().concat("\\" + engine.getPlatform() + "_log.log");
            FileAppender fileAppender = new DailyRollingFileAppender(pattern, path, "'.'yyyy-MM-dd");

            LevelRangeFilter fileLevelFilter = new LevelRangeFilter();

            if (DPAD.getInstance().isVerboseLogging()) {
                fileLevelFilter.setLevelMin(levels.getVerboseFileMin());
                fileLevelFilter.setLevelMax(levels.getVerboseFileMax());
            }

            else {
                fileLevelFilter.setLevelMin(levels.getStandardFileMin());
                fileLevelFilter.setLevelMax(levels.getStandardFileMax());
            }

            fileAppender.addFilter(fileLevelFilter);
            fileAppender.setAppend(true);
            fileAppender.activateOptions();
            logger.addAppender(fileAppender);
        }

        catch (IOException ex) {
            logger.error("Could not create file appender for file logging!");
        }

        if (DPAD.getInstance().isVerboseLogging()) {
            ConsoleAppender consoleAppender = new ConsoleAppender(pattern);
            consoleAppender.addFilter(levelFilter);
            logger.addAppender(consoleAppender);
        }

        Logger actualLogger = org.slf4j.LoggerFactory.getLogger(key);
        this.storedLoggers.put(key, actualLogger);
        return actualLogger;
    }
}
