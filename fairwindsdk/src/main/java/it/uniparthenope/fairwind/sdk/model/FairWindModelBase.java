package it.uniparthenope.fairwind.sdk.model;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoints;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.DepthMode;
import it.uniparthenope.fairwind.sdk.util.DirectionMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.GroundWaterType;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.SpeedMode;
import it.uniparthenope.fairwind.sdk.util.Utils;
import it.uniparthenope.fairwind.sdk.util.WindMode;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelImpl;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;
import xdroid.core.Global;


import static android.os.Looper.getMainLooper;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_outside_pressure;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_outside_temperature;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_outside_humidity;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_time_date;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_time_time;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_water_temperature;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleTrueGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_directionTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.flag;
import static nz.co.fortytwo.signalk.util.SignalKConstants.mmsi;
import static nz.co.fortytwo.signalk.util.SignalKConstants.name;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_course_activeRoute;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseRhumbline_bearingTrackTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseRhumbline_nextPoint_bearingTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseRhumbline_nextPoint_distance;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_gnss_horizontalDilution;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_gnss_methodQuality;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_gnss_satellites;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_headingTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_log;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_logTrip;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_magneticVariation;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_altitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedThroughWater;
import static nz.co.fortytwo.signalk.util.SignalKConstants.port;
import static nz.co.fortytwo.signalk.util.SignalKConstants.resources_waypoints;
import static nz.co.fortytwo.signalk.util.SignalKConstants.steering_rudderAngle;
import static nz.co.fortytwo.signalk.util.SignalKConstants.url;
import static nz.co.fortytwo.signalk.util.SignalKConstants.uuid;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;

/**
 * Created by raffaelemontella on 04/05/16.
 */
public class FairWindModelBase extends SignalKModelImpl {
    private static final String LOG_TAG = "FAIRWIND_MODEL_BASE";

    private String webProtocol="http://";
    private String webUrl="fairwind.cloud";
    private String apiUrl="api."+webUrl;
    public String getWebUrl() { return webProtocol+webUrl; }
    public String getApiUrl() { return webProtocol+apiUrl; }

    private HashMap<String, KeyWithMetadata> keysWithMetadata = new HashMap<String, KeyWithMetadata>();
    public HashMap<String,KeyWithMetadata> getKeysWithMetadata() { return  keysWithMetadata; }

    private static FairWindModel fairWindModel=null;

    private Vector<FairWindEvent> events=new Vector<FairWindEvent>();

    private Handler handler;

