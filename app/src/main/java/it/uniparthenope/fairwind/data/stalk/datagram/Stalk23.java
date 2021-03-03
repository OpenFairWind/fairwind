package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public interface Stalk23 extends Stalk {
    /*
    23  Z1  XX  YY  Water temperature (ST50): XX deg Celsius, YY deg Fahrenheit
                 Flag Z&4: Sensor defective or not connected (Z=4)
                 Corresponding NMEA sentence: MTW
     */
    public Double getWaterTemperature();
}
