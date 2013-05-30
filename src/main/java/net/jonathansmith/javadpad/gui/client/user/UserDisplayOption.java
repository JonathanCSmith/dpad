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
package net.jonathansmith.javadpad.gui.client.user;

import java.awt.event.ActionListener;
import javax.swing.JPanel;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.user.panel.DisplayUserPane;
import net.jonathansmith.javadpad.gui.client.user.panel.ExistingUserPane;
import net.jonathansmith.javadpad.gui.client.user.panel.NewUserPane;
import net.jonathansmith.javadpad.gui.client.user.toolbar.UserToolbar;

/**
 *
 * @author Jon
 */
public class UserDisplayOption extends DisplayOption {
    
    public DisplayUserPane displayPanel;
    public NewUserPane newUserPane;
    public ExistingUserPane existingUserPane;
    public UserToolbar userToolbar;
    
    public UserDisplayOption() {
        super();
        this.displayPanel = new DisplayUserPane();
        this.newUserPane = new NewUserPane();
        this.existingUserPane = new ExistingUserPane();
        this.userToolbar = new UserToolbar();
        this.currentPanel = this.displayPanel;
        this.currentToolbar = this.userToolbar;
    }
    
    @Override
    public void setCurrentView(JPanel panel) {
        super.setCurrentView(panel);
        if (panel instanceof DisplayUserPane) {
            User user = this.controller.getSessionUser();
            this.displayPanel.setCurrentUser(user);
        }
    }

    @Override
    public void validateState(DPADController controlller) {}

    @Override
    public void addDisplayListener(ActionListener listener) {
        this.userToolbar.newUser.addActionListener(listener);
        this.userToolbar.loadUser.addActionListener(listener);
        this.userToolbar.userBack.addActionListener(listener);
        this.newUserPane.submit.addActionListener(listener);
        this.existingUserPane.submit.addActionListener(listener);
    }
}
