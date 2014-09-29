package jonathansmith.dpad.api.database;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import jonathansmith.dpad.api.plugins.IPlugin;
import jonathansmith.dpad.api.plugins.records.IPluginRecord;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Superclass for all plugin database records. Contains all of their pertinent information
 */
@MappedSuperclass
public abstract class PluginRecord<T extends IPlugin> extends Record implements IPluginRecord {

    private String pluginName;
    private String pluginDescription;
    private String version;
    private String pluginAuthor;
    private String pluginOrganisation;

    @Override
    @Column(name = "Name")
    public String getPluginName() {
        return this.pluginName;
    }

    public void setPluginName(String name) {
        this.pluginName = name;
    }

    @Override
    @Column(name = "Description")
    public String getPluginDescription() {
        return this.pluginDescription;
    }

    public void setPluginDescription(String description) {
        this.pluginDescription = description;
    }

    @Column(name = "Version")
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    @Column(name = "Author")
    public String getPluginAuthor() {
        return this.pluginAuthor;
    }

    public void setPluginAuthor(String author) {
        this.pluginAuthor = author;
    }

    @Override
    @Column(name = "Organisation")
    public String getPluginOrganisation() {
        return this.pluginOrganisation;
    }

    public void setPluginOrganisation(String organisation) {
        this.pluginOrganisation = organisation;
    }

    public void buildRecordFromPlugin(T plugin) {
        IPluginRecord pluginRecord = plugin.getPluginRecord();
        this.setPluginName(pluginRecord.getPluginName());
        this.setPluginDescription(pluginRecord.getPluginDescription());
        this.setPluginAuthor(pluginRecord.getPluginAuthor());
        this.setPluginOrganisation(pluginRecord.getPluginOrganisation());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PluginRecord) {
            PluginRecord pr = (PluginRecord) o;
            if (this.getUUID().contentEquals(pr.getUUID())
                    && this.getPluginName().contentEquals(pr.getPluginName())
                    && this.getPluginDescription().contentEquals(pr.getPluginDescription())
                    && this.getVersion().contentEquals(pr.getVersion()) // TODO: use plugin managers version handling
                    && this.getPluginAuthor().contentEquals(pr.getPluginAuthor())
                    && this.getPluginOrganisation().contentEquals(pr.getPluginOrganisation())) {
                return true;
            }
        }

        return false;
    }
}
