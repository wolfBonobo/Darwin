package com.example.pedro.greateranglia.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.pedro.greateranglia.services.CheckLocation;

/**
 * Created by Pedro on 23/04/2018.
 */

public class ServiceLocation extends Service {

    CheckLocation h1;

    @Override
    public void onCreate() {
        /** Servicio creado... */

        System.out.println("Servicio creado...  +++++++++++++++++++++++++++++++++++++++++++++");

         h1=new CheckLocation(getApplicationContext());
         h1.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**  Servicio iniciado...  */

        System.out.println("Servicio iniciado...  +++++++++++++++++++++++++++++++++++++++++++++");
        if(!h1.isAlive()){

             h1=new CheckLocation(getApplicationContext());
             h1.start();
        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
       // Log.d(TAG, "Servicio destruido...");
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
