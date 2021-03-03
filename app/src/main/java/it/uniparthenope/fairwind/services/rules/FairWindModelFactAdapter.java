package it.uniparthenope.fairwind.services.rules;


import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.captain.AlarmDialog;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.KeyWithMetadata;
import it.uniparthenope.fairwind.sdk.ruleengine.Event;
import it.uniparthenope.fairwind.sdk.ruleengine.FactAdapter;
import it.uniparthenope.fairwind.sdk.util.Units;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import xdroid.core.Global;

/**
 * Created by raffaelemontella on 23/01/2017.
 */

public class FairWindModelFactAdapter implements FactAdapter {
    public static final String LOG_TAG="...FactAdapter";


    private Units units;


    public FairWindModelFactAdapter() {
        FairWindModel fairWindModel=FairWindApplication.getFairWindModel();
        try {
            String jsonAsString= Utils.readTextFromResource(Global.getContext().getResources(), it.uniparthenope.fairwind.sdk.R.raw.units);
            Json jsonUnits=Json.read(jsonAsString);
            units=new Units(jsonUnits);
        } catch (IOException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    public Json getRulesConfigAsJson() {
        Log.d(LOG_TAG,"getRulesConfigAsJson");
        FairWindModel fairWindModel=FairWindApplication.getFairWindModel();
        Json result=null;
        String jsonAsString=fairWindModel.getPreferences().getConfigProperty(Constants.PREF_KEY_RULES_CONFIG_RULES, null);
        if (jsonAsString==null || jsonAsString.isEmpty()==true) {
            try {
                jsonAsString= Utils.readTextFromResource(Global.getContext().getResources(), it.uniparthenope.fairwind.sdk.R.raw.default_rulepreferences);
                fairWindModel.getPreferences().setConfigProperty(Constants.PREF_KEY_RULES_CONFIG_RULES, jsonAsString);
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        }
        result=Json.read(jsonAsString);
        return result;
    }

    @Override
    public String onGetFact(String key) {
        Log.d(LOG_TAG,"onGetFact");
        String result=null;
        FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();
        key=key.replace("/self/","/"+ SignalKConstants.self+"/").replace("/",".");
        if (key.startsWith(".")) {
            key=key.substring(1,key.length());
        }
        //String path= FairWindModelBase.getNavPositionPath(SignalKConstants.self);
        try {
            result = (String) fairWindModel.get(key);
        } catch (ClassCastException ex1) {
            Log.e(LOG_TAG,ex1.getMessage());
            result = Double.toString((Double)fairWindModel.get(key));
        }
        return result;
    }

    @Override
    public void onEvents(Vector<Event> events) {
        Log.d(LOG_TAG,"onEvents");
        FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();
        for(Event event:events) {
            if (event.getType().equals("popup")) {
                long span=event.getParamAsLong("span");
                long timeout=event.getParamAsLong("timeout");
                String title=event.getParamAsString("title");
                String message=event.getParamAsString("message");
                AlarmDialog.showAlert(FairWindApplication.getInstance(),event.getUuid(),span,timeout,title,message,null,null);
            }
        }

    }

    @Override
    public double onGetUnitsConvertedValue(double value, String key, String srcUnits) {
        HashMap<String,KeyWithMetadata> keysWithMetadata=Utils.getFairWindModel().getKeysWithMetadata();
        double result=value;
        // replace the uuid field
        String[] parts=key.split("/");
        int nParts=parts.length;
        if (parts[1].equalsIgnoreCase("vessels")) {
            parts[2]="*";
        }
        if (parts[nParts-1].equalsIgnoreCase("value")) {
            nParts--;
        }
        key="/";
        for (int i=1;i<nParts;i++) {
            key+=parts[i]+"/";
        }
        key=key.substring(0,key.length()-1);
        KeyWithMetadata keyWithMetadata=keysWithMetadata.get(key);
        if (keyWithMetadata!=null) {
            String dstUnits=keyWithMetadata.getUnits();
            if (dstUnits!=null) {
                result= units.getUnitConvertedValue(value, srcUnits,dstUnits);
            }
        }
        return result;
    }
}
