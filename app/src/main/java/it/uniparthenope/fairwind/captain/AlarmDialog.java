package it.uniparthenope.fairwind.captain;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import it.uniparthenope.fairwind.FairWindAlertInteraction;
import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;

/**
 * Created by raffaelemontella on 17/10/2017.
 */

public class AlarmDialog {
    private static final String LOG_TAG="ALERT_DIALOG";

    private static TextToSpeech t1;
    static {
        t1 =new TextToSpeech(FairWindApplication.getInstance().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    private static HashMap<UUID,Long> lastMillisMap=new HashMap<UUID,Long>();
    public  static void showAlert(final Context context, final UUID uuid, long span, final long timeout, final String title, final String message, final String check, final FairWindAlertInteraction fairWindAlertInteraction) {
        long millis=System.currentTimeMillis();
        Long lastMillis=lastMillisMap.get(uuid);
        if (lastMillis==null || millis-lastMillis>span || span<0) {


            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    //Context context=getInstance().getApplicationContext();
                    View view = View.inflate(context, R.layout.dialog_alert, null);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            // Save to shared preferences
                            FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();
                            fairWindModel.getPreferences().setConfigPropertyBoolean("alertdialogs."+uuid,isChecked);
                        }
                    });
                    if (check!=null && !check.isEmpty()) {
                        checkBox.setText(check);
                    } else {
                        checkBox.setVisibility(View.INVISIBLE);
                    }


                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
                    alertDialog.setTitle(title);
                    alertDialog.setMessage(message);
                    alertDialog.setView(view);
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (fairWindAlertInteraction!=null) {
                                        fairWindAlertInteraction.onAlertClose();
                                    }
                                }
                            });
                    //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                    // Hide after some seconds
                    final Handler handler  = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        }
                    };
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (handler!=null) {
                                handler.removeCallbacks(runnable);
                            }
                        }
                    });
                    try {
                        alertDialog.show();
                    } catch (WindowManager.BadTokenException ex) {
                        Log.e(LOG_TAG,ex.getMessage());
                    }

                    if (timeout>0) {
                        handler.postDelayed(runnable, timeout);
                        lastMillisMap.put(uuid, System.currentTimeMillis());
                    }
                }
            });


        }
    }
    public static void sayText(final UUID uuid, int span, final String messageToSpeak) {
        long millis=System.currentTimeMillis();
        Long lastMillis=lastMillisMap.get(uuid);
        if (lastMillis==null || millis-lastMillis>span) {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    t1.speak(messageToSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    lastMillisMap.put(uuid,System.currentTimeMillis());
                }
            });
        }
    }
}
