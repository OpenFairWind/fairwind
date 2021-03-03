package it.uniparthenope.fairwind.handler;

import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.Util.R;

/**
 * Created by raffaelemontella on 04/04/2017.
 */

public class CourseHandler extends HandlerBase {
    private static final String LOG_TAG="COURSE_HANDLER";

    private Position positionPrev=null;

    double[] vmgs=new double[8];
    int i=0;

    public double vmgTo(Position positionCurrent,Position positionNext) {
        double vmg=Double.NaN;
        // Calculate the distance from the current position to the next position
        double distance=positionCurrent.distanceTo(positionNext);
        // Calculate the distance from the previous position to the next position
        double distancePrev=positionPrev.distanceTo(positionNext);

        // Calcilate the difference
        // distanceDelta > 0 : getting closer
        // distanceDelta < 0 : getting further
        double distanceDelta=distancePrev-distance;

        long timeStampCurrent=positionCurrent.getTimeStamp().getMillis();
        long timeStampPrev=positionPrev.getTimeStamp().getMillis();

        // Calculate the time since the last position
        double deltaSecs = (timeStampCurrent - timeStampPrev)/1000.0;
        if (distanceDelta!=0 && deltaSecs>0) {
            vmgs[i]= distanceDelta / deltaSecs;
            i++;
            if (i==vmgs.length) {
                vmg=0;
                for(double v:vmgs) {
                    vmg+=v;
                }
                vmg=vmg/(double)vmgs.length;
                i=0;
            }
        }
        return vmg*0.514444;
    }



    public void handle(SignalKModel signalKModel) {
        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        String nextHRef=(String)signalKModel.get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint);
        if (nextHRef!=null && nextHRef.isEmpty()==false) {
            String nextId=nextHRef.split("[.]")[2];
            Waypoint nextWaypoint= (Waypoint) fairWindModel.getWaypoints().asMap().get(nextId);
            if (nextWaypoint!=null) {
                Position nextPosition=nextWaypoint.getPosition();

                // Get the current position
                Position position=fairWindModel.getNavPosition("self");

                Double dtw=position.distanceTo(nextWaypoint.getPosition());
                Double brgTrue=nextWaypoint.getPosition().bearingTo(position);

                fairWindModel.getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_distance,dtw);
                fairWindModel.getEventBus().post(new PathEvent(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_distance,0, PathEvent.EventType.ADD));

                fairWindModel.getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_bearingTrue,Math.toRadians(brgTrue));
                fairWindModel.getEventBus().post(new PathEvent(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_bearingTrue,0, PathEvent.EventType.ADD));


                Log.d(LOG_TAG,"DTW:"+dtw);
                Log.d(LOG_TAG,"True:"+brgTrue);
                Double magneticVariation=fairWindModel.getMagneticVariation();
                if (magneticVariation!=null && Double.isNaN(magneticVariation)==false) {
                    Double brgMagnetic=brgTrue+magneticVariation;
                    fairWindModel.getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic,Math.toRadians(brgMagnetic));
                    fairWindModel.getEventBus().post(new PathEvent(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic,0, PathEvent.EventType.ADD));
                    Log.d(LOG_TAG,"Magnetic:"+brgMagnetic);
                }

                String previousHRef=(String)signalKModel.get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_previousPoint);
                if (previousHRef!=null && previousHRef.isEmpty()==false) {
                    String previousId = previousHRef.split("[.]")[2];
                    Waypoint previousWaypoint = (Waypoint) fairWindModel.getWaypoints().asMap().get(previousId);
                    if (previousWaypoint != null) {
                        Position previousPosition=previousWaypoint.getPosition();
                        double s13=previousPosition.distanceTo(position);
                        double t13=Math.toRadians(previousPosition.bearingTo(position));
                        double t12=Math.toRadians(previousPosition.bearingTo(nextPosition));
                        double crossTrackError = Math.asin(Math.sin(s13 / R) * Math.sin(t13 - t12)) * R;
                        fairWindModel.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_crossTrackError,crossTrackError);
                        fairWindModel.getEventBus().post(new PathEvent(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_crossTrackError,0, PathEvent.EventType.ADD));

                        Log.d(LOG_TAG,"XTE:"+crossTrackError);

                    }
                }
                if (positionPrev!=null) {
                    Double vmg= vmgTo(position,nextPosition);
                    if (vmg!=null && Double.isNaN(vmg)==false && vmg>0) {
                        fairWindModel.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_velocityMadeGood,vmg);
                        fairWindModel.getEventBus().post(new PathEvent(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_velocityMadeGood,0, PathEvent.EventType.ADD));


                        Log.d(LOG_TAG,"VMG:"+vmg);

                        if (Double.isNaN(vmg)==false && vmg>0) {
                            double distance = position.distanceTo(nextPosition);
                            long ttg = (long) (distance / vmg);
                            if ( ttg >= 0) {
                                fairWindModel.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_timeToGo, ttg);
                                fairWindModel.getEventBus().post(new PathEvent(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_timeToGo,0, PathEvent.EventType.ADD));
                                Log.d(LOG_TAG, "TTG:" + ttg);
                            }
                        }
                    }
                }
                // Save latest position
                positionPrev=position;
            }
        }
    }

}
