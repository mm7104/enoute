package com.iota.enroute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.turf.TurfMeasurement.distance;


import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;


public class mapbox extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private FloatingActionButton currLocButton;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker, updateMarker;
    private NavigationMapRoute navigationMapRoute;
    private MapView mapView;
    private String selectedBusNo;
    private String accessToken;


    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;
    KeyStore clientKeyStore = null;
    String certificateId;
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a3eudyk66521e-ats.iot.ap-south-1.amazonaws.com";
    private static final String AWS_IOT_POLICY_NAME = "smartBus";
    private static final Regions MY_REGION = Regions.AP_SOUTH_1;
    private static final String KEYSTORE_NAME = "iot_keystore";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String CERTIFICATE_ID = "default";

    Map<String,Marker> busmap=new HashMap<String, Marker>();
    Map<String, Marker> busStops = new HashMap<>();

    private JSONObject busInfo;
    private String busInfoString;
    private String data;

    private  static final String LOG_TAG="MainActivity";
    private  static final String TAG="MainActivity";

/*
    private class HttpCheckToken extends AsyncTask<String, Void, String> {
        //Getting the Bus List by HTTP GET
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/checkToken").openConnection();
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
    */

 /*   private class HttpLogout extends AsyncTask<String, Void, String> {
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

  */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String apiKey = BuildConfig.ApiKey;
        Mapbox.getInstance(this, apiKey);
        setContentView(R.layout.activity_mapbox);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //IoT Core

        clientId = UUID.randomUUID().toString();

        AWSMobileClient.getInstance().initialize(this, new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {

                initIoTClient();
                connect();
            }

            @Override
            public void onError(Exception e) {
                Log.e(LOG_TAG, "onError: ", e);
            }
        });
//
        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
//        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        accessToken = settings.getString("accessToken", "");
/*

        try {
        JSONObject postData = new JSONObject();
        postData.put("accessToken", accessToken);
        String result = new HttpCheckToken().execute("CheckToken", postData.toString()).get();

        if(result.equals("False"))
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("accessToken", "");
            editor.putString("refreshToken", "");
            editor.putBoolean("hasLogged In", false);
            editor.commit();

            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
            finish();
        }

        } catch (JSONException e) {
            e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
        */

        //Toolbar
/*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //getting Timings and Stops as String
        busInfoString = getIntent().getStringExtra("busInfo");
        Log.d("Output", busInfoString);

        try {
            busInfo = new JSONObject(busInfoString);
            selectedBusNo = busInfo.getString("BusNo");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("JSON", "JSON Object error");
        }
*/
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.Schedule:

                                Log.d("Bottom schedule", "schedule clicked");
                                Intent intent1 = new Intent(getApplicationContext(), Schedule.class);
                                intent1.putExtra("busInfo", busInfoString);
                                startActivity(intent1);
                                finish();
                                break;

                            case R.id.Alerts:

                                Log.d("Bottom schedule", "sched clicked");
                                Intent intent2=new Intent(getApplicationContext(),Alerts.class);
                                intent2.putExtra("busInfo", busInfoString);
                                startActivity(intent2);
                                finish();
                                break;
                        }
                        return true;
                    }
                });

        TextView busLabel = findViewById(R.id.busLabel);
        busLabel.setText("Buses nearby- "+ selectedBusNo);

        //Current Location

        currLocButton = findViewById(R.id.currLocButton);
