package it.uniparthenope.fairwind.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 14/03/16.
 */
public class SeaFloorView extends LinearLayout {
    public static final String LOG_TAG="SEAFLOOR_VIEW";

    private float heading;
    private float speed;
    private float depth;
    private Position position=null;
    private float dist;


    public SeaFloorView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public SeaFloorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public SeaFloorView(Context context, AttributeSet attrs, int defStyleAttr) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeaFloorView);


            inflater.inflate(R.layout.sea_floor_view, this);

        }
        setWillNotDraw(false);



        Log.d(LOG_TAG,"/initializeViews");
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

    private float x0=0;
    private float y0=0;

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Log.d(LOG_TAG,"onDraw");
        float scaleX;
        float scaleY;

        // canvas.getWidth():10000m=x:dist => x=dist*canvas.getWidth()/10000m
        scaleX=canvas.getWidth()/1000f;
        scaleY=canvas.getHeight()/200f;
        Paint paint=new Paint();

        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#CD5C5C"));


        float y1=canvas.getHeight()-(depth*scaleY);
        float x1=x0+dist*scaleX;

        Log.d(LOG_TAG,"dist:"+dist+" depth:"+depth);
        Log.d(LOG_TAG,"x0:"+x0+" y0:"+y0+" x1:"+x1+" y1:"+y1);
        canvas.drawLine(x0,y0,x1,y1,paint);
        x0=x1;
        y0=y1;
    }

    public void setHeading(Double heading) {
        if (heading!=null) {
            this.heading = heading.floatValue();

        }
    }

    public void setSpeed(Double speed) {
        if (speed!=null) {
            Log.d(LOG_TAG,"setSpeed:"+speed);
            this.speed = speed.floatValue();
        }
    }

    public void setDepth(Double depth) {
        if (depth!=null) {
            this.depth = depth.floatValue();
        }
    }

    public void setPosition(Double latitude,Double longitude, Double altitude) {
        Log.d(LOG_TAG,"setPosition");
        if (longitude!=null && latitude!=null) {
            if (position==null) {
                position=new Position(latitude,longitude, altitude);
            } else {
                Position newPosition=new Position(latitude,longitude, altitude);
                dist=(float)position.distanceTo(newPosition);

                if (dist>0) {
                    Log.d(LOG_TAG,"position:"+position.toString()+" newPosition:"+newPosition.toString()+" dist:"+dist);
                    invalidate();
                }
                position.setLongitude(longitude);
                position.setLatitude(latitude);
            }



        }
    }


}
