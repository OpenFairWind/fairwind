package it.uniparthenope.fairwind.data.pcdin.parser;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01FA04;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 12/02/2017.
 */

public class Pgn01FA04Parser extends PgnParser implements Pgn01FA04 {
    public Pgn01FA04Parser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {

    }

    public static Pgn01F805Parser newParser() {
        return new Pgn01F805Parser("$PCDIN,01FA04,00000000,00,0413D11509899804*28");
    }


    @Override
    public void parse(SignalKModel signalKModel, String src) {

    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }
}
