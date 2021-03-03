package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public interface Stalk57 extends Stalk {
    /*
     57  S0  DD      Sat Info: S number of sats, DD horiz. dillution of position, if S=1 -> DD=0x94
                 Corresponding NMEA sentences: GGA, GSA
     */
    public Integer getSats();
    public Integer getHDOP();
}