    public FairWindModelBase() {
        try {
            Context context=Global.getContext();
            String jsonAsString= Utils.readTextFromResource( context.getResources(), it.uniparthenope.fairwind.sdk.R.raw.keyswithmetadata);
            Json jsonKeysWithMetadata=Json.read(jsonAsString);
            Map<String, Json> map=jsonKeysWithMetadata.asJsonMap();
            for(String key:map.keySet()) {
                keysWithMetadata.put(key,new KeyWithMetadata(key,map.get(key)));
            }

        } catch (IOException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        getEventBus().register(this);
    }

    public static Position getDefaultPosition() { return new Position(40.22,14.28,0); }

    private  void unregister(Vector<FairWindEvent> toBeUnregistered) {
        //create another Vector object
        Vector<FairWindEvent> temp = new Vector<FairWindEvent>(events.size());

        temp.setSize(events.size());
        Collections.copy(temp,events);
        Iterator<FairWindEvent>  iter = temp.iterator();

        while (iter.hasNext()) {
            FairWindEvent event = iter.next();
            unregister(event);
            Log.d(LOG_TAG, "Unregistered:" + event.getType() + "/" + event.getPath());
        }
    }
    private  Vector<FairWindEvent> fire(PathEvent pathEvent) {
        Vector<FairWindEvent> toBeUnregistered = null;

        //create another Vector object
        Vector<FairWindEvent> temp = new Vector<FairWindEvent>(events.size());

        temp.setSize(events.size());
        Collections.copy(temp,events);

        Iterator<FairWindEvent> iter = temp.iterator();

        while (iter.hasNext()) {
            FairWindEvent event = iter.next();


            if (event.isMatching(pathEvent)) {
                try {
                    Log.d(LOG_TAG, "Fire:" + event.getListener().getClass().getName() + " -> " + event.getType() + "/" + event.getPath());
                    event.getListener().onEvent(new FairWindEvent(event, pathEvent));
                } catch (NullPointerException e) {

                    if (toBeUnregistered == null) {
                        toBeUnregistered = new Vector<FairWindEvent>();
                    }
                    toBeUnregistered.add(event);

                }
            }
        }

        return toBeUnregistered;
    }

    @Subscribe
    public void onEvent(PathEvent pathEvent) {
        Log.d(LOG_TAG,"onEvent:"+pathEvent.getPath());
        try {
            Vector<FairWindEvent> toBeUnregistered = new Vector<FairWindEvent>();

            //create another Vector object

            Vector<FairWindEvent> temp = new Vector<FairWindEvent>();
            synchronized (events) {
                temp.setSize(events.size());
                Collections.copy(temp, events);
            }
            for(FairWindEvent event:temp) {

                if (event.isMatching(pathEvent)) {

                    //try {
                        Log.d(LOG_TAG, "__EVENT__ Fire:" + event.getListener().getClass().getName() + " -> " + event.getType() + "/" + event.getPath());
                        event.getListener().onEvent(new FairWindEvent(event, pathEvent));


                    //} catch (NullPointerException e) {
//                        toBeUnregistered.add(event);

                    //}
                }
            }

            if (toBeUnregistered.size() > 0) {
                synchronized (events) {
                    temp.setSize(events.size());
                    Collections.copy(temp, events);
                    for (FairWindEvent event : temp) {
                        events.remove(event);
                        Log.d(LOG_TAG, "__EVENT__ Unregistered:" + event.getType() + "/" + event.getPath());
                    }

                }
            }

        } catch (RuntimeException ex) {
            Log.e(LOG_TAG,"__EVENT__"+ex.getMessage());
            StringWriter stringWriter=new StringWriter();
            PrintWriter printWriter=new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            Log.e(LOG_TAG,"__EVENT__"+stringWriter.toString());
            //throw new RuntimeException(ex);
        }
    }
    /*
    public synchronized void onEvent(PathEvent pathEvent) {
        Log.d(LOG_TAG,"onEvent:"+pathEvent.getPath());
        try {
            Vector<FairWindEvent> toBeUnregistered=fire(pathEvent);

            if (toBeUnregistered != null && toBeUnregistered.size() > 0) {
                unregister(toBeUnregistered);

            }
        } catch (RuntimeException ex) {
            Log.e(LOG_TAG,"__RUNTIME__"+ex.getMessage());
            StringWriter stringWriter=new StringWriter();
            PrintWriter printWriter=new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            Log.e(LOG_TAG,"__RUNTIME__"+stringWriter.toString());
            //throw new RuntimeException(ex);
        }
    }
    */



    public  void register(FairWindEvent event) {
        if (event==null) {
            throw new RuntimeException("The event can't be null");
        }
        Log.d(LOG_TAG,"onRegister:"+event.getPath());
        synchronized(events) {
            events.add(event);
        }
    }


    public void unregister(FairWindEvent event) {
        synchronized (events) {
            events.remove(event);
        }
    }


    public void unregister(FairWindEventListener pathEventListener) {
        for(FairWindEvent event:events) {
            if (event.getListener()==pathEventListener) {
                unregister(event);
                break;
            }
        }
    }


/////////////////

    public  Double getWindAngle(String vessel_uuid, WindMode windMode) {
        return getDoubleByPath(getWindAnglePath(vessel_uuid,windMode));
    }

    public  Double getWindDirection(String vessel_uuid, CompassMode compassMode, WindMode windMode) {
        return getDoubleByPath(getWindDirectionPath(vessel_uuid,compassMode,windMode));
    }

    public Double getEnvWindDirectionTrue(String vessel_uuid) {
        return getDoubleByPath(getEnvWindDirectionTruePath(vessel_uuid));
    }
    public Double getEnvWindDirectionMagnetic(String vessel_uuid) {
        return getDoubleByPath(getEnvWindDirectionMagneticPath(vessel_uuid));
    }
    public Double getEnvWindAngleTrueWater(String vessel_uuid) {
        return getDoubleByPath(getEnvWindAngleTrueWaterPath(vessel_uuid));
    }
    public Double getEnvWindAngleTrueGround(String vessel_uuid) {
        return getDoubleByPath(getEnvWindAngleTrueGroundPath(vessel_uuid));
    }
    public Double getEnvWindAngleApparent(String vessel_uuid) {
        return getDoubleByPath(getEnvWindAngleApparentPath(vessel_uuid));
    }


    public Double getBearingTrack(String vessel_uuid, CompassMode compassMode) {
        return getDoubleByPath(getBearingTrackPath(vessel_uuid,compassMode));
    }


    public Double getNavPerformanceVelocityMadeGood(String vessel_uuid) {
        return getDoubleByPath(getNavPerformanceVelocityMadeGoodPath(vessel_uuid));
    }

    
    public Position getNavPosition(String vessel_uuid) {
        Position result=null;
        String path=getNavPositionPath(vessel_uuid);

        Double latitude = (Double)get(path+".latitude");
        Double longitude= (Double)get(path+".longitude");
        Double altitude= (Double)get(path+".altitude");
        if (altitude==null) {
            altitude=0.0;
        }
        if (latitude!=null && longitude!=null) {
            String sTimeStamp=(String)get(path+".timestamp");
            DateTime dateStamp=null;
            if (sTimeStamp!=null && !sTimeStamp.isEmpty()) {
                try {
                    dateStamp = DateTime.parse(sTimeStamp);
                } catch (IllegalFieldValueException ex) {
                    // Supply the datestamp with machine utc time
                    dateStamp = DateTime.now(DateTimeZone.UTC);
                }
            }
            result=new Position(latitude.doubleValue(), longitude.doubleValue(), altitude.doubleValue(),dateStamp);


        }
        return result;
    }

    public void setNavPosition(String vessel_uuid, Position position) {
        Position result=null;
        String path=getNavPositionPath(vessel_uuid);
        putPosition(path,position.getLatitude(),position.getLongitude(),0,"fairwind",Util.getIsoTimeString());
    }


    public Double getEnvDepthBelowKeel(String vessel_uuid) {
        return getDoubleByPath(getEnvDepthBelowKeelPath(vessel_uuid));
    }

    public Double getEnvDepthBelowSurface(String vessel_uuid) {
        return getDoubleByPath(getEnvDepthBelowSurfacePath(vessel_uuid));
    }

    public Double getEnvDepthBelowTransducer(String vessel_uuid) {
        return getDoubleByPath(getEnvDepthBelowTransducerPath(vessel_uuid));
    }

    public Double getDepth(String vessel_uuid, DepthMode depthMode) {
        Double depth=null;
        switch (depthMode) {
            case BELOW_KEEL:
                depth=getEnvDepthBelowKeel(vessel_uuid);
                break;

            case BELOW_SURFACE:
                depth=getEnvDepthBelowSurface(vessel_uuid);
                break;

            case BELOW_TRANSDUCER:
                depth=getEnvDepthBelowTransducer(vessel_uuid);
                break;
        }
        return depth;
    }

    public Double getCourse(String vessel_uuid, GroundWaterType groundWaterType, CompassMode compassMode) {
        Double course=null;
        switch (groundWaterType) {
            case WATER:
                course = getHeading(vessel_uuid, compassMode);
                break;
            case GROUND:
                course = getCourseOverGround(vessel_uuid, compassMode);
                break;
        }
        return course;
    }
    public Double getSpeed(String vessel_uuid,  GroundWaterType groundWaterType) {
        Double speed=null;
        switch (groundWaterType) {
            case WATER:
                speed = getNavSpeedThroughWater(vessel_uuid);
                break;
            case GROUND:
                speed = getNavSpeedOverGround(vessel_uuid);
                break;

        }
        return speed;
    }

    public static String[] getSpeedPathAndLabel(String vessel_uuid, SpeedMode speedMode) {
        String[] result=new String[2];
        String path=null;
        String label="";
        switch (speedMode) {
            case SPEEDOMETER:
                path= FairWindModelBase.getNavSpeedThroughWaterPath(vessel_uuid);
                label="SPEED";
                break;

            case SOG:
                path=FairWindModelBase.getNavSpeedOverGroundPath(vessel_uuid);
                label="SOG";
                break;

            case VMG:
                path=FairWindModelBase.getNavPerformanceVelocityMadeGoodPath(vessel_uuid);
                label="VMG";
                break;

            case VMG2WPT:
                path=FairWindModelBase.getNavPerformanceVelocityMadeGoodToWaypointPath(vessel_uuid);
                label="VMG to WPT";
                break;
        }
        result[0]=path;
        result[1]=label;
        return result;
    }

    public static String[] getDirectionPathAndLabel(String vessel_uuid, DirectionMode directionMode, CompassMode compassMode) {
        String[] result=new String[2];
        String path=null;

        String label="";
        switch (directionMode) {
            case COMPASS:
                path= FairWindModelBase.getHeadingPath(vessel_uuid,compassMode);
                switch (compassMode) {
                    case MAGNETIC:
                        label= "HEADING (m)";
                        break;
                    case TRUE:
                        label= "HEADING";
                        break;
                }
                break;

            case COG:
                path=FairWindModelBase.getCourseOverGroundPath(vessel_uuid,compassMode);
                switch (compassMode) {
                    case MAGNETIC:
                        label= "COG (m)";
                        break;
                    case TRUE:
                        label= "COG";
                        break;
                }
                break;

            case BEARING:
                path=FairWindModelBase.getCourseBearingTrack(vessel_uuid,compassMode);
                switch (compassMode) {
                    case MAGNETIC:
                        label= "BEARING (m)";
                        break;
                    case TRUE:
                        label= "BEARING";
                        break;
                }
                break;

            case BEARING2WPT:
                path=FairWindModelBase.getCourseBearingTrack(vessel_uuid,compassMode);
                switch (compassMode) {
                    case MAGNETIC:
                        label= "BEARING (m)";
                        break;
                    case TRUE:
                        label= "BEARING";
                        break;
                }
                break;
        }
        result[0]=path;
        result[1]=label;
        return result;
    }

    public Double getAnySpeed(String vessel_uuid) {

        Double speed=getNavSpeedThroughWater(vessel_uuid);
        if (speed==null) {
            speed=getNavSpeedOverGround(vessel_uuid);
        }
        return speed;
    }

    public Double getAnyCourse(String vessel_uuid) {
        Double angle=getCourse(vessel_uuid,GroundWaterType.WATER, CompassMode.TRUE);
        if (angle==null) {
            angle=getCourse(vessel_uuid,GroundWaterType.GROUND, CompassMode.TRUE);
        } else
        if (angle==null) {
            angle = getCourse(vessel_uuid, GroundWaterType.WATER, CompassMode.MAGNETIC);
        } else
        if (angle==null) {
            angle = getCourse(vessel_uuid, GroundWaterType.GROUND, CompassMode.MAGNETIC);
        } else
        if (angle==null) {
            String key = SignalKConstants.vessels + SignalKConstants.dot + vessel_uuid + SignalKConstants.dot + SignalKConstants.nav_headingTrue + ".value";
            angle = (Double) ((SignalKModel) this).get(key);
        }
        if (angle!=null) {
            Log.d(LOG_TAG,angle.toString());
        }
        return angle;
    }

    public Double getHeading(String vessel_uuid, CompassMode mode) {

        return getDoubleByPath(getHeadingPath(vessel_uuid,mode));
    }

    public Double getCourseOverGround(String vessel_uuid, CompassMode mode) {
        return getDoubleByPath(getCourseOverGroundPath(vessel_uuid, mode));
    }

    public Double getNavPositionLatitude(String vessel_uuid) {
        return getDoubleByPath(getNavPositionLatitudePath(vessel_uuid));
    }

    public Double getNavPositionLongitude(String vessel_uuid) {
        return getDoubleByPath(getNavPositionLongitudePath(vessel_uuid));
    }

    public Double getNavPositionAltitude(String vessel_uuid) {
        return getDoubleByPath(getNavPositionAltitudePath(vessel_uuid));
    }
    
    public Double getNavSpeedOverGround(String vessel_uuid) {
        return getDoubleByPath(getNavSpeedOverGroundPath(vessel_uuid));
    }

    public Double getNavSpeedThroughWater(String vessel_uuid) {
        return getDoubleByPath(getNavSpeedThroughWaterPath(vessel_uuid));
    }

    public Double getEnvCurrentDrift(String vessel_uuid) {
        return getDoubleByPath(getEnvCurrentDriftPath(vessel_uuid));
    }

    public Double getCurrentSet(String vessel_uuid, CompassMode compassMode) {
        return getDoubleByPath(getCurrentSetPath(vessel_uuid,compassMode));
    }

    public Double getEnvCurrentSetTrue(String vessel_uuid) {
        return getDoubleByPath(getEnvCurrentSetTruePath(vessel_uuid));
    }

    public Double getEnvCurrentSetMagnetic(String vessel_uuid) {
        return getDoubleByPath(getEnvCurrentSetMagneticPath(vessel_uuid));
    }

    public Double getEnvWaterTemperature(String vessel_uuid) {
        return getDoubleByPath(getEnvWaterTemperaturePath(vessel_uuid));
    }

    public Double getEnvWaterSalinity(String vessel_uuid) {
        return getDoubleByPath(getEnvWaterSalinityPath(vessel_uuid));
    }

    public Double getEnvWaterLiveWell(String vessel_uuid) {
        return getDoubleByPath(getEnvWaterLiveWellPath(vessel_uuid));
    }

    public Double getEnvWaterBaitWell(String vessel_uuid) {
        return getDoubleByPath(getEnvWaterBaitWellPath(vessel_uuid));
    }

    public Double getEnvOutsideTemperature(String vessel_uuid) {
        return getDoubleByPath(getEnvOutsideTemperaturePath(vessel_uuid));
    }

    public Double getEnvOutsidePressure(String vessel_uuid) {
        return getDoubleByPath(getEnvOutsidePressurePath(vessel_uuid));
    }

    public Double getEnvOutsideHumidity(String vessel_uuid) {
        return getDoubleByPath(getEnvOutsideHumidityPath(vessel_uuid));
    }

    public Double getEnvInsideTemperature(String vessel_uuid) {
        return getDoubleByPath(getEnvInsideTemperaturePath(vessel_uuid));
    }

    public Double getEnvInsideHumidity(String vessel_uuid) {
        return getDoubleByPath(getEnvInsideHumidityPath(vessel_uuid));
    }

    public Double getNavAttitudePitch(String vessel_uuid) {
        return getDoubleByPath(getNavAttitudePitchPath(vessel_uuid));
    }

    public Double getNavAttitudeRoll(String vessel_uuid) {
        return getDoubleByPath(getNavAttitudeRollPath(vessel_uuid));
    }

    public Double getNavAttitudeYaw(String vessel_uuid) {
        return getDoubleByPath(getNavAttitudeYawPath(vessel_uuid));
    }

    public Double getDoubleByPath(String path) {
        return (Double)get(path+".value");
    }


    ///////////////////////

    public static String getCourseBearingTrack(String vessel_uuid, CompassMode mode) {
        String path=null;
        switch (mode) {
            case MAGNETIC:
                path = getNavCourseBearingTrackMagneticPath(vessel_uuid);
                break;

            default:
                path = getNavCourseBearingTrackTruePath(vessel_uuid);
        }
        return path;
    }


    public static String getHeadingPath(String vessel_uuid, CompassMode mode) {
        String path=null;
        switch (mode) {
            case MAGNETIC:
                path = getNavHeadingMagneticPath(vessel_uuid);
                break;

            default:
                path = getNavHeadingTruePath(vessel_uuid);
        }
        return path;
    }

    public static String getCourseOverGroundPath(String vessel_uuid, CompassMode compassMode) {
        String path=null;
        switch (compassMode) {
            case MAGNETIC:
                path = getNavCourseOverGRoundMagneticPath(vessel_uuid);
                break;

            default:
                path = getNavCourseOverGroundTruePath(vessel_uuid);
        }
        return path;
    }

    public static String getBearingTrackPath(String vessel_uuid, CompassMode mode) {
        String path=null;
        switch (mode) {
            case MAGNETIC:
                path = getNavCourseBearingTrackMagneticPath(vessel_uuid);
                break;

            default:
                path = getNavCourseBearingTrackTruePath(vessel_uuid);
        }
        return path;
    }

    public static String getWindAnglePath(String vessel_uuid, WindMode windMode) {
        String path=null;

        switch (windMode) {
            case TRUE:
                path=getEnvWindAngleTrueWaterPath(vessel_uuid);
                break;

            case OVERGROUND:
                path=getEnvWindAngleTrueGroundPath(vessel_uuid);
                break;

            case APPARENT:
                path=getEnvWindAngleApparentPath(vessel_uuid);
                break;
        }

        return path;
    }

    public static String getWindDirectionPath(String vessel_uuid, CompassMode compassMode, WindMode windMode) {
        String path=null;
        switch (windMode) {
            case TRUE:
                switch (compassMode) {
                    case MAGNETIC:
                        path = getEnvWindDirectionMagneticPath(vessel_uuid);
                        break;

                    case TRUE:
                        path = getEnvWindDirectionTruePath(vessel_uuid);
                }
                break;

            case APPARENT:
                path=getEnvWindAngleApparentPath(vessel_uuid);
                break;

            case OVERGROUND:
                path=getEnvWindDirectionTruePath(vessel_uuid);
                break;
        }
        return path;
    }

    public static String getWindSpeedPath(String vessel_uuid, WindMode windMode) {
        String path=null;

        switch (windMode) {
            case APPARENT:
                path=getEnvWindSpeedApparentPath(vessel_uuid);
                break;
            case TRUE:
                path=getEnvWindSpeedTruePath(vessel_uuid);
                break;

            case OVERGROUND:
                path=getEnvWindSpeedOverGroundPath(vessel_uuid);
                break;

            default:
                throw new RuntimeException();
        }

        return path;
    }


    public static String getDepthPath(String vessel_uuid) {
        return SignalKConstants.vessels+SignalKConstants.dot+fixSelfKey(vessel_uuid)+SignalKConstants.dot+SignalKConstants.env_depth;
    }
    public static String getDepthPath(String vessel_uuid, DepthMode depthMode) {
        String path=null;
        switch (depthMode) {
            case BELOW_TRANSDUCER:
                path=getEnvDepthBelowTransducerPath(vessel_uuid);
                break;

            case BELOW_KEEL:
                path=getEnvDepthBelowKeelPath(vessel_uuid);
                break;

            case BELOW_SURFACE:
                path=getEnvDepthBelowSurfacePath(vessel_uuid);
                break;
        }
        return path;
    }



    ///////////////////////

    public static String getNavAttitudePitchPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_attitude_pitch;
    }

