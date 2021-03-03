/*
 * GLLParser.java
 * Copyright (C) 2010 Kimmo Tuukkanen
 *
 * This file is part of Java Marine API.
 * <http://ktuukkan.github.io/marine-api/>
 *
 * Java Marine API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Java Marine API is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Marine API. If not, see <http://www.gnu.org/licenses/>.
 */
package it.uniparthenope.fairwind.data.nmea0183.parser;

import net.sf.marineapi.nmea.parser.ParseException;
import net.sf.marineapi.nmea.parser.SentenceParser;
import it.uniparthenope.fairwind.data.nmea0183.sentence.STNSentence;

import net.sf.marineapi.nmea.sentence.TalkerId;




/**
 * STN Sentence parser.
 *
 * @author Raffaele Montella
 */
public class STNParser extends SentenceParser implements STNSentence {

    // field indices
    private static final int INTERFACE = 0;
    private static final int COMMAND = 1;
    private static final int LENGTH = 2;
    private static final int BYTE02 = 3;
    private static final int BYTE03 = 4;
    private static final int BYTE04 = 5;
    private static final int BYTE05 = 6;
    private static final int BYTE06 = 7;
    private static final int BYTE07 = 8;
    private static final int BYTE08 = 9;
    private static final int BYTE09 = 10;
    private static final int BYTE10 = 11;
    private static final int BYTE11 = 12;
    private static final int BYTE12 = 13;
    private static final int BYTE13 = 14;
    private static final int BYTE14 = 15;
    private static final int BYTE15 = 16;
    private static final int BYTE16 = 17;
    private static final int BYTE17 = 18;
    private static final int BYTE18 = 19;
    private static final int BYTE19 = 20;


    /**
     * Creates a new instance of STNParser.
     *
     * @param nmea STN sentence String.
     * @throws IllegalArgumentException If the given sentence is invalid or does
     *             not contain STN sentence.
     */
    public STNParser(String nmea) {
        super(nmea, "STN");
    }

    /**
     * Creates STN parser with empty sentence.
     *
     * @param talker TalkerId to set
     */
    public STNParser(TalkerId talker) {
        super(talker, "STN", 4);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.PositionSentence#getPosition()
     */
    public byte[] getMessage() {
        int len=getLen();
        byte[] message=new byte[len];
        for(int i=0;i<len;i++) {
            message[i]=getByteValue(1+i);
        }
        return message;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.GLLSentence#getDataStatus()
     */
    public String getInterface() {
        return getStringValue(INTERFACE);
    }

    public int getLen() {
        return (getByteValue(LENGTH)&0x0f)+3;

    }

    public void setLen(int len) {
        byte h=(byte)(getByteValue(LENGTH)&0xf0);
        setByteValue(LENGTH,(byte)(h+(len-3)));
    }

    public byte getCommand() { return getByteValue(COMMAND);}
    public void setCommand(byte command) { setByteValue(COMMAND,command);}

    /*
     * (non-Javadoc)
     * @see
     * net.sf.marineapi.nmea.sentence.PositionSentence#setPosition(net.sf.marineapi
     * .nmea.util.Position)
     */
    public void setMessage(byte[] message) {
        int length=message[1]&0x0f;
        for (int i=0;i<length;i++) {
            setByteValue(1 + i, message[i]);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * net.sf.marineapi.nmea.sentence.GLLSentence#setDataStatus(net.sf.marineapi
     * .nmea.util.DataStatus)
     */
    public void setInterface(String iface) {
        setStringValue(INTERFACE, iface);
    }

    /**
     * Parse a byte value (2 hex digits) from the specified sentence field.
     *
     * @param index Data field index in sentence
     * @return Character contained in the field
     * @throws net.sf.marineapi.nmea.parser.ParseException If field contains more
     *             than one character
     */
    protected final byte getByteValue(int index) {
        String val = getStringValue(index);
        if (val.length() != 2) {
            String msg = String.format("Expected 2 digits, found something else [%s]", val);
            throw new ParseException(msg);
        }
        return (byte)(Integer.parseInt(val,16) & 0xff);
    }

    /**
     * Set a 2 hex digits in specified field.
     *
     * @param index Field index
     * @param value Value to set
     */
    protected final void setByteValue(int index, byte value) {
        setStringValue(index, String.format("%02X",value));
    }
}
