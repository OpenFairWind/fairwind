package it.uniparthenope.fairwind.data.nmea0183.parser;

import net.sf.marineapi.nmea.parser.SentenceParser;
import it.uniparthenope.fairwind.data.nmea0183.sentence.SUPSentence;

import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public class SUPParser extends SentenceParser implements SUPSentence {

    /**
     * Creates a new instance of SUPParser.
     *
     * @param nmea NMEA sentence String.
     */
    public SUPParser(String nmea) {
        super(nmea, "SUP");
    }

    /**
     * Creates a new empty SUPParser.
     *
     * @param talker TalkerId to set
     */
    public SUPParser(TalkerId talker) {
        super(talker, "SUP", 6);
    }
}
