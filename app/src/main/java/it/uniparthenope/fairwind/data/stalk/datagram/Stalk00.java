package it.uniparthenope.fairwind.data.stalk.datagram;

/**
 * Created by raffaelemontella on 19/08/15.
 */
public interface Stalk00 extends Stalk {
    /*
    00  02  YZ  XX XX  Depth below transducer: XXXX/10 feet
                       Flags in Y: Y&8 = 8: Anchor Alarm is active
                                  Y&4 = 4: Metric display units or
                                           Fathom display units if followed by command 65
                                  Y&2 = 2: Used, unknown meaning
                      Flags in Z: Z&4 = 4: Transducer defective
                                  Z&2 = 2: Deep Alarm is active
                                  Z&1 = 1: Shallow Depth Alarm is active
                    Corresponding NMEA sentences: DPT, DBT
     */
    public void setDepth(Double depth);
    public Double getDepth();
    public boolean isAnchorAlarm();
    public boolean isTrasducerDefective();
    public boolean isDeepAlarm();
    public boolean isShallowDepthAlarm();

}