package it.uniparthenope.fairwind.sdk.ui.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.uniparthenope.fairwind.sdk.R;


/**
 * Created by raffaelemontella on 01/06/2017.
 */

public class BoatUUIDPreference extends DialogPreference {

    public static final String LOG_TAG="BoatUUIDPreference";

    private static final String androidns="http://schemas.android.com/apk/res/android";

    private EditText mUUUIDa,mUUUIDb,mUUUIDc,mUUUIDd;
    private TextView mSplashText,mValueText;
    private Context mContext;

    private String mDialogMessage, mSuffix;
    private String mDefault,mValue;

    public BoatUUIDPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mDialogMessage = attrs.getAttributeValue(androidns,"dialogMessage");
        mSuffix = attrs.getAttributeValue(androidns,"text");
        mDefault = attrs.getAttributeValue(androidns,"defaultValue");
    }

    @Override
    protected View onCreateDialogView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.dialog_preference_boatuuid, null);


        mSplashText = (TextView) view.findViewById(R.id.txt_splash);
        mValueText = (TextView)view.findViewById(R.id.txt_value);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);

        mValueText.setTextSize(32);

        if (shouldPersist())
            mValue = getPersistedString(mDefault);


        return view;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

    }
    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            mValue = shouldPersist() ? getPersistedString(mDefault) : "";
        else
            mValue = (String)defaultValue;
    }







}
