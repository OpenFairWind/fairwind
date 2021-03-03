package it.uniparthenope.fairwind.sdk.model.resources.routes;

import java.util.UUID;

import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 29/09/2017.
 */

public class Route extends Resource {

    private Waypoint start;
    private Waypoint end;

    public final static String LOG_TAG="ROUTE";



    public Route() {

    }

    public Route(String uuid, Json json) {
        byJson(json);
        setUuid(uuid);
    }

    public double getDistance() {
        return 0;
    }
    public Waypoint getStart() { return start; }
    public Waypoint getEnd() { return end; }

    @Override
    public void byJson(Json json) {
        super.byJson(json);
    }

    @Override
    public Json asJson() {
        Json result=super.asJson();

        return result;
    }

    @Override
    public SignalKModel asSignalK() {
        SignalKModel signalKobject= super.asSignalK();

        return signalKobject;
    }

    public int getPoints() {
        return 0;
    }
}
