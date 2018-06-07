package com.example.pedro.greateranglia.activities;

import android.content.Intent;
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

public class SignUp extends AppCompatActivity {

    EditText email,pwd,pwdC;
    Button btnSignIn;
     final  String URL="http://192.168.43.54:3000/newUserapp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email=findViewById(R.id.email);
        pwd=findViewById(R.id.password);
        pwdC=findViewById(R.id.passwordC);
        btnSignIn=findViewById(R.id.btnSignIn);

    }

    public void createUser(View v){


        if(!pwd.getText().toString().equals(pwdC.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords Don't Match", Toast.LENGTH_LONG).show();
        }else {

            Map<String, String> postParam = new HashMap<String, String>();
            postParam.put("username", email.getText().toString());
            postParam.put("password", pwd.getText().toString());
            postParam.put("deviceid", FirebaseInstanceId.getInstance().getToken());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {


                public void onResponse(JSONObject response) {

                    try {

                        //RECUPERO TODO EL ARCHVIO JSON
                        JSONObject json = new JSONObject(response.toString(0));
                        System.out.println(json.toString());
                        String message = json.getString("message");
                        if (message.equals("successfully created")) {
                            Toast.makeText(getApplicationContext(), "successfully created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            Toast.makeText(getApplicationContext(), "Couldn't create the user.", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("----------------------------------- ERROR 1 -----------------------------------");
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    System.out.println("ERROR    ---------------------   ERROR SING IN    2 --------------------");

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
}
