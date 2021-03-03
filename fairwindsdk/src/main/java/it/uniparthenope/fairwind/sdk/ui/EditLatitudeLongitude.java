package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 27/03/2017.
 */

public class EditLatitudeLongitude extends LinearLayout {



    private EditText editText_ddmmss_lat_dd;
    private EditText editText_ddmmss_lat_mm;
    private EditText editText_ddmmss_lat_ss;

    private Spinner spinner_lat_southnorth;

    private EditText editText_ddmmss_lon_ddd;
    private EditText editText_ddmmss_lon_mm;
    private EditText editText_ddmmss_lon_ss;

    private Spinner spinner_lon_westeast;

    public EditLatitudeLongitude(Context context) {
        super(context);
    }

    public EditLatitudeLongitude(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public EditLatitudeLongitude(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    /**
            * Inflates the views in the layout.
            *
            * @param context
    *           the current context for the view.
            */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_latitudelongitudeedit_horizontal, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editText_ddmmss_lat_dd=(EditText)this.findViewById(R.id.editText_lat_degs);
        editText_ddmmss_lat_mm=(EditText)this.findViewById(R.id.editText_lat_mins);
        editText_ddmmss_lat_ss=(EditText)this.findViewById(R.id.editText_lat_secs);
        spinner_lat_southnorth=(Spinner)this.findViewById(R.id.spinner_southnorth);

        editText_ddmmss_lon_ddd=(EditText)this.findViewById(R.id.editText_lon_degs);
        editText_ddmmss_lon_mm=(EditText)this.findViewById(R.id.editText_lon_mins);
        editText_ddmmss_lon_ss=(EditText)this.findViewById(R.id.editText_lon_secs);
        spinner_lon_westeast=(Spinner)this.findViewById(R.id.spinner_westeast);
    }

    public void setPosition(Position position) {
        int dd,mm,ss;
        double mmmm;
        Double latitude=position.getLatitude();

        if (latitude<0) {
            spinner_lat_southnorth.setSelection(0);
        } else {
            spinner_lat_southnorth.setSelection(1);
        }

        dd=latitude.intValue();
        mmmm=(latitude-dd)*60.0;
        mm=(int)mmmm;
        ss=(int)((mmmm-mm)*60);
        editText_ddmmss_lat_dd.setText(Integer.toString(dd));
        editText_ddmmss_lat_mm.setText(Integer.toString(mm));
        editText_ddmmss_lat_ss.setText(Integer.toString(ss));

        Double longitude=position.getLongitude();

        if (longitude<0) {
            spinner_lon_westeast.setSelection(0);
        } else {
            spinner_lon_westeast.setSelection(1);
        }

        dd=longitude.intValue();
        mmmm=(longitude-dd)*60.0;
        mm=(int)mmmm;
        ss=(int)((mmmm-mm)*60);
        editText_ddmmss_lon_ddd.setText(Integer.toString(dd));
        editText_ddmmss_lon_mm.setText(Integer.toString(mm));
        editText_ddmmss_lon_ss.setText(Integer.toString(ss));

    }


    public Position getPosition() {
        double dd,mm,ss;
        dd=Integer.parseInt(editText_ddmmss_lat_dd.getText().toString());
        mm=Integer.parseInt(editText_ddmmss_lat_mm.getText().toString());
        ss=Integer.parseInt(editText_ddmmss_lat_ss.getText().toString());
        double latitude=dd+mm/60.0+ss/3600.0;

        if (spinner_lat_southnorth.getSelectedItemPosition()==0) {
            latitude=-latitude;
        }

        dd=Integer.parseInt(editText_ddmmss_lon_ddd.getText().toString());
        mm=Integer.parseInt(editText_ddmmss_lon_mm.getText().toString());
        ss=Integer.parseInt(editText_ddmmss_lon_ss.getText().toString());
        double longitude=dd+mm/60.0+ss/3600.0;

        if (spinner_lon_westeast.getSelectedItemPosition()==0) {
            longitude=-longitude;
        }

        return new Position(latitude,longitude,0); }
}
