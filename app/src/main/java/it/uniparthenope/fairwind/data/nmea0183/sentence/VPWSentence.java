package it.uniparthenope.fairwind.data.nmea0183.sentence;

import net.sf.marineapi.nmea.sentence.Sentence;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public interface VPWSentence extends Sentence {
    public double getVMG();
    public double getVMGKnots();
}
