package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Ribbon;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screen;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screens;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.model.FairWindModelNotDefinedException;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.DepthMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * TODO: document your custom view class.
 */
public class ButtonBarView extends FairWindView {
    public static final String LOG_TAG="BUTTON_BAR_VIEW";

    private Screens screens=null;

    enum Type {
        TOP,
        BOTTOM
    }

    private CompassMode compassMode=CompassMode.TRUE;
    private DepthMode depthMode=DepthMode.BELOW_TRANSDUCER;
    private int coordsStyle=Formatter.COORDS_STYLE_DDMMSS;
    private int depthUnit= Formatter.UNIT_DEPTH_M;

    private TextView txtDepth;

    private TextView txtLatitude;
    private TextView txtLongitude;

    private TextView txtSOG;
    private TextView txtCOG;

    private TextView txtVmg;
    private TextView txtBearing;

    private TextView txtWptName;
    private TextView txtWptInfo;

    private FairWindEvent eventDepth;
    private FairWindEvent eventPosition;
    private FairWindEvent eventCourseOverGround;
    private FairWindEvent eventSpeedOverGround;
    private FairWindEvent eventBearingActual;
    private FairWindEvent eventVelocityMadeGoodToWaypoint;

    private FairWindEvent eventCourseNextPointHRef;
    private FairWindEvent eventCourseNextPointDistance;
    private FairWindEvent eventCourseNextPointTimeToGo;

    private Type mType=Type.TOP;
    private BarButton mHome;
    private BarButton mInfo;
    private BarButton mBtn1;
    private BarButton mBtn2;
    private BarButton mBtn3;
    private BarButton mBtn4;
    private BarButton mBtn5;

