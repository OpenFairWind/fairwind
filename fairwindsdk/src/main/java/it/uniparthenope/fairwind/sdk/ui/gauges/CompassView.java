package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 01/05/16.
 */
public class CompassView extends SingleDataView {
    public static final String LOG_TAG="COMPASS_VIEW";

    protected CompassMode compassMode;

    public CompassView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompassView);
            String sCompassMode = a.getString(R.styleable.CompassView_compass);
            if (sCompassMode!=null) {
                compassMode = CompassMode.values()[Integer.parseInt(sCompassMode)];
            }
            /*
            String path=null;
            String label=null;
            switch (viewDataType) {

                case COG:
                    path=FairWindModelBase.getCourseOverGroundPath(this.vessel_uuid,compassMode);
                    switch (compassMode) {
                        case MAGNETIC:
                            label= "COG (m)";
                            break;
                        case TRUE:
                            label= "COG";
                            break;
                    }
                    break;

                case BEARING:
                    path=FairWindModelBase.getCourseBearingTrack(this.vessel_uuid,compassMode);
                    switch (compassMode) {
                        case MAGNETIC:
                            label= "BEARING (m)";
                            break;
                        case TRUE:
                            label= "BEARING";
                            break;
                    }
                    break;



            }

            setLabel(label);

            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
            */

        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatDirection(data,"n/a");
    }

    private void setCompassMode(String sCompassMode, CompassMode defValue) {

        if (sCompassMode==null) {
            compassMode=defValue;
        } else if (sCompassMode.equalsIgnoreCase("true")) {
            compassMode=CompassMode.TRUE;
        } else if (sCompassMode.equalsIgnoreCase("magnetic")) {
            compassMode= CompassMode.MAGNETIC;
        } else {
            compassMode=defValue;
        }
    }
}
