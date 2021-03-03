package it.uniparthenope.fairwind.sdk.maps.core;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Date;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapOverlay;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.util.DownloadFileAsync;
import it.uniparthenope.fairwind.sdk.util.GroundWaterType;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 06/07/16.
 */
public class Marker1 extends Marker implements FairWindMapOverlay  {
    public static final String LOG_TAG="MARKER1";

    protected FairWindModel fairWindModel;
    protected MapView mapView;
    private Drawable drawable;

    private float scale;
    private int color;
    private DateTime lastUpdate;
    public DateTime getLastUpdate() { return  lastUpdate; }

    private Handler handler=new Handler();

    public Marker1(MapView mapView, FairWindModel fairWindModel) {
        super(mapView);
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
        setDraggable(false);
    }


    public Marker1(MapView mapView, FairWindModel fairWindModel, int resourceId, int scale, int color) {
        super(mapView);
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
        this.scale=scale;
        this.color=color;
        setDrawable(resourceId);
        setDraggable(false);
    }

    public Marker1(MapView mapView,FairWindModel fairWindModel, Drawable drawable, Position position, Double angle, int scale, int color, String text) {
        super(mapView);
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
        this.scale=scale;
        this.color=color;
        setDrawable(drawable);
        setDraggable(false);
        update(position,angle,text);
    }

    public Drawable getDrawable() {return drawable;}
    public void setDrawable(int resourceId) {
        Drawable drawable=mapView.getContext().getResources().getDrawable(resourceId);
        setDrawable(drawable);
    }
    public void setDrawable(Drawable drawable) {
        this.drawable=drawable;
        setIcon(this.drawable.mutate());
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
    }


    public void setColor(int color) { this.color=color; }
    public int getColor() { return color; }
    public void setScale(float scale) { this.scale=scale; }
    public float getScale() { return scale; }

    public void update(final Position position, final Double angle, final String text) {

        if (position!=null && angle!=null) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "upadte -> angle:" + angle + " position:" + position);
                    if (angle != null) {
                        setRotation(angle.floatValue());
                    }
                    if (position != null) {
                        setPosition(position.asGeoPoint());
                    }
                    lastUpdate= DateTime.now();
                    mapView.invalidate();
                }
            });
        }
    }

    public Drawable getRotateDrawable(final Drawable d, final float angle, final String text) {
        final Drawable[] arD = { d };
        Log.d(LOG_TAG,"getRotateDrawable:"+text);
        final android.graphics.Paint paintText= new android.graphics.Paint();
        paintText.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        paintText.setStrokeWidth(2);
        paintText.setTextSize(30);

        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();

                if(text!=null) {
                    PointF coords=polarToCart(angle);
                    paintText.setColor(color);
                    canvas.drawText(text,coords.x,coords.y, paintText);

                }
                canvas.rotate(angle, d.getBounds().width()/2, d.getBounds().height()/2);

                super.draw(canvas);
                canvas.restore();
            }
        };

    }

    public PointF polarToCart(float angle) {
        float width=drawable.getBounds().width();
        angle-=90;

        PointF point=new PointF();

        if(angle<90)
            angle+=360;

        if(angle>=360)
            angle-=360;

        angle=Math.abs(angle);

        float size=width/scale;

        if(angle>=0 &&angle<=180){
            point.x= (float) (size*Math.cos(Math.toRadians(angle-10)) + width/2);
            point.y=(float)(size*Math.sin(Math.toRadians(angle-10)) + width/2);

        }else if(angle>180 &&angle<360){
            point.x=(float)(size*Math.cos(Math.toRadians(angle+10)) + width/2);
            point.y=(float)(size*Math.sin(Math.toRadians(angle+10)) + width/2);
        }

        return point;

    }

    public void onAdd() {}
    public void onRemove() {}

    public void add() {
        mapView.getOverlays().add(this);
        onAdd();
        mapView.invalidate();
    }

    public void remove() {
        onRemove();
        mapView.getOverlays().remove(this);
        mapView.invalidate();
    }

    @Override
    public void setMapPreferences(MapView mapView, FairWindModel fairWindModel, MapPreferences mapPreferences) {

    }



}
