package com.iota.enroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Register extends AppCompatActivity {

    // Screen fields
    private EditText inUsername;
    private EditText inPassword;
    private EditText inEmail;

    private AlertDialog userDialog;

    private String data="";

    private class HttpRegister extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://ec2-65-0-184-243.ap-south-1.compute.amazonaws.com:5000/signup").openConnection();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView goToLogin = findViewById(R.id.goToRegister);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("pressed", "Login intent");
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        Button signup = findViewById(R.id.signin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inUsername = (EditText) findViewById(R.id.username);
                inPassword = (EditText) findViewById(R.id.password);
                inEmail = (EditText) findViewById(R.id.email);

                final String username = inUsername.getText().toString();
                final String password = inPassword.getText().toString();
                final String email = inEmail.getText().toString();

                if (username == null || username.length() < 1) {
                    Log.d("User", "Short username");

                    showDialogMessage("Oops!", "Username cannot be Empty");
                    return;
                }

                if (password == null || password.length() < 1) {
                    Log.d("P", "Short Pass");
                    showDialogMessage("Oops!", "Password Field cannot be Empty");
                    return;
                }

                if (email == null || email.length() < 1) {
                    Log.d("P", "Short Email");
                    showDialogMessage("Oops!", "Email ID cannot be Empty");
                    return;
                }

                JSONObject postData = new JSONObject();
                try {
                    postData.put("username", username);
                    postData.put("password", password);
                    postData.put("email", email);

                    String result = new HttpRegister().execute("Login", postData.toString()).get();

                    if (result.equals("True")) {
                        showDialogMessage("Yay!", "User successfully Registered! Check your Mail to confirm your registration!");
                    } else {
                        showDialogMessage("Oops! could not register", result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showDialogMessage(String title, String body){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(title.equals("Yay!"))
                    {
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

}
