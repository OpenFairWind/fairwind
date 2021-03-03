package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public enum DatagramId {

    // Source
    // http://www.thomasknauf.de/rap/seatalk2.htm

    ST00, // Depth below transducer
    ST01, // Equipment ID, sent at power on
    ST05, // Engine RPM and PITCH
    ST10, // Apparent Wind Angle
    ST11, // Apparent Wind Speed
    ST20, // Speed through water
    ST21, // Trip Mileage
    ST22, // Total Mileage
    ST23, // Water temperature
    ST24, // Display units for Mileage & Speed
    ST25, // Total & Trip Log
    ST26, // Speed through water
    ST27, // Water temperature
    ST30, // Set lamp Intensity
    ST36, // Cancel MOB
    ST38, // Codelock data
    ST50, // LAT position
    ST51, // LON position
    ST52, // Speed over Ground
    ST53, // Magnetic Course in degrees
    ST54, // GMT-time
    ST55, // TRACK keystroke on GPS unit
    ST56, // Date
    ST57, // Sat Info
    ST58, // Raw unfiltered position
    ST59; // Set Count Down Timer

}
