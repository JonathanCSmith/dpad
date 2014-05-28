package jonathansmith.dpad.common.database.record;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Superclass for all plugin database records. Contains all of their pertinent information
 */
@MappedSuperclass
public abstract class PluginRecord extends Record {

    private String name;
    private String description;
    private String version;
    private String author;
    private String organisation;

    @Column(name = "Name", unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "Description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "Version")
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "Author")
    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "Organisation")
    public String getOrganisation() {
        return this.organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PluginRecord) {
            PluginRecord pr = (PluginRecord) o;
            if (this.getUUID().contentEquals(pr.getUUID())
                    && this.getName().contentEquals(pr.getName())
                    && this.getDescription().contentEquals(pr.getDescription())
                    && this.getVersion().contentEquals(pr.getVersion()) // TODO: use plugin managers version handling
                    && this.getAuthor().contentEquals(pr.getAuthor())
                    && this.getOrganisation().contentEquals(pr.getOrganisation())) {
                return true;
            }
        }

        return false;
    }
}
