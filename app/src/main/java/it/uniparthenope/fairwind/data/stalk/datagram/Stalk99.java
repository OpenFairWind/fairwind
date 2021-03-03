package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public interface Stalk99 extends Stalk {
    /*
    99  00  XX        Compass variation sent by ST40 compass instrument
                     or ST1000, ST2000, ST4000+, E-80 every 10 seconds
                     but only if the variation is set on the instrument
                     Positive XX values: Variation West, Negative XX values: Variation East
                     Examples (XX => variation): 00 => 0, 01 => -1 west, 02 => -2 west ...
                                                 FF => +1 east, FE => +2 east ...
                   Corresponding NMEA sentences: RMC, HDG
     */
    public Double getCompassVariation();
}
