package com.example.pedro.greateranglia.activities;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pedro.greateranglia.sqlite.AdminSQLiteOpenHelper;
import com.example.pedro.greateranglia.objects.Alert;
import com.example.pedro.greateranglia.asyncs.InsertAlert;
import com.example.pedro.greateranglia.R;
import com.example.pedro.greateranglia.services.ServiceLocation;
import com.example.pedro.greateranglia.Volley.VolleySingleton;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //VAR
    private static final String URL = "http://192.168.43.54:3000/stations";
    // private static final String URL="http://10.85.116.205:3000/stations";
    // private static final String URL = "http://192.168.1.179:3000/stations";


    Spinner spOringin, spdestination, spHour, spMinute, spMinuteD, spHourD, spMinuteF, spHourF;
    Button btn;
    ArrayList<String> departure;
    ArrayList<String> destination;
    ArrayList<String> tiplocs;
    ArrayList<String> hours, minutes;
    AdminSQLiteOpenHelper admin;
    SQLiteDatabase bd;
    Map alert;

    CheckBox ch1, ch2, ch3, ch4, ch5, ch6, ch7;
    ArrayList<CheckBox> checksboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startService(new Intent(this,ServiceLocation.class));

        spOringin = findViewById(R.id.origin);
        spdestination = findViewById(R.id.destination);
        spHour = findViewById(R.id.hour);
        spMinute = findViewById(R.id.minute);
        spMinuteD = findViewById(R.id.minuteD);
        spHourD = findViewById(R.id.hourD);
        spMinuteF = findViewById(R.id.minuteF);
        spHourF = findViewById(R.id.hourF);
        btn = findViewById(R.id.btn);

        checksboxes = new ArrayList();
        ch1 = findViewById(R.id.ch1);
        checksboxes.add(ch1);
        ch2 = findViewById(R.id.ch2);
        checksboxes.add(ch2);
        ch3 = findViewById(R.id.ch3);
        checksboxes.add(ch3);
        ch4 = findViewById(R.id.ch4);
        checksboxes.add(ch4);
        ch5 = findViewById(R.id.ch5);
        checksboxes.add(ch5);
        ch6 = findViewById(R.id.ch6);
        checksboxes.add(ch6);
        ch7 = findViewById(R.id.ch7);
        checksboxes.add(ch7);


        departure = new ArrayList();
        destination = new ArrayList();
        hours = new ArrayList();
        minutes = new ArrayList();
        tiplocs = new ArrayList();

        for (int i = 0; i < 24; i++) {
            if (i < 10)
                hours.add("0" + i);
            else
                hours.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10)
                minutes.add("0" + i);
            else
                minutes.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterHours = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, hours);
        spHour.setAdapter(adapterHours);
        spHour.setSelection(9);
        spHourD.setAdapter(adapterHours);
        spHourD.setSelection(9);
        spHourF.setAdapter(adapterHours);
        spHourF.setSelection(17);

        ArrayAdapter<String> adapterMinutes = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, minutes);
        spMinute.setAdapter(adapterMinutes);
        spMinuteD.setAdapter(adapterMinutes);
        spMinuteF.setAdapter(adapterMinutes);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {

                try {

                    //RECUPERO TODO EL ARCHVIO JSON
                    JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));
                  /*  System.out.println("----------------------------------------------------------------------");
                    System.out.println(jsonArrayPrincipal.toString());*/

                  departure.add("London Liverpool Street Rail Station [LST]");
                  destination.add("London Liverpool Street Rail Station [LST]");

                    for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                        JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                        String tiploc = unidad.getString("locname") + "  [" + unidad.getString("crs") + "] ";
                        departure.add(tiploc);
                        destination.add(tiploc);
                        tiplocs.add(unidad.getString("crs"));

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, destination);
                    spOringin.setAdapter(adapter);
                    spdestination.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("REQUEST ERROR");

            }

        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

    }

    public void createAlert(View view) throws JSONException {

        //checkBoxes
        String days = "";
        boolean selectedDay = false;
        for (int i = 0; i < checksboxes.size(); i++) {
            if (checksboxes.get(i).isChecked()) {
                days += i + 1 + "-";
                selectedDay = true;
            }
        }

        if (selectedDay) {


            String[] daysA = days.split("-");
            JSONArray daysJ = new JSONArray(daysA);

            // from to
            String from = spOringin.getSelectedItem().toString();
            String to = spdestination.getSelectedItem().toString();

            if (!from.equals(to)) {

                //starAlert
                String starAlert = spHour.getSelectedItem().toString() + ":" + spMinute.getSelectedItem();
                //System.out.println(starAlert);
                // startJourney
                String startJourney = spHourD.getSelectedItem().toString() + ":" + spMinuteD.getSelectedItem();
                //System.out.println(startJourney);
                // endJourney
                String endJourney = spHourF.getSelectedItem().toString() + ":" + spMinuteF.getSelectedItem();
                //System.out.println(endJourney);


                alert = new HashMap();
                alert.put("deviceid", FirebaseInstanceId.getInstance().getToken());
                alert.put("days", daysJ);
                alert.put("startAlert", starAlert);
                alert.put("startAlert", starAlert);
                alert.put("startJourney", startJourney);
                alert.put("endJourney", endJourney);
                alert.put("from", from);
                alert.put("to", to);

                final String URL = "http://192.168.43.54:3000/alertapp";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(alert), new Response.Listener<JSONObject>() {


                    public void onResponse(JSONObject response) {

                        try {

                            //RECUPERO TODO EL ARCHVIO JSON
                            JSONObject json = new JSONObject(response.toString(0));
                            System.out.println(json.toString());
                            String message = json.getString("message");

                            if (message.equals("Alert created")) {

                                JSONObject aler = new JSONObject(alert);
                                InsertAlert insert=new InsertAlert(getApplicationContext());
                                insert.execute();

                                Toast.makeText(getApplicationContext(), "Alert created", Toast.LENGTH_LONG).show();

                            } else {

                                Toast.makeText(getApplicationContext(), "Something went wrong creating the alert, try again", Toast.LENGTH_LONG).show();
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
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("User-agent", "My useragent");
                        return headers;
                    }

                };

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);

            } else {
                Toast.makeText(getApplicationContext(), "Choose diferent destination", Toast.LENGTH_LONG).show();
            }

            //__
        } else {
            Toast.makeText(getApplicationContext(), "Choose a day of the week", Toast.LENGTH_LONG).show();

        }

    }


    public void myAlerts(View view) {

        // ArrayList<Alert> alerts = new ArrayList();

        //******************  SQLite version  ************************  DEPRECATED

       /* admin = new AdminSQLiteOpenHelper(getApplicationContext(), "ga", null, 1);
        bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("SELECT *  from alerts;", null);
        while (fila.moveToNext()) {
            Alert a = new Alert();
            a.setId(fila.getInt(0));
            a.setAlert(fila.getString(1));
            alerts.add(a);

        }*/
        //****************************************************

        //******************  server  version  ************************

        JSONArray deviceid = new JSONArray();
        deviceid.put(FirebaseInstanceId.getInstance().getToken());
        final String URLS = "http://192.168.43.54:3000/getAlertsApp";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URLS, deviceid, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {
                try {

                    ArrayList<Alert> alerts = new ArrayList();
                    //RECUPERO TODO EL ARCHVIO JSON
                    JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));

                    for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                        JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                        System.out.println(unidad);
                        Alert a = new Alert();
                        a.setId(unidad.getString("id"));
                        a.setAlert(unidad.toString());

                        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        System.out.println(a.getId());
                        alerts.add(a);

                    }
                    Intent intent = new Intent(getApplicationContext(), MyAlerts.class);
                    intent.putExtra("alerts", alerts);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("REQUEST ERROR");

            }

        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);


        //****************************************************


    }

