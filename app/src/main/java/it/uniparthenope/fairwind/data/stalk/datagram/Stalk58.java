package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public interface Stalk58 extends Stalk {
    /*
    58  Z5  LA XX YY LO QQ RR   LAT/LON
                 LA Degrees LAT, LO Degrees LON
                 minutes LAT = (XX*256+YY) / 1000
                 minutes LON = (QQ*256+RR) / 1000
                 Z&1: South (Z&1 = 0: North)
                 Z&2: East  (Z&2 = 0: West)
                 Raw unfiltered position, for filtered data use commands 50&51
                 Corresponding NMEA sentences: RMC, GAA, GLL
     */
    public Double getLatitude();
    public Double getLongitude();
}
