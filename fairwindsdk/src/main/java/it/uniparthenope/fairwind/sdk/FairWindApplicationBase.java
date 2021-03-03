package it.uniparthenope.fairwind.sdk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.util.Log;

import java.util.List;

import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;

/**
 * Created by raffaelemontella on 03/10/2017.
 */

public class FairWindApplicationBase extends android.support.multidex.MultiDexApplication  implements Runnable{
    private static final String LOG_TAG = "FAIRWIND_APP_BASE";

    private static String FAIRWIND_PACKAGE_NAME="it.uniparthenope.fairwind";

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"onCreate");



        // Get the application package name
        String currentPackageName=getApplicationContext().getPackageName();

        // Check if this activity is a boat app
        if (currentPackageName.equals(FAIRWIND_PACKAGE_NAME)==false) {

            // The application is a boat app

            // Check if FairWind is installed
            PackageManager pm = getPackageManager();
            try {
                pm.getPackageInfo("it.uniparthenope.fairwind", PackageManager.GET_ACTIVITIES);

                // FairWind is installed
                boolean isRunning = false;

                // Check if FairWind is running
                final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
                if (procInfos != null) {
                    for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                        if (processInfo.processName.equals(FAIRWIND_PACKAGE_NAME)) {
                            isRunning = true;
                            break;
                        }
                    }
                }

                // if FairWind is not running, the lauch it
                if (isRunning == false) {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(FAIRWIND_PACKAGE_NAME);
                    startActivity(LaunchIntent);
                }


            } catch (PackageManager.NameNotFoundException e) {
                // Open the playstore for fairwind

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + FAIRWIND_PACKAGE_NAME)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + FAIRWIND_PACKAGE_NAME)));
                }

                Handler handler=new Handler();
                handler.postDelayed(this,1000);

            }
        }
        super.onCreate();
    }

    @Override
    public void run() {
        android.os.Process.killProcess(Process.myPid());
    }
}
