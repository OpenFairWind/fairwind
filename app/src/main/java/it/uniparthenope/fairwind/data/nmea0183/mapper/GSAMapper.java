package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.util.GpsFixStatus;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class GSAMapper extends SentenceSignalKAbstractMapper {
    public GSAMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof GSASentence);
    }

    @Override
    public void map() {
        GSASentence sen=(GSASentence) evt.getSentence();



        GpsFixStatus gpsFixStatus=sen.getFixStatus();
        double hDop=sen.getHorizontalDOP();
        double vDop=sen.getVerticalDOP();
        double pDop=sen.getPositionDOP();

        String satelliteIds="";
        for (String satelliteId:sen.getSatelliteIds()) {
            satelliteIds+=satelliteId+",";
        }
        satelliteIds=satelliteIds.substring(0,satelliteIds.length()-1);
        if (!Double.isNaN(hDop)) {
            put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_gnss_horizontalDilution,hDop);
        }
        if (!Double.isNaN(vDop)) {
            // fairWindModel.putData(FairWindModel.VDOP, new FairWindDataItemImpl(vDop));
        }
        if (!Double.isNaN(pDop)) {
            // fairWindModel.putData(FairWindModel.PDOP, new FairWindDataItemImpl(pDop));
        }

        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_gnss_methodQuality,gpsFixStatus.toInt());
        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_gnss_satellites,satelliteIds);

    }
}
