package it.uniparthenope.fairwind.data.stalk.datagram;

import net.sf.marineapi.nmea.util.Date;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public interface Stalk56 extends Stalk {
    /*
    56  M1  DD  YY  Date: YY year, M month, DD day in month
                 Corresponding NMEA sentence: RMC
     */
    public Date getDate();
}
