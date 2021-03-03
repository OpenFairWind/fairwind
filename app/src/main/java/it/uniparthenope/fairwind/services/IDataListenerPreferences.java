package it.uniparthenope.fairwind.services;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public interface IDataListenerPreferences {
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs);
}
