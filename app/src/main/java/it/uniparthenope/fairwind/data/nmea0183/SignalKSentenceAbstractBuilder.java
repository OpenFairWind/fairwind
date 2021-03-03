package it.uniparthenope.fairwind.data.nmea0183;

import net.sf.marineapi.nmea.util.Time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Vector;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public abstract class SignalKSentenceAbstractBuilder {
    private SignalKModel signalKModel;

    public SignalKSentenceAbstractBuilder(SignalKModel signalKModel) {
        this.signalKModel=signalKModel;
    }

    public abstract Vector<String> build(PathEvent pathEvent);

    public SignalKModel getSignalKModel() { return signalKModel; }

    public Time getTime() {
        DateTime dateTime=DateTime.now(DateTimeZone.UTC);
        return new Time(dateTime.getHourOfDay(),dateTime.getMinuteOfHour(),dateTime.getSecondOfMinute());
    }
}
