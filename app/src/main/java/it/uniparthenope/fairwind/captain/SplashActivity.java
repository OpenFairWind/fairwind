package it.uniparthenope.fairwind.captain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.Vector;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.info.InfoActivity;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.services.LookoutService;
import it.uniparthenope.fairwind.services.WatchDogService;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;


/**
 * Created by Lovig90 on 05/10/15.
 */


public class SplashActivity extends Activity  {
    private static final String LOG_TAG = "SPLASH_ACTIVITY";






    private static final long MIN_WAIT_INTERVAL=1500L;
    private static final long MAX_WAIT_INTERVAL=3000L;
    private static final int GO_AHEAD_WHAT=1;
    private long mStartTime;
    private boolean mIsDone;

    private UiHandler mHandler;


    private class UiHandler extends Handler {

        private WeakReference<SplashActivity> mActivityRef;

        public UiHandler(final SplashActivity srcActivity){
            this.mActivityRef=new WeakReference<SplashActivity>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg){
            final SplashActivity srcActivity=this.mActivityRef.get();
            if(srcActivity==null){
                return;
            }
            switch (msg.what){
                case GO_AHEAD_WHAT:
                    long elapsedTime = SystemClock.uptimeMillis()-mStartTime;
                    if(elapsedTime>=MIN_WAIT_INTERVAL && !mIsDone){
                        mIsDone=true;
                        goAhead();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"onCreate");
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mHandler=new UiHandler(this);


    }


    @Override
    protected void onStart(){
        super.onStart();
        Log.d(LOG_TAG,"onStart");
        mStartTime= SystemClock.uptimeMillis();
        final Message goAheadMessage= mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage,mStartTime+MAX_WAIT_INTERVAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    private void goAhead(){
        // Start the desktop activity
        Log.d(LOG_TAG, "goAhead");
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG,"onDestroy");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();

    }


}