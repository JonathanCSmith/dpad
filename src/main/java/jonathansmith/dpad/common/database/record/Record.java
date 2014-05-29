package jonathansmith.dpad.common.database.record;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import jonathansmith.dpad.common.database.util.IdentityGenerator;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Super class for all database entries. Inherently serializable for network transport.
 * Contains a UUID for database entry management as well as hashcoding and implied equals methods.
 */
@MappedSuperclass
public abstract class Record implements Serializable {

    private String uuid = IdentityGenerator.createIdentity();

    @Id
    @Column(name = "UUID")
    public String getUUID() {
        return this.uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public abstract void addToChildren(Record record);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public int hashCode() {
        UUID uuid = UUID.fromString(this.uuid);
        return uuid.hashCode();
    }
}
