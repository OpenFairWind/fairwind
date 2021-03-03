package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public interface Stalk27 extends Stalk {
    /*
     27  01  XX  XX  Water temperature: (XXXX-100)/10 deg Celsius
                 Corresponding NMEA sentence: MTW
     */
    public Double getWaterTemperature();
}
