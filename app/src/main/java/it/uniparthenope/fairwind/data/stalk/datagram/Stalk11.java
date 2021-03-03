package it.uniparthenope.fairwind.data.stalk.datagram;


import net.sf.marineapi.nmea.util.Units;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public interface Stalk11 extends Stalk {
    /*
    11  01  XX  0Y  Apparent Wind Speed: (XX & 0x7F) + Y/10 Knots
                 Units flag: XX&0x80=0    => Display value in Knots
                             XX&0x80=0x80 => Display value in Meter/Second
                 Corresponding NMEA sentence: MWV
     */
    public Double getApparentWindSpeed();
    public int getDisplayUnits();
}
