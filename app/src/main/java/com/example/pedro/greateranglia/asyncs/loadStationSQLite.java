package com.example.pedro.greateranglia.asyncs;



import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.pedro.greateranglia.Volley.VolleySingleton;
import com.example.pedro.greateranglia.activities.Login;

import com.example.pedro.greateranglia.sqlite.AdminSQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class loadStationSQLite extends AsyncTask<Void, Integer, Boolean> {

    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Context context;
    boolean exist;
    private static final String URL = "http://192.168.43.54:3000/sqliteStations";

    public loadStationSQLite(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        //Shared prefered  check if we got the station database set up in device

        SharedPreferences prefe=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        exist= prefe.getBoolean("exist",false);


        System.out.println("-------------------------------");

        System.out.println(exist);
       if(exist==false) {
           //BBDD
           admin = new AdminSQLiteOpenHelper(context, "ga", null, 1);
           bd = admin.getWritableDatabase();
           Toast.makeText(context, "creating database",
                   Toast.LENGTH_SHORT).show();
       }else{
           Toast.makeText(context, "CREADA",
                   Toast.LENGTH_SHORT).show();
       }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        //LOAD THE DATA HERE and inster if de database doesnt exist
        if(exist==false) {
            System.out.println("Loading data from server");

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, null, new Response.Listener<JSONArray>() {


                @Override
                public void onResponse(JSONArray response) {

                    try {

                        //RECUPERO TODO EL ARCHVIO JSON
                        JSONArray jsonObjectPrincipal = new JSONArray(response.toString(0));
                  /*  System.out.println("----------------------------------------------------------------------");
                    System.out.println(jsonArrayPrincipal.toString());*/

                        int cont=0;
                        for (int i = 0; i < jsonObjectPrincipal.length(); i++) {

                            JSONObject unidad = jsonObjectPrincipal.getJSONObject(i);
                            //String tiploc = unidad.getString("locname") + "  [" + unidad.getString("crs") + "] ";

                            System.out.println(unidad.toString());

                            JSONArray latlon = (JSONArray) unidad.get("latlon");

                            //INSERT EN BBDD AQUI
                            ContentValues registro = new ContentValues();
                            registro.put("id", cont);
                            registro.put("station", unidad.getString("station"));
                            registro.put("tpl", unidad.getString("tpl"));
                            registro.put("crs", unidad.getString("crs"));
                            registro.put("lat",latlon.getDouble(0));
                            registro.put("lon", latlon.getDouble(1));
                            bd.insert("stations", null, registro);
                            cont++;

                            }
                        System.out.println("TODO CORRECTO!!!!!!");

                        SharedPreferences preferencias=context.getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferencias.edit();
                        editor.putBoolean("exist",true);
                        //commit
                        editor.commit();


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

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            Toast.makeText(context, "Tarea finalizada!",
                    Toast.LENGTH_SHORT).show();


        }

    }
}
