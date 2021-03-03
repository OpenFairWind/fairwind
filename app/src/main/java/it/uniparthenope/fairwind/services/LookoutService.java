package it.uniparthenope.fairwind.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
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

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.HomeActivity;
import it.uniparthenope.fairwind.captain.SplashActivity;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LookoutService extends IntentService   {
    private static final String LOG_TAG = "LOOKUP_SERVICE";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_MAIN             = "it.uniparthenope.fairwind.services.action.main";
    private static final String ACTION_START            = "it.uniparthenope.fairwind.services.action.START";
    private static final String ACTION_STOP             = "it.uniparthenope.fairwind.services.action.STOP";
    private static final String ACTION_STARTFOREGROUND  = "it.uniparthenope.fairwind.services.action.startforeground";
    private static final String ACTION_STOPFOREGROUND   = "it.uniparthenope.fairwind.services.action.stopforeground";

    // TODO: Rename parameters
    private static final String EXTRA_SERIALDRIVERINDEX = "it.uniparthenope.fairwind.services.extra.SERIALDRIVERINDEX";
    private static final String EXTRA_SERIALPORTINDEX = "it.uniparthenope.fairwind.services.extra.SERIALPORTINDEX";
    private static final String EXTRA_SERVERPORT = "it.uniparthenope.fairwind.services.extra.SERVERPORT";
    private static final String EXTRA_BAUDRATE = "it.uniparthenope.fairwind.services.extra.BAUDRATE";
    private static final String EXTRA_DATABITS = "it.uniparthenope.fairwind.services.extra.DATABITS";
    private static final String EXTRA_STOPBITS = "it.uniparthenope.fairwind.services.extra.STOPBITS";
    private static final String EXTRA_PARITYBIT = "it.uniparthenope.fairwind.services.extra.PARITYBIT";

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    private ExecutorService mMockExecutor;

    private DataListeners dataListeners;
    public DataListeners getDataListeners() { return dataListeners; }

    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, LookoutService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void startActionStartForeground(Context context) {
        Intent intent = new Intent(context, LookoutService.class);
        intent.setAction(ACTION_STARTFOREGROUND);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionStop(Context context) {
        Intent intent = new Intent(context, LookoutService.class);
        intent.setAction(ACTION_STOP);
        context.stopService(intent);
    }

    // The constructor
    public LookoutService() {
        super("LookoutService");

        dataListeners =new DataListeners();
        FairWindModelImpl fairWindModelImpl= FairWindApplication.getFairWindModel();
        fairWindModelImpl.setLookoutService(this);
        //fairWindModel.putData("dummy", new FairWindDataItemImpl("xxxx"));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent==null) {
            Log.i(LOG_TAG, "Received ***RE***start Foreground Intent ");
            handleActionStart(intent);
        }
        else if (intent.getAction().equals(LookoutService.ACTION_STARTFOREGROUND)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");



            //// Check license here


            // Set the mandatory notification
            Intent notificationIntent = new Intent(this, LookoutService.class);
            notificationIntent.setAction(LookoutService.ACTION_MAIN);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification=getMyActivityNotification(pendingIntent,null,null,0,0);

            // Start the service as foreground
            startForeground(LookoutService.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

            // Performs all the needed things
            handleActionStart(intent);

        } else if (intent.getAction().equals(
                LookoutService.ACTION_STOPFOREGROUND)) {
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
        //handleActionStop(null);

        Log.d(LOG_TAG, "Service started");


        mMockExecutor = Executors.newSingleThreadExecutor();
        LookoutServiceThread lookoutServiceThread =new LookoutServiceThread(intent);
        if (lookoutServiceThread !=null) {
            mMockExecutor.submit(lookoutServiceThread);

        }

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
        for (DataListener dataListener : dataListeners.get()) {
            dataListeners.remove(dataListener);
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

    private int smallIcon=R.drawable.ic_stat_fairwind_logo_alpha_splash;
    private int largeIcon=R.drawable.fairwind_logo_alpha_splash;
    private String contentText="FairWind main data listener service.";
    private String ticker="FairWind Ticker";


    private Notification getMyActivityNotification(PendingIntent pendingIntent,String ticker,String contentText, int smallIcon, int largeIcon){

        if (contentText==null) {
            contentText=this.contentText;
        }

        if (ticker==null) {
            ticker=this.ticker;
        }

        if (smallIcon==0) {
            smallIcon=this.smallIcon;
        }

        if (largeIcon==0) {
            largeIcon=this.largeIcon;
        }

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                largeIcon);

        return new NotificationCompat.Builder(this)
                .setContentTitle("FairWind")
                .setTicker(ticker)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    /**
     * This is the method that can be called to update the Notification
     */
    public void updateNotification(String ticker,String contentText, int smallIcon,int largeIcon) {
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, LookoutService.class), 0);
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setAction(LookoutService.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification = getMyActivityNotification(pendingIntent,ticker,contentText,smallIcon, largeIcon);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(LookoutService.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        this.ticker=ticker;
        this.contentText=contentText;
        this.smallIcon=smallIcon;
    }
}











