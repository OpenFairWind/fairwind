package it.uniparthenope.fairwind.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import it.uniparthenope.fairwind.sdk.captain.setup.ui.Button;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Page;
import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screen;

/**
 * Created by raffaelemontella on 12/09/15.
 */
public class BarButton {

    public static final String LOG_TAG="BAR_BUTTON";

    private ButtonBarView buttonBarView;
    private ImageButton imageButton;
    private Drawable drawable;
    private String actionName;

    public void setButton(Button button) {

        actionName=button.getAction();
        String drawableName=button.getDrawableName();
        Log.d(LOG_TAG,"setButton: "+actionName+" ["+drawableName+"]");
        int drawableResourceId = buttonBarView.getResources().getIdentifier(drawableName, "drawable", buttonBarView.getContext().getPackageName());
        if (drawableResourceId!=0) {
            drawable = buttonBarView.getResources().getDrawable(drawableResourceId);
            ImageButton imageButton = this.imageButton;
            setImageButton(imageButton);
        }
    }

    public void setImageButton(ImageButton imageButton) {
        Log.d(LOG_TAG,"setImageButton");
        if (imageButton!=null) {
            this.imageButton = imageButton;
            if (drawable != null) {
                imageButton.setImageDrawable(drawable);
                Log.d(LOG_TAG, "Icon assigned");
            }
            if (actionName != null && !actionName.isEmpty()) {
                Log.d(LOG_TAG, "actionName:" + actionName);

                String actionCommand = actionName.split(":")[0];
                if (actionCommand.equals("fragment")) {
                    String className = actionName.split(":")[1];
                    try {
                        Class.forName(className);
                    } catch (ClassNotFoundException classNotFoundException) {
                        imageButton.setVisibility(View.GONE);
                    }
                } else if (actionCommand.equals("activity")) {
                    String className = actionName.split(":")[1];
                    try {
                        Class.forName(className);
                    } catch (ClassNotFoundException classNotFoundException) {
                        imageButton.setVisibility(View.GONE);
                    }
                } else if (actionCommand.equals("invoke")) {
                    Log.d(LOG_TAG, "invoke:");
                    int visibility=View.GONE;
                    String tmp=actionName.split(":")[1];
                    String[] parts=tmp.split("[.]");

                    String methodName = parts[parts.length-1];
                    String className = tmp.replace("."+methodName,"");

                    if (className.contains("maps")) {
                        Log.d(LOG_TAG,"Maps");
                    }

                    FairWindActivity fairWindActivity = (FairWindActivity)getActivity(buttonBarView.getContext());
                    try {
                        Class cls = Class.forName(className);
                        Class[] params = new Class[3];
                        params[0] = View.class;
                        params[1] = Fragment.class;
                        params[2] = FairWindActivity.class;

                        Method method = cls.getDeclaredMethod(methodName, params);
                        visibility=View.VISIBLE;
                    } catch (NoSuchMethodException ex1) {
                        Toast.makeText(fairWindActivity, ex1.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } catch (ClassNotFoundException ex2) {
                        Toast.makeText(fairWindActivity, ex2.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    imageButton.setVisibility(visibility);
                } /*else if(actionCommand.equals("intent")){
                String intentName = actionName.split(":")[1];

            }*/

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = buttonBarView.getContext();
                        if (context instanceof FairWindActivity) {

                            FairWindActivity fairWindActivity = (FairWindActivity) getActivity(buttonBarView.getContext());
                            if (fairWindActivity != null) {
                                Log.d(LOG_TAG, " Current Activity Name -> " + fairWindActivity.getClass().getName());
                                String actionCommand = actionName.split(":")[0];
                                if (actionCommand.equals("screen")) {
                                    String[] parts=actionName.split(":");
                                    UUID uuid = UUID.fromString(parts[1]);
                                    Screen screen=buttonBarView.getScreens().get(uuid);
                                    Page page=null;
                                    // Check if the page is specified as index
                                    if (parts.length==3) {
                                        page=screen.get(Integer.getInteger(parts[2]));
                                    }

                                    //Log.i(LOG_TAG, "Screen -> " + uuid + " : "+screen.getName()+" / "+page.getName());

                                    fairWindActivity.setCurrentScreenPage(screen,page);

                                } else if (actionCommand.equals("fragment")) {
                                    String className = actionName.split(":")[1];
                                    Log.i(LOG_TAG, "Fragment -> " + className);

                                    for(Screen screen:buttonBarView.getScreens().values()) {
                                        for (Page page:screen) {
                                            //Log.d(LOG_TAG,page.getClassName()+" ? "+className);
                                            if (page.getClassName().equals(className)==true) {
                                                Log.d(LOG_TAG,"Screen:"+screen.getName()+" / "+page.getName());
                                                fairWindActivity.setCurrentScreenPage(screen,page);
                                                break;
                                            }
                                        }
                                        if (fairWindActivity!=null) {
                                            break;
                                        }
                                    }

                                } else if (actionCommand.equals("activity")) {
                                    Log.i(LOG_TAG, "Activity -> " + actionName);

                                    String className = actionName.split(":")[1];
                                    if (!fairWindActivity.getClass().getName().equals(className)) {

                                        try {
                                            Intent intent=new Intent(fairWindActivity,Class.forName(className));
                                            fairWindActivity.startActivity(intent);

                                            Class backActivityClass = fairWindActivity.getBackActivityClass();
                                            if (!backActivityClass.getName().equals(fairWindActivity.getClass().getName())) {
                                                //Log.i(LOG_TAG, "Finishing -> " + activity.getClass().getName());
                                                //activity.finish();
                                            }
                                        } catch (ClassNotFoundException classNotFoundException) {
                                            Log.e(LOG_TAG, classNotFoundException.getMessage());
                                        }
                                    } else {
                                        Log.d(LOG_TAG, "The user selected the same current activity");
                                    }

                                } else if (actionCommand.equals("invoke")) {
                                    Log.i(LOG_TAG, "Method -> " + actionName);

                                    String tmp = actionName.split(":")[1];
                                    String[] parts = tmp.split("[.]");

                                    String methodName = parts[parts.length - 1];
                                    String className = tmp.replace("." + methodName, "");

                                    if (className.contains("maps")) {
                                        Log.d(LOG_TAG,"Maps");
                                    }

                                    try {
                                        Class cls = Class.forName(className);
                                        Class[] params = new Class[3];
                                        params[0] = View.class;
                                        params[1] = Fragment.class;
                                        params[2] = FairWindActivity.class;

                                        try {
                                            Method method = cls.getDeclaredMethod(methodName, params);

                                            Screen screen = buttonBarView.getScreens().getCurrent();
                                            Page page=screen.get(screen.getCurrentPage());
                                            Log.d(LOG_TAG,"Screen:"+screen.getName()+" / "+page.getName());

                                            Fragment fragment = page.getFragment();


                                            Log.i(LOG_TAG, "Class -> " + cls.getName());

                                            try {
                                                Object[] values= new Object[3];
                                                values[0]=view;
                                                values[1]=fragment;
                                                values[2]=fairWindActivity;
                                                Log.d(LOG_TAG, "pre method.invoke");
                                                method.invoke(null, values);
                                                Log.d(LOG_TAG, "post method.invoke)");
                                            } catch (IllegalAccessException e) {
                                                Log.e(LOG_TAG, e.getMessage());
                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                String msg = e.getMessage();
                                                if (msg == null || msg.isEmpty()) {
                                                    msg = "InvocationTargetException";
                                                }
                                                Log.e(LOG_TAG, msg);
                                                e.printStackTrace();
                                            }
                                        } catch (NoSuchMethodException e) {
                                            Log.e(LOG_TAG, e.getMessage());
                                            e.printStackTrace();
                                        }
                                    } catch (ClassNotFoundException e) {
                                        Log.e(LOG_TAG, e.getMessage());
                                        e.printStackTrace();
                                    }


                                } else if (actionCommand.equals("intent")) {
                                    Log.i(LOG_TAG, "Intent -> " + actionName);
                                    String intentAction = actionName.split(":")[1];
                                    Log.i(LOG_TAG, "Intent -> " + intentAction);
                                    buttonBarView.getContext().startActivity(new Intent(intentAction));
                                } else {
                                    Log.d(LOG_TAG, "Unknown action command -> " + actionCommand);
                                }
                            } else {
                                Log.d(LOG_TAG, "Invalid activity");
                            }
                        } else {
                            Log.d(LOG_TAG, "The context is not a FairWindActivity");
                        }
                    }
                });
            } else {
                imageButton.setVisibility(View.INVISIBLE);
            }
        } else {
            Log.d(LOG_TAG,"Imagebutton is null!");
        }
        Log.d(LOG_TAG,"/setImageButton");
    }

    public ImageButton getImageButton() { return  imageButton; }

    public BarButton(ButtonBarView buttonBarView, String actionName, Drawable drawable) {
        this.buttonBarView=buttonBarView;
        this.actionName=actionName;
        this.drawable=drawable;
    }

    private Activity getActivity(Context context) {
        while (context instanceof ContextWrapper ) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}