//
        currLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originLocation != null) {
                    Log.d("Current Location", "Camera Set");
                    originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()))
                            .zoom(17)
                            .build();

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100);
                } else {
                    Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Bus Location Floating button

        View busLocButton = findViewById(R.id.busLocButton);

        busLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (busmap.containsKey(selectedBusNo)) {
                    Log.d("Current Location", "Camera Set");

                    Marker busLocation = busmap.get(selectedBusNo);
                    LatLng busLatLng = busLocation.getPosition();
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(busLatLng.getLatitude(), busLatLng.getLongitude()))
                            .zoom(16)
                            .build();

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100);
                } else {
                    Toast.makeText(getApplicationContext(), "Bus location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



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
                    String result = new HttpLogout().execute("BusList", postData.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                  catch (InterruptedException e) {
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
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finishAffinity();
                finish();

        }
        return true;
    }

*/
//IoT Core

    public void connect()
    {
        Log.d(LOG_TAG, "In connect clientId = " + clientId);

        try {

            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {

                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("checking status",status.toString());
                            if (throwable != null) {
                                Log.e(LOG_TAG, "Connection error.", throwable);
                                Toast.makeText(getApplicationContext(),"Check your connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    if(String.valueOf(status).equals("Connected")) {

                        //Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                        subscribeClick();

                    }

                }
            });
        } catch (final Exception e) {

            Log.e(LOG_TAG, "Connection error.", e);

        }

    }
    void initIoTClient() {
        Region region = Region.getRegion(MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(AWSMobileClient.getInstance());
        mIotAndroidClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
//        keystoreName = KEYSTORE_NAME;
//        keystorePassword = KEYSTORE_PASSWORD;
//        certificateId = CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);

                    /* initIoTClient is invoked from the callback passed during AWSMobileClient initialization.
                    The callback is executed on a background thread so UI update must be moved to run on UI Thread. */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //btnConnect.setEnabled(true);
                        }
                    });
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // Create a new private key and certificate. This call
                        // creates both on the server and returns them to the
                        // device.
                        CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                new CreateKeysAndCertificateRequest();
                        createKeysAndCertificateRequest.setSetAsActive(true);
                        final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                        createKeysAndCertificateResult =
                                mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                        Log.i(LOG_TAG,
                                "Cert ID: " +
                                        createKeysAndCertificateResult.getCertificateId() +
                                        " created.");

                        // store in keystore for use in MQTT client
                        // saved as alias "default" so a new certificate isn't
                        // generated each run of this application
                        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                createKeysAndCertificateResult.getCertificatePem(),
                                createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                keystorePath, keystoreName, keystorePassword);

                        // load keystore from file into memory to pass on
                        // connection
                        clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                keystorePath, keystoreName, keystorePassword);

                        // Attach a policy to the newly created certificate.
                        // This flow assumes the policy was already created in
                        // AWS IoT and we are now just attaching it to the
                        // certificate.
                        AttachPrincipalPolicyRequest policyAttachRequest =
                                new AttachPrincipalPolicyRequest();
                        policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                        policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                .getCertificateArn());
                        mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //btnConnect.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(LOG_TAG,
                                "Exception occurred when generating new private key and certificate.",
                                e);
                    }
                }
            }).start();
        }
    }

    public void subscribeClick() {


        try {
            mqttManager.subscribeToTopic("bus", AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        JSONObject obj = new JSONObject(new String(data));
                                        String message = new String(data, "UTF-8");
                                        String busno= (String) obj.get("BusNo");

                                        if(busno.equals(selectedBusNo)) {
                                            String lat = (String) obj.get("lat");
                                            String lng = (String) obj.get("lng");
                                            Double lati = Double.parseDouble(lat);
                                            Double lngi = Double.parseDouble(lng);
                                            Log.d(LOG_TAG, busno);
                                            // Log.d("checking",String.valueOf(lati+lngi));
                                            Log.d(LOG_TAG, "Message arrived:");
                                            Log.d(LOG_TAG, "   Topic: " + topic);
                                            Log.d(LOG_TAG, " Message: " + message);
                                            func(new LatLng(lati, lngi), busno);
                                            System.out.print(lati);
                                            System.out.print(lngi);
                                        }

                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    } catch (org.json.JSONException e) {
                                        //do nothing
                                    }
                                }
                            } );
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    void func(LatLng point, String busno)
    {        Log.d("checking","func");
/*
        if(busmap.containsKey(busno))
        {
            updateMarker=busmap.get(busno);
            updateMarker.hideInfoWindow();
            updateMarker.setPosition(point);
            //map.removeMarker(busmap.get(busno));
            //  map.updateMarker();
        }
        else {
*/

        MarkerOptions options = new MarkerOptions();
        IconFactory iconFactory = IconFactory.getInstance(mapbox.this);
        Icon icon = iconFactory.fromResource(R.drawable.bus_marker);
        options.icon(icon);
        destinationMarker = map.addMarker(options.position(point));
        System.out.print(point);
        busmap.put(busno, destinationMarker);
//        }

        map.addOnMoveListener(new MapboxMap.OnMoveListener() {
            @Override
            public void onMoveBegin(MoveGestureDetector detector) {
                // user started moving the map
                busmap.forEach((k,v) -> {
                    Marker rm = busmap.get(k);
                    rm.hideInfoWindow();
                });

                busStops.forEach((k,v) -> {
                    Marker rm = busStops.get(k);
                    rm.hideInfoWindow();
                });

            }
            @Override
            public void onMove(MoveGestureDetector detector) {
                // user is moving the map
            }
            @Override
            public void onMoveEnd(MoveGestureDetector detector) {
                // user stopped moving the map
            }
        });

        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                if(originLocation!=null) {
                    destinationPosition=Point.fromLngLat(marker.getPosition().getLongitude(),marker.getPosition().getLatitude());
                    originPosition=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
                    double dist=distance(originPosition,destinationPosition);
                    if(dist<1.00)
                    {
                        dist=dist*1000;
                        DecimalFormat df = new DecimalFormat("#.##");
                        String d = df.format(dist) + " m Away";
                        marker.setTitle(d);
                        marker.showInfoWindow(map,mapView);
                    }
                    else
                    {
                        DecimalFormat df = new DecimalFormat("#.##");
                        String d = df.format(dist) + " Km Away";
                        marker.setTitle(d);
                        marker.showInfoWindow(map,mapView);
                    }
                    marker.hideInfoWindow();
//Toast.makeText(mapbox.this,d,Toast.LENGTH_SHORT).show();
                    Log.d("checking distance", String.valueOf(dist));
                }
                return true;
            }
        });

    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map=mapboxMap;
        enableLocation();
        originPosition=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()), 22));
        //CHANGED HERE

        Log.d(LOG_TAG, "Entered before bus plot");
