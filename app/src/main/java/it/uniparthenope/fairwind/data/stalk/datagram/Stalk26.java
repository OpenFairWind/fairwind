package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public interface Stalk26 extends Stalk {
    /*
    26  04  XX  XX  YY  YY DE  Speed through water:
                     XXXX/100 Knots, sensor 1, current speed, valid if D&4=4
                     YYYY/100 Knots, average speed (trip/time) if D&8=0
                              or data from sensor 2 if D&8=8
                     E&1=1: Average speed calulation stopped
                     E&2=2: Display value in MPH
                     Corresponding NMEA sentence: VHW
     */
    public Double getSpeedThroughWater();
    public Double getAverageSpeed();
    public Double getSpeedThroughWater2();
}

