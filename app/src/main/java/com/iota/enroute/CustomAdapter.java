package com.iota.enroute;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
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
import java.util.concurrent.ExecutionException;

import static com.iota.enroute.Login.PREFS_NAME;

public class CustomAdapter extends AppCompatActivity implements ListAdapter {

    Context context;

    public CustomAdapter(Context context) {
        this.context = context;
    }

    ArrayList<String> arrayList;

    AlertDialog userDialog;
    boolean proceed = false;

    private class HttpNotif extends AsyncTask<String, Void, Void> {

        String data;
        //Getting the Bus List by HTTP GET
        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

                HttpURLConnection httpURLConnection = null;
                try {

                    data = "";

                    httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/").openConnection();
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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            return null;
        }

    }

    private class CheckInternet extends AsyncTask<String, Void, Boolean> {

        String data;
        //Getting the Bus List by HTTP GET
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

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

    }


    public void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {

                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

//    //To Check for Internet
//
//    public boolean isInternetAvailable() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("www.google.com");
//            //You can replace it with your name
//
//            Log.d("Output", "NeT:"+ipAddr.toString());
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            Log.d("Output","Exception:" + e.toString());
//            return false;
//        }
//    }

    public CustomAdapter(Context context, ArrayList<String> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
        Log.d("Output", "Custom : " + arrayList.toString());
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = arrayList.get(position);
        if(convertView==null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.alerts_toggle, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Toast.makeText(context, "Clicked an item", Toast.LENGTH_SHORT).show();
                }
            });

            TextView busStopList=convertView.findViewById(R.id.busStopName);
            Switch toggle = convertView.findViewById(R.id.onoff);
            busStopList.setText(name);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0) ;// 0 - for private mode
            SharedPreferences.Editor editor = settings.edit();

            boolean currentToggleState=false;
            currentToggleState = settings.getBoolean(busStopList.getText().toString(), false);
            toggle.setChecked(currentToggleState);

            if(currentToggleState) {
                busStopList.setTextColor(Color.parseColor("#D81B60"));
                busStopList.setTypeface(null, Typeface.BOLD_ITALIC);
            }

            Log.d("Output", busStopList.getText().toString());

            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // do something, the isChecked will be
                    // true if the switch is in the On position

                    try {

                        boolean internetStatus = new CheckInternet().execute("Internet").get();

                        if (!internetStatus) {
                            showDialogMessage("Oops", "Check your Internet Connnection.");
                            toggle.setChecked(!toggle.isChecked());
                        } else {

                            JSONObject postData = new JSONObject();
                            SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
                            String id=settings.getString("appid","");
                            postData.put("AppID", id);
                            postData.put("stopName", busStopList.getText().toString());

                            boolean toggleState = toggle.isChecked();
                            editor.putBoolean(busStopList.getText().toString(), toggleState);
                            editor.commit();
                            if (toggleState) {

                                busStopList.setTextColor(Color.parseColor("#D81B60"));
                                busStopList.setTypeface(null, Typeface.BOLD_ITALIC);
                                Toast.makeText(context, "Notifications set for : " + busStopList.getText(), Toast.LENGTH_SHORT).show();

                                HttpNotif obj = new HttpNotif();
                                obj.execute("Set Notif", postData.toString());
                            }
                            else {

                                busStopList.setTextColor(Color.parseColor("#000000"));
                                busStopList.setTypeface(null, Typeface.NORMAL);

                                Toast.makeText(context, "Notifications unset for : " + busStopList.getText(), Toast.LENGTH_SHORT).show();
                                HttpNotif obj = new HttpNotif();
                                obj.execute("Unset Notif", postData.toString());
                            }
                        }
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    } catch(ExecutionException e){
                        e.printStackTrace();
                    }
                }
            });

        }
        return convertView;
    }

    public SharedPreferences getSharedPreferences(String prefsName, int i) {

        return context.getSharedPreferences(prefsName, i);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }



}
