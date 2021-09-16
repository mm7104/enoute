package com.iota.enroute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class Alerts extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String busInfoString;
    private JSONObject busInfo;

    String data;
    private String accessToken;

    ArrayList<String> busList = new ArrayList<String>();

    private class HttpLogout extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/logout").openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        accessToken = settings.getString("accessToken", "");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.showMap:
                                Log.d("Bottom schedule", "Map clicked");
                                Intent intent2=new Intent(getApplicationContext(),mapbox.class);
                                intent2.putExtra("busInfo",busInfoString);
                                startActivity(intent2);
                                finish();
                                break;

                            case R.id.Schedule:

                                Log.d("Bottom schedule", "schedule clicked");
                                Intent intent1 = new Intent(getApplicationContext(), Schedule.class);
                                intent1.putExtra("busInfo", busInfoString);

                                startActivity(intent1);
                                finish();
                                break;
                        }
                        return true;
                    }
                });

        //getting Stops as String
        busInfoString = getIntent().getStringExtra("busInfo");
        try {
            busInfo = new JSONObject(busInfoString);
            Log.i("JSON",busInfo.toString());

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //Parsing the JSON Schedule out of the whole JSON
//            JSONArray Schedule = new JSONArray(busInfo.getString("Time"));
//            Log.i("JSON",Schedule.toString());
            JSONObject Stops = new JSONObject(busInfo.getString("Stops"));
            Iterator<String> stopNames = Stops.keys();
            while (stopNames.hasNext()) {
//                JSONObject jsonobject = Schedule.getJSONObject(i);
//                String stopName = jsonobject.getString("name");
                String stopName = stopNames.next();

                busList.add(stopName);
                Log.i("I", stopName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("JSON", "JSON Array/Object error");
        }

        ListView list = (ListView) findViewById(R.id.listView1);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                (this, android.R.layout.simple_list_item_1,
//                        busList);

        CustomAdapter customAdapter = new CustomAdapter(this, busList);
        Log.d("Output", "busList : " + busList.toString());
        list.setAdapter(customAdapter);


    }

    //Three dots
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hamburger_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.aboutUs:
                Intent about = new Intent(getApplicationContext(), aboutUs.class);
                startActivity(about);
                return true;

            case R.id.howToUse:
                Intent how = new Intent(getApplicationContext(), HowToUse.class);
                startActivity(how);
                return true;

            case R.id.logout:
                JSONObject postData = new JSONObject();
                try {
                    postData.put("accessToken", accessToken);
                    String result = new HttpLogout().execute("Logout", postData.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("accessToken", "");
                editor.putBoolean("hasLoggedIn", false);
                editor.commit();

                Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finishAffinity();
                finish();

        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(this, "Clicked item", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
