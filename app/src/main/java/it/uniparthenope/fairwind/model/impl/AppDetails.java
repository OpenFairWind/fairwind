package it.uniparthenope.fairwind.model.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by raffaelemontella on 19/09/15.
 */
public class AppDetails extends ArrayList<AppDetail> {
    public static final int ALL=0;
    public static final int MARINE=1;
    public static final int BOAT=2;
    public static final int HOME=3;

    private Context context;


    public AppDetails() {

    }

    public Set<String> getSetList(){
        ArrayList<String> nameApps=new ArrayList<String>();
        for(int i=0;i<this.size();i++)
        nameApps.add(i,this.get(i).getPackageName().toString());
        Set<String> set=new HashSet<String>(nameApps);
        return set;
    }

    public AppDetails(Context context) {
        this.context=context;
        PackageManager manager = context.getPackageManager();
        AppDetail appDetail=null;

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        //try {
            //String fileName="/sdcard/nmea/apps.txt";
           // String fileName= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "nmea" + File.separator+"apps.txt";
           // FileWriter fileWriter = new FileWriter(fileName);
           // PrintWriter printWriter = new PrintWriter(fileWriter);


            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {

              //  printWriter.write(ri.activityInfo.packageName + ":0\n");

                if (context.getPackageName().equals(ri.activityInfo.packageName)) continue;

                appDetail = new AppDetail(
                        ri.loadLabel(manager),
                        ri.activityInfo.packageName,
                        ri.activityInfo.loadIcon(manager));

                add(appDetail);

            }

         //   printWriter.flush();
          //  printWriter.close();
          //  fileWriter.flush();
         //   fileWriter.close();
      //  } catch (IOException e ){
     //       Log.e("APPDETAILS",e.getMessage());
     //   }
    }
/*
    public AppDetails select(int type) {
        HashMap<CharSequence,Byte> apps= ((FairWindModelImpl)FairWindModelImpl.getInstance()).loadapps();
        AppDetails result=new AppDetails();
        byte mask= (byte) Math.pow(2,type-1);
        for(AppDetail appDetail:this) {
            byte appMask=0;
            if (apps.get(appDetail.getPackageName())!=null) {
                appMask=apps.get(appDetail.getPackageName());
            }
            if ( (appMask & mask)==mask ) {
                result.add(appDetail);
            }
        }
        return result;
    }
*/
    public AppDetails selectApp(int type){

        AppDetails result=new AppDetails();
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set=new HashSet<String>();

        if(type==HOME){
            set=sp.getStringSet("home_apps",new HashSet<String>());

        }else if(type==MARINE){
            set=sp.getStringSet("marine_apps",new HashSet<String>());
        }

            for (AppDetail appDetail : this) {
                if(type!=ALL) {
                    if(type!=BOAT) {
                        if (set.contains(appDetail.getPackageName().toString())) {
                            result.add(appDetail);
                        }
                    }else{
                        if(appDetail.getPackageName().toString().contains(".bapp")){
                            result.add(appDetail);
                        }
                    }
                }else{
                    result.add(appDetail);
                }
        }


        return result;
    }






}
