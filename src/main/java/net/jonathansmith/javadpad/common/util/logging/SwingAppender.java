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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Jon
 */
public class SwingAppender extends AppenderSkeleton {

    private final PatternLayout layout;
    private final LogDisplay target;
    
    public SwingAppender(PatternLayout layout, LogDisplay area) {
        this.layout = layout;
        this.target = area;
    }
    
    @Override
    protected void append(LoggingEvent le) {
        this.target.updateLog(layout.format(le));
    }

    public void close() {}

    public boolean requiresLayout() {
        return true;
    }
}
