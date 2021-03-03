package it.uniparthenope.fairwind;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.UUID;
import java.util.Vector;

import it.uniparthenope.fairwind.captain.AlarmDialog;
import it.uniparthenope.fairwind.captain.HomeActivity;
import it.uniparthenope.fairwind.captain.SplashActivity;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.FairWindApplicationBase;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.services.LookoutService;
import it.uniparthenope.fairwind.services.WatchDogService;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import static nz.co.fortytwo.signalk.model.impl.SignalKModelFactory.insertMetaToModel;
import static nz.co.fortytwo.signalk.model.impl.SignalKModelFactory.removeOtherVessels;

/**
 * Created by raffaelemontella on 19/01/16.
 */
public class FairWindApplication extends FairWindApplicationBase implements FairWindEventListener {
    private static final String LOG_TAG = "FAIRWIND_APPLICATION";

    public static final String[] permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    private boolean mPermissions=false;
    public boolean hasPermissions() {return mPermissions; }

    private static FairWindModelImpl fairWindModel;
    private static FairWindApplication instance;
    public static FairWindModelImpl getFairWindModel() { return fairWindModel; }
    public static FairWindApplication getInstance() { return instance; }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"onCreate");
        instance=this;

        mPermissions=checkPermissions(this, FairWindApplication.permissions);

        if (mPermissions && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissions=Settings.canDrawOverlays(this);
        }

        // Check if no permission have to be requested
        if (mPermissions) {
            File externalFilesDir=FairWindApplication.getInstance().getExternalFilesDir(null);
            String extStorageDirectory=externalFilesDir.getAbsolutePath()+File.separator;
            Log.d(LOG_TAG,"confPath:"+extStorageDirectory);


            Util.setRootPath(extStorageDirectory);
            fairWindModel=new FairWindModelImpl();


            File sdDir = new File(extStorageDirectory+"conf");
            sdDir.mkdir();

            try {
                SignalKModelFactory.load(fairWindModel);
                fairWindModel.sharedPreferences2Model();
            } catch (IOException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }

            // Start the watchdog service
            WatchDogService.startActionStartForeground(getApplicationContext());

            // Start the lookout service
            LookoutService.startActionStartForeground(getApplicationContext());
        }

        super.onCreate();
    }

    private boolean checkPermissions(Context context, String... permissions) {
        boolean result=true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG,permission+" -> no");
                    result=false;
                    break;
                } else {
                    Log.d(LOG_TAG,permission+" -> granted");
                }
            }
        }
        return result;
    }




    public static void restart() {
        try {
            ProcessPhoenix.triggerRebirth(getInstance());
        } catch (RuntimeException ex) {
            Toast.makeText(getInstance().getApplicationContext(),"Could not restart",Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG,ex.getMessage());
        }
    }

    public static void reboot() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
            proc.waitFor();
        } catch (Exception ex) {
            Toast.makeText(getInstance().getApplicationContext(),"Could not reboot",Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Could not reboot", ex);
        }
    }



    @Override
    public void onEvent(FairWindEvent event) {
        Log.d(LOG_TAG, event.getPathValue());
        UUID uuid = UUID.fromString("0150428a-f487-11e6-bc64-92361f002671");
        FairWindModelImpl fairWindModel = getFairWindModel();
        String notificationKey = event.getPathValue();
        String key = notificationKey.replace(".notifications", "");
        String displayName = (String) fairWindModel.get(key + SignalKConstants.dot + SignalKConstants.meta + SignalKConstants.dot + "displayName");
        String alarmState = (String) fairWindModel.get(notificationKey + SignalKConstants.dot + SignalKConstants.alarmState);
        String message = (String) getFairWindModel().get(notificationKey + SignalKConstants.dot + SignalKConstants.message);
        String method = (String) getFairWindModel().get(key + SignalKConstants.dot +"meta"+ SignalKConstants.dot+ alarmState + "Method");
        String methodMessage = (String) getFairWindModel().get(key + SignalKConstants.dot +"meta"+ SignalKConstants.dot+ alarmState + "Message");
        if (method != null) {
            if (method.equalsIgnoreCase("visual")) {
                AlarmDialog.showAlert(this,uuid, 30000, 15000, alarmState.toUpperCase() + ":" + displayName, methodMessage + ":\n" + message,null,null);
            } else if (method.equalsIgnoreCase("sound")) {
                String messageToSpeak = alarmState+", "+displayName+". "+methodMessage+". "+message;
                AlarmDialog.sayText(uuid, 30000, messageToSpeak);
            }
        }
    }
}
