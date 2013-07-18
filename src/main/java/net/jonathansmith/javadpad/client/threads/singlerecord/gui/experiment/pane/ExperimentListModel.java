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
package net.jonathansmith.javadpad.client.threads.singlerecord.gui.experiment.pane;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author jonathansmith
 */
public class ExperimentListModel extends AbstractListModel {
    
    public List<Experiment> experiments = new ArrayList<Experiment> () ;

    public int getSize() {
        return this.experiments.size();
    }
    
    public void addElement(Experiment experiment) {
        this.experiments.add(experiment);
        this.fireIntervalAdded(this, this.experiments.size() - 1, this.experiments.size() - 1);
    }

    public Object getElementAt(int i) {
        Experiment experiment = this.experiments.get(i);
        return experiment.getName();
    }
    
    public String getDescription(int i) {
        Experiment experiment = this.experiments.get(i);
        return experiment.getDescription();
    }
    
    public Experiment getData(int i) {
        return this.experiments.get(i);
    }
    
    public void setData(RecordsList<Record> list) {
        this.experiments.clear();
        
        for (Record data : list) {
            if (data instanceof Experiment) {
                this.addElement((Experiment) data);
            }
        }
    }
    
    public void clearData() {
        this.experiments.clear();
    }
}