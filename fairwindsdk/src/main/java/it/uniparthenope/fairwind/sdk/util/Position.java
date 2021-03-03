package it.uniparthenope.fairwind.sdk.util;


import android.util.Log;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by Lovig90 on 13/10/15.
 */
public class Position extends net.sf.marineapi.nmea.util.Position {
    private static final String LOG_TAG = "POSITION_EX";
    private DateTime timeStamp= DateTime.now();
    private String source="self";

    public Position(GeoPoint p) {
        super(p.getLatitude(),p.getLongitude(),p.getAltitude());
    }

    public Position(SignalKModel signalKModel) {
        super(Double.NaN,Double.NaN,Double.NaN);
        String path=SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position;
        Double latitude = (Double)signalKModel.get(path+".latitude");
        Double longitude= (Double)signalKModel.get(path+".longitude");
        Double altitude= (Double)signalKModel.get(path+".altitude");
        if (altitude==null || Double.isNaN(altitude)) {
            altitude=0.0;
        }
        if (latitude!=null && longitude!=null && Double.isNaN(latitude)==false && Double.isNaN(longitude)==false) {
            setLatitude(latitude);
            setLongitude(longitude);
            setAltitude(altitude);
        }
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }
    public String getSource() { return source; }

    public Position(double lat, double lon, double alt) {

        super(lat, lon, alt);
    }

    public Position(double lat, double lon, double alt, DateTime timeStamp) {

        super(lat, lon, alt);
        this.timeStamp=timeStamp;
    }

    public Position(Json json) {
        super(0,0,0);
        byJson(json);
    }

    public Json asJson() {
        Json json=Json.object();
        json.set("latitude",getLatitude());
        json.set("longitude",getLongitude());
        json.set("altitude",getAltitude());
        json.set("$source",source);
        String timestamp=timeStamp.toDateTimeISO().toString();
        json.set("timestamp",timestamp);
        return json;
    }

    public void byJson(Json json) {

        setLatitude(json.at("latitude").asDouble());
        setLongitude(json.at("longitude").asDouble());

        try {
            setAltitude(json.at("altitude").asDouble());
        } catch (Exception ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }

        try {
            source=(json.at("$source").asString());
        } catch (Exception ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }

        try {
            timeStamp=DateTime.parse(json.at("timestamp").asString());
        } catch (Exception ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }

    }

    public  Position getPredictPosition(Double speed, Double cog,int minutes){

        Position position=null;
        if (speed!=null && cog!=null) {
            Double latitude, longitude;
            Double mN = ((speed * 3.6) / 1.852) * minutes / 60;

            Double deltaLat = (mN * Math.cos(cog)) / 60;
            latitude = getLatitude() + deltaLat;

            Double latMedia = (latitude + getLatitude()) / 2;
            Double gnu = mN * Math.sin(cog);

            Double deltaLong = (gnu / Math.cos(Math.toRadians(latMedia))) / 60;
            longitude = getLongitude() + deltaLong;

            position=new Position(latitude, longitude, getAltitude());
        }
        return position;
    }

    public Double bearingTo(Position position) {
        Log.d(LOG_TAG,"bearingTo");
        if (position==null) return null;

        Log.d(LOG_TAG,this.toString()+"->"+position.toString());

        Double lat2=Math.toRadians(getLatitude());
        Double lon2=Math.toRadians(getLongitude());
        Double lat1=Math.toRadians(position.getLatitude());
        Double lon1=Math.toRadians(position.getLongitude());

        Double y = Math.sin(lon2-lon1) * Math.cos(lat2);
        Double x = Math.cos(lat1)*Math.sin(lat2) -
                Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1);

        Double result=Math.toDegrees(Math.atan2(y, x));
        if (result<0) {result=360+result;}
        Log.d(LOG_TAG,result.toString());
        return  result;
    }

    public GeoPoint asGeoPoint() {
        return new GeoPoint(getLatitude(),getLongitude());
    }

    public boolean isValid() {
        if (Double.isNaN(getLatitude()) == false &&
                Double.isNaN(getLongitude()) == false &&
                Double.isNaN(getAltitude()) == false) {
            return true;
        }

        return false;
    }



}
