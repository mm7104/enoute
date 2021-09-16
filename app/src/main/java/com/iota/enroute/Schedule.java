package com.iota.enroute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Schedule extends AppCompatActivity {

    List<String> ScheduleList = new ArrayList<String>();

    String busInfoString;
    JSONObject busInfo;

    String data;
    JSONObject Stops;

    private String accessToken;


    private class HttpLogout extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://ec2-65-0-184-243.ap-south-1.compute.amazonaws.com:5000/logout").openConnection();
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



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //getting Timings and Stops as String
        busInfoString = getIntent().getStringExtra("busInfo");

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
                                Intent intent2 = new Intent(getApplicationContext(), mapbox.class);
                                intent2.putExtra("busInfo", busInfoString);
                                startActivity(intent2);
                                finish();
                                break;

                            case R.id.Alerts:

                                Log.d("Bottom schedule", "sched clicked");
                                Intent intent = new Intent(getApplicationContext(), Alerts.class);
                                intent.putExtra("busInfo", busInfoString);
                                startActivity(intent);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
        try {
            busInfo = new JSONObject(busInfoString);
            Log.i("JSON", busInfo.toString());

        } catch (JSONException e) {
            e.printStackTrace();

        }

        ListView list = (ListView) findViewById(R.id.listView1);

        try {
            //Parsing the JSON Schedule out of the whole JSON
//            JSONArray Schedule = new JSONArray(busInfo.getString("Time"));
            Log.d("Output",busInfo.toString());
            Stops = new JSONObject(busInfo.getString("Stops"));
//            Log.i("JSON",Schedule.toString());
            Iterator<String> stopNames = Stops.keys();

//            for (int i = 0; i < Schedule.length(); i++) {
            while (stopNames.hasNext()) {
//                JSONObject jsonobject = Schedule.getJSONObject(i);
//                String time = jsonobject.getString("timing");
//                String stopName = jsonobject.getString("name");
                String key = stopNames.next();
                JSONObject details = new JSONObject(Stops.getString(key));
                String time = details.getString("timing");
                String stopName = key;

                ScheduleList.add(stopName + " : " + time);

                Log.i("I", time);
                Log.i("I", stopName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("JSON", "JSON Array/Object error");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1,
                        ScheduleList);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/*
                    Iterator<String> stopNames = Stops.keys();

//            for (int i = 0; i < Schedule.length(); i++) {
                    while (stopNames.hasNext()) {
//                JSONObject jsonobject = Schedule.getJSONObject(i);
//                String time = jsonobject.getString("timing");
//                String stopName = jsonobject.getString("name");
                        String key = stopNames.next();
                        JSONObject details = new JSONObject(Stops.getString(key));

                        if(position==0) {

                            String lat = details.getString("lat");
                            String lng = details.getString("lng");

                            Log.i("Output", lat);
                            Log.i("Output", lng);
                            break;

                        }
                        else
                            position-=1;
*/


                SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("zoomBusStop", position);
                editor.commit();

                Intent i = new Intent(getApplicationContext(), mapbox.class);
                i.putExtra("busInfo", busInfoString);
                startActivity(i);
                finish();
            }
        });

    }




    //Three dots
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hamburger_menu, menu);
        return true;
    }

  /*  @Override
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
    
   */

}
