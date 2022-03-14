package com.example.androidserveur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {

    EditText txt_server_address;
    Button btn_connect;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txt_server_address = findViewById(R.id.txt_server_address);
        btn_connect = findViewById(R.id.btn_connect);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test_connection();
            }
        });
    }

    private void test_connection() {
        String server_url = txt_server_address.getText().toString() + "/viewList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Home.this, "Connection Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue = Volley.newRequestQueue(Home.this);
        requestQueue.add(stringRequest);
    }

    public void startActivity() {
        Intent INT = new Intent(this, MainActivity.class);
        INT.putExtra("server", txt_server_address.getText().toString());
        startActivity(INT);
    }
}