package it.uniparthenope.fairwind.data.nmea0183.parser;

import net.sf.marineapi.nmea.parser.SentenceParser;
import it.uniparthenope.fairwind.data.nmea0183.sentence.ATDSentence;

import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public class ATDParser extends SentenceParser implements ATDSentence {

    /**
     * Creates a new instance of ATDParser.
     *
     * @param nmea NMEA sentence String.
     */
    public ATDParser(String nmea) {
        super(nmea, "ATD");
    }

    /**
     * Creates a new empty ATDParser.
     *
     * @param talker TalkerId to set
     */
    public ATDParser(TalkerId talker) {
        super(talker, "ATD", 6);
    }
}