    public static String getNavAttitudeRollPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_attitude_roll;
    }

    public static String getNavAttitudeYawPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_attitude_yaw;
    }

    public static String getEnvOutsidePressurePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_outside_pressure;
    }

    public static String getEnvInsideTemperaturePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_inside_temperature;
    }

    public static String getEnvInsideHumidityPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_inside_humidity;
    }

    public static String getEnvOutsideTemperaturePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_outside_temperature;
    }

    public static String getEnvOutsideHumidityPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_outside_humidity;
    }


    public static String getEnvOutsideTheoreticalWindChillTemperaturePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env+SignalKConstants.dot+"outside"+SignalKConstants.dot+"theoreticalWindChillTemperature";
    }

    public static String getEnvWaterTemperaturePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_water_temperature;
    }

    public static String getEnvWaterSalinityPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env+SignalKConstants.dot+"water"+SignalKConstants.dot+"salinity";
    }

    public static String getEnvWaterLiveWellPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env+SignalKConstants.dot+"water"+SignalKConstants.dot+"livewell";
    }

    public static String getEnvWaterBaitWellPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env+SignalKConstants.dot+"water"+SignalKConstants.dot+"baitwell";
    }

    public static String getEnvCurrentDriftPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_current_drift;
    }

    public static String getEnvCurrentSetTruePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_current_setTrue;
    }

    public static String getEnvCurrentSetMagneticPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_current_setMagnetic;
    }

    public static String getCurrentSetPath(String vessel_uuid, CompassMode compassMode) {
        String path=null;
        switch (compassMode) {
            case TRUE:
                path=getEnvCurrentSetTruePath(vessel_uuid);
                break;
            case MAGNETIC:
                path=getEnvCurrentSetMagneticPath(vessel_uuid);
                break;

        }
        return path;
    }


    public static String getEnvWindPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind;
    }

    public static String getEnvDepthPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_depth;
    }

    public static String getEnvDepthBelowTransducerPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_depth_belowTransducer;
    }
    public static String getEnvDepthBelowKeelPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_depth_belowKeel;
    }
    public static String getEnvDepthBelowSurfacePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_depth_belowSurface;
    }

    public static String getEnvDepthSurfaceToTransducerPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_depth_surfaceToTransducer;
    }
    public static String getEnvDepthTransducerToKeelPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_depth_transducerToKeel;
    }


    public  static String getNavPositionPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_position;
    }

    public  static String getNavPositionLatitudePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_position_latitude;
    }

    public  static String getNavPositionLongitudePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_position_longitude;
    }

    public  static String getNavPositionAltitudePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_position_altitude;
    }

    public static String getNavCourseBearingTrackMagneticPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav+".course.bearingTrackMagnetic";
    }

    public static String getNavCourseBearingTrackTruePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav+".course.bearingTrackTrue";
    }


    public static String getNavSpeedOverGroundPath(String vessel_uuid){
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_speedOverGround;
    }

    public static String getNavSpeedThroughWaterPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_speedThroughWater;

    }

    public static String getNavCourseOverGRoundMagneticPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_courseOverGroundMagnetic;

    }

    public static String getNavCourseOverGroundTruePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_courseOverGroundTrue;

    }

    public static String getNavHeadingMagneticPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_headingMagnetic;
    }

    public static String getNavHeadingTruePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav_headingTrue;
    }

    public static String getNavPerformanceVelocityMadeGoodPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav+SignalKConstants.dot+SignalKConstants.performance_velocityMadeGood;
    }

    public static String getNavPerformanceVelocityMadeGoodToWaypointPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.nav+SignalKConstants.dot+SignalKConstants.performance_velocityMadeGoodToWaypoint;
    }

    //public static String getEnvWindSpeedAlarmPath(String vessel_uuid) {
    //    return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_speed_alarm;
    //}

    public static String getEnvWindSpeedTruePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_speedTrue;
    }

    public static String getEnvWindSpeedOverGroundPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_speedOverGround;
    }

    public static String getEnvWindSpeedApparentPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_speedApparent;
    }

    public static String getEnvWindDirectionMagneticPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_directionMagnetic;
    }

    public static String getEnvWindDirectionTruePath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_directionTrue;
    }

    public static String getEnvWindDirectionChangeAlarmPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_directionChangeAlarm;
    }

    public static String getEnvWindAngleApparentPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_angleApparent;
    }

    public static String getEnvWindAngleTrueWaterPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_angleTrueWater;
    }

    public static String getEnvWindAngleTrueGroundPath(String vessel_uuid) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.env_wind_angleTrueGround;
    }

    /////////////////////////

    public static String getPropulsionPath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion+SignalKConstants.dot+propulsion_id;
    }

    public static String getPropulsionStatePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_state.replace("*",propulsion_id);
    }

    public static String getPropulsionRevolutionsPath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_revolutions.replace("*",propulsion_id);
    }

    public static String getPropulsionTemperaturePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_temperature.replace("*",propulsion_id);
    }

    public static String getPropulsionOilTemperaturePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_oilTemperature.replace("*",propulsion_id);
    }

    public static String getPropulsionOilPressurePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_oilPressure.replace("*",propulsion_id);
    }

    public static String getPropulsionCoolantTemperaturePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion+SignalKConstants.dot+propulsion_id+".coolantTemperature";
    }

    public static String getPropulsionCoolantPressurePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion+SignalKConstants.dot+propulsion_id+".coolantPressure";
    }

    //public static String getPropulsionEngineTypePath(String vessel_uuid, String propulsion_id) {
    //    return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_engineType.replace("*",propulsion_id);
    //}

    public static String getPropulsionExhaustTemperaturePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_exhaustTemperature.replace("*",propulsion_id);
    }

    public static String getPropulsionFuelRatePath(String vessel_uuid, String propulsion_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.propulsion_id_fuel_rate.replace("*",propulsion_id);
    }
    /////////////////////////

    public static String getTankLevelPath(String vessel_uuid, String tank_id) {
        return getVesselsDotUuidDot(vessel_uuid)+SignalKConstants.tanks_fuel_id_currentLevel.replace("*",tank_id);
    }

    /////////////////////////

    public static String fixSelfKey(String selfKey) {
        if (selfKey==null||selfKey.isEmpty()||selfKey.equalsIgnoreCase("self")) {
            return SignalKConstants.self;
        } else {
            return Util.fixSelfKey(selfKey);
        }
    }

    public static String getVesselsDotUuidDot(String vessel_uuid) {
        return SignalKConstants.vessels + SignalKConstants.dot + fixSelfKey(vessel_uuid) + SignalKConstants.dot;
    }



    /////////////////////////

    public int getNumberOfEngines() {
        return 1;
    }


    public int getNumberOfFuelTanks() {
        return 1;
    }


    public int getNumberOfTrimTabs() {
        return 0;
    }


    public Position getPosition() {
        return getNavPosition("self");
    }

    
    public String getBoatName() {
        Object data=getData().get(vessels_dot_self_dot +name);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    
    public String getBoatPort() {
        Object data=getData().get(vessels_dot_self_dot +port);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }


    public String getBoatUuid() {
        Object data=getData().get(vessels_dot_self_dot +uuid);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }


    public String getBoatMmsi() {
        Object data=getData().get(vessels_dot_self_dot +mmsi);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }


    public String getBoatUrl() {
        Object data=getData().get(vessels_dot_self_dot +url);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    
    public String getBoatFlag() {
        Object data=getData().get(vessels_dot_self_dot +flag);
        if (data!=null) {
            return data.toString();
        } else {
            return null;
        }
    }

    public Double getLatitude() {
        return getNavPositionLatitude("self");
    }

    public Double getLongitude() {
        return getNavPositionLongitude("self");
    }

    public Double getAltitude() {
        return  getNavPositionAltitude("self");
    }

    
    public DateTime getDateTime() {
        String millis=(String)getData().get(vessels_dot_self_dot + SignalKConstants.env_time_millis+".value");
        String timeZone=(String)getData().get(vessels_dot_self_dot + SignalKConstants.env_timezone+".value");
        DateTime dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone)));
        return dateTime;
    }


    public String getTime() {
        return (String)getData().get(vessels_dot_self_dot + env_time_time+".value");
    }

    public String getDate() {
        return (String)getData().get(vessels_dot_self_dot + env_time_date+".value");
    }

    public Long getMilliseconds() {
        return System.currentTimeMillis();
    }

    public Double getSOG() {
        Object data=getData().get(vessels_dot_self_dot +nav_speedOverGround+".value");
        if (data!=null) {
            return Double.parseDouble(data.toString());
        } else {
            return null;
        }
    }

    public Double getCOG() {
        Object data=getData().get(vessels_dot_self_dot +nav_courseOverGroundMagnetic+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    public Double getDepth() {
        Object data=getData().get(vessels_dot_self_dot +env_depth_belowTransducer+".value");
        if (data!=null) {
            return Double.parseDouble(data.toString());
        } else {
            return null;
        }
    }

    public Double getTrueWindDirection() {
        Object data=getData().get(vessels_dot_self_dot +env_wind_directionTrue+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    public Double getApparentWindAngle() {
        Object data=getData().get(vessels_dot_self_dot +env_wind_angleApparent+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    public Double getApparentWindSpeed() {
        Object data=getData().get(vessels_dot_self_dot +env_wind_speedApparent+".value");
        if (data!=null) {
            return Double.parseDouble(data.toString());
        } else {
            return null;
        }
    }

    public Double getTrueWindAngle() {
        Object data=getData().get(vessels_dot_self_dot +env_wind_angleTrueGround+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    public Double getTrueWindSpeed() {
        Object data=getData().get(vessels_dot_self_dot +env_wind_speedTrue+".value");
        if (data!=null) {
            return Double.parseDouble(data.toString());
        } else {
            return null;
        }
    }

    
    public Double getSpeed() {
        Object data=getData().get(vessels_dot_self_dot +nav_speedThroughWater+".value");
        if (data!=null) {
            return Double.parseDouble(data.toString());
        } else {
            return null;
        }
    }

    
    public Double getHeading() {
        return getHeading("self",CompassMode.MAGNETIC);
    }

    public String getName(String vessel_uuid) {
        String key=SignalKConstants.vessels + SignalKConstants.dot + vessel_uuid + SignalKConstants.dot + SignalKConstants.name;
        return (String)(get(key));
    }

    public String getMmsi(String vessel_uuid) {

        String key=SignalKConstants.vessels + SignalKConstants.dot + vessel_uuid + SignalKConstants.dot + SignalKConstants.mmsi;//+".value";
        String mmsi=(String)((SignalKModel)this).getFullData().get(key);
        //mmsi=get(key).toString();
        return mmsi;
    }

    public String getNavigationState(String vessel_uuid) {

        String key=SignalKConstants.vessels + SignalKConstants.dot + vessel_uuid + SignalKConstants.dot + SignalKConstants.nav_state + ".value";
        return (String)(get(key));
    }

    public Position getPredictor(String uuid, int minutes) {
        Position predicted=null;
        Position position=getNavPosition(uuid);
        if (position!=null) {
            Double speed=getAnySpeed(uuid);
            Double heading=getAnyCourse(uuid);

            predicted=position.getPredictPosition(speed,heading,minutes);
        }
        return predicted;
    }
    /*
    public Position getPredictor(int minutes) {


        Double latitude;
        Double longitude;
        Double speed;
        Double heading;

        latitude=getLatitude();
        if (latitude==null) return null;

        longitude=getLongitude();
        if (longitude==null) return null;

        speed=getSpeed(self,GroundWaterType.GROUND);
        if (speed == null) {
            speed=getSpeed(getBoatUuid(),GroundWaterType.WATER);
        }
        heading=getCourse(getBoatUuid(),GroundWaterType.GROUND,CompassMode.TRUE);
        if (heading == null ) {
            heading=getCourse(getBoatUuid(),GroundWaterType.WATER,CompassMode.TRUE);
            if (heading == null ) {
                heading = getCourse(getBoatUuid(), GroundWaterType.GROUND, CompassMode.MAGNETIC);
                if (heading == null) {
                    heading = getCourse(getBoatUuid(), GroundWaterType.WATER, CompassMode.MAGNETIC);
                }
            }
        }
        if(speed!=null && heading!=null){

            return Position.getPredictPosition(latitude,longitude,speed,Math.toDegrees(heading),minutes);

        }else{
            return null;
        }

    }
    */
    //get added by Lovig90 21/10/2015
    //*******************************************************************************************

    
    public Double getMagneticVariation() {
        Object data=getData().get(vessels_dot_self_dot+nav_magneticVariation+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    
    public Double getWaterTemperature() {
        return ((Double)getData().get(vessels_dot_self_dot+env_water_temperature+".value"));
    }

    
    public Double getTrueDirection() {
        Object data=getData().get(vessels_dot_self_dot + nav_headingTrue+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    
    public Position getDestinationWayPointPosition(String wayPointId) {
        return ((Position)getData().get(resources_waypoints+dot+wayPointId+".position"));
    }

    
    public Double getDestinationWayPointLatitude(String wayPointId) {
        return ((Double)getData().get(resources_waypoints+dot+wayPointId+".position.latitude"));
    }

    
    public Double getDestinationWayPointLongitude(String wayPointId) {
        return ((Double)getData().get(resources_waypoints+dot+wayPointId+".position.longitude"));
    }

    
    public Double getDestinationWayPointAltitude(String wayPointId) {
        return ((Double)getData().get(resources_waypoints+dot+wayPointId+".position.altitude"));
    }

    
    public String getDestinationWayPointId(String wayPointId) {

        return wayPointId;
    }

    
    public Double getBearingActualToNextWayPoint() {

        Object data=getData().get(vessels_dot_self_dot+nav_courseRhumbline_nextPoint_bearingTrue);
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    
    public Double getDistanceActualToNextWayPoint() {
        return ((Double)getData().get(vessels_dot_self_dot+nav_course_activeRoute+".distanceActual"));
    }

    
    public Double getBearingDirect() {
        Object data=getData().get(vessels_dot_self_dot+nav_course_activeRoute);
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    
    public Double getGpsQuality() {
        return ((Double)getData().get(vessels_dot_self_dot+nav_gnss_methodQuality+".value"));
    }

    
    public Double getSatellities() {
        return ((Double)getData().get(vessels_dot_self_dot+nav_gnss_satellites+".value"));
    }

    
    public Double getHDop() {
        return ((Double)getData().get(vessels_dot_self_dot+nav_gnss_horizontalDilution+".value"));
    }

    
    public Double getVMG() {
        return ((Double)getData().get(vessels_dot_self_dot+"performance.velocityMadeGood"+".value"));
    }

    
    public Double getRuddleAngle() {

        Object data=getData().get(vessels_dot_self_dot+steering_rudderAngle+".value");
        if (data!=null) {
            return Math.toDegrees(Double.parseDouble(data.toString()));
        } else {
            return null;
        }
    }

    
    public Double getTripMiles() {
        return ((Double)getData().get(vessels_dot_self_dot+nav_logTrip+".value"));
    }

    
    public Double getTotalMiles() {
        return ((Double)getData().get(vessels_dot_self_dot+nav_log+".value"));
    }

    
    public Double getAirPressure() {
        return ((Double) getData().get(vessels_dot_self_dot + env_outside_pressure + ".value"));
    }

    
    public Double getAirTemp() {
        return ((Double) getData().get(vessels_dot_self_dot + env_outside_temperature + ".value"));
    }

    
    public Double getWaterTemp() {
        return ((Double) getData().get(vessels_dot_self_dot + env_water_temperature + ".value"));
    }

    
    public Double getHumidity() {
        return ((Double) getData().get(vessels_dot_self_dot + env_outside_humidity + ".value"));
    }

    
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

    
    public void setBoatName(String value) {
        //put(vessels_dot_self_dot+name,value,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
        getFullData().put(vessels_dot_self_dot+name,value);
    }

    
    public void setBoatFlag(String value) {
        //put(vessels_dot_self_dot+flag,value,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
        getFullData().put(vessels_dot_self_dot+flag,value);
    }

    
    public void setBoatPort(String value) {
        //put(vessels_dot_self_dot+port,value,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
        getFullData().put(vessels_dot_self_dot+port,value);
    }

    
    public void setBoatUuid(String value) {
        //put(vessels_dot_self_dot+uuid,value,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
        getFullData().put(vessels_dot_self_dot+uuid,value);
    }

    
    public void setBoatMmsi(String value) {
        //put(vessels_dot_self_dot+mmsi,value,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
        getFullData().put(vessels_dot_self_dot+name,value);
    }

    
    public void setBoatUrl(String value) {
        //put(vessels_dot_self_dot+url,value,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
        getFullData().put(vessels_dot_self_dot+url,value);
    }

    
    public void setLatitude(Double latitude) {
        put(vessels_dot_self_dot+nav_position_latitude,latitude,FairWindModel.FAIRWIND_SIGNALK_SOURCE);

    }

    
    public void setLongitude(Double longitude) {
        put(vessels_dot_self_dot +nav_position_longitude,longitude,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setAltitude(Double altitude) {
        put(vessels_dot_self_dot +nav_position_altitude,altitude,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTime(String time) {

        put(vessels_dot_self_dot + env_time_time+".value",time,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDate(String date) {
        put(vessels_dot_self_dot + env_time_date+".value",date,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setSOG(Double sog) {
        put(vessels_dot_self_dot +nav_speedOverGround+".value",sog,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setCOG(Double cog) {
        put(vessels_dot_self_dot +nav_courseOverGroundMagnetic+".value",cog,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDepth(Double depth) {
        put(vessels_dot_self_dot +env_depth_belowTransducer+".value",depth,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTrueWindDirection(Double trueWindDirection) {
        put(vessels_dot_self_dot +env_wind_directionTrue+".value",trueWindDirection,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setApparentWindAngle(Double apparentWindAngle) {
        put(vessels_dot_self_dot +env_wind_angleApparent+".value",apparentWindAngle,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setApparentWindSpeed(Double apparentWindSpeed) {
        put(vessels_dot_self_dot +env_wind_speedApparent+".value",apparentWindSpeed,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTrueWindAngle(Double trueWindAngle) {
        put(vessels_dot_self_dot +env_wind_angleTrueGround+".value",trueWindAngle,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTrueWindSpeed(Double trueWindSpeed) {
        put(vessels_dot_self_dot +env_wind_speedTrue+".value",trueWindSpeed,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setSpeed(Double speed) {
        put(vessels_dot_self_dot +nav_speedThroughWater+".value",speed,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setHeading(Double heading) {
        setHeading("self",CompassMode.MAGNETIC,heading);
    }

    public void setHeading(String vessel_uuid, CompassMode compassMode, Double heading) {
        String path= FairWindModelBase.getHeadingPath(vessel_uuid,compassMode);
        put(path+".value",heading,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setMagneticVariation(Double magneticVariation) {
        put(vessels_dot_self_dot+nav_magneticVariation+".value",magneticVariation,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setWaterTemperature(Double waterTemperature) {
        put(vessels_dot_self_dot+env_water_temperature+".value",waterTemperature,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTrueDirection(Double trueDirection) {
        put(vessels_dot_self_dot + nav_headingTrue+".value",trueDirection,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDestinationWayPointPosition(String wayPointId, Position position) {
        put(resources_waypoints+dot+wayPointId+".position",position,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDestinationWayPointLatitude(String wayPointId, Double destinationWayPointLatitude) {
        put(resources_waypoints+dot+wayPointId+".position.latitude",destinationWayPointLatitude,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDestinationWayPointLongitude(String wayPointId, Double destinationWayPointLongitude) {
        put(resources_waypoints+dot+wayPointId+".position.longitude",destinationWayPointLongitude,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDestinationWayPointAltitude(String wayPointId, Double destinationWayPointAltitude) {
        put(resources_waypoints+dot+wayPointId+".position.altitude",destinationWayPointAltitude,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDestinationWayPointId(String wayPointId) {
        put(resources_waypoints+dot+wayPointId,wayPointId,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setBearingActualToNextWayPoint(Double bearingActualToNextWayPoint) {
        put(vessels_dot_self_dot+nav_courseRhumbline_nextPoint_bearingTrue,bearingActualToNextWayPoint,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setDistanceActualToNextWayPoint(Double distanceActualToNextWayPoint) {
        put(vessels_dot_self_dot+nav_courseRhumbline_nextPoint_distance,distanceActualToNextWayPoint,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setBearingDirect(Double bearingDirect) {
        put(vessels_dot_self_dot+nav_courseRhumbline_bearingTrackTrue,bearingDirect,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setGpsQuality(Double gpsQuality) {
        put(vessels_dot_self_dot+nav_gnss_methodQuality+".value",gpsQuality,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setSatellities(Double satellities) {
        put(vessels_dot_self_dot+nav_gnss_satellites+".value",satellities,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setHDop(Double hDop) {
        put(vessels_dot_self_dot+nav_gnss_horizontalDilution+".value",hDop,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setVMG(Double vmg) {
        put(vessels_dot_self_dot+"performance.velocityMadeGood"+".value",vmg,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setRuddleAngle(Double ruddleAngle) {
        put(vessels_dot_self_dot+steering_rudderAngle+".value",ruddleAngle,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTripMiles(Double tripMiles) {
        put(vessels_dot_self_dot+nav_logTrip+".value",tripMiles,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    
    public void setTotalMiles(Double totalMiles) {
        put(vessels_dot_self_dot+nav_log+".value",totalMiles,FairWindModel.FAIRWIND_SIGNALK_SOURCE);
    }

    //*******************************************************************************************


    public int getAccurancy() { return 1; }

    /*

    private SignalKModel signalKModelDiff;
    public  SignalKModel getDiff() {
        return signalKModelDiff;
    }

    public SignalKModel getSignalKModelCopy() {
        SignalKModel signalKModelDst= SignalKModelFactory.getCleanInstance();
        SignalKModel signalKModelSrc=(SignalKModel)this;
        NavigableSet<String> keys=signalKModelSrc.getKeys();
        for (String key:keys) {
            Object value=signalKModelSrc.getFullData().get(key);
            signalKModelDst.getFullData().put(key,value);
        }
        return signalKModelDst;
    }


    public  void evalDiff(SignalKModel signalKModelPre) {
        Log.d(LOG_TAG,"evalDiff");
        signalKModelDiff= SignalKModelFactory.getCleanInstance();
        SignalKModel signalKModelCurr=(SignalKModel)this;
        NavigableSet<String> keys=signalKModelCurr.getKeys();
        for (String key:keys) {
            Log.d(LOG_TAG,"evalDiff: key="+key);

            Object valueCurr = signalKModelCurr.getFullData().get(key);
            Object valuePre = signalKModelPre.getFullData().get(key);
            if (valueCurr == null || valueCurr.equals(valuePre) == false) {
                signalKModelDiff.getData().put(key, valueCurr);
            }

        }
        Log.d(LOG_TAG,"done.");
    }
    */





    private Waypoints waypoints=new Waypoints(this);
    public Waypoints getWaypoints() { return waypoints; }

    public void createWaypoint(Context context,View view) {

        Position position=getNavPosition("self");
        Waypoint waypoint=new Waypoint("MapWaypoint X",position);
        waypoint.setDescription("A new waypoint");
        getWaypoints().add(waypoint);

        String msg="New waypoint in "+
                Formatter.formatLatitude(Formatter.COORDS_STYLE_DDMM,waypoint.getLatitude(),"n/a")+","+
                Formatter.formatLongitude(Formatter.COORDS_STYLE_DDMM,waypoint.getLongitude(),"n/a")+
                " at "+waypoint.getTimeStamp();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG,msg);

    }

}
