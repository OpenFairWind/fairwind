package it.uniparthenope.fairwind.services.mocklocations;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 09/12/15.
 */
public class MockLocationsListener extends DataListener implements IDataListenerPreferences {
    private static final String LOG_TAG = "MOCKLOCATIONS_LISTENER";

    private LocationManager locationManager;
    private boolean running;

    private int timeout=250;

    public MockLocationsListener() {
        init();
    }

    // The constructor
    public MockLocationsListener(String name) {
        super(name);
        init();

    }

    public MockLocationsListener(MockLocationsListenerPreferences prefs) {
        super(prefs.getName());
        init();

    }

    private void init() {
        type="Mocking Location Service";

        // Get the location manager system
        locationManager=(LocationManager) FairWindApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        // Add a test provider (our provider!)
        running=false;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void onStart() throws DataListenerException {
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                false, false, false, false, true, true, true,
                Criteria.POWER_LOW, Criteria.ACCURACY_FINE);


        running=true;
    }

    @Override
    public boolean onIsAlive() {
        return running;
    }

    @Override
    public void onStop() {

        try {
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            locationManager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
            locationManager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
            locationManager.clearTestProviderStatus(LocationManager.GPS_PROVIDER);
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.d(LOG_TAG,illegalArgumentException.getMessage());
        }
        running=false;
    }

    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {
        Log.d(LOG_TAG, "update");
        if (pathEvent.getPath().contains(SignalKConstants.nav_position)) {
            Log.d(LOG_TAG, "update position");
            Double speed, bearing;

            FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();

            Position position=fairWindModel.getNavPosition("self");
            if (position!=null) {


                speed = fairWindModel.getAnySpeed("self");
                bearing = fairWindModel.getAnyCourse("self");

                Location loc = new Location(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    loc.setLongitude(position.getLongitude());
                    loc.setLatitude(position.getLatitude());
                    loc.setAltitude(position.getAltitude());

                    if (speed!=null && Double.isNaN(speed)==false) {
                        loc.setSpeed(speed.floatValue());
                    }
                    if (bearing!=null && Double.isNaN(bearing)==false) {
                        loc.setBearing((float) Math.toDegrees(bearing));
                    }

                    loc.setTime(System.currentTimeMillis());
                    loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    loc.setAccuracy(fairWindModel.getAccurancy());


                    Log.d(LOG_TAG, "Mock GPS -> " + loc.toString());
                    locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, loc);

                    //loc.reset();
                }
            }
        }
    }

    @Override
    public boolean mayIUpdate() {
        boolean result=false;

        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        Position position=fairWindModel.getNavPosition("self");
        if (position!=null) {
            if (Double.isNaN(position.getLatitude())==false && Double.isNaN(position.getLongitude())==false) {
                result=true;
            }
        }

        Log.d(LOG_TAG, "myIUpdate:"+result);
        return result;
    }

    @Override
    public  boolean isOutput() {
        return true;
    }

    @Override
    public  boolean isInput() {
        return false;
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new MockLocationsListener((MockLocationsListenerPreferences)prefs);
    }



}
