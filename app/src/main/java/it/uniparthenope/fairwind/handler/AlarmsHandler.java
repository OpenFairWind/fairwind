package it.uniparthenope.fairwind.handler;

import android.util.Log;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class AlarmsHandler extends HandlerBase {

    private static final String LOG_TAG="ALARMS_HANDLER";

    public AlarmsHandler() {

    }

    /**
     * Scans the signal k model for alarm conditions and sets/unsets alarms
     * Only looks at vessels.self
     * @param signalkModel
     */
    public  void handle(SignalKModel signalkModel) {
        try {
            for(String key : signalkModel.getKeys()){
                //we check if there is a key.meta key
                //only check SignalKConstants.self
                if(!key.startsWith(SignalKConstants.vessels_dot_self_dot))continue;
                //remove .value
                key=key.replace(SignalKConstants.dot+SignalKConstants.value,"");
                String metaZonesKey=key+SignalKConstants.dot+SignalKConstants.meta+SignalKConstants.dot+SignalKConstants.zones;
                Object metaZones = signalkModel.get(metaZonesKey);
                if(metaZones!=null) {
                    if (metaZones instanceof Json && ((Json) metaZones).isArray()) {
                        //if zones is empty clear the alarms
                        //String alarmKey = vessels_dot_self_dot+alarms+dot+key.substring(vessels_dot_self_dot.length());
                        //zones object

                        //AlarmManager alarmManager = new AlarmManager((Json) metaZones);

                        String[] alarmStates={"emergency",SignalKConstants.alarm,SignalKConstants.warn,"alert",SignalKConstants.normal};

                        if (((Json) metaZones).asJsonList().size() == 0) {
                            //clear all alarms.
                            setAlarm(signalkModel, key, SignalKConstants.normal, null);
                        }
                        Number val = (Number) signalkModel.getValue(key);
                        String msg="";
                        for (String alarmState:alarmStates) {
                            Double value = val.doubleValue();
                            for(Json zone : ((Json)metaZones).asJsonList()){
                                if(alarmState.equals(zone.at("state").asString())){
                                    double low = zone.at("lower").asDouble();
                                    double high = zone.at("upper").asDouble();
                                    if(Math.min(low, value)==low && Math.max(high, value)==high){
                                        //its between
                                        msg = (String)zone.at("message").asString();
                                        if (msg==null || msg.isEmpty()) {
                                            msg = (String) signalkModel.get(key + SignalKConstants.dot + SignalKConstants.meta + SignalKConstants.dot + alarmState + "Message");
                                        }
                                        setAlarm(signalkModel, key, alarmState, msg);
                                    }

                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    public void setAlarm(SignalKModel signalkModel,String key, String value, String msg) {
        int pos = key.indexOf(SignalKConstants.dot);
        pos=key.indexOf(SignalKConstants.dot,pos+1)+1;
        String alarmKey = key.substring(0,pos)+SignalKConstants.notifications+SignalKConstants.dot+key.substring(pos);
        Object obj = signalkModel.get(alarmKey);
        if(obj!=null && obj instanceof Json){
            Json json = ((Json)obj).at(SignalKConstants.alarmState);
            if(json!=null && value.equals(json.asString())){
                //already set return
                Log.d(LOG_TAG,"Alarm already set for:"+key);
                return;
            }
        }
        signalkModel.getFullData().put(alarmKey+SignalKConstants.dot+SignalKConstants.alarmState, value);
        if (msg==null)msg="";
        signalkModel.getFullData().put(alarmKey+SignalKConstants.dot+SignalKConstants.message, msg);
        signalkModel.getEventBus().post(new PathEvent(alarmKey, 1, PathEvent.EventType.ADD));
        Log.d(LOG_TAG,"Event:"+alarmKey);
    }
}
