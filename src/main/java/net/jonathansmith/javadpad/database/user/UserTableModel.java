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
package net.jonathansmith.javadpad.database.user;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Jon
 */
public class UserTableModel extends AbstractTableModel {

    public List<User> users = new ArrayList<User> ();
    
    private String[] columnNames = {
        "Username",
        "First Name",
        "Last Name",
        "Experiment No."
    };
    
    public UserTableModel() {}
    
    public int getRowCount() {
        return users.size();
    }

    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int i) {
        return this.columnNames[i];
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    @Override
    public void setValueAt(Object o, int i, int i1) {}

    public Object getValueAt(int i, int i1) {
        User user = this.users.get(i);
        
        switch (i1) {
            case 1:     return user.getUsername();
            case 2:     return user.getFirstName();
            case 3:     return user.getLastName();
            case 4:     return user.getExperiments().size();
            default:    return null;
        }
    }
    
    public User getData(int rowNum) {
        return this.users.get(rowNum);
    }
    
    public void setData(List<User> users) {
        this.users = users;
    }
}
