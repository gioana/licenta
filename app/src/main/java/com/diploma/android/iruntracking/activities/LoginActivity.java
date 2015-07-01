package com.diploma.android.iruntracking.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.model.User;
import com.diploma.android.iruntracking.utils.Constants;
import com.diploma.android.iruntracking.utils.Validator;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.GraphJSONObjectCallback;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private CallbackManager mCallbackManager;
    private RunManager mRunManager;

    private GraphJSONObjectCallback graphCallback = new GraphJSONObjectCallback() {
        @Override
        public void onCompleted(JSONObject user, GraphResponse graphResponse) {
            User fbUser = new User();
            fbUser.setEmail(user.optString("email"));
            fbUser.setFacebookId(user.optString("id"));
            loginUser(fbUser);
        }
    };

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            GraphRequest.newMeRequest(accessToken, graphCallback).executeAsync();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplication(), "fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException exception) {
            Toast.makeText(getApplication(), "error", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRunManager = RunManager.get(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(Constants.PREF_USER_ID)) {
            // user is logged in
            long id = prefs.getLong(Constants.PREF_USER_ID, -1);
            if (id != -1) {
                User currentUser = mRunManager.queryUser(id);
                goToMainActivity(currentUser);
            }
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().registerCallback(mCallbackManager, facebookCallback);
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = (EditText) findViewById(R.id.email_editText);
                EditText passwordEditText = (EditText) findViewById(R.id.password_editText);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                loginUser(new User(email, password));
            }
        });

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = (EditText) findViewById(R.id.email_editText);
                EditText passwordEditText = (EditText) findViewById(R.id.password_editText);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!Validator.isEmailValid(email)) {
                    Toast.makeText(getApplicationContext(), R.string.wrong_email_format,
                            Toast.LENGTH_LONG).show();
                } else if (!Validator.isPasswordValid(password)) {
                    Toast.makeText(getApplicationContext(), R.string.wrong_password_format,
                            Toast.LENGTH_LONG).show();
                } else {
                    registerUser(new User(email, password));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginUser(User user) {
        User dbUser = mRunManager.queryUser(user.getEmail());
        if (dbUser == null) {
            if (user.getFacebookId() != null) {
                registerUser(user);
            } else {
                Toast.makeText(getApplicationContext(), R.string.wrong_email, Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            if (user.getFacebookId() == null && !user.getPassword().equals(dbUser.getPassword())) {
                Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_LONG).show();
                return;
            }
        }
        goToMainActivity(dbUser);
    }

    private void registerUser(User newUser) {
        if (mRunManager.queryUser(newUser.getEmail()) == null) {
            long userId = mRunManager.insertUser(newUser);
            newUser.setId(userId);
        } else {
            Toast.makeText(getApplicationContext(), R.string.email_exists, Toast.LENGTH_LONG).show();
            return;
        }
        goToMainActivity(newUser);
    }

    private void goToMainActivity(User user) {
        getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREF_USER_ID, user.getId())
                .commit();

        mRunManager.setCurrentUser(user);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}