package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public interface Stalk20 extends Stalk {
    /*
    20  01  XX  XX  Speed through water: XXXX/10 Knots
                 Corresponding NMEA sentence: VHW
     */
    public Double getSpeedThroughWater();
}
