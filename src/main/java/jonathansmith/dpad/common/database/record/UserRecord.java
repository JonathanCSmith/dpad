package jonathansmith.dpad.common.database.record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User database record
 */
@Entity
@Table(name = "User")
public class UserRecord extends Record {

    private String  username   = "";
    private String  password   = "";
    private boolean isVerified = false;

    @Column(name = "Username")
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "Password")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String salt) {
        this.password = salt;
    }

    @Column(name = "User_Verification")
    public boolean isVerified() {
        return this.isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserRecord) {
            UserRecord user = (UserRecord) o;
            if (user.getUsername().contentEquals(this.username)
                    && user.isVerified() == this.isVerified) {
                return true;
            }
        }

        return false;
    }
}
