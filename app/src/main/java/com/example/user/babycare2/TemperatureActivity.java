package com.example.user.babycare2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shinelw.library.ColorArcProgressBar;


public class TemperatureActivity extends AppCompatActivity {
    ColorArcProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        progressbar = (ColorArcProgressBar) findViewById(R.id.bar1);
        progressbar.setCurrentValues(38);
    }
}
