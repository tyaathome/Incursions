package com.tyaathome.incursions.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tyaathome.incursions.R;

/**
 * Created by tyaathome on 2018/12/18.
 */
public class LoadingActivity extends AppCompatActivity {

    private static final long delayMillis = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoMainActivity();
            }
        }, delayMillis);
    }

    private void gotoMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
