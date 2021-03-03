package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk84;
import it.uniparthenope.fairwind.data.stalk.util.Bits;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk84Parser extends StalkParser implements Stalk84 {
    public static final String LOG_TAG="STALK_84_PARSER";
    /*
    84  U6  VW  XY 0Z 0M RR SS TT  Compass heading  Autopilot course and
                  Rudder position (see also command 9C)
                  Compass heading in degrees:
                    The two lower  bits of  U * 90 +
                    the six lower  bits of VW *  2 +
                    number of bits set in the two higher bits of U =
                    (U & 0x3)* 90 + (VW & 0x3F)* 2 + (U & 0xC ? (U & 0xC == 0xC ? 2 : 1): 0)
                  Turning direction:
                    Most significant bit of U = 1: Increasing heading, Ship turns right
                    Most significant bit of U = 0: Decreasing heading, Ship turns left
                  Autopilot course in degrees:
                    The two higher bits of  V * 90 + XY / 2
                  Z & 0x2 = 0 : Autopilot in Standby-Mode
                  Z & 0x2 = 2 : Autopilot in Auto-Mode
                  Z & 0x4 = 4 : Autopilot in Vane Mode (WindTrim), requires regular "10" datagrams
                  Z & 0x8 = 8 : Autopilot in Track Mode
                  M: Alarms + audible beeps
                    M & 0x04 = 4 : Off course
                    M & 0x08 = 8 : Wind Shift
                  Rudder position: RR degrees (positive values steer right,
                    negative values steer left. Example: 0xFE = 2° left)
                  SS & 0x01 : when set, turns off heading display on 600R control.
                  SS & 0x02 : always on with 400G
                  SS & 0x08 : displays “NO DATA” on 600R
                  SS & 0x10 : displays “LARGE XTE” on 600R
                  SS & 0x80 : Displays “Auto Rel” on 600R
                  TT : Always 0x08 on 400G computer, always 0x05 on 150(G) computer
     */

    public Stalk84Parser(String sentence) {
        super(sentence);
    }

    public Stalk84Parser() {
        super((byte)0x84, (byte)0x06);
    }


    @Override
    public Double getCompassHeading() {
        Double compassHeading=Double.NaN;
        if (isVaild()) {
            try {
                byte angle = (byte) ((getByte(2) >> 6) | (getByte(3) << 2));
                if (angle != 0xff) {
                    compassHeading = 1.0 * (((getByte(2) >> 4) & 0x03) * 180) + angle;
                    if (compassHeading > 719) {
                        compassHeading -= 720;
                    }
                    compassHeading /= 2;

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
            int RR=getByte(6);
            rudderPosition=(double)(RR);
        }
        return rudderPosition;
    }

    @Override
    public Double getAutopilotCourse() {
        Log.d(LOG_TAG,"autopilotCourse");
        Double autopilotCourse=Double.NaN;
        if (isVaild()) {
            try {
                int V = getLowNibble(2) >> 2;
                int XY = getByte(3);
                autopilotCourse = (double) (V * 90 + XY / 2);
                Log.d(LOG_TAG, "autopilotCourse:" + autopilotCourse);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return autopilotCourse;
    }

    @Override
    public boolean isOffCourse() {
        Log.d(LOG_TAG,"isOffCourse");
        boolean result=false;
        if (isVaild()) {
            try {
                int M=getLowNibble(5);
                result=((M & 0x04)== 4);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return result;
    }

    @Override
    public boolean isWindShift() {
        Log.d(LOG_TAG,"isWindShift");
        boolean result=false;
        if (isVaild()) {
            try {
                int M=getLowNibble(5);
                result=((M & 0x04)== 8);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return result;
    }
}
