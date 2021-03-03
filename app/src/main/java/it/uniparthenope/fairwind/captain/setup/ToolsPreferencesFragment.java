package it.uniparthenope.fairwind.captain.setup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.security.GeneralSecurityException;

import cz.msebera.android.httpclient.Header;
import it.uniparthenope.fairwind.BuildConfig;
import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.model.impl.LicenseManager;
import it.uniparthenope.fairwind.model.impl.SSL;
import it.uniparthenope.fairwind.model.impl.ValidationException;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;

/**
 * Created by raffaelemontella on 12/09/16.
 */
public class ToolsPreferencesFragment extends PreferenceFragment {
    public static final String LOG_TAG="TOOLSPREFERENCES";

    private OnListFragmentInteractionListener mListener;


    public ToolsPreferencesFragment() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ToolsPreferencesFragment newInstance(int columnCount) {
        ToolsPreferencesFragment fragment = new ToolsPreferencesFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"onPause");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();

        View view = inflater.inflate(R.layout.fragment_toolpreferences, container, false);

        String url=fairWindModel.getApiUrl()+"/";
        RequestHandle requestHandle=invokeWS(url, null);
        if (requestHandle!=null) {
            Log.d(LOG_TAG,"Requested: "+url);
        }

        Button btnResetServices=(Button)view.findViewById(R.id.btn_reset_services);
        Button btnResetMaps=(Button)view.findViewById(R.id.btn_reset_maps);
        Button btnResetRules=(Button)view.findViewById(R.id.btn_reset_rules);
        Button btnRestartApp=(Button)view.findViewById(R.id.btn_restart_app);
        Button btnReboot=(Button)view.findViewById(R.id.btn_reboot);
        Button btnUpdate=(Button)view.findViewById(R.id.btn_update);
        btnUpdate.setEnabled(false);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });


        btnResetServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
                try {
                    String jsonAsString= Utils.readTextFromResource(getResources(), it.uniparthenope.fairwind.sdk.R.raw.default_datalistenerpreferences);
                    fairWindModel.getPreferences().setConfigProperty(Constants.PREF_KEY_SERVICES_CONFIG_DATALISTENERS, jsonAsString);
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        });

        btnResetMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
                try {
                    String jsonAsString= Utils.readTextFromResource(getResources(), it.uniparthenope.fairwind.sdk.R.raw.default_mappreferences);
                    fairWindModel.getPreferences().setConfigProperty(Constants.PREF_KEY_MAPS_CONFIG_OVERLAYS, jsonAsString);
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        });

        btnResetRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
                try {
                    String jsonAsString= Utils.readTextFromResource(getResources(), it.uniparthenope.fairwind.sdk.R.raw.default_rulepreferences);
                    fairWindModel.getPreferences().setConfigProperty(Constants.PREF_KEY_RULES_CONFIG_RULES, jsonAsString);
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        });

        btnRestartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox ckbFirstRun=(CheckBox)getView().findViewById(R.id.ckb_first_run);
                if (ckbFirstRun.isSelected()) {
                    fairWindModel.getPreferences().setConfigPropertyBoolean("firstrun", false);
                }
                FairWindApplication.restart();
            }
        });

        btnReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FairWindApplication.reboot();
            }
        });


        TextView textViewDeviceId = (TextView)view.findViewById(R.id.textViewDeviceId);
        EditText editTextUserId = (EditText)view.findViewById(R.id.editTextUserId);
        EditText editTextPassword=(EditText)view.findViewById(R.id.editTextPassword);

        String userId=fairWindModel.getLicenseManager().getUserId();
        if (userId!=null) {
            editTextUserId.setText(userId);
        }


        textViewDeviceId.setText(LicenseManager.getDeviceId());


        /*
        try {
            String password = licenseManager.getPassword();


            if (password != null) {
                editTextPassword.setText(password);
            }
        } catch (ValidationException ex) {
            Toast.makeText(FairWindApplication.getInstance(), "An invalid or abstent license prevents the password presetting.", Toast.LENGTH_LONG).show();
        }
        */

        Button btnCheckOut=(Button)view.findViewById(R.id.btn_checkout);
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextUserId = (EditText)getView().findViewById(R.id.editTextUserId);
                EditText editTextPassword = (EditText)getView().findViewById(R.id.editTextPassword);

                String userId=editTextUserId.getText().toString();
                String password=editTextPassword.getText().toString();

                if (userId!=null && password!=null && !userId.isEmpty() && !password.isEmpty()) {
                    SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + LicenseManager.getDeviceId());
                    if (ssl!=null ) {
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("userid", userId.trim());
                        requestParams.add("password", password.trim());
                        requestParams.add("deviceid", LicenseManager.getDeviceId());

                        String url = fairWindModel.getApiUrl() + "/license/checkout";
                        RequestHandle requestHandle = invokeWS(url, requestParams);
                        if (requestHandle != null) {
                            Log.d(LOG_TAG,"Requested: "+url);
                        } else {
                            Toast.makeText(FairWindApplication.getInstance(), "Request failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(FairWindApplication.getInstance(), "Username and password not specified", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnRelease=(Button)view.findViewById(R.id.btn_release);
        btnRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextUserId = (EditText)getView().findViewById(R.id.editTextUserId);
                EditText editTextPassword = (EditText)getView().findViewById(R.id.editTextPassword);

                String userId=editTextUserId.getText().toString();
                String password=editTextPassword.getText().toString();

                if (userId!=null && password!=null && !userId.isEmpty() && !password.isEmpty()) {
                    SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + LicenseManager.getDeviceId());

                    if (ssl!=null) {
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("userid", userId.trim());
                        requestParams.add("password", password.trim());
                        requestParams.add("deviceid", LicenseManager.getDeviceId());

                        String url = fairWindModel.getApiUrl() + "/license/release";
                        RequestHandle requestHandle=invokeWS(url, requestParams);
                        if (requestHandle!=null) {
                            Log.d(LOG_TAG,"Requested: "+url);
                        } else {
                            Toast.makeText(FairWindApplication.getInstance(), "Request failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(FairWindApplication.getInstance(), "Username and password not specified", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnWww=(Button)view.findViewById(R.id.btn_www);
        btnWww.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://fairwind.cloud";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        try {
            fairWindModel.getLicenseManager().check();
            btnCheckOut.setEnabled(false);
            editTextUserId.setEnabled(false);
            btnRelease.setEnabled(true);
        } catch (ValidationException e) {
            btnCheckOut.setEnabled(true);
            editTextUserId.setEnabled(true);
            btnRelease.setEnabled(false);
            Log.e(LOG_TAG,e.getMessage());
            Toast.makeText(FairWindApplication.getInstance(), "FairWind is not licenced!", Toast.LENGTH_LONG).show();
        }



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction();
    }

    /******************/

    class ResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Log.d(LOG_TAG,"onSuccess");
            for (Header header : headers) {
                Log.d(LOG_TAG, "header:" + header);
            }

            View view=getView();
            if (view!=null) {

                FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
                LicenseManager licenseManager = fairWindModel.getLicenseManager();
                String response = new String(responseBody);
                Json result = null;
                String status = "fail";
                String message = "FairWind received a bad response from the server";
                String tags = "";

                try {
                    result = Json.read(response);

                    status = result.asMap().get("status").toString();
                    message = result.asMap().get("message").toString();
                    tags = result.asMap().get("tag").toString();


                } catch (RuntimeException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }


                String text = message + " (" + status + ")";

                // Check if the status is ok or fail
                if (status != null && status.equals("ok")) {

                    Button btnCheckOut = (Button) view.findViewById(R.id.btn_checkout);
                    Button btnRelease = (Button) view.findViewById(R.id.btn_release);
                    EditText editTextUserId = (EditText) view.findViewById(R.id.editTextUserId);
                    EditText editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);

                    if (tags.equals("welcome")) {
                        String versionName = BuildConfig.VERSION_NAME;
                        String availableVersionName = result.asMap().get("apk").toString();
                        if (versionName.compareTo(availableVersionName) < 0) {
                            Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
                            btnUpdate.setEnabled(true);
                            text += ". Update your FairWind!";
                        } else {
                            text += ". FairWind is updated.";
                        }

                    } else if (tags.equals("checkout")) {
                        String licenseString = result.asMap().get("license").toString();
                        String userId = editTextUserId.getText().toString();
                        String password = editTextPassword.getText().toString();
                        licenseManager.setUserId(userId);

                        try {
                            licenseManager.setLicenseString(licenseString);
                            licenseManager.setPassword(password);

                            btnCheckOut.setEnabled(false);
                            editTextUserId.setEnabled(false);
                            btnRelease.setEnabled(true);
                        } catch (ValidationException e) {
                            text += ". Invalid license!";
                        }
                    } else if (tags.equals("release")) {
                        Log.d(LOG_TAG, "license -> null");
                        licenseManager.clean();
                        btnCheckOut.setEnabled(true);
                        editTextUserId.setEnabled(true);
                        btnRelease.setEnabled(false);
                    }
                } else {
                    text += ". Activation error!";
                }

                Toast.makeText(FairWindApplication.getInstance(), text, Toast.LENGTH_LONG).show();

                TextView textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
                if (textViewMessage != null) {
                    textViewMessage.setText(text);
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Log.d(LOG_TAG,"onFailure");
            if (headers!=null) {
                for (Header header : headers) {
                    Log.d(LOG_TAG, "header:" + header);
                }
            }

            View view=getView();
            if (view!=null) {

                // Hide Progress Dialog
                //prgDialog.hide();
                // When Http response code is '404'
                String text="";
                if(statusCode == 404){
                    text="Requested resource not found";

                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    text="Something went wrong at server end";

                }
                // When Http response code other than 404, 500
                else{
                    text= "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
                }

                Toast.makeText(FairWindApplication.getInstance(), text, Toast.LENGTH_LONG).show();


                TextView textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
                if (textViewMessage!=null) {
                    textViewMessage.setText(text);
                }
            }
        }
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public RequestHandle invokeWS(String stringURL, RequestParams params){
        // Show Progress Dialog
        //prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        RequestHandle requestHandle=null;
        AsyncHttpClient client = new AsyncHttpClient();
        if (params!=null) {
            requestHandle=client.post(stringURL, params, new ResponseHandler());
        } else {
            requestHandle=client.get(stringURL,new ResponseHandler());

        }
        return requestHandle;
    }
}
