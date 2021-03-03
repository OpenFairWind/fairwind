package it.uniparthenope.fairwind.data.nmea0183.sentence;

import net.sf.marineapi.nmea.sentence.Sentence;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public interface HSCSentence extends Sentence {
    public double getTrueDirection();
    public double getMagneticDirection();

}
