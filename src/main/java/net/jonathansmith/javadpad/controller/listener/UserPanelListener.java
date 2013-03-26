/* 
 * Copyright (C) 2013 Jonathan Smith
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

package net.jonathansmith.javadpad.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.jonathansmith.javadpad.controller.DPADController;
import net.jonathansmith.javadpad.database.user.User;
import net.jonathansmith.javadpad.database.user.UserManager;
import net.jonathansmith.javadpad.gui.user.panel.ExistingUserPane;
import net.jonathansmith.javadpad.gui.user.panel.NewUserPane;
import net.jonathansmith.javadpad.util.logging.DPADLogger;

/**
 * UserListener
 *
 * @author Jonathan Smith
 */
public class UserPanelListener implements ActionListener {

    private DPADController controller;
    
    public UserPanelListener(DPADController controller) {
        this.controller = controller;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.controller.getGui().userSelect.userToolbar.newUser) {
            if (!(this.controller.getGui().userSelect.getCurrentView() instanceof NewUserPane)) {
                this.controller.getGui().userSelect.setCurrentView(this.controller.getGui().userSelect.newUserPane);
            }
        }
        
        else if (evt.getSource() == this.controller.getGui().userSelect.userToolbar.loadUser) {
            if (!(this.controller.getGui().userSelect.getCurrentView() instanceof ExistingUserPane)) {
                this.controller.getGui().userSelect.setCurrentView(this.controller.getGui().userSelect.existingUserPane);
                this.controller.getGui().userSelect.existingUserPane.insertData(UserManager.getInstance().loadUsers());
            }
        }
        
        else if (evt.getSource() == this.controller.getGui().userSelect.userToolbar.userBack) {
            if (this.controller.getGui().userSelect.getCurrentView() instanceof NewUserPane) {
                this.controller.getGui().userSelect.setCurrentView(this.controller.getGui().userSelect.blankPanel);
            }
            
            else if (this.controller.getGui().userSelect.getCurrentView() instanceof ExistingUserPane) {
                this.controller.getGui().userSelect.setCurrentView(this.controller.getGui().userSelect.blankPanel);
                
            } else {
                this.controller.getEngine().sendQuitToRuntime();
            }
        }
        
        else if (evt.getSource() == this.controller.getGui().userSelect.newUserPane.submit) {
            String username = this.controller.getGui().userSelect.newUserPane.username.getText();
            String firstName = this.controller.getGui().userSelect.newUserPane.firstName.getText();
            String lastName = this.controller.getGui().userSelect.newUserPane.lastName.getText();
            char[] password = this.controller.getGui().userSelect.newUserPane.password.getPassword();
            
            if (!username.contentEquals("") && !firstName.contentEquals("") && !lastName.contentEquals("") && password.length != 0) {
                User user = new User();
                user.setUsername(username);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPassword(password);
                
                UserManager manager = UserManager.getInstance();
                manager.saveNewUser(user);
                
            } else {
                DPADLogger.warning("Some fields were incomplete, returning. Your entry was not saved.");
            }
            
            this.controller.getGui().userSelect.setCurrentView(this.controller.getGui().userSelect.blankPanel);
        }
        
        else if (evt.getSource() == this.controller.getGui().userSelect.existingUserPane.submit) {
            User user = this.controller.getGui().userSelect.existingUserPane.getSelectedUser();
            
            if (user == null) {
                DPADLogger.warning("No user selected, returning to main user screen.");
            
            } else {
                this.controller.setSessionUser(user);
            }
            
            this.controller.getGui().userSelect.setCurrentView(this.controller.getGui().userSelect.blankPanel);
        }
    }
}
