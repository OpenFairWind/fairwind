package it.uniparthenope.fairwind.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.HomeActivity;
import it.uniparthenope.fairwind.sdk.util.Utils;

/**
 * Created by raffaelemontella on 23/04/16.
 */
public class WatchDogService extends IntentService implements Runnable {

    private static final String LOG_TAG = "WATCHDOG_SERVICE";

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_MAIN             = "it.uniparthenope.fairwind.services.watchdog.main";
    private static final String ACTION_START            = "it.uniparthenope.fairwind.services.watchdog.START";
    private static final String ACTION_STOP             = "it.uniparthenope.fairwind.services.watchdog.STOP";
    private static final String ACTION_STARTFOREGROUND  = "it.uniparthenope.fairwind.services.watchdog.startforeground";
    private static final String ACTION_STOPFOREGROUND   = "it.uniparthenope.fairwind.services.watchdog.stopforeground";

    private ExecutorService mMockExecutor;

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 102;
    }

    // The constructor
    public WatchDogService() {
        super("WatchdogService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WatchDogService(String name) {
        super(name);
    }




    @Override
    public void run() {
        while(true) {
            if (Utils.isServiceRunning(this,"it.uniparthenope.fairwind.services.LookoutService")==false) {
                Log.d(LOG_TAG,"Restarting LookoutService");
                // Start the mock location service
                LookoutService.startActionStartForeground(getApplicationContext());
            } else {
                Log.d(LOG_TAG,"LookoutService is Running");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void startActionStart(Context context) {
        Log.i(LOG_TAG, "startActionStart");
        Intent intent = new Intent(context, WatchDogService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void startActionStartForeground(Context context) {
        Log.i(LOG_TAG, "startActionStartForeground");
        Intent intent = new Intent(context, WatchDogService.class);
        intent.setAction(ACTION_STARTFOREGROUND);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "onStartCommand");
        if (intent==null) {
            Log.i(LOG_TAG, "Received ***RE***start Foreground Intent ");
            handleActionStart(intent);
        }
        else if (intent.getAction().equals(WatchDogService.ACTION_STARTFOREGROUND)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");

            // Set the mandatory notification
            Intent notificationIntent = new Intent(this, HomeActivity.class);
            notificationIntent.setAction(WatchDogService.ACTION_MAIN);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.imagebutton_info);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("FairWind Watchdog")
                    .setContentText("Check is the FairWind main service is dead or alive.")
                    .setSmallIcon(R.drawable.ic_stat_action_verified_user)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(notificationPendingIntent)
                    .setOngoing(true)
                    .build();


            // Start the service as foreground
            startForeground(WatchDogService.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);

            // Performs all the needed things
            handleActionStart(intent);
        } else if (intent.getAction().equals(
                WatchDogService.ACTION_STOPFOREGROUND)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            handleActionStop(intent);
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStart(Intent intent) {

        // Stop everything is still running
        Log.d(LOG_TAG, "Service starting");

        if (mMockExecutor!=null && (!mMockExecutor.isTerminated() || !mMockExecutor.isShutdown())) {
            Log.d(LOG_TAG, "Service altready started");
            return;
        }
        mMockExecutor = Executors.newSingleThreadExecutor();
        mMockExecutor.submit(this);
        Log.d(LOG_TAG, "Service started!");
    }



    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStop(Intent intent) {

        if (mMockExecutor!=null) {
            if (!mMockExecutor.isShutdown() || !mMockExecutor.isTerminated()) {
                mMockExecutor.shutdown();
                mMockExecutor=null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handleActionStop(null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onHandleIntent(Intent intent) {
        // Manage intent actions...

        // Check if the intent is not null
        if (intent != null) {

            // Get the action string
            final String action = intent.getAction();

            // Check action
            if (ACTION_START.equals(action)) {

                // Perform the start action
                handleActionStart( intent );
            } else
                // Check action
                if (ACTION_STOP.equals(action)) {
                    // Perform the stop action
                    handleActionStop( intent );
                }
        }
    }
}
