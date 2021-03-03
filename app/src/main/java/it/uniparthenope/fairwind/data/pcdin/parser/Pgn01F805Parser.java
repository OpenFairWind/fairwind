package it.uniparthenope.fairwind.data.pcdin.parser;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F805;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 12/02/2017.
 */

public class Pgn01F805Parser extends PgnParser implements Pgn01F805 {
    public Pgn01F805Parser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {

    }

    public static Pgn01F805Parser newParser() {
        return new Pgn01F805Parser("$PCDIN,01F805,00000000,00,393843308E4605C09FE1E0D44FBA0400D10593A94E93EFF5276D050000000000FC00AC26AC2663F2FFFF00FFFFFFFF*52");
    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {

    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }
}
