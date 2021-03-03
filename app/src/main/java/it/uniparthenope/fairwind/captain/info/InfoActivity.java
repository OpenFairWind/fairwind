package it.uniparthenope.fairwind.captain.info;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import it.uniparthenope.fairwind.BuildConfig;
import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.HomeActivity;

public class InfoActivity extends AppCompatActivity {
    private static final String LOG_TAG = "INFO_ACTIVITY";
    //private static final FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FairWindModelImpl fairWindModel = (FairWindModelImpl) (FairWindModelImpl.getInstance());
        //if (FairWindApplication.getFairWindModel().isKeepScreenOn()) {
        //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //}
        setContentView(R.layout.activity_info);
        String version = BuildConfig.VERSION_NAME;
        TextView tvVersion = (TextView)findViewById(R.id.textView_info_version);
        tvVersion.setText(version);

        TextView tvLinks = (TextView) findViewById(R.id.textLinks);
        tvLinks.setMovementMethod(LinkMovementMethod.getInstance());

        if (getIntent().getBooleanExtra("quitOnOk",false)==true) {
            Button btnOk=(Button)findViewById(R.id.btn_ok);
            btnOk.setText("Quit FairWind");
        }

    }

    public void btn_ok_onClick(View view) {
        if (getIntent().getBooleanExtra("quitOnOk",false)==false) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();

    }
}
