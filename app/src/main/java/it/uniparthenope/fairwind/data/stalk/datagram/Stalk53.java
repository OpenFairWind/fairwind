package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public interface Stalk53 extends Stalk{
    /*
    53  U0  VW      Magnetic Course in degrees:
                 The two lower  bits of  U * 90 +
                    the six lower  bits of VW *  2 +
                    the two higher bits of  U /  2 =
                    (U & 0x3) * 90 + (VW & 0x3F) * 2 + (U & 0xC) / 8
                 The Magnetic Course may be offset by the Compass Variation (see datagram 99) to get the Course Over Ground (COG).
                 Corresponding NMEA sentences: RMC, VTG


     */
    public Double getMagneticCourse();

}
