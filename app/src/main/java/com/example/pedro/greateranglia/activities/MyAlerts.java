package com.example.pedro.greateranglia.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.pedro.greateranglia.objects.Alert;
import com.example.pedro.greateranglia.adapters.AlertAdapter;
import com.example.pedro.greateranglia.R;
import com.example.pedro.greateranglia.objects.Station;
import com.example.pedro.greateranglia.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAlerts extends AppCompatActivity {

    ListView lvAlerts;
    AlertAdapter adapter;
    Alert ale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_alerts);

        ArrayList<Alert> alerts = (ArrayList<Alert>) getIntent().getSerializableExtra("alerts");
        lvAlerts = findViewById(R.id.lvAlerts);
        adapter = new AlertAdapter(getApplicationContext(), alerts);
        lvAlerts.setAdapter(adapter);

        lvAlerts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Must be a Array to get the location from the server (mongo query $in[])
                JSONArray station = new JSONArray();

                ale = (Alert) adapterView.getItemAtPosition(i);
                try {
                    JSONObject alObj = new JSONObject(ale.getAlert());
                    String fromSTR = alObj.getString("from");
                   // String from = fromSTR.substring(fromSTR.indexOf('[') + 1, fromSTR.indexOf(']'));
                    station.put(fromSTR);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //--------------------------------

                final String URLS = "http://192.168.43.54:3000/getStationsApp";
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URLS, station, new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            ArrayList<Station> stations = new ArrayList();
                            //RECUPERO TODO EL ARCHVIO JSON
                            JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));
                            for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                                JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                                System.out.println("--");

                                JSONArray latlon = (JSONArray) unidad.get("latlon");
                                ArrayList<Double> location = new ArrayList();
                                location.add(latlon.getDouble(0));
                                location.add(latlon.getDouble(1));
                                ale.setLocation(location);

                            }

                            Intent intent = new Intent(getApplicationContext(), DetailAlert.class);

                            intent.putExtra("alert", (Serializable) ale);
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


            }

        });

    }
}
