package it.uniparthenope.fairwind.sdk.maps;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;


import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.maps.core.Marker1;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.GroundWaterType;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 06/07/16.
 */
public class Boat extends Marker1 implements FairWindEventListener {
    public static final String LOG_TAG="BOAT";

    private String vessel_uuid;
    private RhumbLine rhumbLine;
    private BearingLine bearingLine;
    private Predictor predictor;
    private Track track;
    private WindApparent windApparent;
    private WindTrue windTrue;
    private GroundWaterType groundWaterType;

    private Double speed=null;
    private Double angle=null;


    public Boat(MapView mapView, FairWindModel fairWindModel, String vessel_uuid, GroundWaterType groundWaterType, int predictorValue) {
        super(mapView,fairWindModel);

        this.vessel_uuid= FairWindModelBase.fixSelfKey(vessel_uuid);
        this.groundWaterType=groundWaterType;

        if (vessel_uuid.equals("self")==true) {
            windApparent = new WindApparent(mapView, fairWindModel, this.vessel_uuid, CompassMode.TRUE, groundWaterType);
            windTrue = new WindTrue(mapView, fairWindModel, this.vessel_uuid, CompassMode.TRUE, groundWaterType);
            track = new Track(mapView, fairWindModel, this.vessel_uuid);

            setDrawable(R.drawable.boat_red_96x96);
            setScale(3);
            setColor(Color.RED);
        } else {
            setDrawable(R.drawable.boat_blue_96x96);
            setScale(3);
            setColor(Color.BLUE);
        }

        rhumbLine = new RhumbLine(mapView,fairWindModel,this.vessel_uuid);
        bearingLine = new BearingLine(mapView,fairWindModel,this.vessel_uuid);
        predictor = new Predictor(mapView, fairWindModel, this.vessel_uuid, CompassMode.TRUE, predictorValue);

        setInfoWindow(new MyInfoWindow());
        update();

        fairWindModel.register(
                new FairWindEvent(FairWindModelBase.getNavPositionPath(this.vessel_uuid)+".*",null,0, PathEvent.EventType.ADD,this));


        //setIcon(mapView.getContext().getResources().getDrawable(R.drawable.boat2).mutate());
        //startMarker.setImage(getResources().getDrawable(R.drawable.boat2));
        //startMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble_black, map));

    }

    private void update() {
        speed=fairWindModel.getAnySpeed(vessel_uuid);
        angle=fairWindModel.getAnyCourse(vessel_uuid);


        Position position=fairWindModel.getNavPosition(vessel_uuid);
        String title="";
        String name=fairWindModel.getName(vessel_uuid);
        if (name==null || name.isEmpty()) {
            title=fairWindModel.getMmsi(vessel_uuid);
        }

        Log.d(LOG_TAG,"title:"+title);
        setTitle(title);

        if (position != null && angle!= null) {

            update(position, Math.toDegrees(angle), null);
        }
    }

    @Override
    public void onEvent(FairWindEvent event) {
        update();
    }

    @Override
    public void onAdd() {
        if (bearingLine!=null) {
            bearingLine.add();
        }
        if (rhumbLine!=null) {
            rhumbLine.add();
        }
        if (track!=null) {
            track.add();
        }
        if (predictor!=null) {
            predictor.add();
        }
        if (windApparent!=null) {
            windApparent.add();
        }

        if (windTrue!=null) {
            windTrue.add();
        }
    }

    @Override
    public void onRemove() {
        if (bearingLine!=null) {
            bearingLine.remove();
        }
        if (rhumbLine!=null) {
            rhumbLine.remove();
        }
        if (windTrue!=null) {
            windTrue.remove();
        }
        if (windApparent!=null) {
            windApparent.remove();
        }
        if (predictor!=null) {
            predictor.remove();
        }
        if (track!=null) {
            track.remove();
        }
    }

    class MyInfoWindow extends InfoWindow{
        public MyInfoWindow() {
            super(R.layout.infowindow_boat, mapView);
        }
        public void onClose() {
        }

        public void onOpen(Object arg0) {
            LinearLayout layout = (LinearLayout) mView.findViewById(R.id.layout_infowindow_boat);

            TextView txtMmsi = (TextView) mView.findViewById(R.id.textView_boat_mmsi);
            TextView txtName = (TextView) mView.findViewById(R.id.textView_boat_name);
            TextView txtStatus = (TextView) mView.findViewById(R.id.textView_boat_status);
            TextView txtCourse = (TextView) mView.findViewById(R.id.textView_boat_course);
            TextView txtSpeed = (TextView) mView.findViewById(R.id.textView_boat_speed);

            txtMmsi.setText(Formatter.formatString(fairWindModel.getMmsi(vessel_uuid),"n/a"));
            txtName.setText(Formatter.formatString(fairWindModel.getName(vessel_uuid),"n/a"));
            txtStatus.setText(Formatter.formatString(fairWindModel.getNavigationState(vessel_uuid),"n/a"));

            if (angle!=null) {
                txtCourse.setText(Formatter.formatDirection(angle, "n/a"));
            }
            if (speed!=null) {
                txtSpeed.setText(Formatter.formatSpeed(Formatter.UNIT_SPEED_KNT, speed, "n/a"));
            }

            layout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Override Marker's onClick behaviour here
                    closeInfoWindow();
                }
            });
        }
    }
}
