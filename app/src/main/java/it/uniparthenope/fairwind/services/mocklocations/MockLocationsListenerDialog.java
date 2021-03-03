package it.uniparthenope.fairwind.services.mocklocations;

import android.content.Context;
import android.view.View;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 30/07/16.
 */
public class MockLocationsListenerDialog extends DataListenerPreferencesDialog {

    @Override
    public int getLayoutId() {
        return R.layout.dialog_mocklocationslistener;
    }

    @Override
    public void onInit() {
        MockLocationsListenerPreferences mockLocationsListenerPreferences=(MockLocationsListenerPreferences)dataListenerPreferences;


    }

    @Override
    public void onFinish() {
        MockLocationsListenerPreferences mockLocationsListenerPreferences=(MockLocationsListenerPreferences)dataListenerPreferences;
    }
}
