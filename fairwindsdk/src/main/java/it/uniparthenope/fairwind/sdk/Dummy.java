package it.uniparthenope.fairwind.sdk;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;




import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.ui.FairWindActivity;
import it.uniparthenope.fairwind.sdk.ui.GetFairWindActivity;

/**
 * Created by raffaelemontella on 20/09/15.
 */
public class Dummy implements Runnable, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LIST_ID = 1;
    public static final int REFRESH_MILLS=1000;
    public static final int WATCHDOG=10;

    private Handler handler;
    private FairWindActivity activity;
    private int watchDog;

    public Dummy(FairWindActivity activity) {
        this.activity=activity;
        watchDog=Dummy.WATCHDOG;
        handler = new Handler();
        LoaderManager loaderManager=activity.getLoaderManager();
        loaderManager.initLoader(LIST_ID, null, this);
        handler.postDelayed(this, REFRESH_MILLS);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String clientChallenge="xxxx";
        CursorLoader loader = new CursorLoader(
                activity,
                Uri.parse(FairWindModel.SignalK.CONTENT_URI+""), // + "/dummy/"+clientChallenge),
                null,
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {

            int idIndex = data.getColumnIndex(FairWindModel.SignalK._ID);
            int itemIndex = data.getColumnIndex(FairWindModel.SignalK.ITEM);

            data.moveToFirst();
            do {
                if (data.getString(idIndex).equals("dummy")) {
                    String serverChallenge=data.getString(itemIndex);
                    String clientChallenge="xxxx";
                    if (clientChallenge.equals(serverChallenge)) {
                        watchDog = Dummy.WATCHDOG;
                    }
                }
            } while(data.moveToNext());

        } else {
            watchDog--;
        }

        if (watchDog==0) {
            //Intent intent=new Intent(activity,GetFairWindActivity.class);
            //activity.startActivity(intent);
            //activity.finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void run() {
        // Restart the loader
        activity.getLoaderManager().restartLoader(LIST_ID, null, this);

        // Set the next call
        handler.postDelayed(this, REFRESH_MILLS);

    }
}
