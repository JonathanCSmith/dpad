package jonathansmith.kellycharacterisationwithod;

import jonathansmith.dpad.api.plugins.records.IAnalyserPluginRecord;

/**
 * Created by Jon on 09/11/2014.
 */
public class KellyCharacterisationWithODPluginRecord implements IAnalyserPluginRecord {

    private static final String NAME         = "Kelly Characterisation Analysis Method With OD";
    private static final String DESCRIPTION  = "Analysis plugin for producing Kelly et al.'s type of characterisation data using OD";
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
