package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.PressureView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class EngineCoolantPressureView extends PressureView {
    public static final String LOG_TAG="PRESSURE_VIEW";

    private int unit= Formatter.UNIT_PRESSURE_PA;
    private String propulsion_id;

    public EngineCoolantPressureView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public EngineCoolantPressureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public EngineCoolantPressureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }
    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EngineCoolantPressureView);
            String sPropulsionId=a.getString(R.styleable.EngineCoolantPressureView_propulsion_id);
            if (propulsion_id!=null) {
                propulsion_id = sPropulsionId;
            }

            String path = FairWindModelBase.getPropulsionCoolantPressurePath(vessel_uuid,propulsion_id);
            String label = "OIL PRESSURE";

            setLabel(label);
            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

}
