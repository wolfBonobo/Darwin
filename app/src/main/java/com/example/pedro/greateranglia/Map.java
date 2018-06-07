package com.example.pedro.greateranglia;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pedro.greateranglia.objects.Station;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    SupportMapFragment fragmentMap;
    double lat;
    double lon;
    LatLng location;
    ArrayList<Station> stations;

    //LOCATION
    private LocationManager locationManager;
    private Criteria criteria;
    private String supplier;
    Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fragmentMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragMap);
        fragmentMap.getMapAsync(this);

        stations = (ArrayList<Station>) getIntent().getSerializableExtra("stations");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        supplier = String.valueOf(locationManager.getBestProvider(criteria, true));


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        userLocation = locationManager.getLastKnownLocation(supplier);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(googleMap.MAP_TYPE_NORMAL); //Establecemos tipo de mapa. Podemos poner tb MAP_TYPE_SATELLITE, o MAP_TYPE_HYBRID  o MAP_TYPE_NORMAL
        map.getUiSettings().setZoomControlsEnabled(true); //Para botones de zoom el el mapa
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);





        //Bury ST Edmunds location for Camera Map
        lat = 52.2392932;
        lon = 0.6836618;
        location = new LatLng(lat, lon);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8));
        // String info="Penzance Rail Station";


        for (Station st : stations) {
            LatLng loc = new LatLng(st.getLocation().get(0), st.getLocation().get(1));

            Location distance = new Location(st.getStation());
            distance.setLatitude(loc.latitude);
            distance.setLongitude(loc.longitude);


            String distan = String.format("%.2f", userLocation.distanceTo(distance) * 0.000621371) + " miles away";
            MarkerOptions mk = new MarkerOptions().title(st.getStation()).position(loc).title(st.getStation()).snippet(distan);
            map.addMarker(mk);

        }


    }
}
