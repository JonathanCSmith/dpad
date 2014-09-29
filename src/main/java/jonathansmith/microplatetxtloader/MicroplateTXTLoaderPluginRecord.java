package jonathansmith.microplatetxtloader;

import jonathansmith.dpad.api.plugins.records.ILoaderPluginRecord;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateTXTLoaderPluginRecord implements ILoaderPluginRecord {

    private static final String NAME         = "MICROPLATE TXT FILE LOADER";
    private static final String DESCRIPTION  = "Loads microplate data from text files, specifically files that are commma delimited";
    private static final String AUTHOR       = "Jonathan Smith";
    private static final String ORGANISATION = "Imperial College London";

    @Override
    public String getPluginName() {
        return NAME;
    }

    @Override
    public String getPluginDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getPluginAuthor() {
        return AUTHOR;
    }

    @Override
    public String getPluginOrganisation() {
        return ORGANISATION;
    }
}
