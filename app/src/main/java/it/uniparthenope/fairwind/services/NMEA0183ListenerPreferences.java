package it.uniparthenope.fairwind.services;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class NMEA0183ListenerPreferences extends DataListenerPreferences {
    public NMEA0183ListenerPreferences() {

    }

    public NMEA0183ListenerPreferences(String name, String desc) {
        super(name,desc);
    }
}
