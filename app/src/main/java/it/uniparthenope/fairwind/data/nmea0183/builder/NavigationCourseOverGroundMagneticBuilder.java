package it.uniparthenope.fairwind.data.nmea0183.builder;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 28/05/2017.
 */

public class NavigationCourseOverGroundMagneticBuilder extends SignalKSentenceAbstractBuilder {

    public static final String LOG_TAG="NMEA0183 COURSe BUILDER";

    public NavigationCourseOverGroundMagneticBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        return null;
    }
}
