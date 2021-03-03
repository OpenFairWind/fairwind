package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 19/08/15.
 */
public interface Stalk50 extends Stalk {
    /*
    50  Z2  XX  YY  YY  LAT position: XX degrees, (YYYY & 0x7FFF)/100 minutes
                     MSB of Y = YYYY & 0x8000 = South if set, North if cleared
                     Z= 0xA or 0x0 (reported for Raystar 120 GPS), meaning unknown
                     Stable filtered position, for raw data use command 58
                     Corresponding NMEA sentences: RMC, GAA, GLL
     */

    public Double getLatitude();
    public void setLatitude(Double latitude);
}
