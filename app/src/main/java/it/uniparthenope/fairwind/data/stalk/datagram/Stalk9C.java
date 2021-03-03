package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public interface Stalk9C extends Stalk {
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
    public Double getCompassHeading();
    public int getTurningDirection();
    public Double getRudderPosition();
}
