package in.dailyhunt.ugc.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import in.dailyhunt.ugc.R;
import in.dailyhunt.ugc.Utilities.Pair;
import in.dailyhunt.ugc.Utilities.Uploader;
import in.dailyhunt.ugc.Utilities.UtilProperties;


public class LoginActivity extends AppCompatActivity {
    private EditText _email;
    private EditText _password;
    private Button _loginbutton;
    private TextView _signuplink;
    private static final int REQUEST_SIGNUP = 0;

    private int userId;
    private String response;
    private int responseCode;

    private ArrayList<Pair> loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _email = findViewById(R.id.username);
        _password = findViewById(R.id.password);
        _loginbutton = findViewById(R.id.button_login);
        _signuplink = (TextView) findViewById(R.id.link_signup);

        _loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });

        _signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Log.d("Registration", "Registration successful");
            }
        }

    }

    public void login() {
        Log.d("login", "Login");
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginbutton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                makeLoginRequest();
                parseJson();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (responseCode== HttpURLConnection.HTTP_OK)
                    onLoginSuccess();
                else
                    onLoginFailed();
                progressDialog.dismiss();
            }
        }.execute();


    }

    // Parse response json string
    private void parseJson() {
        try {
            JSONObject jsonObject = new JSONObject(response);
            responseCode = jsonObject.getInt("status");
            JSONObject dataObject = (JSONObject) jsonObject.get("data");
            userId = dataObject.getInt("user_id");
            Log.d("JSON","Response code : "+responseCode);
            Log.d("JSON","User Id : "+userId);
            Log.d("Response",response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeLoginRequest() {

        loginData = new ArrayList<>();
        loginData.add(new Pair("email", _email.getText().toString()));
        loginData.add(new Pair("password",_password.getText().toString()));
        loginData.add(new Pair("device","android"));

        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    response=Uploader.sendPostRequest(UtilProperties.getProperty("loginApi",getApplicationContext()),loginData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loginThread.start();

        try {
            loginThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onLoginSuccess() {
        _loginbutton.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();

        Intent intent  = new Intent(LoginActivity.this,HomeActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
        _loginbutton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _email.getText().toString();
        String password = _password.getText().toString();
        Log.d("credentials", email + " " + password);

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("Enter a valid email address");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (password.isEmpty()) {
            _password.setError("Enter password");
            valid = false;
        } else {
            _password.setError(null);
        }

        return valid;
    }


}
