package jonathansmith.dpad.server.database.record.serverconfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import jonathansmith.dpad.api.database.Record;

/**
 * Created by Jon on 17/07/2014.
 * <p/>
 * Server configuration record. Holds the server behaviour information.
 */
@Entity
@Table(name = "Server_Configuration")
public class ServerConfigurationRecord extends Record {

    private boolean userVerificationRequired;
    private boolean groupCreationVerificationRequired;

    @Column(name = "User_Verification")
    public boolean isUserVerificationRequired() {
        return this.userVerificationRequired;
    }

    public void setUserVerificationRequired(boolean flagState) {
        this.userVerificationRequired = flagState;
    }

    @Column(name = "Group_Creation_Verification")
    public boolean isGroupCreationVerificationRequired() {
        return this.groupCreationVerificationRequired;
    }

    public void setGroupCreationVerificationRequired(boolean flagState) {
        this.groupCreationVerificationRequired = flagState;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerConfigurationRecord)) {
            return false;
        }

        ServerConfigurationRecord config = (ServerConfigurationRecord) o;

        if (this.getUUID().contentEquals((config.getUUID()))
                && this.isUserVerificationRequired() == config.isUserVerificationRequired()
                && this.isGroupCreationVerificationRequired() == config.isGroupCreationVerificationRequired()) {
            return true;
        }

        return false;
    }
}
