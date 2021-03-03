package it.uniparthenope.fairwind.sdk.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.UUID;

import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screen;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screens;

/**
 * Created by raffaelemontella on 13/09/2017.
 */

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    public static final String LOG_TAG="PAGERADAPTER";

    private Screens screens = null;

    public SectionsPagerAdapter(FragmentManager fm, Screens screens) {
        super(fm);
        this.screens=screens;
    }

    public void setCurrentScreen(Screen currentScreen) {
        Log.d(LOG_TAG,"setCurrentScreen:"+currentScreen.getName());
        screens.setCurrent(currentScreen);
        notifyDataSetChanged();
    }



    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d(LOG_TAG,"getItem:"+screens.getCurrent().getName()+" / "+position);
        return screens.getCurrent().get(position).getFragment();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return screens.getCurrent().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return screens.getCurrent().get(position).getName();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

