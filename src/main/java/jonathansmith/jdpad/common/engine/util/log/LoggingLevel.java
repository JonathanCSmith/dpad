package jonathansmith.jdpad.common.engine.util.log;

import org.apache.log4j.Level;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Used to dictate the various logging levels for loggers
 */
public class LoggingLevel {

    private final Level verboseConsoleMin;
    private final Level verboseConsoleMax;
    private final Level standardConsoleMin;
    private final Level standardConsoleMax;
    private final Level verboseFileMin;
    private final Level verboseFileMax;
    private final Level standardFileMin;
    private final Level standardFileMax;

    public LoggingLevel(Level verboseConsoleMin, Level standardConsoleMin, Level verboseFileMin, Level standardFileMin) {
        this(verboseConsoleMin, Level.FATAL, standardConsoleMin, Level.FATAL, verboseFileMin, Level.FATAL, standardFileMin, Level.FATAL);
    }

    public LoggingLevel(Level verboseConsoleMin, Level verboseConsoleMax, Level standardConsoleMin, Level standardConsoleMax, Level verboseFileMin, Level verboseFileMax, Level standardFileMin, Level standardFileMax) {
        this.verboseConsoleMin = verboseConsoleMin;
        this.verboseConsoleMax = verboseConsoleMax;
        this.standardConsoleMin = standardConsoleMin;
        this.standardConsoleMax = standardConsoleMax;
        this.verboseFileMin = verboseFileMin;
        this.verboseFileMax = verboseFileMax;
        this.standardFileMin = standardFileMin;
        this.standardFileMax = standardFileMax;
    }

    public Level getVerboseConsoleMin() {
        return this.verboseConsoleMin;
    }

    public Level getVerboseConsoleMax() {
        return this.verboseConsoleMax;
    }

    public Level getStandardConsoleMin() {
        return this.standardConsoleMin;
    }

    public Level getStandardConsoleMax() {
        return this.standardConsoleMax;
    }

    public Level getVerboseFileMin() {
        return this.verboseFileMin;
    }

    public Level getVerboseFileMax() {
        return this.verboseFileMax;
    }

    public Level getStandardFileMin() {
        return this.standardFileMin;
    }

    public Level getStandardFileMax() {
        return this.standardFileMax;
    }
}