/**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemStation:

                JSONArray stations = new JSONArray();
                admin = new AdminSQLiteOpenHelper(getApplicationContext(), "ga", null, 1);
                bd = admin.getWritableDatabase();
                Cursor fila = bd.rawQuery("SELECT *  from alerts;", null);
                while (fila.moveToNext()) {

                    //**++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                    try {
                        JSONObject al = new JSONObject(fila.getString(1));
                        System.out.println(fila.getString(0));
                        System.out.println(fila.getString(1));
                        System.out.println(fila.getString(2));
                        System.out.println(fila.getString(3));
                        System.out.println("++++++++++++++++++++++++++++++++++++++++++");
                        String fromSTR = al.getString("from");
                      //  String from = fromSTR.substring(fromSTR.indexOf('[') + 1, fromSTR.indexOf(']'));
                        System.out.println("-------------------------------");
                        System.out.println(fromSTR);
                        System.out.println("-------------------------------");
                        stations.put(fromSTR);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                final String URLS = "http://192.168.43.54:3000/getStationsApp";
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URLS, stations, new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            ArrayList<Station> stations = new ArrayList();
                            //RECUPERO TODO EL ARCHVIO JSON
                            JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));
                            for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                                JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                                if (tiplocs.contains(unidad.getString("crs"))) {
                                    System.out.println("--");
                                    Station st = new Station();

                                    JSONArray latlon = (JSONArray) unidad.get("latlon");
                                    ArrayList<Double> location = new ArrayList();
                                    location.add(latlon.getDouble(0));
                                    location.add(latlon.getDouble(1));
                                    st.setLocation(location);
                                    st.setStation(unidad.getString("station"));
                                    st.setCrs(unidad.getString("crs"));
                                    st.setTiploc(unidad.getString("tpl"));
                                    //System.out.println(latlon.get(0) + " " + latlon.get(1));
                                    stations.add(st);
                                }

                            }
                            Intent intent = new Intent(getApplicationContext(), com.example.pedro.greateranglia.Map.class);
                            intent.putExtra("stations", stations);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        System.out.println("REQUEST ERROR");

                    }

                });

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);


                //----------------------------------------------------------------------------------

                break;

        }
        return true;
    }  */


}
