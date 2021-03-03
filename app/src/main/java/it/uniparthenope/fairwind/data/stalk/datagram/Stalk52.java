package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public interface Stalk52 extends Stalk{
    /*
    52  01  XX  XX  Speed over Ground: XXXX/10 Knots
                 Corresponding NMEA sentences: RMC, VTG
     */
    public Double getSOG();
}
