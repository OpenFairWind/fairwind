package it.uniparthenope.fairwind.sdk.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.SortedMap;

import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoints;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.DepthMode;
import it.uniparthenope.fairwind.sdk.util.GroundWaterType;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.WindMode;


/**
 * Created by raffaelemontella on 09/08/15.
 */
public interface FairWindModel {
    public final static String FAIRWIND_SIGNALK_SOURCE="urn:fairwind";




    public static final int SPEED_OVER_GROUND=0;
    public static final int SPEED_OVER_WATER=1;

    String AUTHORITY="it.uniparthenope.fairwind";


    public static final String id="id";
    public static final String sailno="sailno";

    public boolean isKeepScreenOn();




    class SignalK implements BaseColumns {
        public static final String TABLE_NAME="SIGNALK";
        public static final String ITEM="item";
        public static final String PATH="signalk";
        public static final Uri CONTENT_URI = Uri.parse(ContentResolver.SCHEME_CONTENT+"://"+AUTHORITY+"/"+PATH);
        public static final String MIME_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.fairwind";
        public static final String MIME_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.fairwind";


    }

    public Preferences getPreferences();

    public HashMap<String,KeyWithMetadata> getKeysWithMetadata();

    public void register(FairWindEvent event);
    public void unregister(FairWindEvent event);
    public void unregister(FairWindEventListener pathEventListener);

    public static final int UNIT_BOAT_SPEED=1;
    public static final int UNIT_WIND_SPEED=3;
    public static final int UNIT_WATERTEMP=6;
    public static final int UNIT_AIRTEMP=7;
    public static final int UNIT_AIRPRESSURE=8;
    public static final int UNIT_DEPTH=9;
    public static final int COORDS_STYLE=10;
    public static final int UNIT_WINDCHILL=11;

    public int getNumberOfEngines();
    public int getNumberOfFuelTanks();
    public int getNumberOfTrimTabs();

    public Double getAnySpeed(String vessel_uuid);
    public Double getAnyCourse(String vessel_uuid);

    public  Double getWindAngle(String vessel_uuid, WindMode windMode);
    public  Double getWindDirection(String vessel_uuid, CompassMode compassMode, WindMode windMode);

    public Double getEnvWindDirectionTrue(String vessel_uuid);
    public Double getEnvWindDirectionMagnetic(String vessel_uuid);
    public Double getEnvWindAngleTrueWater(String vessel_uuid);
    public Double getEnvWindAngleTrueGround(String vessel_uuid);
    public Double getEnvWindAngleApparent(String vessel_uuid);


    public Double getEnvCurrentDrift(String vessel_uuid);
    public Double getCurrentSet(String vessel_uuid, CompassMode compassMode);
    public Double getEnvCurrentSetTrue(String vessel_uuid);
    public Double getEnvCurrentSetMagnetic(String vessel_uuid);

    public Double getEnvDepthBelowKeel(String vessel_uuid);
    public Double getEnvDepthBelowSurface(String vessel_uuid);
    public Double getEnvDepthBelowTransducer(String vessel_uuid);
    public Double getDepth(String vessel_uuid, DepthMode depthMode);

    public String getName(String vessel_uuid);
    public String getMmsi(String vessel_uuid);
    public String getNavigationState(String vessel_uuid);

    public Position getNavPosition(String vessel_uuid);
    public Double getCourse(String vessel_uuid, GroundWaterType groundWaterType, CompassMode compassMode);
    public Double getSpeed(String vessel_uuid, GroundWaterType groundWaterType);
    public Double getHeading(String vassel_uuid, CompassMode compassMode);
    public Double getCourseOverGround(String vassel_uuid, CompassMode compassMode);
    public Double getBearingTrack(String vessel_uuid,CompassMode compassMode);
    public Double getNavSpeedOverGround(String vessel_uuid);
    public Double getNavPerformanceVelocityMadeGood(String vessel_uuid);
    public Double getNavSpeedThroughWater(String uuid);

    public Double getDoubleByPath(String path);


    public Position getPosition();

    public SortedMap<String,Object> getData();

    //public it.uniparthenope.fairwind.sdk.model.signalk.SignalK getSignalKDOM();

