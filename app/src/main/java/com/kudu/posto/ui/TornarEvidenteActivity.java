package com.kudu.posto.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kudu.posto.R;

public class TornarEvidenteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tornar_evidente);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}