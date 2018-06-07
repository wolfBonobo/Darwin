package com.example.pedro.greateranglia.activities;


import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pedro.greateranglia.R;
import com.example.pedro.greateranglia.Volley.VolleySingleton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText email, password;


    Button btnLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLog = findViewById(R.id.btnLogin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 10);



    }

    public void log(View view) {

        validateUser();

       /* Intent intent=new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();//si vuelvo atras no se carga de nuevo*/

    }

    public void signUp(View view) {

        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);

    }

    public void validateUser() {


        //  final  String URL="http://10.85.116.205:3000/newUserapp";
        // final  String URL="http://192.168.43.54:3000/newUserapp";
        //final String URL = "http://192.168.1.179:3000/check";
        //  final  String URL="http://10.85.116.205:3000/check";
        final String URL = "http://192.168.43.54:3000/check";
        // final  String URL="http://192.168.1.179:3000/newUserapp";


        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("username", email.getText().toString());
        postParam.put("password", password.getText().toString());
        postParam.put("deviceid", FirebaseInstanceId.getInstance().getToken());

            /*JSONObject postParam= new JSONObject();
            try {
                postParam.put("username", email.getText().toString());
                postParam.put("password", password.getText().toString());
                postParam.put("deviceid", "pruebaKey");
            }catch (Exception e){
                e.printStackTrace();
            }*/

        System.out.println("------------------------------------------");
        System.out.println(postParam.toString());
        System.out.println(new JSONObject(postParam).toString());
        System.out.println("------------------------------------------");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {


            public void onResponse(JSONObject response) {

                try {

                    //RECUPERO TODO EL ARCHVIO JSON
                    JSONObject json = new JSONObject(response.toString(0));
                    System.out.println(json.toString());
                    String message = json.getString("message");
                    if (message.equals("logged")) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getApplicationContext(), "the username or password is incorrect.", Toast.LENGTH_LONG).show();
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

    }
}
