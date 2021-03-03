package it.uniparthenope.fairwind.services.rules;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;

/**
 * Created by raffaelemontella on 23/07/16.
 */
public class RulesListenerPreferences extends DataListenerPreferences {
    public RulesListenerPreferences(String name, String desc) {
        super(name, desc);
    }

    public RulesListenerPreferences() {
        super();
        setName("Alerts");
        setDesc("Manage the alert rule system");
    }
}
