package it.uniparthenope.fairwind.sdk.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.FairWindView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Utils;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;


/**
 * Created by raffaelemontella on 14/03/16.
 */
public class RollingRoadView extends FairWindView implements Runnable {
    public static final String LOG_TAG="ROLLING_ROAD_VIEW";

    private float skySeaRate=.40f;
    private float pixelPerDeg=2.0f;
    private float pixelPerMeter=.1f;
    private float pixelPerMeterAtHorizon=Float.NaN;

    private double portLineRange=1852;
    private double starboardLineRange=1852;
    private double rangeTick=463;
    private int rangeTicks=10;

    private double speed=Double.NaN;
    private double course=Double.NaN;
    private double bearing=Double.NaN;
    private double crossTrackError=Double.NaN;

    private FairWindEvent courseCrossTrackErrorAdd;
    private FairWindEvent courseNextPointBearingMagneticAdd;
    private FairWindEvent courseOverGroundTrueAdd;
    private FairWindEvent speedOverGroundAdd;

    private Handler handler=new Handler();

    protected String vessel_uuid="self";

    private float[] y=new float[5];

    public RollingRoadView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public RollingRoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public RollingRoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context,attrs,defStyleAttr);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RollingRoadView);
            String sVesselUuid = a.getString(R.styleable.SingleDataView_vessel_uuid);
            if (sVesselUuid==null) {
                sVesselUuid="self";
            }

            vessel_uuid = FairWindModelBase.fixSelfKey(sVesselUuid);

            inflater.inflate(R.layout.rolling_road_view, this);

        }
        setWillNotDraw(false);
        String basePath=SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;

        courseCrossTrackErrorAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_crossTrackError,
                basePath+SignalKConstants.nav_courseRhumbline_crossTrackError,0, PathEvent.EventType.ADD,this);

        courseNextPointBearingMagneticAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic,
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic,0, PathEvent.EventType.ADD,this);

        courseOverGroundTrueAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseOverGroundTrue+".value",
                basePath+SignalKConstants.nav_courseOverGroundTrue+".value",0, PathEvent.EventType.ADD,this);

        speedOverGroundAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_speedOverGround+".value",
                basePath+SignalKConstants.nav_speedOverGround+".value",0, PathEvent.EventType.ADD,this);



        getFairWindModel().register(courseCrossTrackErrorAdd);
        getFairWindModel().register(courseNextPointBearingMagneticAdd);
        getFairWindModel().register(courseOverGroundTrueAdd);
        getFairWindModel().register(speedOverGroundAdd);

        pixelPerMeterAtHorizon=pixelPerMeter*skySeaRate*1.0f;

        y[0]=0.00f;
        y[1]=0.20f;
        y[2]=0.40f;
        y[3]=0.60f;
        y[4]=0.80f;



        long timer=1000;
        if (Double.isNaN(speed)==false) {
            timer=(long)(1000/speed);
        }
        handler.postDelayed(this,timer);

        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    protected void onUpdate(FairWindEvent event) {
        SignalKModel signalKModel=(SignalKModel)getFairWindModel();

        if (event.isMatching(courseCrossTrackErrorAdd)) {
            Object o=signalKModel.getFullData().get(courseCrossTrackErrorAdd.getPathValue());
            if (o!=null) {
                crossTrackError = (Double)o;
                invalidate();
            }
        } else if (event.isMatching(courseNextPointBearingMagneticAdd)) {
            Object o=signalKModel.getFullData().get(courseNextPointBearingMagneticAdd.getPath());
            if (o!=null) {
                bearing=(Double) o;
                invalidate();
            }
        } else if (event.isMatching(courseOverGroundTrueAdd)) {
            Object o=signalKModel.getFullData().get(courseOverGroundTrueAdd.getPath());
            if (o!=null) {
                course = (Double)o;
                invalidate();
            }
        } else if (event.isMatching(speedOverGroundAdd)) {
            Object o=signalKModel.getFullData().get(speedOverGroundAdd.getPath());
            if (o!=null) {
                speed = (Double) o;
                invalidate();
            }
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d(LOG_TAG,"onFinishInflate");

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.


        Log.d(LOG_TAG,"/onFinishInflate");
    }

    public void hideShow() {
        Log.d(LOG_TAG,"hideShow");
        if (getVisibility() == View.VISIBLE) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Log.d(LOG_TAG,"onDraw:");


        int width=canvas.getWidth();
        int center=canvas.getWidth()/2;
        int horizonInPixel=(int)(canvas.getHeight()*skySeaRate);
        int height=canvas.getHeight();

        double headingDeg=Math.toDegrees(course);

        double bearingDeg=Double.NaN;
        double bearingCenter=center;

        Paint paintShader=new Paint();
        Paint paintBitmap=new Paint();

        int scaledLineSizePlain = getResources().getDimensionPixelSize(R.dimen.rolling_road_line_plain);
        int scaledLineSizeThick = getResources().getDimensionPixelSize(R.dimen.rolling_road_line_thick);

        Paint paintLineWhite=new Paint();
        paintLineWhite.setColor(Color.WHITE);
        paintLineWhite.setStrokeWidth(scaledLineSizePlain);

        Paint paintLineBlack=new Paint();
        paintLineBlack.setColor(Color.BLACK);
        paintLineBlack.setStrokeWidth(scaledLineSizeThick);

        Paint paintLineRed=new Paint();
        paintLineRed.setColor(Color.RED);
        paintLineRed.setStrokeWidth(scaledLineSizeThick);

        Paint paintLineGreen=new Paint();
        paintLineGreen.setColor(Color.GREEN);
        paintLineGreen.setStrokeWidth(scaledLineSizeThick);

        Paint paintLineYellow=new Paint();
        paintLineYellow.setColor(Color.YELLOW);
        paintLineYellow.setStrokeWidth(scaledLineSizeThick);

        Paint paintText=new Paint();
        paintText.setColor(Color.BLACK);
        int scaledTextSize = getResources().getDimensionPixelSize(R.dimen.rolling_road_font);
        paintText.setTextSize(scaledTextSize);
        paintText.setTextAlign(Paint.Align.CENTER);

        Bitmap bitmapBoatRed96 = BitmapFactory.decodeResource(getResources(),R.drawable.boat_red_96x96);


        // Draw the sky
        Shader shaderSky = new LinearGradient(0, 0, 0, horizonInPixel, Color.parseColor("#477EC4"), Color.parseColor("#C3D6DE"), Shader.TileMode.CLAMP);
        paintShader.setShader(shaderSky);
        canvas.drawRect(0,0,width,horizonInPixel,paintShader);

        // Draw the sea
        Shader shaderSea = new LinearGradient(0, 0, 0, height-horizonInPixel,Color.parseColor("#326CBC"),Color.parseColor("#D1E3F3"), Shader.TileMode.CLAMP);
        paintShader.setShader(shaderSea);
        canvas.drawRect(0,horizonInPixel,width,height,paintShader);

        // Draw clouds bitmap
        Bitmap bitmapNavigationClouds = BitmapFactory.decodeResource(getResources(),R.drawable.navigation_clouds_1440_80);
        canvas.drawBitmap(bitmapNavigationClouds,
                (float)(center-bitmapNavigationClouds.getScaledWidth(canvas)/2),
                horizonInPixel-bitmapNavigationClouds.getHeight(),paintBitmap);


        // Draw range ticks
        for (int i=1;i<=rangeTicks;i++) {
            float range=(float)(i*rangeTick);
            canvas.drawLine(
                    (center-range * pixelPerMeterAtHorizon),horizonInPixel,
                    (center-range * pixelPerMeter),height,
                    paintLineWhite);

            canvas.drawLine(
                    (center+range * pixelPerMeterAtHorizon),horizonInPixel,
                    (center+range * pixelPerMeter),height,
                    paintLineWhite);
        }

        // Draw horizonal (moving) lines
        float deltaInPixel=height - horizonInPixel;
        for (int i=0;i<y.length;i++) {
            canvas.drawLine(0, horizonInPixel + y[i] * deltaInPixel, width, horizonInPixel + y[i] * deltaInPixel, paintLineWhite);
        }

        // Draw the heading line
        canvas.drawLine(
                center,horizonInPixel,
                center,height-bitmapBoatRed96.getHeight()/2,
                paintLineBlack);

        // Check if the bearing is available
        if (Double.isNaN(bearing)==false) {
            bearingDeg = Math.toDegrees(bearing);
            bearingCenter=center-(headingDeg-bearingDeg)*pixelPerDeg;
        }

        // Draw the port range line
        canvas.drawLine(
                (float)(bearingCenter-portLineRange * pixelPerMeterAtHorizon),horizonInPixel,
                (float)(center-portLineRange * pixelPerMeter),height,
                paintLineRed);

        // Draw the starboard range line
        canvas.drawLine(
                (float)(bearingCenter+starboardLineRange * pixelPerMeterAtHorizon),horizonInPixel,
                (float)(center+starboardLineRange * pixelPerMeter),height,
                paintLineGreen);


        // Draw the compass ribbon
        Bitmap bitmapCompassRibbon = BitmapFactory.decodeResource(getResources(),R.drawable.compass_ribbon);
        double compassRibbonHeadingDeg=headingDeg;
        if (headingDeg>=0 && headingDeg<=center/pixelPerDeg) {
            compassRibbonHeadingDeg+=360;
        }
        float compassRibbonHorizonatalOffset=(float)(((360-compassRibbonHeadingDeg)*pixelPerDeg)+center-bitmapCompassRibbon.getWidth()/pixelPerDeg);
        canvas.drawBitmap(bitmapCompassRibbon,compassRibbonHorizonatalOffset,0,paintBitmap);

        // Check if the boat is following a route
        if (Double.isNaN(bearing)==false && Double.isNaN(crossTrackError)==false) {
            // The boat is following

            // Draw the bearing on the compass ribbon
            Bitmap bitmapBoatBlue = BitmapFactory.decodeResource(getResources(),R.drawable.boat_blue_32x32);
            canvas.drawBitmap(bitmapBoatBlue,
                    (float) (bearingCenter - (bitmapBoatBlue.getWidth() / 2)),
                    (float) (bitmapCompassRibbon.getHeight() * .75), paintBitmap);

            // Draw the bearing yellow line
            canvas.drawLine(
                    (float)bearingCenter, horizonInPixel,
                    (float) (center + crossTrackError * pixelPerMeter), height-bitmapBoatRed96.getHeight()/2,
                    paintLineYellow);

            // Print the bearing value
            canvas.drawText(Formatter.formatDirection(bearing,"-.-"),
                    (float)bearingCenter, horizonInPixel,
                    paintText);

            // Draw the boat on the rolling road
            canvas.drawBitmap(bitmapBoatRed96,
                    (float) (center + crossTrackError * pixelPerMeter - (bitmapBoatRed96.getWidth() / 2)),
                    (float) (height-bitmapBoatRed96.getHeight()),
                    paintBitmap);

            // Print the cross track error value
            canvas.drawText(Formatter.formatRange(Formatter.UNIT_RANGE_NM,crossTrackError,"-.-"),
                    (float) (center + crossTrackError * pixelPerMeter - (bitmapBoatRed96.getWidth() / 2)),
                    (float) (height-bitmapBoatRed96.getHeight()),
                    paintText);
        } else {
            // The boat is NOT following

            // Draw the boat with no cross track error
            canvas.drawBitmap(bitmapBoatRed96,
                    (float) (center - (bitmapBoatRed96.getWidth() / 2)),
                    (float) (height-bitmapBoatRed96.getHeight()), paintBitmap);

            // Print the not following message
            // Print the cross track error value
            canvas.drawText("Not following",
                    (float) (center),
                    (float) (height/2),
                    paintText);
        }

        // Draw the centerline on the compass ribbon
        Bitmap bitmapBoatRed = BitmapFactory.decodeResource(getResources(),R.drawable.boat_red_32x32);
        canvas.drawBitmap(bitmapBoatRed,
            (float)((canvas.getWidth()/2)-(bitmapBoatRed.getWidth()/2)),
            (float)(bitmapCompassRibbon.getHeight()*.75),paintBitmap);

        // Draw the horizon
        canvas.drawLine(0,horizonInPixel,width,horizonInPixel,paintLineWhite);

    }


    public void setSpeed(Double speed) {
        if (speed!=null) {
            this.speed = speed;
            invalidate();
        }
    }
    
    public void setCourse(Double course) {
        if (course!=null) {
            this.course = course;
            invalidate();
        }
    }


    public void setBearing(Double bearing) {
        if (bearing!=null) {
            this.bearing = bearing;
            invalidate();
        }
    }

    public void setCrossTrackError(Double crossTrackError) {
        if (crossTrackError!=null) {
            this.crossTrackError = crossTrackError;
            invalidate();
        }
    }

    @Override
    public void run() {
        for(int i=0;i<y.length;i++) {
            y[i] = y[i] + .05f;
            if (y[i] >= 1) {
                y[i] = 0;
            }
        }
        invalidate();

        long timer=1000;
        if (Double.isNaN(speed)==false) {
            timer=(long)(1000/speed);
        }
        handler.postDelayed(this,timer);
    }
}
