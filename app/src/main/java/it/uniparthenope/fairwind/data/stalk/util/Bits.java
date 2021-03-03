package it.uniparthenope.fairwind.data.stalk.util;

/**
 * Created by raffaelemontella on 27/08/15.
 */
public class Bits {
    public static byte getLowNibble(byte b) {
        return (byte)(b & 0xf);

    }
    public static byte getHighNibble(byte b) {
        return (byte)(b >> 8);
    }

    public static int get2BytesInteger(byte[] message,int index) {
        return (((message[index+1] & 0xff) << 8) | (message[index] & 0xff)) & 0xffff;
    }
}
