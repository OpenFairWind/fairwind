package it.uniparthenope.fairwind.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

public class FairWindContentProvider extends ContentProvider {

    private static final String LOG_TAG = "CONTENT_PROVIDER";

    private final static UriMatcher URI_MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
    private final static int FAIRWIND_SIGNALK_DIR_INDICATOR=5;
    private final static int FAIRWIND_SIGNALK_ITEM_INDICATOR=6;

    static {
        URI_MATCHER.addURI(FairWindModel.AUTHORITY,FairWindModel.SignalK.PATH, FAIRWIND_SIGNALK_DIR_INDICATOR);
        URI_MATCHER.addURI(FairWindModel.AUTHORITY,FairWindModel.SignalK.PATH+"/*",FAIRWIND_SIGNALK_ITEM_INDICATOR);
    }

    //private FairWindModel fairWindModel;


    public FairWindContentProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (URI_MATCHER.match(uri)) {

            case FAIRWIND_SIGNALK_DIR_INDICATOR:
                return FairWindModel.SignalK.MIME_TYPE_DIR;
            case FAIRWIND_SIGNALK_ITEM_INDICATOR:
                return FairWindModel.SignalK.MIME_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("The Uri "+uri+" is unknown for this Content Provider");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        //fairWindModel=LookoutService.getFairWindModel();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Log.d(LOG_TAG,"query");
        final int uriMatcherCode=URI_MATCHER.match(uri);
        Cursor cursor=null;
        String itemId=null;


        switch (uriMatcherCode) {

            case FAIRWIND_SIGNALK_DIR_INDICATOR:
                Log.d(LOG_TAG,"querySignalK()");
                cursor = FairWindApplication.getFairWindModel().querySignalK(null);
                break;
            case FAIRWIND_SIGNALK_ITEM_INDICATOR:
                itemId = uri.getPathSegments().get(1);
                Log.d(LOG_TAG,"querySignalK("+itemId+")");
                cursor = FairWindApplication.getFairWindModel().querySignalK(itemId);
                break;
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), FairWindModel.SignalK.CONTENT_URI);
        }

        Log.d(LOG_TAG,"query result="+cursor);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return 0;
    }

    Cursor computeDummy(String clientChallenge) {
        Cursor cursor=null;
        if (clientChallenge!=null & !clientChallenge.isEmpty()) {
            String serverChallenge="xxxx";
            if (serverChallenge.equals(clientChallenge)) {
                String[] columns = new String[] { FairWindModel.SignalK._ID, FairWindModel.SignalK.ITEM};
                MatrixCursor matrixCursor= new MatrixCursor(columns);
                matrixCursor.addRow(new Object[]{"dummy", "plug"});
                cursor=matrixCursor;
            }
        }
        return cursor;
    }
}
