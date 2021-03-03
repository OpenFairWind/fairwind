package it.uniparthenope.fairwind.data.nmea0183.sentence;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

/**
 * Created by raffaelemontella on 22/08/15.
 */
public interface BWCSentence extends Sentence {
    public double getDistance();
    public Position getDestinationWaypointPosition();
    public Time getTime();
    public String getDestinationWaypointId();
    public double getMagneticBearing();
    public double getTrueBearing();
}
