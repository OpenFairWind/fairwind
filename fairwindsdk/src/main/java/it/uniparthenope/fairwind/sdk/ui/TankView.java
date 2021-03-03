package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 24/05/16.
 */
public class TankView extends FairWindView {

    private View mView;
    private RatioView mCoolantLevelView;
    private RatioView mOilLevelView;
    private RatioView mFuelLevelView;


    public TankView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public TankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public TankView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {

        mView=inflate(context, R.layout.fuel_view, this);
    }


    @Override
    protected void onUpdate(FairWindEvent event) {

    }
}

