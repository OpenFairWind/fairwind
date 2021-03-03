package it.uniparthenope.fairwind.data.nmea0183.parser;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.TalkerId;
import it.uniparthenope.fairwind.data.nmea0183.sentence.VPWSentence;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public class VPWParser extends SentenceParser implements VPWSentence {
    /*
    PW - Speed - Measured Parallel to Wind

        1   2 3   4 5
        |   | |   | |
 $--VPW,x.x,N,x.x,M*hh<CR><LF>

 Field Number:
  1) Speed, "-" means downwind
  2) N = Knots
  3) Speed, "-" means downwind
  4) M = Meters per second
  5) Checksum
     */

    private static int VMG_KNOTS = 0;
    private static int VMG_KNOTS_UNIT = 1;
    private static int VMG_METERS = 2;
    private static int VMG_METERS_UNIT = 3;

    /**
     * Creates a new instance of VPWParser.
     *
     * @param nmea VPW sentence String
     * @throws IllegalArgumentException If specified sentence is invalid
     */
    public VPWParser(String nmea) {
        super(nmea, "VPW");
    }

    /**
     * Creates VWP parser with empty sentence.
     *
     * @param talker TalkerId to set
     */
    public VPWParser(TalkerId talker) {
        super(talker, "VPW", 4);
        setCharValue(VMG_METERS_UNIT, 'M');
        setCharValue(VMG_KNOTS_UNIT, 'N');

    }

    @Override
    public double getVMG() {
        return getDoubleValue(VMG_METERS);
    }

    @Override
    public double getVMGKnots() {
        return getDoubleValue(VMG_KNOTS);
    }
}
