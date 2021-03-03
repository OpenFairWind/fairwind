package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapView;
import it.uniparthenope.fairwind.sdk.ui.base.PositionView;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 31/05/16.
 */
public class MapView extends PositionView {
    public static final String LOG_TAG="MAP_VIEW";



    public MapView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }
    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        initializeViews(context,attrs,defStyle);
    }


    @Override
    protected  int getTextLayout() { return R.layout.map1_view; }
    @Override
    protected  int getGaugeLayout() { return R.layout.map1_view; }
    @Override
    protected  int getPlotLayout() { return R.layout.map1_view; }
    @Override
    protected  int getMeterLayout() { return R.layout.map1_view; }


    String pathSpeedThroughWater;
    String pathtHeadingTrue;

    FairWindEvent eventSpeedThroughWater;
    FairWindEvent eventHeadingTrue;

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");


        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MapView);

            setLabel("POSITION");

            FairWindMapView mapView=(FairWindMapView)findViewById(R.id.map_data);
            if (mapView!=null) {
                FairWindModel fairWindModel=getFairWindModel();

                //////////////////
                pathSpeedThroughWater= SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_speedThroughWater;
                pathtHeadingTrue=SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_headingTrue;
                eventSpeedThroughWater=new FairWindEvent(pathSpeedThroughWater+".*",pathSpeedThroughWater,0, PathEvent.EventType.ADD,this);
                eventHeadingTrue=new FairWindEvent(pathtHeadingTrue+".*",pathtHeadingTrue,0, PathEvent.EventType.ADD,this);
                fairWindModel.register(eventSpeedThroughWater);
                fairWindModel.register(eventHeadingTrue);


            }
        }



        ImageButton btnMyLoctaion=(ImageButton)findViewById(R.id.ibtn_mylocation);
        if (btnMyLoctaion!=null) {
            btnMyLoctaion.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    findVessel();
                }
            });
        }

        ImageButton btnRuler=(ImageButton)findViewById(R.id.ibtn_ruler);
        if (btnRuler!=null) {
            btnRuler.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    FairWindMapView mapView=(FairWindMapView)findViewById(R.id.map_data);
                    if (mapView!=null) {
                        mapView.toggleRuler();
                    }
                }
            });
        }

        Log.d(LOG_TAG,"/initializeViews");
    }
/*

    @Override
    protected  void onUpdate(FairWindEvent event) {
        if (eventSpeedThroughWater.isMatching(event)) {

                    if (fairWindModel.getSpeed() != null && fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_SPEED)) {
                        txt_speed.setVisibility(VISIBLE);
                        lbl_speed.setVisibility(VISIBLE);
                        txt_speed.setText(Formatter.formatSpeed(fairWindModel.getPreferences().getUnit(FairWindModel.UNIT_BOAT_SPEED),fairWindModel.getSpeed(), "n/a"));
                    } else {
                        txt_speed.setVisibility(INVISIBLE);
                        lbl_speed.setVisibility(INVISIBLE);
                    }


        } else if (eventHeadingTrue.isMatching(event)) {

                    if (fairWindModel.getHeading() != null && fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_HEADING)) {
                        txt_heading.setVisibility(VISIBLE);
                        lbl_heading.setVisibility(VISIBLE);
                        txt_heading.setText(Formatter.formatDirection(fairWindModel.getHeading(), "n/a"));

                    } else {
                        txt_heading.setVisibility(INVISIBLE);
                        lbl_heading.setVisibility(INVISIBLE);
                    }


        }
    }
*/
    public  void findVessel() {
        FairWindMapView mapView=(FairWindMapView)findViewById(R.id.map_data);
        if (mapView!=null) {
            mapView.findVessel();
        }
    }

    public  void setCenter(Position position) {
        FairWindMapView mapView=(FairWindMapView)findViewById(R.id.map_data);
        if (mapView!=null) {
            mapView.setCenter(position);
        }
    }

    public void notifyLayersUpdate() {
        FairWindMapView mapView=(FairWindMapView)findViewById(R.id.map_data);
        if (mapView!=null) {
            mapView.layersUpdate();
        }
    }
}
