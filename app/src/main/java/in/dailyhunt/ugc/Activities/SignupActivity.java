package in.dailyhunt.ugc.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by pinal on 28/1/18.
 */

public class SignupActivity extends AppCompatActivity {
    private EditText _fullname;
    private EditText _email;
    private EditText _password;
    private EditText _repassword;
    private Button _signupbtn;
    private TextView _loginlink;
    private String response;
    private int responseCode;
    private int userId;

    private final String signupUrl = "http://10.42.0.40/taggify-laravel/public/register";
    private ArrayList<Pair> signupData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        _fullname = findViewById(R.id.your_full_name);
        _email = findViewById(R.id.your_email_address);
        _password = findViewById(R.id.create_new_password);
        _repassword = findViewById(R.id.retype_new_password);
        _signupbtn = findViewById(R.id.signup_button);
        _loginlink = findViewById(R.id.link_login);

        _loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        _signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

    public void signup() {
        Log.d("signup", "Sign up");
        if (!validate()) {
            onSignupFailed();
            return;
        }
        _signupbtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        makeSignupRequest();
        parseJson();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        if (responseCode== HttpURLConnection.HTTP_OK)
                            onSignupSuccess();
                        else
                            onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    private void makeSignupRequest() {

        signupData = new ArrayList<>();
        signupData.add(new Pair("name",_fullname.getText().toString()));
        signupData.add(new Pair("email",_email.getText().toString()));
        signupData.add(new Pair("password",_password.getText().toString()));
        signupData.add(new Pair("device","android"));

        Thread signupThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    response = Uploader.sendPostRequest(signupUrl,signupData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        signupThread.start();
        try {
            signupThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseJson() {
        // Parse response json string
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

    public void onSignupSuccess() {
        _signupbtn.setEnabled(true);
        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration Failed", Toast.LENGTH_SHORT).show();

        _signupbtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _fullname.getText().toString();
        String email = _email.getText().toString();
        String password = _password.getText().toString();
        String repassword = _repassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _fullname.setError("At least 3 characters");
            valid = false;
        } else {
            _fullname.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("Enter a valid email address");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 15) {
            _password.setError("Enter a password between 4 and 15 alphanumeric characters");
            valid = false;
        } else {
            _password.setError(null);
        }

        if (repassword.isEmpty() || !(password.equals(repassword))) {
            _repassword.setError("Password doesn't match");
            valid = false;
        } else {
            _repassword.setError(null);
        }
        return valid;
    }

}
