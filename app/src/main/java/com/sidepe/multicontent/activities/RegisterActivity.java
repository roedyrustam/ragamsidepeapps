package com.sidepe.multicontent.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.utils.AppController;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.sidepe.multicontent.R;

public class RegisterActivity extends AppCompatActivity {

    CoordinatorLayout registerCoordinatorLayout;
    TextInputEditText firstnameRegister;
    TextInputEditText lastnameRegister;
    TextInputEditText usernameRegister;
    TextInputEditText emailRegister;
    TextInputEditText passwordRegister;
    TextInputEditText referralRegister;
    TextInputLayout ilReferralRegister;
    TextView goToLoginActivity;
    TextView tvReferralGuide;
    private ProgressWheel progressWheelInterpolated;
    String user_onesignal_player_id;
    Button btnRegister;

    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get OneSignal Player ID
        //https://documentation.onesignal.com/docs/android-native-sdk#section--getpermissionsubscriptionstate-
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();
        status.getSubscriptionStatus().getSubscribed();
        status.getSubscriptionStatus().getUserSubscriptionSetting();
        status.getSubscriptionStatus().getPushToken();
        if(status.getSubscriptionStatus().getUserId() == null)
            user_onesignal_player_id = "Not set yet.";
        else
            user_onesignal_player_id = status.getSubscriptionStatus().getUserId();
        //Toast.makeText(RegisterActivity.this, "Player ID: "+user_onesignal_player_id, Toast.LENGTH_LONG).show();


        //Hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        registerCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.registerCoordinatorLayout);

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.register_progress_wheel);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        goToLoginActivity = (TextView)findViewById(R.id.tv_register_login);
        goToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        firstnameRegister = (TextInputEditText)findViewById(R.id.et_register_firstname);
        lastnameRegister = (TextInputEditText)findViewById(R.id.et_register_lastname);
        usernameRegister = (TextInputEditText)findViewById(R.id.et_register_username);
        emailRegister = (TextInputEditText)findViewById(R.id.et_register_email);
        passwordRegister = (TextInputEditText)findViewById(R.id.et_register_password);
        referralRegister = (TextInputEditText)findViewById(R.id.et_register_referral);
        ilReferralRegister = (TextInputLayout)findViewById(R.id.il_register_referral);
        tvReferralGuide = (TextView)findViewById(R.id.tv_referral_guide);

        //Hide if referral system is disable
        if (((AppController) this.getApplication()).getReward_coin_referral_friend().equals("0") && ((AppController) this.getApplication()).getReward_coin_referral_user().equals("0")) {
            //ilReferralRegister.setVisibility(View.GONE);
            tvReferralGuide.setVisibility(View.GONE);
        }

        //Show done button on phone keyboard
        passwordRegister.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hidden the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(registerCoordinatorLayout.getWindowToken(), 0);

                    performRegister();
                    return true;
                }
                return false;
            }
        });

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hidden the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(registerCoordinatorLayout.getWindowToken(), 0);

                performRegister();
            }
        });

    }


    //==========================================================================//
    public void performRegister() {
        final String firstname = firstnameRegister.getText().toString();
        final String lastname = lastnameRegister.getText().toString();
        final String username = usernameRegister.getText().toString();
        final String email = emailRegister.getText().toString();
        final String password = passwordRegister.getText().toString();
        final String referral = referralRegister.getText().toString();

        if (firstname.equals("")) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_empty_fullname, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (firstname.length() < 3) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_firstname_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (lastname.equals("")) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_empty_fullname, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (lastname.length() < 3) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_lastame_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (email.equals("")) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_empty_email, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (email.length() < 8) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_email_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (!Config.isEmailValid(email)) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_email_not_valid, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (username.equals("")) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_empty_mobile, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (username.length() < 5) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_mobile_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (password.equals("")) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_empty_password, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (password.length() < 6) {
            Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_password_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else {
            btnRegister.setEnabled(false);
            btnRegister.setText(R.string.txt_please_wait);
            //Send Register Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.REGISTER_REQUEST_URL + "?api_key=" + Config.API_KEY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            //Toast.makeText(getApplicationContext(),getAnswer,Toast.LENGTH_LONG).show();
                            if (getAnswer.equals("Success")) {
                                Toast.makeText(getApplicationContext(),R.string.txt_register_success,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);

                            }else if (getAnswer.equals("UsernameExist")) {
                                Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_mobile_is_exist, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                btnRegister.setEnabled(true);
                                btnRegister.setText(R.string.txt_register);

                            }else if (getAnswer.equals("EmailExist")) {
                                Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_email_is_exist, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                btnRegister.setEnabled(true);
                                btnRegister.setText(R.string.txt_register);

                            }else if (getAnswer.equals("ReferralNotExist")) {
                                Snackbar snackbar = Snackbar.make(registerCoordinatorLayout, R.string.txt_register_referral_not_exist, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                btnRegister.setEnabled(true);
                                btnRegister.setText(R.string.txt_register);
                            }else {
                                Toast.makeText(getApplicationContext(), getAnswer, Toast.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                                btnRegister.setText(R.string.txt_register);
                            }
                            progressWheelInterpolated.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Error: "+error,Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            btnRegister.setText(R.string.txt_register);
                        }
                    }
            ){
                //To send our parametrs
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("user_firstname",firstname);
                    params.put("user_lastname",lastname);
                    params.put("user_username",username);
                    params.put("user_email",email);
                    params.put("user_password",password);
                    params.put("user_referral",referral);
                    params.put("user_device_type_id","2"); // 2: Android
                    params.put("user_onesignal_player_id",user_onesignal_player_id);

                    return params;
                }
            };

            //To avoid send twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(requestPostResponse);
        }
    }


    //==========================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }


    //==========================================================================//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}