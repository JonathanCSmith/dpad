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
package net.jonathansmith.javadpad.common.database;

import net.jonathansmith.javadpad.common.network.session.DatabaseRecord;
import net.jonathansmith.javadpad.server.database.recordsaccess.QueryType;

/**
 * NOTE! Current implementation of db packets limits the data types to 256!
 * @author Jon
 */
public enum SessionData {
    ALL_USERS(DatabaseRecord.USER, QueryType.SESSION_SPECIFIC),
    USER(DatabaseRecord.USER, QueryType.SINGLE),
    
    USER_EXPERIMENTS(DatabaseRecord.EXPERIMENT, QueryType.SESSION_SPECIFIC),
    EXPERIMENT(DatabaseRecord.EXPERIMENT, QueryType.SINGLE);
    
    private final DatabaseRecord recordType;
    private final QueryType queryType;
    
    private SessionData(DatabaseRecord record, QueryType type) {
        this.recordType = record;
        this.queryType = type;
    }
    
    public DatabaseRecord getRecordType() {
        return this.recordType;
    }
    
    public QueryType getQueryType() {
        return this.queryType;
    }
    
    public boolean isQueryType(QueryType type) {
        return this.queryType == type;
    }
    
    public static SessionData getSessionDataFromDatabaseRecordAndQuery(DatabaseRecord record, QueryType query) {
        for (SessionData data : SessionData.values()) {
            if (data.getRecordType() == record && data.getQueryType() == query) {
                return data;
            }
        }
        
        return null;
    }
}