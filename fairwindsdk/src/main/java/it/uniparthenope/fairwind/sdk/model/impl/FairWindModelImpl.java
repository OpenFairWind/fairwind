package it.uniparthenope.fairwind.sdk.model.impl;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;
import java.util.Vector;

import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.model.Preferences;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelImpl;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.*;

/**
 * Created by Lovig90 on 27/10/15.
 */
public class FairWindModelImpl extends FairWindModelBase implements FairWindModel {
    private static final String LOG_TAG = "FAIRWIND_MODEL";




    @Override
    public Preferences getPreferences() {
        throw new RuntimeException("Method getPreferences not already implemented");
    }


    @Override
    public Position getPosition() {
        Double latitude=getLatitude();
        Double longitude=getLongitude();
        if (latitude!=null && longitude !=null) {
            return new Position(getLatitude(),getLongitude(),getAltitude());
        }
        return null;
    }

    @Override
    public String getBoatName() {
        Object data=getData().get(vessels_dot_self_dot +name);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    @Override
    public String getBoatFlag() {
        Object data=getData().get(vessels_dot_self_dot +flag);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    @Override
    public String getBoatPort() {
        Object data=getData().get(vessels_dot_self_dot +port);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    @Override
    public String getBoatUuid() {
        Object data=getData().get(vessels_dot_self_dot +uuid);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    @Override
    public String getBoatMmsi() {
        Object data=getData().get(vessels_dot_self_dot +mmsi);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    @Override
    public String getBoatUrl() {
        Object data=getData().get(vessels_dot_self_dot +url);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }


    private static FairWindModel fairWindModel;

    static {
        if (fairWindModel == null)
            fairWindModel = new FairWindModelImpl();
    }

    /**
     * Get the signalKModel singleton
     *
     * @return
     */
    public static FairWindModel getInstance() {
        return fairWindModel;
    }


    @Override
    public boolean isKeepScreenOn() {
        return true;
    }

    @Override
    public int getNumberOfEngines() {
        return 1;
    }

    @Override
    public int getNumberOfFuelTanks() {
        return 1;
    }

    @Override
    public int getNumberOfTrimTabs() {
        return 0;
    }



    /*
    @Override
    public it.uniparthenope.fairwind.sdk.model.signalk.SignalK getSignalKDOM() {
        return new it.uniparthenope.fairwind.sdk.model.signalk.SignalK(this);
    }
    */

    public FairWindModelImpl() {
        super();

    }

    public FairWindModelImpl(Cursor data) {

        if (data != null && data.getCount() > 0) {

            int idIndex=data.getColumnIndex(FairWindModel.SignalK._ID);
            int itemIndex = data.getColumnIndex(FairWindModel.SignalK.ITEM);

            data.moveToFirst();

            do {

                String sId = data.getString(idIndex);
                Double sData = data.getDouble(itemIndex);
                put(sId,sData,FAIRWIND_SIGNALK_SOURCE);


            } while(data.moveToNext());

        }

    }


    public Cursor querySignalK(String itemId) {
        String[] columns = new String[] { SignalK._ID, SignalK.ITEM};
        String value;


        MatrixCursor matrixCursor= new MatrixCursor(columns);
        if (getData()!=null) {
            if (itemId == null) {
                for (String key : getKeys()) {
                    if (getData().get(key)!= null) {
                        value = getData().get(key).toString();
                        if (!value.isEmpty()) {
                            matrixCursor.addRow(new Object[]{key, value});
                        }
                    }
                }
            } else {
                if (getData().get(itemId) != null) {
                    value = getData().get(itemId).toString();
                    if (!value.isEmpty()) {
                        matrixCursor.addRow(new Object[]{itemId, value});
                    }
                }
            }
        }
        return matrixCursor;
    }
    public Double getLatitude() {
        return (Double) getData().get(vessels_dot_self_dot + nav_position_latitude);
    }

    public Double getLongitude() {
        return (Double) getData().get(vessels_dot_self_dot + nav_position_longitude);
    }

    public Double getAltitude() {
        return (Double) getData().get(vessels_dot_self_dot + nav_position_altitude);
    }


    public DateTime getDateTime() {
        String millis=(String)getData().get(vessels_dot_self_dot + SignalKConstants.env_time_millis+".value");
        String timeZone=(String)getData().get(vessels_dot_self_dot + SignalKConstants.env_timezone+".value");
        DateTime dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone)));
        return dateTime;
    }

    public String getTime() {
        return (String) getData().get(vessels_dot_self_dot + env_time_time + ".value");

    }

    public String getDate() {
        return (String) getData().get(vessels_dot_self_dot + env_time_date + ".value");
    }


    public Long getMilliseconds() {
        return System.currentTimeMillis();
    }

    public Double getSOG() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_speedOverGround + ".value"));
    }

    public Double getCOG() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_courseOverGroundMagnetic + ".value"));
    }

    public Double getDepth() {
        return ((Double) getData().get(vessels_dot_self_dot + env_depth_belowTransducer + ".value"));
    }

    public Double getTrueWindDirection() {
        return ((Double) getData().get(vessels_dot_self_dot + env_wind_directionTrue + ".value"));
    }

    public Double getApparentWindAngle() {
        return ((Double) getData().get(vessels_dot_self_dot + env_wind_angleApparent + ".value"));
    }

    public Double getApparentWindSpeed() {
        return ((Double) getData().get(vessels_dot_self_dot + env_wind_speedApparent + ".value"));
    }

    public Double getTrueWindAngle() {
        return ((Double) getData().get(vessels_dot_self_dot + env_wind_angleTrueGround + ".value"));
    }

    public Double getTrueWindSpeed() {
        return ((Double) getData().get(vessels_dot_self_dot + env_wind_speedTrue + ".value"));
    }

    @Override
    public Double getSpeed() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_speedThroughWater + ".value"));
    }

    @Override
    public Double getHeading() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_headingMagnetic + ".value"));
    }

    /*
    @Override
    public Position getPredictor(int minutes) {


        Double latitude;
        Double longitude;
        Double speed;
        Double heading;

        latitude = getLatitude();
        if (latitude == null) return null;

        longitude = getLongitude();
        if (longitude == null) return null;

        speed = getSpeed();
        if (speed == null) {
            speed = getSOG();
        }
        heading = getHeading();
        if (heading == null) {
            heading = getCOG();
        }
        if (speed != null && heading != null) {

            return Position.getPredictPosition(latitude, longitude, speed, heading, minutes);

        } else {
            return null;
        }

    }
    */
    //get added by Lovig90 21/10/2015
    //*******************************************************************************************

    @Override
    public Double getMagneticVariation() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_magneticVariation + ".value"));
    }

    @Override
    public Double getWaterTemperature() {
        return ((Double) getData().get(vessels_dot_self_dot + env_water_temperature + ".value"));
    }

    @Override
    public Double getTrueDirection() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_headingTrue + ".value"));
    }

    @Override
    public Position getDestinationWayPointPosition(String wayPointId) {
        return ((Position) getData().get(resources_waypoints + dot + wayPointId + ".position"));
    }

    @Override
    public Double getDestinationWayPointLatitude(String wayPointId) {
        return ((Double) getData().get(resources_waypoints + dot + wayPointId + ".position.latitude"));
    }

    @Override
    public Double getDestinationWayPointLongitude(String wayPointId) {
        return ((Double) getData().get(resources_waypoints + dot + wayPointId + ".position.longitude"));
    }

    @Override
    public Double getDestinationWayPointAltitude(String wayPointId) {
        return ((Double) getData().get(resources_waypoints + dot + wayPointId + ".position.altitude"));
    }

    @Override
    public String getDestinationWayPointId(String wayPointId) {

        return wayPointId;
    }

    @Override
    public Double getBearingActualToNextWayPoint() {

        return ((Double) getData().get(vessels_dot_self_dot + nav_courseRhumbline_nextPoint_bearingTrue));
    }

    @Override
    public Double getDistanceActualToNextWayPoint() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_courseRhumbline_nextPoint_distance));
    }

    @Override
    public Double getBearingDirect() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_courseRhumbline_bearingTrackTrue));
    }

    @Override
    public Double getGpsQuality() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_gnss_methodQuality + ".value"));
    }

    @Override
    public Double getSatellities() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_gnss_satellites + ".value"));
    }

    @Override
    public Double getHDop() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_gnss_horizontalDilution + ".value"));
    }

    @Override
    public Double getVMG() {
        return ((Double) getData().get(vessels_dot_self_dot + "performance.velocityMadeGood" + ".value"));
    }

    @Override
    public Double getRuddleAngle() {
        return ((Double) getData().get(vessels_dot_self_dot + steering_rudderAngle + ".value"));
    }

    @Override
    public Double getTripMiles() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_logTrip + ".value"));
    }

    @Override
    public Double getTotalMiles() {
        return ((Double) getData().get(vessels_dot_self_dot + nav_log + ".value"));
    }

    @Override
    public Double getAirPressure() {
        return ((Double) getData().get(vessels_dot_self_dot + env_outside_pressure + ".value"));
    }

    @Override
    public Double getAirTemp() {
        return ((Double) getData().get(vessels_dot_self_dot + env_outside_temperature + ".value"));
    }

    @Override
    public Double getWaterTemp() {
        return ((Double) getData().get(vessels_dot_self_dot + env_water_temperature + ".value"));
    }

    @Override
    public Double getHumidity() {
        return ((Double) getData().get(vessels_dot_self_dot + env_outside_humidity + ".value"));
    }

    @Override
    public Double getTempWindChill() {
        Double tws=getTrueWindSpeed();
        Double airTemp=getAirTemp();
        Double result=null;
        if (tws != null && airTemp != null) {
            if (airTemp < 283.15) {
                double tF = airTemp * 9 / 5 - 459.67;
                double w = Math.pow(tws, .16);
                double wcF = 35.74 + (0.6215 * tF) - (35.75 * w) + (0.4275 * tF * w);
                result = (wcF + 459.67) * 5 / 9;
            } else {
                result = airTemp;
            }
        }
        return result;
    }

    @Override
    public void setBoatName(String boatName) {
        put(vessels_dot_self_dot + name, boatName,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBoatFlag(String value) {
        put(vessels_dot_self_dot + flag, value,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBoatPort(String value) {
        put(vessels_dot_self_dot + port, value,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBoatUuid(String value) {
        put(vessels_dot_self_dot + uuid, value,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBoatUrl(String value) {
        put(vessels_dot_self_dot + url, value,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBoatMmsi(String value) {
        put(vessels_dot_self_dot + mmsi, value,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setLatitude(Double latitude) {
        put(vessels_dot_self_dot + nav_position_latitude, latitude,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setLongitude(Double longitude) {
        put(vessels_dot_self_dot + nav_position_longitude, longitude,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setAltitude(Double altitude) {
        put(vessels_dot_self_dot + nav_position_altitude, altitude,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTime(String time) {

        put(vessels_dot_self_dot + env_time_time + ".value", time,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDate(String date) {
        put(vessels_dot_self_dot + env_time_date + ".value", date,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setSOG(Double sog) {
        put(vessels_dot_self_dot + nav_speedOverGround + ".value", sog,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setCOG(Double cog) {
        put(vessels_dot_self_dot + nav_courseOverGroundMagnetic + ".value", cog,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDepth(Double depth) {
        put(vessels_dot_self_dot + env_depth_belowTransducer + ".value", depth,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTrueWindDirection(Double trueWindDirection) {
        put(vessels_dot_self_dot + env_wind_directionTrue + ".value", trueWindDirection,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setApparentWindAngle(Double apparentWindAngle) {
        put(vessels_dot_self_dot + env_wind_angleApparent + ".value", apparentWindAngle,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setApparentWindSpeed(Double apparentWindSpeed) {
        put(vessels_dot_self_dot + env_wind_speedApparent + ".value", apparentWindSpeed,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTrueWindAngle(Double trueWindAngle) {
        put(vessels_dot_self_dot + env_wind_angleTrueGround + ".value", trueWindAngle,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTrueWindSpeed(Double trueWindSpeed) {
        put(vessels_dot_self_dot + env_wind_speedTrue + ".value", trueWindSpeed,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setSpeed(Double speed) {
        put(vessels_dot_self_dot + nav_speedThroughWater + ".value", speed,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setHeading(Double heading) {
        put(vessels_dot_self_dot + nav_headingMagnetic + ".value", heading,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setMagneticVariation(Double magneticVariation) {
        put(vessels_dot_self_dot + nav_magneticVariation + ".value", magneticVariation,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setWaterTemperature(Double waterTemperature) {
        put(vessels_dot_self_dot + env_water_temperature + ".value", waterTemperature,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTrueDirection(Double trueDirection) {
        put(vessels_dot_self_dot + nav_headingTrue + ".value", trueDirection,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDestinationWayPointPosition(String wayPointId, Position position) {
        put(resources_waypoints + dot + wayPointId + ".position", position,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDestinationWayPointLatitude(String wayPointId, Double destinationWayPointLatitude) {
        put(resources_waypoints + dot + wayPointId + ".position.latitude", destinationWayPointLatitude,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDestinationWayPointLongitude(String wayPointId, Double destinationWayPointLongitude) {
        put(resources_waypoints + dot + wayPointId + ".position.longitude", destinationWayPointLongitude,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDestinationWayPointAltitude(String wayPointId, Double destinationWayPointAltitude) {
        put(resources_waypoints + dot + wayPointId + ".position.altitude", destinationWayPointAltitude,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDestinationWayPointId(String wayPointId) {
        put(resources_waypoints + dot + wayPointId, wayPointId,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBearingActualToNextWayPoint(Double bearingActualToNextWayPoint) {
        put(vessels_dot_self_dot + nav_courseRhumbline_nextPoint_bearingTrue, bearingActualToNextWayPoint,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setDistanceActualToNextWayPoint(Double distanceActualToNextWayPoint) {
        put(vessels_dot_self_dot + nav_courseRhumbline_nextPoint_distance, distanceActualToNextWayPoint,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setBearingDirect(Double bearingDirect) {
        put(vessels_dot_self_dot + nav_courseRhumbline_bearingTrackTrue, bearingDirect,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setGpsQuality(Double gpsQuality) {
        put(vessels_dot_self_dot + nav_gnss_methodQuality + ".value", gpsQuality,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setSatellities(Double satellities) {
        put(vessels_dot_self_dot + nav_gnss_satellites + ".value", satellities,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setHDop(Double hDop) {
        put(vessels_dot_self_dot + nav_gnss_horizontalDilution + ".value", hDop,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setVMG(Double vmg) {
        put(vessels_dot_self_dot + "performance.velocityMadeGood" + ".value", vmg,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setRuddleAngle(Double ruddleAngle) {
        put(vessels_dot_self_dot + steering_rudderAngle + ".value", ruddleAngle,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTripMiles(Double tripMiles) {
        put(vessels_dot_self_dot + nav_logTrip + ".value", tripMiles,FAIRWIND_SIGNALK_SOURCE);
    }

    @Override
    public void setTotalMiles(Double totalMiles) {
        put(vessels_dot_self_dot + nav_log + ".value", totalMiles,FAIRWIND_SIGNALK_SOURCE);
    }

    //*******************************************************************************************


    public int getAccurancy() {
        return 1;
    }
}