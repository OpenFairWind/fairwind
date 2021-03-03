package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public interface Stalk84 extends Stalk {
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
    public Double getCompassHeading();
    public int getTurningDirection();
    public Double getRudderPosition();
    public Double getAutopilotCourse();
    public boolean isOffCourse();
    public boolean isWindShift();

}
