package it.uniparthenope.fairwind.data.nmea0183.parser;

import net.sf.marineapi.nmea.parser.SentenceParser;
import it.uniparthenope.fairwind.data.nmea0183.sentence.HSCSentence;

import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public class HSCParser extends SentenceParser implements HSCSentence {

    /*
    HSC - Heading Steering Command

        1   2 3   4  5
        |   | |   |  |
 $--HSC,x.x,T,x.x,M,*hh<CR><LF>

 Field Number:
  1) Heading Degrees, True
  2) T = True
  3) Heading Degrees, Magnetic
  4) M = Magnetic
  5) Checksum
     */

    private static int HEADING_TRUE = 0;
    private static int HEADING_TRUE_INDICATOR = 1;
    private static int HEADING_MAGN = 2;
    private static int HEADING_MAGN_INDICATOR = 3;

    /**
     * Creates a new instance of HSCParser.
     *
     * @param nmea NMEA sentence String.
     */
    public HSCParser(String nmea) {
        super(nmea, "HSC");
    }

    /**
     * Creates a new empty HSCParser.
     *
     * @param talker TalkerId to set
     */
    public HSCParser(TalkerId talker) {
        super(talker, "HSC", 4);
        setCharValue(HEADING_TRUE_INDICATOR, 'T');
        setCharValue(HEADING_MAGN_INDICATOR, 'M');
    }

    @Override
    public double getTrueDirection() {
        return getDoubleValue(HEADING_TRUE);
    }


    @Override
    public double getMagneticDirection() {
        return getDoubleValue(HEADING_MAGN);
    }
}