    public String getBoatName();

    public String getBoatFlag();

    public String getBoatPort();

    public String getBoatUuid();

    public String getBoatMmsi();

    public String getBoatUrl();

    public Cursor querySignalK(String itemId);

    public Double getLatitude();

    public Double getLongitude();

    public Double getAltitude();

    public DateTime getDateTime();

    public String getTime();
    public String getDate();

    public Long getMilliseconds();

    public Double getSOG() ;

    public Double getCOG();

    public Double getDepth();

    public int getAccurancy();

    public Double getTrueWindDirection();

    public Double getApparentWindAngle();

    public Double getApparentWindSpeed();

    public Double getTrueWindAngle();

    public Double getTrueWindSpeed();

    public Double getSpeed();

    public Double getHeading();


    public Position getPredictor(String uuid, int minuts);

    public Double getMagneticVariation();

    public Double getWaterTemperature();

    public Double getTrueDirection();

    public Position getDestinationWayPointPosition(String wayPointId);

    public Double getDestinationWayPointLatitude(String wayPointId);

    public Double getDestinationWayPointLongitude(String wayPointId);

    public Double getDestinationWayPointAltitude(String wayPointId);

    public String getDestinationWayPointId(String wayPointId);

    public Double getBearingActualToNextWayPoint();

    public Double getDistanceActualToNextWayPoint();

    public Double getBearingDirect();

    public Double getGpsQuality();

    public Double getSatellities();

    public Double getHDop();

    public Double getVMG();

    public Double getRuddleAngle();

    public Double getTripMiles();

    public Double getTotalMiles();


    public Double getAirPressure();
    public Double getAirTemp();
    public Double getWaterTemp();
    public Double getHumidity();
    public Double getTempWindChill();


    public void setBoatName(String value);

    public void setBoatFlag(String value);

    public void setBoatPort(String value);

    public void setBoatUuid(String value);

    public void setBoatUrl(String value);

    public void setBoatMmsi(String value);

    public void setLatitude(Double latitude);

    public void setLongitude(Double longitude);

    public void setAltitude(Double altitude);

    public void setTime(String date);
    public void setDate(String date);

    public void setSOG(Double sog) ;

    public void setCOG(Double cog);

    public void setDepth(Double depth);

    public void setTrueWindDirection(Double trueWindDirection);

    public void setApparentWindAngle(Double apparentWindAngle);

    public void setApparentWindSpeed(Double apparentWindSpeed);

    public void setTrueWindAngle(Double trueWindAngle);

    public void setTrueWindSpeed(Double trueWindSpeed);

    public void setSpeed(Double speed);

    public void setHeading(Double heading);
    public void setHeading(String vessel_uuid, CompassMode compassMode, Double heading);

    public void setMagneticVariation(Double magneticVariation);

    public void setWaterTemperature(Double waterTemperature);

    public void setTrueDirection(Double trueDirection);

    public void setDestinationWayPointPosition(String wayPointId,Position position);

    public void setDestinationWayPointLatitude(String wayPointId,Double destinationWayPointLatitude);

    public void setDestinationWayPointLongitude(String wayPointId,Double destinationWayPointLongitude);

    public void setDestinationWayPointAltitude(String wayPointId,Double destinationWayPointAltitude);

    public void setDestinationWayPointId(String wayPointId);

    public void setBearingActualToNextWayPoint(Double bearingActualToNextWayPoint);

    public void setDistanceActualToNextWayPoint(Double distanceActualToNextWayPoint);

    public void setBearingDirect(Double bearingDirect);

    public void setGpsQuality(Double gpsQuality);

    public void setSatellities(Double satellities);

    public void setHDop(Double hDop);

    public void setVMG(Double vmg);

    public void setRuddleAngle(Double ruddleAngle);

    public void setTripMiles(Double tripMiles);

    public void setTotalMiles(Double totalMiles);


    //public void putSentence(String key,FairWindSentenceItem item);
    //public void putData(String key,FairWindDataItem item);

    public void setNavPosition(String vessel_uuid, Position position);



    public Waypoints getWaypoints() ;
}
