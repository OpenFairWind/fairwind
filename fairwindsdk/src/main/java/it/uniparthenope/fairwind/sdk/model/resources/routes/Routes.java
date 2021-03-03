package it.uniparthenope.fairwind.sdk.model.resources.routes;

import java.util.UUID;

import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.Resources;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;

import static nz.co.fortytwo.signalk.util.SignalKConstants.resources_routes;
import static nz.co.fortytwo.signalk.util.SignalKConstants.routes;


/**
 * Created by raffaelemontella on 29/09/2017.
 */

public class Routes extends Resources {

    public static final String LOG_TAG="TRACKS";

    public Routes(SignalKModel signalKModel) {
        super(signalKModel,resources_routes,routes);
    }


    @Override
    public Resource onNewResource(String uuid, Json jsonItem) {
        return new Route(uuid,jsonItem);
    }
}
