package it.uniparthenope.fairwind.data.stalk;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class StalkEvent {

    private SignalKModel signalKModel;
    private Stalk stalk;
    private String sourceRef="nmea_stalk";
    private String now= Util.getIsoTimeString();

    public SignalKModel getSignalKModel() { return  signalKModel; }
    public Stalk getStalk() { return stalk; }

    public StalkEvent(SignalKModel signalKModel, Stalk stalk) {
        this.signalKModel=signalKModel;
        this.stalk=stalk;
    }

    public String getSourceRef() {
        return sourceRef;
    }
    public void setSourceRef(String sourceRef) { this.sourceRef=sourceRef; }
    public String getNow() {return now;}
}
