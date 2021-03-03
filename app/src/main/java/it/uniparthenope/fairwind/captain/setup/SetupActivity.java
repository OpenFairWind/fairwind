package it.uniparthenope.fairwind.captain.setup;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import android.preference.Preference;
import android.preference.MultiSelectListPreference;
import android.preference.ListPreference;
import android.preference.CheckBoxPreference;

import android.support.v7.app.ActionBar;



import android.text.TextUtils;
import android.view.MenuItem;

import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.net.Uri;



import android.util.Log;
import android.support.v4.app.NavUtils;
import android.view.WindowManager;

import com.nononsenseapps.filepicker.FilePickerActivity;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.HomeActivity;
import it.uniparthenope.fairwind.captain.setup.preferences.datalistener.DataListenerPreferencesFragment;
import it.uniparthenope.fairwind.captain.setup.preferences.map.MapPreferencesFragment;
import it.uniparthenope.fairwind.captain.setup.preferences.meta.MetaPreferencesFragment;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.MetaPreferences;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.model.impl.AppDetail;
import it.uniparthenope.fairwind.model.impl.AppDetails;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SetupActivity extends AppCompatPreferenceActivity implements DataListenerPreferencesFragment.OnListFragmentInteractionListener, MapPreferencesFragment.OnListFragmentInteractionListener, MetaPreferencesFragment.OnListFragmentInteractionListener, ToolsPreferencesFragment.OnListFragmentInteractionListener {
    private static final String LOG_TAG = "SETUP_ACTIVITY";

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FairWindApplication.getFairWindModel().isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (fragmentName.startsWith(SetupActivity.class.getName())) return true;
        if (fragmentName.startsWith("it.uniparthenope.fairwind.captain.setup.")==true && fragmentName.endsWith("Fragment")==true) return true;
        Log.d(LOG_TAG,"Setup:"+fragmentName);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return true;
        //return (context.getResources().getConfiguration().screenLayout
        //        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);


                // If is a multi select list
            } else if(preference instanceof MultiSelectListPreference) {

                //facciamo il cast come multilist
                MultiSelectListPreference pref = (MultiSelectListPreference) preference;
                String selectedElementsString = "";
                String separator = "";

                //qui dovrebbero esserci gli elementi fleggati ma in realtà non escono mai è sempre vuoto questo set
                Set<String> selectedElements = (Set<String>)value;

                // per ogni elemento selezionato
                for(String element : selectedElements) {
                    //prendi l'indice
                    int index = pref.findIndexOfValue(element);
                    CharSequence entry = null;
                    if(index >= 0 && pref.getEntries() != null)
                    {
                        //salva il nome
                        entry = pref.getEntries()[index];
                    }
                    //componi la stringa che comparirà nelle preference con i solo nomi selezionati separati dal ;
                    selectedElementsString = selectedElementsString + separator + entry;
                    separator = "; ";
                }

                // setta la stringa come sommario
                pref.setSummary(selectedElementsString);

                // If is a text field
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }


            try {
                FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
                fairWindModel.sharedPreferences2Model();
            } catch (IOException ex) {
                //throw new RuntimeException(ex);
                Log.e(LOG_TAG,ex.getMessage());
            }

            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        if (preference!=null) {

            //imposta il listener
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            //recupera la key della preference
            String s = preference.getKey();

            //salva in sp le SharedPreferences
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            String s1 = "";

            //se non è una checkbox o una multiselectlist
            if (!(preference instanceof CheckBoxPreference) &&!(preference instanceof MultiSelectListPreference)) {

                s1 = sp.getString(s, "");

            }

            //se è una multiselectlist
            if(preference instanceof MultiSelectListPreference){

                // ci salviamo il set di stringhe della multilist
                Set<String> set=((MultiSelectListPreference) preference).getValues();

                //invochiamo il listner
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, set);
                return;
            }

            // Trigger the listener immediately with the preference's
            // current value.

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, s1);

        }
    }

    @Override
    public void onListFragmentInteraction(DataListenerPreferences item) {

    }

    @Override
    public void onListFragmentInteraction(MapPreferences item) {

    }

    @Override
    public void onListFragmentInteraction(MetaPreferences item) {

    }

    @Override
    public void onListFragmentInteraction() {

    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_NAME));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_FLAG));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_PORT));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_UUID));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_MMSI));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_URL));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GENERAL_BOAT_TYPE));

        }


    }





    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GuiPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_gui);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GUI_THEME));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_GUI_KEEP_SCREEN_ON));

        }


    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MapsForgePreferenceFragment extends PreferenceFragment  implements Preference.OnPreferenceClickListener {

        private static Preference filePickMap;
        private static Preference prefMapsDownload;

        public static final int FILE_CODE_MAPS_FILENAME=001;

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.d(LOG_TAG,"onPreferenceClick");

            //FairWindModelImpl fairWindModel=(FairWindModelImpl) FairWindModelImpl.getInstance();
            String mapsPath = FairWindApplication.getFairWindModel().getPreferences().getConfigProperty(Constants.PREF_KEY_MAPS_PATH);

            // This always works
            Intent i = new Intent(getActivity(), FilePickerActivity.class);
            // This works if you defined the intent filter
            // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

            // Set these depending on your use case. These are the defaults.
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
            i.putExtra(FilePickerActivity.EXTRA_START_PATH, mapsPath);

            startActivityForResult(i, FILE_CODE_MAPS_FILENAME);

            return true;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(LOG_TAG,"onActivityResult -> onActivityResult: "+requestCode);
            if (requestCode == FILE_CODE_MAPS_FILENAME && resultCode == Activity.RESULT_OK) {

                if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                    // For JellyBean and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clip = data.getClipData();

                        if (clip != null) {
                            for (int i = 0; i < clip.getItemCount(); i++) {
                                Uri uri = clip.getItemAt(i).getUri();
                                // Do something with the URI
                                filePickMap.getEditor().putString(Constants.PREF_KEY_MAPS_FILENAME,uri.getPath()).apply();
                                bindPreferenceSummaryToValue(filePickMap);
                                break;
                            }
                        }
                        // For Ice Cream Sandwich
                    } else {
                        ArrayList<String> paths = data.getStringArrayListExtra
                                (FilePickerActivity.EXTRA_PATHS);

                        if (paths != null) {
                            for (String path: paths) {
                                Uri uri = Uri.parse(path);
                                // Do something with the URI
                                filePickMap.getEditor().putString(Constants.PREF_KEY_MAPS_FILENAME,uri.getPath()).apply();
                                bindPreferenceSummaryToValue(filePickMap);
                                break;
                            }
                        }
                    }

                } else {
                    Uri uri = data.getData();
                    // Do something with the URI
                    filePickMap.getEditor().putString(Constants.PREF_KEY_MAPS_FILENAME,uri.getPath()).apply();
                    bindPreferenceSummaryToValue(filePickMap);
                }
            }
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_maps);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_FILENAME));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_PATH));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_DOWNLOAD));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_DOWNLOAD_URL));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_BOAT));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_PREDICTOR));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_SPEED));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_HEADING));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_APPARENT_WIND));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_TRUE_WIND));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_KEY_MAPS_TRACK));


            class BrowserMapsDownload implements Preference.OnPreferenceClickListener {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d(LOG_TAG,"onPreferenceClick -> BrowserMapsDownload");

                    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    //String mapsPath=prefs.getString(
                    //        getString(R.string.pref_key_maps_path),getString(R.string.pref_default_maps_path));

                    //FairWindModelImpl fairWindModel=(FairWindModelImpl) FairWindModelImpl.getInstance();
                    String mapsPath = FairWindApplication.getFairWindModel().getPreferences().getConfigProperty(Constants.PREF_KEY_MAPS_PATH);


                    String urlString="http://web.uniparthenope.it/~osp/maps/europe/italy.map";
                    Uri uri=Uri.parse(urlString);
                    String destinationFilename=uri.getLastPathSegment();

                    Log.d(LOG_TAG,"Maps Download -> "+ uri.toString() + " -- " + uri.getLastPathSegment());

                    DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    if (request!=null) {
                        request.setDestinationInExternalPublicDir(mapsPath, destinationFilename);
                        request.setVisibleInDownloadsUi(true);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                        request.setDescription("...").setTitle("Download map: " + destinationFilename);
                        long id = downloadManager.enqueue(request);
                        Log.d(LOG_TAG,"Maps Download -> "+id);
                    }
                    return true;
                }
            }

            filePickMap=findPreference(Constants.PREF_KEY_MAPS_FILENAME);
            filePickMap.setOnPreferenceClickListener(this);


            prefMapsDownload=findPreference(Constants.PREF_KEY_MAPS_DOWNLOAD);
            prefMapsDownload.setOnPreferenceClickListener(new BrowserMapsDownload());

        }


    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ApplicationsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_applications);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            //AppDetails appDetails=((FairWindModelImpl) (FairWindModelImpl.getInstance())).getAppDetails(getActivity(), AppDetails.ALL);
            AppDetails appDetails=FairWindApplication.getFairWindModel().getAppDetails(getActivity(), AppDetails.ALL);

            Collections.sort(appDetails, new Comparator<AppDetail>() {
                @Override
                public int compare(AppDetail appDetail, AppDetail t1) {
                    return appDetail.getLabel().toString().compareToIgnoreCase(t1.getLabel().toString());
                }
            });


            MultiSelectListPreference marineApps=(MultiSelectListPreference)findPreference("marine_apps");
            MultiSelectListPreference homeApps=(MultiSelectListPreference)findPreference("home_apps");

            Set<String>set=appDetails.getSetList();

            CharSequence[] packageName=new CharSequence[appDetails.size()];
            CharSequence[] labelName=new CharSequence[appDetails.size()];

            for(int i=0;i<appDetails.size();i++){
                packageName[i]=appDetails.get(i).getPackageName();
                labelName[i]=appDetails.get(i).getLabel();
            }

            marineApps.setEntries(labelName);
            marineApps.setEntryValues(packageName);
            //marineApps.setValues(set);


            homeApps.setEntries(labelName);
            homeApps.setEntryValues(packageName);
            //homeApps.setValues(set);

            bindPreferenceSummaryToValue(marineApps);
            bindPreferenceSummaryToValue(homeApps);

        }


    }

    @Override
    public void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();

    }
}
