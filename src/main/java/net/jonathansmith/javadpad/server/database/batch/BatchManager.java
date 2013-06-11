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
package net.jonathansmith.javadpad.server.database.batch;

import net.jonathansmith.javadpad.common.database.Batch;
import net.jonathansmith.javadpad.server.database.GenericDAO;
import net.jonathansmith.javadpad.server.database.GenericManager;

/**
 *
 * @author jonathansmith
 */
public class BatchManager extends GenericManager<Batch> {
    
    private static BatchManager instance;
    
    private BatchManager() {
        super(new GenericDAO<Batch, String>(), Batch.class);
    }
    
    public static BatchManager getInstance() {
        if (instance == null) {
            instance = new BatchManager();
        }
        
        return instance;
    }
    
    @Override
    public GenericDAO getDAO() {
        return this.dao;
    }
}
