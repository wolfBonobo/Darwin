package com.example.pedro.greateranglia.asyncs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.pedro.greateranglia.Volley.VolleySingleton;
import com.example.pedro.greateranglia.objects.Alert;
import com.example.pedro.greateranglia.sqlite.AdminSQLiteOpenHelper;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pedro on 23/04/2018.
 */

public class InsertAlert extends AsyncTask<Void, Integer, Boolean> {

    Context context;
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Alert newAlert;

    public InsertAlert(Context context) {
        this.context = context;

    }


    @Override
    protected void onPreExecute() {
        //BBDD
        admin = new AdminSQLiteOpenHelper(context, "ga", null, 1);
        bd = admin.getWritableDatabase();

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        System.out.println("ASYNC METHOD  ");
       /* ContentValues registro = new ContentValues();
        System.out.println(alert.toString());
        registro.put("alert", aler.toString());
        bd.insert("alerts", null, registro);*/
        getAndInsertAlerts();

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(Boolean result) {
      //  bd.close();

    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, "ERROR!!",
                Toast.LENGTH_SHORT).show();
    }

    //METHODS

    protected void getAndInsertAlerts() {

        //******************  server  version  ************************

        JSONArray deviceid = new JSONArray();
        deviceid.put(FirebaseInstanceId.getInstance().getToken());
        final String URLS = "http://192.168.43.54:3000/getAlertsApp";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URLS, deviceid, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {
                try {

                    //RECUPERO TODO EL ARCHVIO JSON
                    JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));

                    for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                        JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                        System.out.println(unidad);

                        String id = unidad.getString("id");
                        //consulra base de datos si objeto existe

                        Cursor fila = bd.rawQuery("SELECT * from alerts where id like '" + id + "'", null);
                        if (!fila.moveToNext()) {
                            //doesnt exist
                            Alert a = new Alert();
                            a.setId(id);
                            a.setAlert(unidad.toString());
                            insertLocations(a);
                        }
                    }


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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);


        //****************************************************

    }

    protected void insertLocations(Alert ale) {

        newAlert = ale;

        JSONArray station = new JSONArray();

        try {
            JSONObject alObj = new JSONObject(newAlert.getAlert());
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

                    JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));

                    for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                        JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                        JSONArray latlon = (JSONArray) unidad.get("latlon");
                        ArrayList<Double> location = new ArrayList();
                        location.add(latlon.getDouble(0));
                        location.add(latlon.getDouble(1));
                        newAlert.setLocation(location);

                    }

                    //INSERT EN BBDD AQUI
                    ContentValues registro = new ContentValues();
                    registro.put("id", newAlert.getId());
                    registro.put("alert", newAlert.getAlert());
                    registro.put("lat", newAlert.getLocation().get(0).toString());
                    registro.put("lon", newAlert.getLocation().get(1).toString());
                    bd.insert("alerts", null, registro);

                    System.out.println("TODO CORRECTO!!!!!!");

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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);


    }
}
