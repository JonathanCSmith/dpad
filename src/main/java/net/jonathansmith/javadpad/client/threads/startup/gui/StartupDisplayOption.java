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
package net.jonathansmith.javadpad.client.threads.startup.gui;

import net.jonathansmith.javadpad.common.gui.DisplayOption;
import net.jonathansmith.javadpad.client.threads.startup.gui.pane.StartupPane;
import net.jonathansmith.javadpad.client.threads.startup.gui.toolbar.StartupToolbar;

/**
 *
 * @author jonathansmith
 */
public class StartupDisplayOption extends DisplayOption {

    public StartupPane mainPane;
    public StartupToolbar mainToolbar;
    
    public StartupDisplayOption() {
        super();
        this.mainPane = new StartupPane();
        this.mainToolbar = new StartupToolbar();
        this.currentPanel = this.mainPane;
        this.currentToolbar = this.mainToolbar;
    }

    @Override
    public void validateState() {}
}