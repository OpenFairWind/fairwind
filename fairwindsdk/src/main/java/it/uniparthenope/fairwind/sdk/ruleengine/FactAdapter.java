package it.uniparthenope.fairwind.sdk.ruleengine;

import java.util.Vector;

/**
 * Created by raffaelemontella on 23/01/2017.
 */

public interface FactAdapter {
    public String onGetFact(String key);
    public void onEvents(Vector<Event> events);
    public double onGetUnitsConvertedValue(double value, String key, String srcUnits);
}
