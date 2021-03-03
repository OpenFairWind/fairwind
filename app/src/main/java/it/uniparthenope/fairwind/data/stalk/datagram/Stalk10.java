package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public interface Stalk10 extends Stalk {
    /*
    10  01  XX  YY  Apparent Wind Angle: XXYY/2 degrees right of bow
                 Used for autopilots Vane Mode (WindTrim)
                 Corresponding NMEA sentence: MWV
     */
    public Double getApparentWindAngle();
}
