package it.uniparthenope.fairwind.services.internallocations;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import nz.co.fortytwo.signalk.handler.DeclinationHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static android.content.Context.LOCATION_SERVICE;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;

/**
 * Created by raffaelemontella on 10/12/15.
 */
public class InternalLocationsListener extends DataListener implements IDataListenerPreferences, LocationListener, Runnable {

    private static final String LOG_TAG = "INTLOCATION_LISTENER";





    private int timeout = 250;

    private Position lastPosition=null;
    private ExecutorService mExecutor;

    private double[] instCalculatedCourse;
    private double[] instCalculatedSpeed;
    private int windowSize;
    private int windowIndex;

    private DeclinationHandler declinationHandler;

    public InternalLocationsListener() {
    }

    // The constructor
    public InternalLocationsListener(String name) {
        super(name);
        init();
    }

    public InternalLocationsListener(InternalLocationsListenerPreferences prefs) {
        super(prefs.getName());
        init();
        this.timeout = prefs.getTimeout();
    }

    private void init() {
        declinationHandler=new DeclinationHandler();
        windowSize=10;
        instCalculatedCourse=new double[windowSize];
        instCalculatedSpeed=new double[windowSize];
        windowIndex=0;
        for (int i=0;i<windowSize;i++) {
            instCalculatedCourse[i]=Double.NaN;
            instCalculatedSpeed[i]=Double.NaN;
        }
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void onStart() throws DataListenerException {
        mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.submit(this);
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }

    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {

    }

    @Override
    public boolean mayIUpdate() {
        return false;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG,"onLocationChanged:"+location);
        // Check if location is not from mock
        if (!location.isFromMockProvider()) {
            DateTime now=new DateTime(DateTimeZone.UTC);
            Position position=new Position(location.getLatitude(),location.getLongitude(),location.getAltitude(),now);
            double speed=location.getSpeed();
            double course=location.getBearing();
            if (lastPosition!=null) {
                instCalculatedCourse[windowIndex] = position.bearingTo(lastPosition);
                double dT=(position.getTimeStamp().getMillis() - lastPosition.getTimeStamp().getMillis())/1000;
                instCalculatedSpeed[windowIndex] = lastPosition.distanceTo(position) / dT;
                double calculatedCourse=0;
                int n=0;
                for (double d:instCalculatedCourse) {
                    if (!Double.isNaN(d)) {
                        calculatedCourse += d;
                        n++;
                    }
                }
                calculatedCourse=calculatedCourse/n;
                n=0;
                double calculatedSpeed=0;
                for (double d:instCalculatedSpeed) {
                    if (!Double.isNaN(d)) {
                        calculatedSpeed += d;
                        n++;
                    }
                }
                calculatedSpeed=calculatedSpeed/n;

                if (Math.abs(calculatedCourse-course)>0.01) {
                    course=calculatedCourse;
                }
                if (Math.abs(calculatedSpeed-speed)>0.01) {
                    speed=calculatedSpeed;
                }
                windowIndex++;
                if (windowIndex==windowSize) {
                    windowIndex=0;
                }
            }
            SignalKModel signalKObject= SignalKModelFactory.getCleanInstance();
            signalKObject.putPosition(vessels_dot_self_dot + nav_position, (double)location.getLatitude(), (double)location.getLongitude(), 0.0, location.getProvider(), now.toString());
            signalKObject.put(vessels_dot_self_dot +SignalKConstants.nav_speedOverGround,speed,location.getProvider(),now.toString());
            signalKObject.put(vessels_dot_self_dot +SignalKConstants.nav_courseOverGroundTrue,Math.toRadians(course),location.getProvider(),now.toString());

            double declination=declinationHandler.handle(location.getLatitude(),location.getLongitude(),now.getYear());
            signalKObject.put(vessels_dot_self_dot +SignalKConstants.nav_courseOverGroundMagnetic,Math.toRadians(course+declination),location.getProvider(),now.toString());


            lastPosition=position;
            process(signalKObject);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(LOG_TAG,"onStatusChanged:"+provider+" --> "+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG,"onProviderEnabled:"+provider);
        Toast.makeText( FairWindApplication.getInstance().getApplicationContext(), provider+" Enabled", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(LOG_TAG,"onProviderDisabled:"+provider);
        Toast.makeText( FairWindApplication.getInstance().getApplicationContext(), provider+" Disabled", Toast.LENGTH_SHORT ).show();

    }



    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new InternalLocationsListener((InternalLocationsListenerPreferences)prefs);
    }

    @Override
    public boolean isLicensed() {
        return true;
    }

    @Override
    public boolean isInput() {
        return true;
    }

    public boolean isOutput() {
        return false;
    }

    //public Handler mHandler;

    @Override
    public void run() {
        final Context context=FairWindApplication.getFairWindModel().getLookoutService();

        // Get the internal gps location provider
        final LocationManager locationManager = (LocationManager)context.getApplicationContext().getSystemService(LOCATION_SERVICE);


        // Get the location from the given provider
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Request internal gps location update periodically
            // Hard coded at now: 5s, 1m
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this,context.getMainLooper());

            while (!isDone()) {
              try {
                    //Log.d(LOG_TAG,"sleep");
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            locationManager.removeUpdates(this);

        }
    }

}
