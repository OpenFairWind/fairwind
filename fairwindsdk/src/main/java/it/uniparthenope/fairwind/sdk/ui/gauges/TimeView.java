package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 01/05/16.
 */
public class TimeView extends SingleDataView {
    public static final String LOG_TAG="TIME_VIEW";

    private int timeStyle= Formatter.UNIT_DEPTH_M;


    public TimeView(Context context) {
        super(context);
        Log.d(LOG_TAG,"TimeView(Context context)");
        initializeViews(context,null,0);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG,"TimeView(Context context, AttributeSet attrs)");
        initializeViews(context,attrs,0);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(LOG_TAG,"TimeView(Context context, AttributeSet attrs, int defStyle)");
        initializeViews(context,attrs,defStyle);
    }


    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeView);

            String sTimes=a.getString(R.styleable.TimeView_times);
            if (sTimes!=null) {
                timeStyle=Integer.parseInt(sTimes);
            }

            setLabel("TIME");

            setEvent(new FairWindEvent(SignalKConstants.vessels+SignalKConstants.dot+vessel_uuid+".environment.time.*",null,
                    0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return null;
    }


    public void setTime(DateTime time) {
        // Set view values
        TextView txtTime = (TextView) findViewById(R.id.txt_time);
        txtTime.setText(Formatter.formatTime(timeStyle,time,"__.__"));

    }

    @Override
    protected void onUpdate(FairWindEvent event) {
        //DateTime time=getFairWindModel().getDateTime();
        Long millis = (Long)getFairWindModel().getData().get("vessels." + vessel_uuid + ".environment.time.millis");
        if (millis!=null) {
            DateTime dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getDefault()));
            setTime(dateTime);
        }
    }
}
