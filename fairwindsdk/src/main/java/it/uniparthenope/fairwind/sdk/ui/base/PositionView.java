package it.uniparthenope.fairwind.sdk.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.DoubleDataView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 30/04/16.
 */
public class PositionView extends DoubleDataView {
    public static final String LOG_TAG="POSITION_VIEW";

    private int coords=Formatter.COORDS_STYLE_DDMMSS;

    public PositionView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public PositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public PositionView(Context context, AttributeSet attrs, int defStyle) {
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

        if (this.isInEditMode()==false && attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PositionView);

            String sCoords=a.getString(R.styleable.PositionView_coords);
            if (sCoords!=null) {
                coords = Integer.parseInt(sCoords);
            }

            String path=FairWindModelBase.getNavPositionPath(vessel_uuid);
            FairWindEvent event=new FairWindEvent(path+".*",null,0, PathEvent.EventType.ADD,this);
            setEvent(event);
            setLabel("POSITION");
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatLatitude(coords, data,"n/a");
    }

    @Override
    public String getFormattedValueSecondary(Double data) {
        return Formatter.formatLongitude(coords, data,"n/a");
    }



    public void setPosition(Position position) {
        // Set view values
        setData(position.getLatitude());
        setDataSecondary(position.getLongitude());
    }

    @Override
    protected void onUpdate(FairWindEvent event) {
        Position position=getFairWindModel().getNavPosition(vessel_uuid);
        if (position!=null) {
            setPosition(position);
        }
    }




}
