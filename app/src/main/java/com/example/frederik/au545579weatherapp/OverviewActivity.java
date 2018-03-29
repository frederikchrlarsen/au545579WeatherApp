package com.example.frederik.au545579weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.frederik.au545579weatherapp.services.WeatherDataService;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Button btnTest  = findViewById(R.id.btnTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backgroundServiceIntent = new Intent(OverviewActivity.this, WeatherDataService.class);
                startService(backgroundServiceIntent);
            }
        });
    }
}
