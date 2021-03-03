package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.util.Position;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.nmea0183.sentence.BWCSentence;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class BWCMapper extends SentenceSignalKAbstractMapper {
    public BWCMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof BWCSentence);
    }

    @Override
    public void map() {
        BWCSentence sen=(BWCSentence) evt.getSentence();
        if (sen!=null) {

            String destinationWaypointId = sen.getDestinationWaypointId();
            if (destinationWaypointId != null && !destinationWaypointId.isEmpty()) {
                //sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId, sen.getDestinationWaypointId());
                put(SignalKConstants.resources_waypoints + dot + destinationWaypointId, sen.getDestinationWaypointId());

                Position destinationWaypointPosition=sen.getDestinationWaypointPosition();
                if (destinationWaypointPosition!=null) {

                    putPosition(destinationWaypointId, destinationWaypointPosition, 0.0);
                            /*
                            //sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position", sen.getDestinationWaypointPosition());
                            sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position", sen.getDestinationWaypointPosition(),src.getSourceRef(), src.getNow());

                            if (!Double.isNaN(destinationWaypointPosition.getLatitude())) {
                                //sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position.latitude", destinationWaypointPosition.getLatitude());
                                sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position.latitude", destinationWaypointPosition.getLatitude(),src.getSourceRef(), src.getNow());
                            }

                            if (!Double.isNaN(destinationWaypointPosition.getLongitude())) {
                                //sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position.longitude", destinationWaypointPosition.getLongitude());
                                sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position.longitude", destinationWaypointPosition.getLongitude(),src.getSourceRef(), src.getNow());
                            }

                            if (!Double.isNaN(destinationWaypointPosition.getAltitude())) {
                                //sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position.altitude", destinationWaypointPosition.getAltitude());
                                sk.put(SignalKConstants.resources_waypoints + dot + destinationWaypointId + ".position.altitude", destinationWaypointPosition.getAltitude(),src.getSourceRef(), src.getNow());
                            }
                            */
                }
            }
            if (!Double.isNaN(sen.getMagneticBearing())) {
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_bearingTrackMagnetic, Math.toRadians(sen.getMagneticBearing()));
            }

            if (!Double.isNaN(sen.getTrueBearing())) {
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_bearingTrackTrue, Math.toRadians(sen.getTrueBearing()));
            }
            // in meters
            if (!Double.isNaN(sen.getDistance())) {
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_course_activeRoute + ".distance", sen.getDistance());
            }
            if (sen.getTime()!=null) {
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_course_activeRoute + dot + timestamp, sen.getTime().toISO8601());
            }
        }
    }
}
