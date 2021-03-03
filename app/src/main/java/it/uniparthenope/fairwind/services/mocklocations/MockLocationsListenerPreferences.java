package it.uniparthenope.fairwind.services.mocklocations;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class MockLocationsListenerPreferences extends DataListenerPreferences {
    public MockLocationsListenerPreferences(String name, String desc) {
        super(name, desc);
    }

    public MockLocationsListenerPreferences() {
        super();
        setName("Mock Locations");
        setDesc("Publish position imitating the local Android location provider");
    }
}
