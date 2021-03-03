package it.uniparthenope.fairwind.data.nmea0183.parser;

import net.sf.marineapi.nmea.parser.SentenceParser;
import it.uniparthenope.fairwind.data.nmea0183.sentence.HVMSentence;

import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public class HVMParser extends SentenceParser implements HVMSentence {
    /*
    HVM - Magnetic Variation, Manually Set
Magnetic variation, automatically derived (calculated or from a data base) (HVD), or manually entered (HVM). The use of $--HDG is recommended.
$--HVD,x.x,a*hh<CR><LF> $--HVM,x.x,a*hh<CR><LF> 1
Magnetic variation, degrees E/W
Notes:
1) Easterly variation (E) subtracts from True Heading
Westerly variation (W) adds to True Heading
     */

    private static int MAGNETIC_VARIATION = 0;
    private static int MAGNETIC_VARIATION_WE = 1;

    /**
     * Creates a new instance of HVMParser.
     *
     * @param nmea NMEA sentence String.
     */
    public HVMParser(String nmea) {
        super(nmea, "HVM");
    }

    /**
     * Creates a new empty HVMParser.
     *
     * @param talker TalkerId to set
     */
    public HVMParser(TalkerId talker) {
        super(talker, "HVM", 2);
        setCharValue(MAGNETIC_VARIATION_WE, 'W');
    }

    @Override
    public double getMagneticVariation() {
        double sign=1;
        if (getCharValue(MAGNETIC_VARIATION_WE)=='E') sign=-1;
        return sign*getDoubleValue(MAGNETIC_VARIATION);
    }
}
