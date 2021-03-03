package it.uniparthenope.fairwind.captain;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.UUID;

import it.uniparthenope.fairwind.FairWindApplication;

import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.ui.FairWindActivity;

public class HomeActivity extends FairWindActivity implements  Runnable {

    private static final String LOG_TAG = "HOME_ACTIVITY";



    private Handler handler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!FairWindApplication.getInstance().hasPermissions()) {
            Intent intentPermissions=new Intent(this,PermissionsActivity.class);
            intentPermissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentPermissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intentPermissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intentPermissions);
            finish();
        }
        super.onCreate(savedInstanceState);

        setBackActivityClass(HomeActivity.class);

        handler = new Handler();
        handler.postDelayed(this,5000);
    }

    @Override
    public void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }


    @Override
    public void run() {
        UUID uuid = UUID.fromString("d749ad34-fea2-11e6-bc64-92361f002671");
        boolean checked=false;
        FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
        if (fairWindModel!=null) {
            checked = fairWindModel.getPreferences().getConfigPropertyBoolean("alertdialogs." + uuid, false);
        }
        if (checked == false) {
            String title = "Warning and Disclaimer";
            String message = "WARNINGS: The electronic chart is an aid to navigation designed to facilitate the use of authorized government charts, not to replace them. Only official government charts and notices to mariners contain all information needed for the safety of navigation, and as always, the captain is responsible for their prudent use.";
            message += "\n\n";
            message += "DISCLAIMER: THE PRODUCT AND RELATED MATERIALS ARE LICENSED “AS IS” AND FAIRWIND (AND ITS AFFILIATES, CONTRACTORS, EMPLOYEES, DEVELOPERS, CLIENTS, AGENTS, SUPPLIERS, OR THIRD-PARTY PARTNERS) DISCLAIMS ANY AND ALL OTHER WARRANTIES, WHETHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT.  YOU ASSUME THE ENTIRE RISK AS TO THE PERFORMANCE AND RESULTS OF PRODUCT.  FAIRWIND’S CUMULATIVE LIABILITY TO YOU OR ANY OTHER PARTY FOR ANY LOSS OR DAMAGES RESULTING FROM ANY CLAIMS, DEMANDS, OR ACTIONS ARISING OUT OF OR RELATING TO THIS AGREEMENT SHALL NOT EXCEED THE PURCHASE PRICE YOU PAID FOR THE LICENSE TO USE THE PRODUCT (IF ANY).  IN NO EVENT SHALL FAIRWIND BE LIABLE FOR ANY INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, OR EXEMPLARY DAMAGES OR LOST PROFITS, EVEN IF FAIRWIND HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.  SOME JURISDICTIONS DO NOT ALLOW THE LIMITATION OR EXCLUSION OF LIABILITY FOR INCIDENTAL OR CONSEQUENTIAL DAMAGES, SO THE FOREGOING LIMITATION OR EXCLUSION MAY NOT APPLY TO YOU.";
            AlarmDialog.showAlert(HomeActivity.this, uuid, -1, -1, title, message, "I understand. I don't want to see this Warning and Disclaimer anymore.", null);
        }
    }





    /************************************/



    public static void onStopAlarms(View view, Fragment fragment, FairWindActivity activityBase) {
        HomeActivity homeActivity=(HomeActivity) activityBase;
        homeActivity.stopAlarms(view);

    }

    public static void onCreateWaypoint(View view, Fragment fragment, FairWindActivity activityBase) {
        HomeActivity homeActivity=(HomeActivity) activityBase;
        homeActivity.createWaypoint(view);
    }

    public  void stopAlarms(View view) {
        Toast.makeText(this, (String)"Stop Alarms",
                Toast.LENGTH_LONG).show();
    }

    public void createWaypoint(View view) {
        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        fairWindModel.createWaypoint(this,view);
    }
}
