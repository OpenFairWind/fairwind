package it.uniparthenope.fairwind.sdk.ui;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModelNotDefinedException;
import it.uniparthenope.fairwind.sdk.util.Utils;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 30/04/16.
 */
public abstract class FairWindView extends LinearLayout implements FairWindEventListener {
    public static final String LOG_TAG="FAIRWIND_VIEW";


    private Handler handler=new Handler();

    public FairWindView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public FairWindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public FairWindView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (isInEditMode()==false) {
            fairWindModel = Utils.getFairWindModel();
        }
    }

    protected abstract void onUpdate(FairWindEvent event);


    private FairWindModel fairWindModel;


    public FairWindModel getFairWindModel()  {
        if (fairWindModel!=null ) {
            return fairWindModel;
        } else {
            if ( !getRootView().isInEditMode() ) {
                throw new FairWindModelNotDefinedException("FairWindModel not defined");
            }
        }
        return null;
    }



    public void onEvent(final FairWindEvent event) {
        //
        handler.post(new Runnable() {
            @Override
            public void run() {
                onUpdate(event);
            }
        });
    }
}
