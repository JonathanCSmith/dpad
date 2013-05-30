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

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.database.user.UserManager;
import net.jonathansmith.javadpad.engine.DPADEngine;
import net.jonathansmith.javadpad.engine.local.DPADLocalEngine;
import net.jonathansmith.javadpad.gui.client.DisplayOption;
import net.jonathansmith.javadpad.gui.client.user.panel.DisplayUserPane;
import net.jonathansmith.javadpad.gui.client.user.panel.ExistingUserPane;
import net.jonathansmith.javadpad.gui.client.user.panel.NewUserPane;
import net.jonathansmith.javadpad.gui.client.user.toolbar.UserToolbar;
import net.jonathansmith.javadpad.util.RuntimeType;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

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
        
        this.userToolbar.newUser.addActionListener(this);
        this.userToolbar.loadUser.addActionListener(this);
        this.userToolbar.userBack.addActionListener(this);
        this.newUserPane.submit.addActionListener(this);
        this.existingUserPane.submit.addActionListener(this);
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

    public void actionPerformed(ActionEvent evt) {
        DPADEngine engine = this.controller.getEngine();
        if (engine == null || !(engine instanceof DPADLocalEngine)) {
            return;
        }
        
        UserDisplayOption display = (UserDisplayOption) RuntimeType.USER_SELECT.getDisplay();
        if (evt.getSource() == display.userToolbar.newUser) {
            if (!(display.getCurrentView() instanceof NewUserPane)) {
                display.setCurrentView(display.newUserPane);
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == display.userToolbar.loadUser) {
            if (!(display.getCurrentView() instanceof ExistingUserPane)) {
                display.setCurrentView(display.existingUserPane);
                display.existingUserPane.insertData(UserManager.getInstance().loadAll());
                this.controller.getGui().validateState();
            }
        }
        
        else if (evt.getSource() == display.userToolbar.userBack) {
            if (display.getCurrentView() instanceof NewUserPane) {
                display.setCurrentView(display.displayPanel);
                this.controller.getGui().validateState();
            }
            
            else if (display.getCurrentView() instanceof ExistingUserPane) {
                display.setCurrentView(display.displayPanel);
                this.controller.getGui().validateState();
                
            } else {
                this.controller.getEngine().sendQuitToRuntime();
            }
        }
        
        else if (evt.getSource() == display.newUserPane.submit) {
            String username = display.newUserPane.username.getText();
            display.newUserPane.username.setText("");
            
            String firstName = display.newUserPane.firstName.getText();
            display.newUserPane.firstName.setText("");
            
            String lastName = display.newUserPane.lastName.getText();
            display.newUserPane.lastName.setText("");
            
            char[] password = display.newUserPane.password.getPassword();
            display.newUserPane.password.setText("");
            
            if (!username.contentEquals("") 
                && !firstName.contentEquals("") 
                    && !lastName.contentEquals("") 
                        && password.length != 0) {
                User user = new User();
                user.setUsername(username);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPassword(password);
                
                UserManager manager = UserManager.getInstance();
                manager.saveNew(user);
                this.controller.setSessionUser(user);
                
            } else {
                DPADLogger.warning("Some fields were incomplete, returning. Your entry was not saved.");
            }
            
            display.setCurrentView(display.displayPanel);
            this.controller.getGui().validateState();
        }
        
        else if (evt.getSource() == display.existingUserPane.submit) {
            User user = display.existingUserPane.getSelectedUser();
            
            if (user == null) {
                DPADLogger.warning("No user selected, returning to main user screen.");
            
            } else {
                this.controller.setSessionUser(user);
            }
            
            display.setCurrentView(display.displayPanel);
            this.controller.getGui().validateState();
        }
    }
}
