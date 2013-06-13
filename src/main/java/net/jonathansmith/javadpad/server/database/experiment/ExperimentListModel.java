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
package net.jonathansmith.javadpad.server.database.experiment;

import net.jonathansmith.javadpad.common.database.records.Experiment;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;

/**
 *
 * @author jonathansmith
 */
public class ExperimentListModel extends AbstractListModel {
    
    public List<Experiment> experiments = new ArrayList<Experiment> () ;

    public int getSize() {
        return this.experiments.size();
    }

    public Object getElementAt(int i) {
        Experiment experiment = this.experiments.get(i);
        return experiment.getName();
    }
    
    public String getCurrentDescription(int i) {
        Experiment experiment = this.experiments.get(i);
        return experiment.getDescription();
    }
    
    public Experiment getData(int i) {
        return this.experiments.get(i);
    }
    
    public void setData(Set<Experiment> list) {
        this.experiments.clear();
        this.experiments.addAll(list);
    }
}
