package com.example.pedro.greateranglia.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pedro.greateranglia.sqlite.AdminSQLiteOpenHelper;
import com.example.pedro.greateranglia.objects.Alert;
import com.example.pedro.greateranglia.R;
import com.example.pedro.greateranglia.Volley.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class DetailAlert extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap map;
    SupportMapFragment fragmentMap;
    Alert alert;
    LatLng location;
    //LOCATION
    private LocationManager locationManager;
    private Criteria criteria;
    private String supplier;
    Location userLocation;
    JSONObject al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_alert);
        alert = (Alert) getIntent().getExtras().getSerializable("alert");
        TextView alarm = findViewById(R.id.starting);
        TextView days = findViewById(R.id.days);
        TextView forTrains = findViewById(R.id.forTrains);
        TextView leaving = findViewById(R.id.leaving);

        criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.req
        supplier = String.valueOf(locationManager.getBestProvider(criteria, true));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        userLocation = locationManager.getLastKnownLocation(supplier);


        fragmentMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragMap);
        fragmentMap.getMapAsync(this);


        try {
            al = new JSONObject(alert.getAlert());

            String starting = "Alert starting at " + al.getString("startAlert");
            JSONArray daysN = (JSONArray) al.get("days");
            System.out.println(daysN.toString());
            String setOn = "Set on: ";

            for (int j = 0; j < daysN.length(); j++) {
                System.out.println(setOn);
                if (daysN.getString(j).equals("1")) {
                    setOn += "MON ";
                } else if (daysN.getString(j).equals("2")) {
                    setOn += "TUE ";
                } else if (daysN.getString(j).equals("3")) {
                    setOn += "WED ";
                } else if (daysN.getString(j).equals("4")) {
                    setOn += "THU ";
                } else if (daysN.getString(j).equals("5")) {
                    setOn += "FRI ";
                } else if (daysN.getString(j).equals("6")) {
                    setOn += "SAT ";
                } else if (daysN.getString(j).equals("7")) {
                    setOn += " SUN";
                }
            }
            String trains = "From  " + al.getString("startName") + " To " + al.getString("endName");
            String time = "Leaving between " + al.getString("startJourney") + " and " + al.getString("endJourney");
            alarm.setText(starting);
            days.setText(setOn);
            forTrains.setText(trains);
            leaving.setText(time);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //METODOS DE GOOGLE MAPS

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


        location = new LatLng(alert.getLocation().get(0), alert.getLocation().get(1));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        Location distance = new Location("Station");
        distance.setLatitude(alert.getLocation().get(0));
        distance.setLongitude(alert.getLocation().get(1));

        if (userLocation == null) {

            System.out.println(" LOCALIZACION DE USARIO NULA");

        }

        String distan = String.format("%.2f", userLocation.distanceTo(distance) * 0.000621371) + " miles away";
        MarkerOptions mk = new MarkerOptions().title("Your Station").position(location).snippet(distan);
        map.addMarker(mk);

    }

    public void deleteAlert(View view) throws JSONException {

        //deviceID and alertID

        final String URL = "http://192.168.43.54:3000/removeAlertApp";
        //JSONObject jsonObject=al.getJSONObject("id");
        HashMap idAlert = new HashMap();
        idAlert.put("alertId", al.getString("id"));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(idAlert), new Response.Listener<JSONObject>() {


            public void onResponse(JSONObject response) {

                try {

                    //RECUPERO TODO EL ARCHVIO JSON
                    JSONObject json = new JSONObject(response.toString(0));
                    System.out.println(json.toString());
                    String message = json.getString("message");

                    if (message.equals("Alert deleted")) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);


                        Toast.makeText(getApplicationContext(), "Alert deleted", Toast.LENGTH_LONG).show();

                        AdminSQLiteOpenHelper admin=new AdminSQLiteOpenHelper(getApplicationContext(), "ga", null, 1);
                        SQLiteDatabase bd=admin.getWritableDatabase();
                        int cantidadElementosBorrados = bd.delete("alerts", "id like '" + alert.getId()+"'", null);
                        if(cantidadElementosBorrados==1){
                            System.out.println("BORRADO DE LA DATABASE COOOOOOORECTAMENTE");
                        }

                        bd.close();

                    } else {

                        Toast.makeText(getApplicationContext(), "Something went wrong deleting the alert, try again", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("----------------------------------- ERROR LOGIN   1 -----------------------------------");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("ERROR    ---------------------   ERROR LOGIN    2 --------------------");

            }

        }) {

            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }

        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);

    }

    /** Register for the updates when Activity is in foreground */
    @Override
    protected void onResume() {
        super.onResume();
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
        locationManager.requestLocationUpdates(supplier, 20000, 1, this);
    }

    /** Stop the updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    /**LOCATIONS METHODS OF LocationListener Interface*/

    @Override
    public void onLocationChanged(Location location) {

        System.out.println("+++++++ LOCATION CHANGE +++++++");

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


}
