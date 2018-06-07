package com.example.pedro.greateranglia.activities;
import com.example.pedro.greateranglia.asyncs.loadStationSQLite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.content.Intent;

import com.example.pedro.greateranglia.R;


public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loadStationSQLite load=new loadStationSQLite(getApplicationContext());
        load.execute();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Lanzaremos despues de 3000ms(3s), el MainActivity
                Intent intent = new Intent(SplashScreenActivity.this, Login.class);
                startActivity(intent);
                finish();

            }
        }, 3000);
    }
}
