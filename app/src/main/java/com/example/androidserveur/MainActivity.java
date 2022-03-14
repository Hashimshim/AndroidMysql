package com.example.androidserveur;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<String, String> server_urls = new HashMap<String, String>();
    EditText txt_id, txt_name, txt_address, txt_data;
    Button btn_add, btn_show, btn_update, btn_delete, btn_clear;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        String server_addr = getIntent().getStringExtra("server");
        server_urls.put("insert", server_addr + "/insert.php");
        server_urls.put("show", server_addr + "/show.php");
        server_urls.put("update", server_addr + "/update.php");
        server_urls.put("delete", server_addr + "/delete.php");
        server_urls.put("list", server_addr + "/viewList.php");

        txt_id = findViewById(R.id.txt_id);
        txt_name = findViewById(R.id.txt_name);
        txt_address = findViewById(R.id.txt_address);
        txt_data = findViewById(R.id.txt_data);

        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        btn_show = findViewById(R.id.btn_show);
        btn_clear = findViewById(R.id.btn_clear);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("insert");
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_id.setText("");
                txt_address.setText("");
                txt_name.setText("");
                List();
            }
        });

        List();

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("show");
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("update");
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                sendRequest("delete");
            }
        });
    }

    public void sendRequest(String op) {
        if (txt_id.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Please submit a valid ID !", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (op) {
            case "insert":
                Add(txt_name.getText().toString(), txt_address.getText().toString());
                break;
            case "update":
                Update(txt_id.getText().toString(), txt_name.getText().toString(), txt_address.getText().toString());
                break;
            case "show":
                Show(txt_id.getText().toString());
                break;
            case "delete":
                Delete(txt_id.getText().toString());
                break;
        }
        List();
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void Add(String name, String address) {
        String server_insert_url = server_urls.get("insert");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_insert_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                            txt_id.setText("");
                            txt_name.setText("");
                            txt_address.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("address", address);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void Show(String id) {
        String server_show_url = server_urls.get("show") + "?id=" + txt_id.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_show_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = "";
                            if (jsonObject.getInt("success") == 0) {
                                message = "The Request Resource Does not Exist !";
                                txt_id.setText("");
                                txt_name.setText("");
                                txt_address.setText("");
                            } else {
                                message = "Operation Success";
                                txt_name.setText(jsonObject.getJSONObject("order").getString("name"));
                                txt_address.setText(jsonObject.getJSONObject("order").getString("address"));
                            }
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_LONG).show();
                            Log.w("show : ", "Show failed ! ");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        );
        requestQueue.add(stringRequest);
    }


    public void Update(String id, String new_name, String new_address) {
        String server_update_url = server_urls.get("update");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_update_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("UPDATE RESPONSE :", response );
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = "";
                            if (jsonObject.getInt("success") == 1) {
                                message = "Update Success !";
                                txt_id.setText("");
                                txt_name.setText("");
                                txt_address.setText("");
                            } else
                                message = "Error Updating Resource ! ";
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("name", new_name);
                params.put("address", new_address);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void Delete(String id) {
        String server_delete_url = server_urls.get("delete") + "?id=" + txt_id.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_delete_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = "";
                            if (jsonObject.getInt("success") == 1) {
                                message = "Operation Success";
                                txt_id.setText("");
                                txt_name.setText("");
                                txt_address.setText("");
                            } else
                                message = "Error Deleting Resource";
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    public void List() {
        String server_list_url = server_urls.get("list");
        Log.w("server list url", server_list_url );
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_list_url,
                new Response.Listener<String>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(String response) {
                        try {
                            txt_data.setText("");
                            Log.w("REPONSE :",response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("success") == 1) {
                                StringBuilder orders = new StringBuilder();
                                orders.append("ID | NAME | ADDRESS \n");
                                orders.append("----------------------------------------------- \n");
                                JSONArray liste = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < liste.length(); i++) {
                                    int id = liste.getJSONObject(i).getInt("id");
                                    String nom = liste.getJSONObject(i).getString("name");
                                    String addr = liste.getJSONObject(i).getString("address");
                                    orders.append(String.format("%d | %s | %s \n", id, nom, addr));
                                    orders.append("-------------------------------------------- \n");
                                }
                                txt_data.setText(orders);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.w("list :", "List method failed ! " );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        requestQueue.add(stringRequest);
    }
}