package it.uniparthenope.fairwind.data.nmea0183.parser;


import net.sf.marineapi.nmea.parser.ParseException;
import net.sf.marineapi.nmea.parser.SentenceParser;
import it.uniparthenope.fairwind.data.nmea0183.sentence.BWCSentence;

import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

/**
 * Created by raffaelemontella on 22/08/15.
 */
public class BWCParser extends SentenceParser implements BWCSentence {

    // field indices
    private static final int UTC_TIME = 0;
    private static final int LATITUDE = 1;
    private static final int LAT_HEMISPHERE = 2;
    private static final int LONGITUDE = 3;
    private static final int LON_HEMISPHERE = 4;
    private static final int BEARING_TRUE = 5;
    private static final int TRUE_INDICATOR = 6;
    private static final int BEARING_MAGN = 7;
    private static final int MAGN_INDICATOR = 8;
    private static final int DISTANCE = 9;
    private static final int DISTANCE_INDICATOR = 10;
    private static final int DESTINATION = 11;

    /**
     * Creates a new instance of BOD parser.
     *
     * @param nmea BOD sentence String
     * @throws IllegalArgumentException If specified String is invalid or does
     *             not contain a BOD sentence.
     */
    public BWCParser(String nmea) {
        super(nmea, "BWC");
    }

    /**
     * Creates GSA parser with empty sentence.
     *
     * @param talker TalkerId to set
     */
    public BWCParser(TalkerId talker) {
        super(talker, "BWC", 12);
        setCharValue(TRUE_INDICATOR, 'T');
        setCharValue(MAGN_INDICATOR, 'M');
        setCharValue(DISTANCE_INDICATOR, 'N');
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see net.sf.marineapi.nmea.sentence.TTMSentence#getDistance()
	 */
    @Override
    public double getDistance() {
        return getDoubleValue(DISTANCE);
    }

    /*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.PositionSentence#getPosition()
	 */
    @Override
    public Position getDestinationWaypointPosition() {
        if (hasValue(LATITUDE) && hasValue(LAT_HEMISPHERE) && hasValue(LONGITUDE) && hasValue(LON_HEMISPHERE)) {
            return parsePosition(LATITUDE, LAT_HEMISPHERE, LONGITUDE, LON_HEMISPHERE);
        } else {
            return null;
        }
    }

    /*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.TimeSentence#getTime()
	 */
    @Override
    public Time getTime() {
        String str = getStringValue(UTC_TIME);
        return new Time(str);
    }

    /*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.BODSentence#getDestinationWaypointId()
	 */
    @Override
    public String getDestinationWaypointId() {
        return getStringValue(DESTINATION);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.BODSentence#getMagneticBearing()
     */
    @Override
    public double getMagneticBearing() {
        return getDoubleValue(BEARING_MAGN);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.BODSentence#getTrueBearing()
     */
    @Override
    public double getTrueBearing() {
        return getDoubleValue(BEARING_TRUE);
    }

    /**
     * Parses the hemisphere of latitude from specified field.
     *
     * @param index Index of field that contains the latitude hemisphere value.
     * @return Hemisphere of latitude
     */
    protected CompassPoint parseHemisphereLat(int index) {
        char ch = getCharValue(index);
        CompassPoint d = CompassPoint.valueOf(ch);
        if (d != CompassPoint.NORTH && d != CompassPoint.SOUTH) {
            throw new ParseException("Invalid latitude hemisphere '" + ch + "'");
        }
        return d;
    }

    /**
     * Parses the hemisphere of longitude from the specified field.
     *
     * @param index Field index for longitude hemisphere indicator
     * @return Hemisphere of longitude
     */
    protected CompassPoint parseHemisphereLon(int index) {
        char ch = getCharValue(index);
        CompassPoint d = CompassPoint.valueOf(ch);
        if (d != CompassPoint.EAST && d != CompassPoint.WEST) {
            throw new ParseException("Invalid longitude hemisphere " + ch + "'");
        }
        return d;
    }

    /**
     * Parses the latitude degrees from the specified field. The assumed String
     * format for latitude is <code>ddmm.mmm</code>.
     *
     * @param index Index of field containing the latitude value.
     * @return Latitude value in degrees
     */
    protected double parseLatitude(int index) {
        String field = getStringValue(index);
        int deg = Integer.parseInt(field.substring(0, 2));
        double min = Double.parseDouble(field.substring(2));
        return deg + (min / 60);
    }

    /**
     * Parses the longitude degrees from the specified field. The assumed String
     * format for longitude is <code>dddmm.mmm</code>.
     *
     * @param index Index of field containing the longitude value.
     * @return Longitude value in degrees
     */
    protected double parseLongitude(int index) {
        String field = getStringValue(index);
        int deg = Integer.parseInt(field.substring(0, 3));
        double min = Double.parseDouble(field.substring(3));
        return deg + (min / 60);
    }
        /**
         * Parses a <code>Position</code> from specified fields.
         *
         * @param latIndex Latitude field index
         * @param latHemIndex Latitude hemisphere field index
         * @param lonIndex Longitude field index
         * @param lonHemIndex Longitude hemisphere field index
         * @return Position object
         */
    protected Position parsePosition(int latIndex, int latHemIndex,
                                     int lonIndex, int lonHemIndex) {

        double lat = parseLatitude(latIndex);
        double lon = parseLongitude(lonIndex);
        CompassPoint lath = parseHemisphereLat(latHemIndex);
        CompassPoint lonh = parseHemisphereLon(lonHemIndex);
        if (lath.equals(CompassPoint.SOUTH)) {
            lat = -lat;
        }
        if (lonh.equals(CompassPoint.WEST)) {
            lon = -lon;
        }
        return new Position(lat, lon);
    }


}
