package it.uniparthenope.fairwind.data.stalk.parser;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk58;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk58Parser extends StalkParser implements Stalk58 {
    public static final String LOG_TAG="STALK_58_PARSER";
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
    public Stalk58Parser(String sentence) {
        super(sentence);
    }

    public Stalk58Parser() {
        super((byte)0x58, (byte)0x05);
    }

    @Override
    public Double getLatitude() {
        Double latitude=Double.NaN;
        if (isVaild()) {

        }
        return latitude;
    }

    @Override
    public Double getLongitude() {
        Double longitude=Double.NaN;
        if (isVaild()) {

        }
        return longitude;
    }
}
