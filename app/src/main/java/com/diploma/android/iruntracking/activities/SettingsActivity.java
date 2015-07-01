package com.diploma.android.iruntracking.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.model.User;
import com.diploma.android.iruntracking.utils.Constants;
import com.diploma.android.iruntracking.utils.Validator;
import com.facebook.login.LoginManager;

public class SettingsActivity extends Activity{

    private EditText mName;
    private EditText mWeight;
    private EditText mEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final RunManager runManager = RunManager.get(getApplicationContext());
        final User currentUser = runManager.queryUser(runManager.getCurrentUser().getId());

        mName = (EditText) findViewById(R.id.full_name_TextView);
        mName.setText(currentUser.getName());
        mWeight = (EditText) findViewById(R.id.weight_textView);
        mWeight.setText(String.valueOf(currentUser.getWeight()));
        mEmail = (EditText) findViewById(R.id.email_textView);
        mEmail.setText(String.valueOf(currentUser.getEmail()));
        TextView logout = (TextView) findViewById(R.id.logout_textView);

        mName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newName = ((EditText) v).getText().toString();
                    if (!Validator.isNameValid(newName)) {
                        Toast.makeText(getApplicationContext(), R.string.wrong_name_format,
                                Toast.LENGTH_LONG).show();
                    } else if (!newName.equals(currentUser.getName())) {
                        currentUser.setName(newName);
                        runManager.updateUser(currentUser);
                    }
                }
            }
        });

        mWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    float validWeight;
                    String newWeight = ((EditText) v).getText().toString();
                    if (!Validator.isWeightValid(newWeight)) {
                        Toast.makeText(getApplicationContext(), R.string.wrong_weight_format,
                                Toast.LENGTH_LONG).show();
                    } else if ((validWeight = Float.valueOf(newWeight)) != currentUser.getWeight()) {
                        currentUser.setWeight(validWeight);
                        runManager.updateUser(currentUser);
                    }
                }
            }
        });

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newEmail = ((EditText) v).getText().toString().trim();
                    if (!newEmail.equals(currentUser.getEmail())) {
                        if (!Validator.isEmailValid(newEmail)) {
                            Toast.makeText(getApplicationContext(), R.string.wrong_email_format,
                                    Toast.LENGTH_LONG).show();
                        } else if (runManager.queryUser(newEmail) != null) {
                            Toast.makeText(getApplicationContext(), R.string.email_exists,
                                    Toast.LENGTH_LONG).show();
                            mEmail.setText("");
                        } else {
                            currentUser.setEmail(newEmail);
                            runManager.updateUser(currentUser);
                        }
                    }
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunManager.deleteRunManagerInstance();
                SharedPreferences prefs = getSharedPreferences(
                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().remove(Constants.PREF_USER_ID).apply();
                LoginManager.getInstance().logOut();
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mName.isFocused()) {
                Rect outRect = new Rect();
                mName.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    mName.clearFocus();
                }
            }
            if (mWeight.isFocused()) {
                Rect outRect = new Rect();
                mWeight.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    mWeight.clearFocus();
                }
            }
            if (mEmail.isFocused()) {
                Rect outRect = new Rect();
                mEmail.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    mEmail.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
