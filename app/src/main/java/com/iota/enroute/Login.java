package com.iota.enroute;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    // Screen fields
    private EditText inUsername;
    private EditText inPassword;
    private AlertDialog userDialog;
    private String accessToken;
    private String refreshToken;
    private String appid;
    private boolean connected;
    //Give your SharedPreferences file a name and save it to a static variable
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "MainActivity";
    private String data="";

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

    private class HttpGetter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            connected = isInternetAvailable();

            if(!connected)
            {
                return "Check your Internet Connection";
            }

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://ec2-65-0-184-243.ap-south-1.compute.amazonaws.com:5000/login").openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                httpURLConnection.setDoOutput(true);

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
            catch (ConnectException e)
            {
                return "Server is Down. Try again later!";
            }

            catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            Log.d("Output", data);
            return data;
        }
    }

    private class HttpForgot extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            connected = isInternetAvailable();

            if(!connected)
            {
                return "Check your Internet Connection";
            }

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://ec2-65-0-184-243.ap-south-1.compute.amazonaws.com:5000/forgot").openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                httpURLConnection.setDoOutput(true);

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
            } catch (ConnectException e)
            {
                return "Server is Down. Try again later!";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            Log.d("Output", data);

            return data;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loadingCircle).setVisibility(View.GONE);

        FirebaseApp.initializeApp(this);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        appid = task.getResult().getToken();
                        Log.d("appid",appid);

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                    }
                });

        SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("appid",appid);
        editor.commit();

        if((Boolean) settings.getBoolean("hasLoggedIn", false))
        {
            Intent intent = new Intent(getApplicationContext(), mapbox.class);
            startActivity(intent);
            finish();
        }


        TextView goToLogin = findViewById(R.id.goToRegister);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("pressed", "Login intent");
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        TextView forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inUsername = (EditText) findViewById(R.id.username);

                final String username = inUsername.getText().toString();

                if(username == null || username.length() < 1) {
                    Log.d("User", "Short username");

                    showDialogMessage("Oops!", "Username cannot be Empty");
                    return;
                }
                else {

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("username", username);

                        String result = new HttpForgot().execute("Forgot", postData.toString()).get();

                        if(result.equals("True")) {
                            Toast.makeText(Login.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                            i.putExtra("username", username);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            showDialogMessage("Oops!", result);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        Button signin = findViewById(R.id.signin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                inUsername = (EditText) findViewById(R.id.username);
                inPassword = (EditText) findViewById(R.id.password);

                final String username = inUsername.getText().toString();
                final String password = inPassword.getText().toString();

                if(username == null || username.length() < 1) {
                    Log.d("User","Short username");

                    showDialogMessage("Oops!", "Username cannot be Empty");
                    return;
                }

                if(password == null || password.length() < 1) {
                    Log.d("P","Short Pass");
                    showDialogMessage("Oops!", "Password Field cannot be Empty");
                    return;
                }

                findViewById(R.id.loadingCircle).setVisibility(View.VISIBLE);

                String result="";

                JSONObject postData = new JSONObject();
//                try {

                    Toast.makeText(Login.this, "Signing in...", Toast.LENGTH_SHORT).show();

//                    postData.put("username", username );
//                    postData.put("password", password);
                 //   postData.put("appid", appid);

                    //Get results from the server
//                    result = new HttpGetter().execute("Login", postData.toString()).get();
//
//                    Log.d("Output",result.substring(0,4));
//
//                    JSONObject jsonResult = new JSONObject(result);
//
//                    SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0) ;// 0 - for private mode
//                    SharedPreferences.Editor editor = settings.edit();
//                    //Set "hasLoggedIn" to true
//                    //accessToken = jsonResult.getString("AccessToken");
//                    //refreshToken = jsonResult.getString("RefreshToken");
//                    //editor.putString("accessToken", accessToken);
//                    editor.putBoolean("hasLoggedIn",true);
                    // editor.putString("refreshToken", refreshToken);
                    // Commit the edits!
                    editor.commit();
                    Toast.makeText(Login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();

                    findViewById(R.id.loadingCircle).setVisibility(View.GONE);
                    Intent i = new Intent(getApplicationContext(), mapbox.class);
                    startActivity(i);
                    finish();
//                }
//                catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                } catch (ExecutionException e1) {
//                    e1.printStackTrace();
//                }
//                catch(JSONException err) {
//                    Log.d("Error",err.toString());
//                    showDialogMessage("Oops!", result);
//                    findViewById(R.id.loadingCircle).setVisibility(View.GONE);
//                }

            }
        });

    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

}


