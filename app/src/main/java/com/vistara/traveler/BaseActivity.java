package com.vistara.traveler;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Sharad on 26-06-2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract void initData();
    protected abstract int initResource();
    protected abstract void initComponent();
    TextView versionNumber;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(initResource());

        initData();
        initComponent();

        int splashId = initResource();
        if(splashId == R.layout.splash_screen)
        {
            versionNumber = findViewById(R.id.versionNumber);
            try
            {
                PackageInfo pInfo = BaseActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                versionNumber.setText("Version : "+version);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }

    }

    public Toolbar setupToolbar(){
        try{
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }finally {
            return mToolbar;
        }
    }

}
