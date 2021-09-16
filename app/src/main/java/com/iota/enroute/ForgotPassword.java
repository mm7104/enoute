package com.iota.enroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ForgotPassword extends AppCompatActivity {

    private String data="";
    private AlertDialog userDialog;
    private String username;

    private Button forgotPasswordButton;
    // Screen fields
    private EditText password;
    private EditText confirmPassword;
    private EditText code;

    private class HttpConfirm extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            HttpURLConnection httpURLConnection = null;
            try {

                data="";

                httpURLConnection = (HttpURLConnection) new URL("http://13.234.77.157:5000/confor").openConnection();
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
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        username = getIntent().getStringExtra("username");

        forgotPasswordButton = findViewById(R.id.ForgotPassword_button);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = (EditText) findViewById(R.id.password);
                code = (EditText) findViewById(R.id.Code);

                final String pwd = password.getText().toString();
                final String confirmCode = code.getText().toString();

                if(pwd == null || pwd.length() < 6) {
                    Log.d("User","Null password");

                    showDialogMessage("Oops!", "Password cannot be empty");
                    return;
                }

                if(pwd.length() < 6)
                {
                    showDialogMessage("Oops!", "Password length too short!");
                    return;
                }

                if(confirmCode == null)
                {
                    showDialogMessage("Oops!", "Confirmation Code is Empty!");
                    return;
                }

                JSONObject postData = new JSONObject();
                try {
                    postData.put("username", username);
                    postData.put("password", pwd);
                    postData.put("code", confirmCode);

                    String result = new ForgotPassword.HttpConfirm().execute("Confirm", postData.toString()).get();

                    if(result.equals("True")) {
                        Toast.makeText(ForgotPassword.this, "Password Successfully Changed!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Login.class);
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