/*
        plotBusStops(new LatLng(12.8228567,80.0465889),1 );
        plotBusStops(new LatLng(12.822859, 80.044540),2);
        plotBusStops(new LatLng(12.823048, 80.043769),3);
        plotBusStops(new LatLng(12.821558, 80.038131),4);
*/

        try {
            busInfo = new JSONObject(busInfoString);
            //Parsing the JSON Schedule out of the whole JSON
            JSONObject Stops = new JSONObject(busInfo.getString("Stops"));
//            JSONArray StopNames = new JSONArray(busInfo.getString("Time"));
            Iterator<String> stopNames = Stops.keys();

//           for (int i = 0; i < Stops.length(); i++) {
            int i=1;
            while (stopNames.hasNext()) {
                String key = stopNames.next();
                JSONObject details = new JSONObject(Stops.getString(key));
                String lat = details.getString("lat");
                String lng = details.getString("lng");

                Log.i("I", lat);
                Log.i("I", lng);

                plotBusStops(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), i, key );
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("JSON", "JSON object error");
        }

        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Integer zoomBusNumber = settings.getInt("zoomBusStop", -1);
        Log.d("Output", zoomBusNumber.toString());
        //zoomBusNumber=-1;


        try {
            if (zoomBusNumber != -1) {
                JSONObject Stops = new JSONObject(busInfo.getString("Stops"));

                Iterator<String> stopNames = Stops.keys();
                int i = 0;

                while (stopNames.hasNext()) {
                    String key = stopNames.next();
                    JSONObject details = new JSONObject(Stops.getString(key));

                    if (i == zoomBusNumber) {

                        Double lat = Double.valueOf(details.getString("lat"));
                        Double lng = Double.valueOf(details.getString("lng"));

                        Log.i("Output", "Zoom lat" + lat.toString());
                        Log.i("Output", "Zoom lng" + lng.toString());

                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lat, lng))
                                .zoom(17)
                                .build();

                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                        editor.putInt("zoomBusStop", -1);

                        break;

                    }
                    i++;

                }
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void plotBusStops(LatLng point, int i, String stopName)
    {
        Log.d("BusStop 1","Plotted");
        MarkerOptions options = new MarkerOptions();
        IconFactory iconFactory=IconFactory.getInstance(mapbox.this);
        Icon icon=iconFactory.fromResource(R.drawable.stop);
        options.icon(icon);
        Marker stop = map.addMarker(options.position(point));
        stop.setTitle(stopName);
        stop.setSnippet(stopName);
        busStops.put(Integer.toString(i), stop);
    }

    private void enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            initializeLocationLayer();

        }
        else
        {
            permissionsManager=new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){
        locationEngine=new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        locationEngine.requestLocationUpdates();
        Location lastLocation=locationEngine.getLastLocation();
        if(lastLocation!=null)
        {
            originLocation=lastLocation;
            setCameraPosition(lastLocation);
        }
        else
        {
            locationEngine.addLocationEngineListener(this);
        }

    }
    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),18.0));
    }

    public void onConnected() {
        locationEngine.requestLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null)
        {
            originLocation=location;
            setCameraPosition(location);
        }

    }
    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){
        locationLayerPlugin=new LocationLayerPlugin(mapView,map,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);

    }
    @Override
    @SuppressWarnings("MissingPermission")
    protected void onStart() {
        super.onStart();
        if(locationEngine!=null)
        {
            locationEngine.requestLocationUpdates();
        }
        if(locationLayerPlugin!=null)
        {
            locationLayerPlugin.onStart();
        }

        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(locationEngine!=null)
        {
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin!=null)
            locationLayerPlugin.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null)
            locationEngine.deactivate();
        mapView.onDestroy();
    }
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }
    @Override
    public void onPermissionResult(boolean granted) {
        if(granted)
        {
            enableLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* @Override
     public void onMapClick(@NonNull LatLng point) {
         Log.d("checking","onmapclick");
         if(destinationMarker!=null)
         {
             map.removeMarker(destinationMarker);
         }
         destinationMarker=map.addMarker(new MarkerOptions().position(point));

         destinationPosition=Point.fromLngLat(point.getLongitude(),point.getLatitude());
         Log.d("checkinh lat",String.valueOf(point.getLatitude()));
         Log.d("checkinh long",String.valueOf(point.getLongitude()));
         originPosition=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
         getRoute(originPosition,destinationPosition);
         startButton.setEnabled(true);
         startButton.setBackgroundResource(R.color.mapboxBlue);

     }*/
    private void getRoute(Point origin,Point destination)
    {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body()==null)
                        {
                            Log.e(TAG,"No routes found,check rightuser and  access token ");
                            return;
                        }
                        else if(response.body().routes().size()==0)
                        {
                            Log.e(TAG,"No routes found");
                            return;
                        }
                        DirectionsRoute currentRoute=response.body().routes().get(0);
                        if(navigationMapRoute != null)
                        {
                            navigationMapRoute.removeRoute();
                        }
                        else
                        {
                            navigationMapRoute=new NavigationMapRoute(null,mapView,map);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG,"Error:"+t.getMessage());

                    }
                });
    }
}

