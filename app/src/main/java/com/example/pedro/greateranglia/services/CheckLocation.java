package com.example.pedro.greateranglia.services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import java.util.List;
import com.example.pedro.greateranglia.objects.PointF;

/**
 * Created by Pedro on 23/04/2018.
 */

public class CheckLocation extends Thread implements LocationListener {

    private LocationManager locationManager;
    private Criteria criteria;
    private String supplier;
    Location userLocation;
    Context context;

    public CheckLocation(Context context) {
        this.context = context;

        criteria = new Criteria();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        supplier = String.valueOf(locationManager.getBestProvider(criteria, true));

    }

    public void run() {


        while (!interrupted()) {

            //  System.out.println("probando hilo");

            userLocation = getLastKnownLocation();

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("User Latitud " + userLocation.getLatitude());
            System.out.println("User Longitud " + userLocation.getLongitude());
            System.out.println("User Altitude " + userLocation.getAltitude());
            System.out.println("User Accuracy " + userLocation.getAccuracy());
            System.out.println("User Provider " + userLocation.getProvider());
            System.out.println("User Speed " + userLocation.getSpeed());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

            PointF actualLocations= new PointF( userLocation.getLatitude(),userLocation.getLongitude());



            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point
     *           Point of origin
     * @param range
     *           Range in meters
     * @param bearing
     *           Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     *
     * x -> latitude
     * y -> longitude
     *
     */
    public  PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.getX());
        double lonA = Math.toRadians(point.getY());
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }

    public  boolean pointIsInCircle(PointF pointForCheck, PointF center,
                                          double radius) {
        if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius)
            return true;
        else
            return false;
    }

    public  double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.getX() - p1.getX());
        double dLon = Math.toRadians(p2.getY() - p1.getY());
        double lat1 = Math.toRadians(p1.getX());
        double lat2 = Math.toRadians(p2.getX());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }
}
