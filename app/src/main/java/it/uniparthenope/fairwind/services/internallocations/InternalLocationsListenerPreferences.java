package it.uniparthenope.fairwind.services.internallocations;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class InternalLocationsListenerPreferences extends DataListenerPreferences {
    public InternalLocationsListenerPreferences(String name, String desc) {
        super(name, desc);
    }

    public InternalLocationsListenerPreferences() {
        super();
        setName("Internal Locations");
        setDesc("Use the internal GPS as main position provider");
    }
}