    public ButtonBarView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public ButtonBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public ButtonBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            FairWindModel fairWindModel=getFairWindModel();
            if (fairWindModel!=null) {
                if (attrs != null) {

                    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ButtonBarView);
                    String homeClassName = a.getString(R.styleable.ButtonBarView_home_action);
                    Log.d(LOG_TAG, "homeClassName:" + homeClassName);

                    if (homeClassName == null || homeClassName.isEmpty()) {
                        mType = Type.BOTTOM;
                        inflater.inflate(R.layout.bottom_button_bar_view, this);

                        eventDepth = new FairWindEvent(FairWindModelBase.getDepthPath(SignalKConstants.self) + ".*", FairWindModelBase.getDepthPath(SignalKConstants.self), 0, PathEvent.EventType.ADD, this);
                        getFairWindModel().register(eventDepth);

                        txtDepth = (TextView) findViewById(R.id.txt_depth);

                        mBtn5 = new BarButton(this,
                                a.getString(R.styleable.ButtonBarView_btn5_action),
                                a.getDrawable(R.styleable.ButtonBarView_btn5_icon));

                    } else {
                        mType = Type.TOP;
                        inflater.inflate(R.layout.top_button_bar_view, this);

                        compassMode = CompassMode.TRUE;

                        String self = SignalKConstants.self;
                        Log.d(LOG_TAG, "boat UUID -> " + self);

                        String pathPosition = FairWindModelBase.getNavPositionPath(SignalKConstants.self);
                        String pathCourseOverGround = FairWindModelBase.getCourseOverGroundPath(SignalKConstants.self, compassMode);
                        String pathSpeedOverGroud = FairWindModelBase.getNavSpeedOverGroundPath(SignalKConstants.self);

                        String pathCourseNextPointHRef = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint;
                        String pathCourseNextPointDistance = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_distance;
                        String pathCourseNextPointTimeToGo = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_timeToGo;
                        String pathBearingActual = FairWindModelBase.getBearingTrackPath(SignalKConstants.self, compassMode);
                        String pathVelocityMadeGood = FairWindModelBase.getNavPerformanceVelocityMadeGoodToWaypointPath(SignalKConstants.self);

                        pathBearingActual = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic;
                        pathVelocityMadeGood = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_velocityMadeGood;

                        eventPosition = new FairWindEvent(pathPosition + ".*", pathPosition, 0, PathEvent.EventType.ADD, this);
                        eventCourseOverGround = new FairWindEvent(pathCourseOverGround + ".*", pathCourseOverGround, 0, PathEvent.EventType.ADD, this);
                        eventSpeedOverGround = new FairWindEvent(pathSpeedOverGroud + ".*", pathSpeedOverGroud, 0, PathEvent.EventType.ADD, this);
                        eventBearingActual = new FairWindEvent(pathBearingActual, pathBearingActual, 0, PathEvent.EventType.ADD, this);
                        eventVelocityMadeGoodToWaypoint = new FairWindEvent(pathVelocityMadeGood, pathVelocityMadeGood, 0, PathEvent.EventType.ADD, this);

                        eventCourseNextPointHRef = new FairWindEvent(pathCourseNextPointHRef, pathCourseNextPointHRef, 0, PathEvent.EventType.ADD, this);
                        eventCourseNextPointDistance = new FairWindEvent(pathCourseNextPointDistance, pathCourseNextPointDistance, 0, PathEvent.EventType.ADD, this);
                        eventCourseNextPointTimeToGo = new FairWindEvent(pathCourseNextPointTimeToGo, pathCourseNextPointTimeToGo, 0, PathEvent.EventType.ADD, this);

                        getFairWindModel().register(eventPosition);
                        getFairWindModel().register(eventCourseOverGround);
                        getFairWindModel().register(eventSpeedOverGround);
                        getFairWindModel().register(eventBearingActual);
                        getFairWindModel().register(eventVelocityMadeGoodToWaypoint);


                        getFairWindModel().register(eventCourseNextPointHRef);
                        getFairWindModel().register(eventCourseNextPointDistance);
                        getFairWindModel().register(eventCourseNextPointTimeToGo);

                        txtLatitude = (TextView) findViewById(R.id.txt_latitude);
                        txtLongitude = (TextView) findViewById(R.id.txt_longitude);

                        txtSOG = (TextView) findViewById(R.id.txt_sog);
                        txtCOG = (TextView) findViewById(R.id.txt_cog);

                        txtVmg = (TextView) findViewById(R.id.txt_vmg);
                        txtBearing = (TextView) findViewById(R.id.txt_bearing);

                        txtWptName = (TextView) findViewById(R.id.txt_wptname);
                        txtWptInfo = (TextView) findViewById(R.id.txt_wptinfo);

                        mHome = new BarButton(this,
                                a.getString(R.styleable.ButtonBarView_home_action),
                                a.getDrawable(R.styleable.ButtonBarView_home_icon));

                        mInfo = new BarButton(this,
                                a.getString(R.styleable.ButtonBarView_info_action),
                                a.getDrawable(R.styleable.ButtonBarView_info_icon));


                    }
                    Log.d(LOG_TAG, "mType:" + mType);


                    mBtn1 = new BarButton(this,
                            a.getString(R.styleable.ButtonBarView_btn1_action),
                            a.getDrawable(R.styleable.ButtonBarView_btn1_icon));

                    mBtn2 = new BarButton(this,
                            a.getString(R.styleable.ButtonBarView_btn2_action),
                            a.getDrawable(R.styleable.ButtonBarView_btn2_icon));

                    mBtn3 = new BarButton(this,
                            a.getString(R.styleable.ButtonBarView_btn3_action),
                            a.getDrawable(R.styleable.ButtonBarView_btn3_icon));

                    mBtn4 = new BarButton(this,
                            a.getString(R.styleable.ButtonBarView_btn4_action),
                            a.getDrawable(R.styleable.ButtonBarView_btn4_icon));


                }
            }
        } catch (FairWindModelNotDefinedException ex){
            Log.e(LOG_TAG,ex.getMessage());
        }
        Log.d(LOG_TAG,"/initializeViews");
    }





    @Override
    protected void onUpdate(FairWindEvent event) {
        if (mType==Type.TOP) {
            if (eventPosition.isMatching(event)) {
                updatePosition();
            } else if (eventCourseOverGround.isMatching(event)) {
                updateCOG();
            } else if (eventSpeedOverGround.isMatching(event)) {
                updateSOG();
            } else if (eventBearingActual.isMatching(event)) {
                updateBearing(event);
            } else if (eventVelocityMadeGoodToWaypoint.isMatching(event)) {
                updateVmg(event);
            } else if (eventCourseNextPointHRef.isMatching(event)) {
                updateWptName(event);
            } else if (eventCourseNextPointDistance.isMatching(event)) {
                updateWptInfo(event);
            } else if (eventCourseNextPointTimeToGo.isMatching(event)) {
                updateWptInfo(event);
            }
        } else {
            if (eventDepth.isMatching(event)) {
                updateDepth();
            }

        }
    }

    private void updatePosition() {
        Position position=getFairWindModel().getNavPosition(SignalKConstants.self);
        if (position!=null) {
            setPosition(position);
        }
    }

    public void setDepth(Double depth) {
        // Set view values
        txtDepth.setText(Formatter.formatDepth(depthUnit, depth,"n/a"));

    }

    public void setPosition(Position position) {
        // Set view values
        txtLatitude.setText(Formatter.formatLatitude(coordsStyle, position.getLatitude(),"n/a"));
        txtLongitude.setText(Formatter.formatLongitude(coordsStyle, position.getLongitude(),"n/a"));
    }

    protected void updateDepth() {
        Double depth=getFairWindModel().getDepth(SignalKConstants.self,depthMode);
        if (depth!=null) {
            setDepth(depth);
        }
    }

    public void setCOG(Double cog) {
        // Set view values
        txtCOG.setText(Formatter.formatDirection(cog,"n/a"));

    }

    public void setSOG(Double sog) {
        // Set view values
        txtSOG.setText(Formatter.formatSpeed(Formatter.UNIT_SPEED_KNT,sog,"n/a"));

    }





    protected void updateCOG() {

        Double cog=getFairWindModel().getCourseOverGround(SignalKConstants.self,compassMode);
        if (cog!=null)    {
            setCOG(cog);
        }
    }

    private void updateSOG() {
        Double sog=getFairWindModel().getNavSpeedOverGround(SignalKConstants.self);
        if (sog!=null) {
            setSOG(sog);
        }

    }

    private void updateVmg(PathEvent event) {
        updateWptName(event);
        //Double vmg=getFairWindModel().getNavPerformanceVelocityMadeGood(SignalKConstants.self);
        Double vmg=null;
        if (event.getType() == PathEvent.EventType.ADD) {
            vmg=(Double) ((SignalKModel)getFairWindModel()).get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_velocityMadeGood);
        }
        txtVmg.setText(Formatter.formatSpeed(Formatter.UNIT_SPEED_KNT,vmg,"-.-"));
    }

    private void updateBearing(PathEvent event) {
        updateWptName(event);
        //Double bearing=getFairWindModel().getBearingTrack(SignalKConstants.self,compassMode);
        Double bearing=null;
        if (event.getType()== PathEvent.EventType.ADD) {
            bearing=(Double) ((SignalKModel)getFairWindModel()).get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic);
        }
        txtBearing.setText(Formatter.formatDirection(bearing,"-.-"));
    }

    private void updateWptName(PathEvent event) {
        String waypointid="-.-";
        if (event.getType()== PathEvent.EventType.ADD) {
            String href = (String) ((SignalKModel) getFairWindModel()).get(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint);
            if (href != null && href.isEmpty() == false) {
                String id = href.split("[.]")[2];
                Waypoint waypoint = (Waypoint) getFairWindModel().getWaypoints().asMap().get(id);
                if (waypoint != null) {
                    waypointid = waypoint.getId();
                }
            }
        }
        txtWptName.setText(waypointid);
    }

    private void updateWptInfo(PathEvent event) {
        updateWptName(event);

        Double distance =null;
        Double timeToGo =null;

        if (event.getType()== PathEvent.EventType.ADD) {
            //Double bearing=getFairWindModel().getBearingTrack(SignalKConstants.self,compassMode);
            distance = (Double) ((SignalKModel) getFairWindModel()).get(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_distance);
            timeToGo = (Double) ((SignalKModel) getFairWindModel()).get(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_timeToGo);
        }
        String sDistance=Formatter.formatRange(Formatter.UNIT_RANGE_NM,distance,"-.-");
        String sTimeToGo=Formatter.formatTimeSpan(Formatter.TIME_STYLE_HHMM,timeToGo,"-.-");
        txtWptInfo.setText(sDistance+" "+sTimeToGo);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d(LOG_TAG,"onFinishInflate");

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.

        Log.d(LOG_TAG,"mType:"+mType);
        if (mType == Type.TOP) {

            View view= findViewById(R.id.btn_home);
            ImageButton imageButtonHome=(ImageButton) findViewById(R.id.btn_home);
            Log.d(LOG_TAG,"imageButtonHome:"+imageButtonHome);

            if (mHome!=null) {
                mHome.setImageButton((ImageButton) findViewById(R.id.btn_home));
            }
            if (mInfo!=null) {
                mInfo.setImageButton((ImageButton) findViewById(R.id.btn_info));
            }
        }
        if (mBtn1!=null) {
            mBtn1.setImageButton((ImageButton) findViewById(R.id.btn_1));
        }

        if (mBtn2!=null) {
            mBtn2.setImageButton((ImageButton) findViewById(R.id.btn_2));
        }
        if (mBtn3!=null) {
            mBtn3.setImageButton((ImageButton) findViewById(R.id.btn_3));
        }
        if (mBtn4!=null) {
            mBtn4.setImageButton((ImageButton) findViewById(R.id.btn_4));
        }
        if (mType == Type.BOTTOM) {
            if (mBtn5!=null) {
                mBtn5.setImageButton((ImageButton) findViewById(R.id.btn_5));
            }
        }
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

    public void setScreens(Screens screens) {
        this.screens=screens;
    }

    public Screens getScreens() { return screens; }

    public void setByScreen(Screen screen) {

        Ribbon ribbon=screen.getBottomRibbon();
        if (mType == Type.TOP) {

            if (mHome!=null) {
                mHome.setButton(screen.getHome());
            }

            if (mInfo!=null) {
                mInfo.setButton(screen.getInfo());
            }
            ribbon=screen.getTopRibbon();
        }

        if (mBtn1!=null) {
            mBtn1.setButton(ribbon.get(0));
        }

        if (mBtn2!=null) {
            mBtn2.setButton(ribbon.get(1));
        }

        if (mBtn3!=null) {
            mBtn3.setButton(ribbon.get(2));
        }
        if (mBtn4!=null) {
            mBtn4.setButton(ribbon.get(3));
        }

        if (mType == Type.BOTTOM) {
            if (mBtn5!=null) {
                mBtn5.setButton(ribbon.get(4));
            }
        }

    }
}
