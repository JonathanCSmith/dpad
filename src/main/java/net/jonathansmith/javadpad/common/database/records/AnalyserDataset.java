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
package net.jonathansmith.javadpad.common.database.records;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.Dataset;
import net.jonathansmith.javadpad.common.database.Record;

/**
 *
 * @author Jon
 */
@Entity
@Table(name = "AnalysedData", uniqueConstraints = @UniqueConstraint(columnNames = "UUID"))
public class AnalyserDataset extends Dataset {

    @Override
    public void addToChildren(Record record) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    @Override
    @Transient
    public DatabaseRecord getType() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }
}
