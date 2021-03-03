package it.uniparthenope.fairwind.handler;

import android.util.Log;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Util;

import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.meta;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_anchor_currentRadius;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_anchor_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_anchor_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.zones;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class AnchorHandler extends HandlerBase {
    public static final String LOG_TAG="ANCHOR_HANDLER";

    private static String zonesKey = vessels_dot_self_dot+nav_anchor_currentRadius+dot+meta+dot+zones;
    private static String radiusKey = vessels_dot_self_dot+nav_anchor_currentRadius;
    private static String latKey = vessels_dot_self_dot+nav_position_latitude;
    private static String lonKey = vessels_dot_self_dot+nav_position_longitude;
    private static String anchorLatKey = vessels_dot_self_dot+nav_anchor_position_latitude;
    private static String anchorLonKey = vessels_dot_self_dot+nav_anchor_position_longitude;

    public AnchorHandler() {

    }

    @Override
    public void handle(SignalKModel signalKModel) {
        Object zonesJson = signalKModel.get(zonesKey);
        if(zonesJson !=null && zonesJson instanceof Json && ((Json)zonesJson).isArray() && ((Json)zonesJson).asJsonList().size()>0){
            //anchor watch is on
            double lat = (double) signalKModel.get(latKey);
            double lon = (double) signalKModel.get(lonKey);
            double anchorLat = (double) signalKModel.get(anchorLatKey);
            double anchorLon = (double) signalKModel.get(anchorLonKey);
            //workout distance
            double distance = Util.haversineMeters(lat,lon,anchorLat, anchorLon);
            Log.d(LOG_TAG,"Updating anchor distance:"+distance);
            signalKModel.put(radiusKey,distance,null, Util.getIsoTimeString(System.currentTimeMillis()));
        }
    }
}
