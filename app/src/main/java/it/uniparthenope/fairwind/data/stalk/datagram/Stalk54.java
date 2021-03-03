package it.uniparthenope.fairwind.data.stalk.datagram;

import net.sf.marineapi.nmea.util.Time;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public interface Stalk54 extends Stalk {
    /*
     54  T1  RS  HH  GMT-time: HH hours,
                           6 MSBits of RST = minutes = (RS & 0xFC) / 4
                           6 LSBits of RST = seconds =  ST & 0x3F
                 Corresponding NMEA sentences: RMC, GAA, BWR, BWC
     */
    public Time getTime();
}
