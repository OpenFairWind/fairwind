package it.uniparthenope.fairwind.data.pcdin.parser;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F513;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 14/02/2017.
 */

public class Pgn01F513Parser extends PgnParser implements Pgn01F513 {
    public Pgn01F513Parser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {

    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {

    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }
}