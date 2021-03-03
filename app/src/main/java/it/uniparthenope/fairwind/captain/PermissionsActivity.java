package it.uniparthenope.fairwind.captain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Vector;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.info.InfoActivity;

/**
 * Created by raffaelemontella on 20/10/2017.
 */

public class PermissionsActivity  extends Activity {
    private static final String LOG_TAG = "PERMISSIONS_ACTIVITY";

    private static final int REQUEST_PERMISSION_ALL = 1;
    private static final int REQUEST_PERMISSION_OVERLAY=2;

    private Vector<String> permissionsToRequest=new Vector<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"onCreate");
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(!hasPermissions(this, FairWindApplication.permissions)){
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_PERMISSION_ALL);
        }
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                requestPermissionOverlay();
            }
        }
        if (permissionsToRequest.size()==0 ) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                finishOnHasPermissions();
            } else {
                if (Settings.canDrawOverlays(this)) {
                    finishOnHasPermissions();
                }
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case REQUEST_PERMISSION_ALL:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!Settings.canDrawOverlays(this)) {
                            requestPermissionOverlay();
                        }
                    } else {
                        finishOnNoPermissions();
                    }
                    break;

            }
        }
    }

    private void finishOnHasPermissions() {
        FairWindApplication.restart();
    }

    private void finishOnNoPermissions() {
        Toast.makeText(this, "Please consider granting it this permission.", Toast.LENGTH_LONG).show();
        // Start the info activity
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("quitOnOk", true);
        startActivity(intent);
        finish();
    }


    private boolean hasPermissions(Context context, String... permissions) {
        boolean result=true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG,permission+" -> no");
                    result=false;
                    permissionsToRequest.add(permission);
                } else {
                    Log.d(LOG_TAG,permission+" -> granted");
                }
            }
        }
        return result;
    }


    private void requestPermissionOverlay() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        finish();
    }


}
