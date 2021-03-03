package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk9C;
import it.uniparthenope.fairwind.data.stalk.util.Bits;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk9CParser extends StalkParser implements Stalk9C {
    public static final String LOG_TAG="STALK_9C_PARSER";
    /*
    9C  U1  VW  RR    Compass heading and Rudder position (see also command 84)
                     Compass heading in degrees:
                       The two lower  bits of  U * 90 +
                       the six lower  bits of VW *  2 +
                       number of bits set in the two higher bits of U =
                       (U & 0x3)* 90 + (VW & 0x3F)* 2 + (U & 0xC ? (U & 0xC == 0xC ? 2 : 1): 0)
                     Turning direction:
                       Most significant bit of U = 1: Increasing heading, Ship turns right
                       Most significant bit of U = 0: Decreasing heading, Ship turns left
                     Rudder position: RR degrees (positive values steer right,
                       negative values steer left. Example: 0xFE = 2° left)
                     The rudder angle bar on the ST600R uses this record
     */

    public Stalk9CParser(String sentence) {
        super(sentence);
    }

    public Stalk9CParser() {
        super((byte)0x9c, (byte)0x01);
    }

/*

unsigned char angle = (seatalk_messages_in[i][2] >> 6) | (seatalk_messages_in[i][3] << 2);
                if(angle!=0xFF)
                        {
                        seatalk_heading_magnetic=((unsigned int)((seatalk_messages_in[i][2]>>4)&0x03)*180)+angle;
                        if(seatalk_heading_magnetic>719)
                                {
                                        seatalk_heading_magnetic-=720;
                                }
                                seatalk_heading_magnetic/=2;
                                handler(SEATALK_ID_HEADING_MAGNETIC);
                        }

                        if(message_id==SEATALK_ID_COMP_RUDD)
                        {
                                seatalk_rudder=(signed char)seatalk_messages_in[i][4];
                        }
                        else
                        {
                                seatalk_rudder=(signed char)seatalk_messages_in[i][7];
                        }
                        handler(SEATALK_ID_RUDDER);
                }


 */
    @Override
    public Double getCompassHeading() {

        Double compassHeading=Double.NaN;
        if (isVaild()) {
            try {
                byte angle = (byte)((getByte(2) >> 6) | (getByte(3) << 2));
                if (angle!=0xff) {
                    compassHeading=1.0*(((getByte(2)>>4) & 0x03)*180)+angle;
                    if(compassHeading>719)
                    {
                        compassHeading-=720;
                    }
                    compassHeading/=2;

                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        Log.d(LOG_TAG,"compassHeading:"+compassHeading);
        return compassHeading;
    }

    @Override
    public int getTurningDirection() {
        Log.d(LOG_TAG,"getTurningDirection");
        int turningDirection=0;
        if (isVaild()) {

            int U=getHighNibble(1);
            if ((U & 0x80)==0x80) {
                turningDirection=1;
            } else {
                turningDirection=-1;
            }
        }
        return turningDirection;
    }

    @Override
    public Double getRudderPosition() {
        Log.d(LOG_TAG,"rudderPosition");
        Double rudderPosition=Double.NaN;
        if (isVaild()) {
            int RR=getByte(3);
            rudderPosition=(double)(RR);
        }
        return rudderPosition;
    }
}
