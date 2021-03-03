package it.uniparthenope.fairwind.ui;

import android.os.Bundle;
import android.view.WindowManager;
import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.captain.HomeActivity;
;

/**
 * Created by raffaelemontella on 03/10/15.
 */
public class FairWindActivity extends it.uniparthenope.fairwind.sdk.ui.FairWindActivity {

    public static final String LOG_TAG="FAIRWIND ACTIVITY";

    //private Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FairWindApplication.getFairWindModel().isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        setBackActivityClass(HomeActivity.class);
    }
}