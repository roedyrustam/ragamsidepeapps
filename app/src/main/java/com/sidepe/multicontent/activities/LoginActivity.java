package com.sidepe.multicontent.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.sidepe.multicontent.R;

public class LoginActivity extends AppCompatActivity {
    TextView goToRegisterActivity;
    TextView goToForgotPasswordActivity;
    RelativeLayout loginRelativeLayout;
    TextInputEditText mobileLogin;
    TextInputEditText passwordLogin;
    private ProgressWheel progressWheelInterpolated;
    Button btnLogin;

    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        loginRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutLogin);

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.login_progress_wheel);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        goToRegisterActivity = (TextView)findViewById(R.id.tv_login_register);
        goToRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        goToForgotPasswordActivity = (TextView) findViewById(R.id.tv_login_forgot_password);
        goToForgotPasswordActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Config.EXTERNAL_FORGOT_PASSWORD_URL));
                startActivity(i);
            }
        });

        mobileLogin = (TextInputEditText)findViewById(R.id.et_login_mobile);
        passwordLogin = (TextInputEditText)findViewById(R.id.et_login_password);

        //Show done button on phone keyboard
        passwordLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hidden the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(loginRelativeLayout.getWindowToken(), 0);

                    performLogin();
                    return true;
                }
                return false;
            }
        });

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hidden the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginRelativeLayout.getWindowToken(), 0);

                performLogin();
            }
        });

    }


    //==========================================================================//
    public void performLogin() {
        final String mobile = mobileLogin.getText().toString();
        final String password = passwordLogin.getText().toString();

        if (mobile.equals("")) {
            Snackbar snackbar = Snackbar.make(loginRelativeLayout, R.string.txt_empty_mobile, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (mobile.length() < 5) {
            Snackbar snackbar = Snackbar.make(loginRelativeLayout, R.string.txt_mobile_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (password.equals("")) {
            Snackbar snackbar = Snackbar.make(loginRelativeLayout, R.string.txt_empty_password, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (password.length() < 6) {
            Snackbar snackbar = Snackbar.make(loginRelativeLayout, R.string.txt_password_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else{
            btnLogin.setEnabled(false);
            btnLogin.setText(R.string.txt_please_wait);
            //Send Login Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.LOGIN_REQUEST_URL + "?api_key=" + Config.API_KEY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.equals("Success")) {
                                //Toast.makeText(getApplicationContext(),getAnswer,Toast.LENGTH_LONG).show();
                                //Mobile and password is correct
                                //Set sharedpreferences
                                SharedPreferences.Editor editor = getSharedPreferences("USER_LOGIN", MODE_PRIVATE).edit();
                                editor.putString("mobile", mobile);
                                editor.commit();
                                Toast.makeText(getApplicationContext(),R.string.txt_success_login,Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(LoginActivity.this, OneSplashActivity.class);
                                finish();
                                //finishAffinity(); // Close all activites
                                //System.exit(0);  // Closing files, releasing resources
                                startActivity(intent);

                            }else if (getAnswer.equals("Failed")) {
                                Snackbar snackbar = Snackbar.make(loginRelativeLayout, R.string.txt_failed_login, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                btnLogin.setEnabled(true);
                                btnLogin.setText(R.string.txt_login);

                            }else {
                                Toast.makeText(getApplicationContext(), getAnswer, Toast.LENGTH_LONG).show();
                                btnLogin.setEnabled(true);
                                btnLogin.setText(R.string.txt_login);
                            }
                            progressWheelInterpolated.setVisibility(View.GONE);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            btnLogin.setText(R.string.txt_login);
                        }
                    }
            ){
                //To send our parametrs
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("user_username",mobile);
                    params.put("user_password",password);

                    return params;
                }
            };

            //To avoid send twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(30000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
