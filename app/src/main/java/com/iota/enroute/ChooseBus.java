package com.iota.enroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class ChooseBus extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String data;

    Button goButton;
    Integer selectedIndex = 0;

    //To Check Internet connectivity

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("www.google.com");
            //You can replace it with your name

            Log.d("Output", "NeT:"+ipAddr.toString());
            return !ipAddr.equals("");

        } catch (Exception e) {
            Log.d("Output","Exception:" + e.toString());
            return false;
        }
    }

    private String accessToken;
    private String refreshToken;

    JSONObject obj;
    AlertDialog userDialog;

    ArrayList<String> busList = new ArrayList<String>();

  /*  private class HttpGetter extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean connected = isInternetAvailable();

            if(!connected)
            {
                return "Check your Internet Connection.";
            }

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/buslist").openConnection();
                httpURLConnection.setRequestMethod("POST");
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
    } */

  /*  private class HttpChosenBus extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean connected = isInternetAvailable();

            if(!connected)
            {
                return "Check your Internet Connection.";
            }

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/dynamo").openConnection();
                httpURLConnection.setRequestMethod("POST");
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

   */

 /*   private class HttpLogout extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean connected = isInternetAvailable();

            if(!connected)
            {
                return "Check your Internet Connection.";
            }

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/logout").openConnection();
                httpURLConnection.setRequestMethod("POST");
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

  */


  /*  private class HttpRefresh extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean connected = isInternetAvailable();

            if(!connected)
            {
                return "Check your Internet Connection.";
            }

            HttpURLConnection httpURLConnection = null;
            try {

                data="";
                Log.d("Output","Inside HttpRefresh");

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/refreshToken").openConnection();
                httpURLConnection.setRequestMethod("POST");
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

*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bus);


        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
        // Get all the tokens for verification.
        accessToken = settings.getString("accessToken", "");
        refreshToken = settings.getString("refreshToken", "");

        goButton = findViewById(R.id.ButtonGo);

        busList.add("Select a Bus");
        JSONObject postData = new JSONObject();

      /*  try {
            //Calling func for bus list

            if(accessToken.equals(""))
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("hasLoggedIn", false);
                editor.commit();

                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }

            postData.put("accessToken", accessToken);
            postData.put("refreshToken", refreshToken);

            String busListString = new HttpGetter().execute("BusList", postData.toString()).get();

            Log.d("Output", "Result from HttpGetter()" + busListString);

            if(busListString.equals("Check your Internet Connection."))
            {
                showDialogMessage("Oops!", "Check your Internet Connection. Restart the app after you have a working internet connection.");
            }
            else if(busListString.equals("False"))
            {

                Log.d("Output","postData : "+ postData.getString("refreshToken"));


                String result = new HttpRefresh().execute("RefreshToken", postData.toString()).get();
                Log.d("Output","After Refresh() is called:"+result);
                //If refresh token was invalid (fake)
                if(result.equals("False"))
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accessToken", "");
                    editor.putString("refreshToken", "");
                    editor.commit();

                    Intent i = new Intent(getApplicationContext(), Login.class);
                    startActivity(i);
                    finish();

                }
                else {
                    JSONObject jsonObject = new JSONObject(result);

                    accessToken = jsonObject.getString("AccessToken");
                    //refreshToken = jsonObject.getString("RefreshToken");


                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accessToken", accessToken);
                   // editor.putString("refreshToken", refreshToken);
                    editor.commit();

                    Intent i = new Intent(getApplicationContext(), Login.class);
                    startActivity(i);
                    finish();
                }
            }
            else {

                busListString = busListString.substring(1, busListString.length() - 1);

                //busList2 holds all the bus numbers sliced out from the received string
                ArrayList busList2 = new ArrayList<String>(Arrays.asList(busListString.split(", ")));

                Log.d("Output", busList2.toString());

                for (int i = 0; i < busList2.size(); i++) {
                    busList.add("Bus " + busList2.get(i));
                }
                Log.d("Output", busList.toString());
            }
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
*/
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Spinner Code
        Spinner spin = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner, busList);
        spin.setOnItemSelectedListener(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setSelection(0);
        adapter.notifyDataSetChanged();
        //ends code

       goButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

               /* if (selectedIndex == 0) {
                    showDialogMessage("Oops!", "Select a Bus Number.");
                } */

                JSONObject postData = new JSONObject();

                try {
                 /*   String busno = busList.get(selectedIndex);
                    busno = busno.substring(4);
                    Log.d("Output", busno);
                    postData.put("BusNo", busno);
                    postData.put("accessToken", accessToken);
                    postData.put("refreshToken", refreshToken);
                    Log.d("Output", "Token: " + accessToken.toString());
                    Log.d("Output", "Refresh Token: " + refreshToken.toString());
                     */
               //     String ChosenBusJSON = new HttpChosenBus().execute("Chosen Bus", postData.toString()).get();

                  /*  if(ChosenBusJSON.equals("Check your Internet Connection.")) {
                        showDialogMessage("Oops!", "Check your Internet Connection.");
                    }

                   */
                  /*  else if (ChosenBusJSON.equals("False")) {
                        Toast.makeText(ChooseBus.this, "Unauthorized Access!", Toast.LENGTH_SHORT).show();
                        Log.d("Output", "After Toast");

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("accessToken", "");
                        editor.putString("refreshToken", "");
                        editor.commit();

                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        finish();
                    }

                   */

              /*  SharedPreferences.Editor editor = settings.edit();
                editor.putString("accessToken", accessToken);
                editor.putString("refreshToken", refreshToken);
                editor.commit();

               */
/*
                obj = new JSONObject(ChosenBusJSON);
                Log.d("Output", obj.get("Item").toString());

 */

                /*Intent i = new Intent(getApplicationContext(), mapbox.class);
                i.putExtra("busInfo", obj.get("Item").toString());
                startActivity(i);

                 */
                    findViewById(R.id.loadingCircle).setVisibility(View.GONE);
                    Intent i = new Intent(getApplicationContext(), mapbox.class);
                    startActivity(i);



                }
          //      catch (ExecutionException e) {
          //          e.printStackTrace();
         //       } catch (InterruptedException e) {
          //          e.printStackTrace();
           //     }
              /*   catch (JSONException e) {
                    e.printStackTrace();
                }

               */
                catch(Exception e) {
                  //  showDialogMessage("Oops!", result);
                    findViewById(R.id.loadingCircle).setVisibility(View.GONE);
                }


            }
        }

        );

    }

    //Spinner items click

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Log.i("Output","Clicked");

        selectedIndex = position;
        if(position>0) {

            Toast.makeText(getApplicationContext(), "Selected : "+busList.get(position) ,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("Output","Not Clicked");

    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(body.equals("Check your Internet Connection. Restart the app after you have a working internet connection."))
                    {
                        finish();
                    }
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
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
                    if(result.equals("Check your Internet Connection."))
                    {
                        showDialogMessage("Oops!", result);

                    }
                    else
                    {
                        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("accessToken", "");
                        editor.putBoolean("hasLoggedIn", false);
                        editor.commit();

                        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        finish();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }



        }
        return true;
    }

   */


}
