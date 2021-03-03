package it.uniparthenope.fairwind.sdk.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import it.uniparthenope.fairwind.sdk.FairWindApplicationBase;
import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Page;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screen;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screens;
import it.uniparthenope.fairwind.sdk.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;

/**
 * Created by raffaelemontella on 16/09/2017.
 */

public class FairWindActivity extends AppCompatActivity  implements FairWindFragment.OnFragmentInteractionListener {
    public static final String LOG_TAG="ACTIVITY_BASE";



    private Screens screens;

    private ButtonBarView topBarView;
    private ButtonBarView bottomBarView;

    private View mView;

    private Class backActivityClass=null;
    public Class getBackActivityClass() {
        return backActivityClass;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    protected SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;





    public void setCurrentScreenPage(Screen screen, Page page) {
        if (screen!=null) {
            //Log.d(LOG_TAG,"setCurrentScreenPage:"+screen.getName()+" / "+page.getName());
            if (screen.size()==1) {
                mFrameLayout.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.GONE);

                Fragment fragment=screen.get(0).getFragment();
                if (getSupportFragmentManager().findFragmentByTag("single")==null) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container_single, fragment,"single").commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_single, fragment,"single").commit();
                }
                screens.setCurrent(screen);
                screen.setCurrentPage(0);

            } else {
                mFrameLayout.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                mSectionsPagerAdapter.setCurrentScreen(screen);

                int position = 0;
                if (page==null) {
                    position=screen.getCurrentPage();
                } else {
                    for (Page item : screen) {
                        if (item == page) {
                            break;
                        }
                        position++;
                    }
                }
                mViewPager.setCurrentItem(position);
            }
            topBarView.setByScreen(screen);
            bottomBarView.setByScreen(screen);
        }
    }

    public void setBackActivityClass(Class backActivityClass) {
        this.backActivityClass=backActivityClass;
    }



    @Override
    public void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if(backActivityClass==null){
            super.onBackPressed();
        }else{
            Intent intent=new Intent(this, backActivityClass);
            startActivity(intent);
            finish();
        }
    }



    public void setScreens(Json result) {
        screens=new Screens(result);

        if (screens!=null) {
            topBarView.setScreens(screens);
            bottomBarView.setScreens(screens);


            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),screens);


            mViewPager.setAdapter(mSectionsPagerAdapter);

            setCurrentScreenPage(screens.getDefault(),null);

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (FairWindApplication.getFairWindModel().isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


        setBackActivityClass(HomeActivity.class);
        */


        setContentView(R.layout.activity_base);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_hideshow);
        if (fab!=null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonBarView buttonBarView = (ButtonBarView) findViewById(R.id.bottomBarView);
                    if (buttonBarView != null) {
                        buttonBarView.hideShow();
                    }
                }
            });
        }

        FloatingActionButton buttonOpenDrawer = (FloatingActionButton) findViewById(R.id.btn_showDrawer);
        buttonOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (screens!=null) {
                    Screen screen=screens.getCurrent();
                    FairWindFragment fairWindFragment=screen.get(screen.getCurrentPage()).getFragment();
                    fairWindFragment.onDrawer(FairWindActivity.this);
                }

            }
        });

        // Get the top bar
        topBarView = (ButtonBarView) findViewById(R.id.topBarView);

        // Get the top bar
        bottomBarView = (ButtonBarView) findViewById(R.id.bottomBarView);

        // The frame layout
        mFrameLayout=(FrameLayout) findViewById(R.id.container_single);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_multi);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (screens!=null) {
                    screens.getCurrent().setCurrentPage(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        screens=null;
        try {
            String jsonAsString= Utils.readTextFromResource(getResources(), it.uniparthenope.fairwind.sdk.R.raw.default_uipreferences);
            Json result=Json.read(jsonAsString);
            setScreens(result);
        } catch (IOException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

    }



    public static void onVoid(View view, Fragment fragment, FairWindActivity activityBase) {
        Toast.makeText(activityBase, (String)"Void Method",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
