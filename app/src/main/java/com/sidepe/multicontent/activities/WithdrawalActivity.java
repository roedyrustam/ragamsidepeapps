package com.sidepe.multicontent.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.sidepe.multicontent.activities.MainActivity.user_role_id;

public class WithdrawalActivity extends AppCompatActivity {
    ConstraintLayout constraintlayoutWithdrawalCoin;
    String userId, mobileUserLogin, spinnerAccountTypeId, spinnerAccountType, etAccountName, etUserComment;
    TextInputEditText et_withdrawal_account_name, et_withdrawal_user_comment;
    Spinner spinner;
    ArrayList<String> accountType;
    int accountTypePosition;
    Map<String,String> accountTypeMap = new HashMap<String,String>();
    Button btnWithdrawalCoin;
    private ProgressWheel progressWheelInterpolated;
    TextView txtWithdrawalStatus, txt_use_coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        if(user_role_id.equals("Not Login")) {
            Toast.makeText(WithdrawalActivity.this, R.string.txt_please_login_first, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(WithdrawalActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.withdrawal_progress_wheel);
        userId = ((AppController) this.getApplication()).getUserId();
        //Check user login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        accountType = new ArrayList<String>();
        constraintlayoutWithdrawalCoin = (ConstraintLayout) findViewById(R.id.constraintlayoutWithdrawalCoin);
        et_withdrawal_account_name = (TextInputEditText) findViewById(R.id.et_withdrawal_account_name);
        et_withdrawal_user_comment = (TextInputEditText) findViewById(R.id.et_withdrawal_user_comment);
        txtWithdrawalStatus = (TextView) findViewById(R.id.txtWithdrawalStatus);
        txt_use_coin = (TextView) findViewById(R.id.txt_use_coin);
        btnWithdrawalCoin = (Button) findViewById(R.id.btnWithdrawalCoin);
        spinner = (Spinner) findViewById(R.id.spinnerAccountType);

        getAccountTypeData();
        getUserCoin();

        btnWithdrawalCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide keyboard
                ((InputMethodManager) getSystemService(WithdrawalActivity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                if (et_withdrawal_account_name.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(constraintlayoutWithdrawalCoin, R.string.txt_withdrawal_account_name_is_empty, Snackbar.LENGTH_LONG);
                    snackbar.show();

                }else{
                    if(Integer.valueOf(((AppController) WithdrawalActivity.this.getApplication()).getReward_coin_withdrawal_coin_minimum_req()) > Integer.valueOf(((AppController) WithdrawalActivity.this.getApplication()).getUserCoin())) {
                        // You have not enough coin to withdrawal
                        txtWithdrawalStatus.setVisibility(View.VISIBLE);
                        txtWithdrawalStatus.setText(getString(R.string.txt_you_have_not_enough_coin_to_withdrawal)+" "+getString(R.string.txt_minimum_coin_required)+" "+((AppController) WithdrawalActivity.this.getApplication()).getReward_coin_withdrawal_coin_minimum_req());

                    }else{
                        performWithdrawalCoinRequest();
                    }
                }
            }
        });
    }

    //============================================================================//
    private void getAccountTypeData(){
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_WITHDRAWAL_ACCOUNT_TYPE_URL + "?api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String key = jsonObject.getString("withdrawal_account_type_id");
                        String value = jsonObject.getString("withdrawal_account_type_title");
                        accountType.add(value);

                        //Map
                        accountTypeMap = new LinkedHashMap<String, String>();
                        accountTypeMap.put(key,value);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Setting adapter to show the items in the spinner
                //Array
                spinner.setAdapter(new ArrayAdapter<String>(WithdrawalActivity.this, android.R.layout.simple_spinner_dropdown_item, accountType));
                //Map
                //spinner.setAdapter(new LinkedHashMapAdapter<String, String>(WithdrawalActivity.this, android.R.layout.simple_spinner_dropdown_item, accountTypeMap));


                progressWheelInterpolated.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                Toast.makeText(WithdrawalActivity.this, R.string.txt_error+": "+error, Toast.LENGTH_SHORT).show();
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    //==========================================================================//
    public void performWithdrawalCoinRequest() {
        spinnerAccountType = spinner.getSelectedItem().toString();
        accountTypePosition = spinner.getSelectedItemPosition();
        accountTypePosition = accountTypePosition+1;

        etAccountName = et_withdrawal_account_name.getText().toString();
        etUserComment = et_withdrawal_user_comment.getText().toString();

        if (etAccountName.equals("")) {
            Snackbar snackbar = Snackbar.make(constraintlayoutWithdrawalCoin, R.string.txt_withdrawal_account_name_is_empty, Snackbar.LENGTH_LONG);
            snackbar.show();

        } else {
            btnWithdrawalCoin.setEnabled(false);
            btnWithdrawalCoin.setText(R.string.txt_sending);
            //Send Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.WITHDRAWAL_COIN_REQUEST_URL + "?api_key=" + Config.API_KEY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.equals("Success")) {
                                Toast.makeText(WithdrawalActivity.this, R.string.txt_withdrawal_request_sent_successfully, Toast.LENGTH_LONG).show();
                                progressWheelInterpolated.setVisibility(View.GONE);
                                txtWithdrawalStatus.setVisibility(View.VISIBLE);
                                txtWithdrawalStatus.setText(R.string.txt_withdrawal_request_sent_successfully_please_wait);
                                btnWithdrawalCoin.setText(R.string.txt_done);
                                btnWithdrawalCoin.setEnabled(false);

                            }else if (getAnswer.equals("NotEnoughCoin")){
                                Toast.makeText(WithdrawalActivity.this, R.string.txt_you_have_not_enough_coin_to_withdrawal, Toast.LENGTH_LONG).show();
                                progressWheelInterpolated.setVisibility(View.GONE);
                                txtWithdrawalStatus.setVisibility(View.VISIBLE);
                                txtWithdrawalStatus.setText(getString(R.string.txt_you_have_not_enough_coin_to_withdrawal)+" "+getString(R.string.txt_minimum_coin_required)+" "+((AppController) WithdrawalActivity.this.getApplication()).getReward_coin_withdrawal_coin_minimum_req());
                                btnWithdrawalCoin.setText(R.string.txt_done);
                                btnWithdrawalCoin.setEnabled(false);

                            }else{
                                Toast.makeText(WithdrawalActivity.this, R.string.txt_error+": "+getAnswer, Toast.LENGTH_LONG).show();
                            }
                                progressWheelInterpolated.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                        }
                    }
            ) {
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("withdrawal_user_id", userId);
                    params.put("withdrawal_req_coin", ((AppController) WithdrawalActivity.this.getApplication()).getUserCoin());
                    params.put("withdrawal_account_type", accountTypePosition+"");
                    params.put("withdrawal_account_name", etAccountName);
                    params.put("withdrawal_user_comment", etUserComment); // Web:1 Android: 2 iOS: 3 Other: 4

                    return params;
                }
            };

            //To avoid send twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(requestPostResponse);
        }


    }


    //==========================================================================//
    public void getUserCoin() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, Config.GET_USER_COIN_URL + "?user_username="+mobileUserLogin+"&api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        //Set local variable and use it in whole application
                        ((AppController) WithdrawalActivity.this.getApplication()).setUserCoin(jsonObject.getString("user_coin"));
                        Float your_cash = Float.valueOf(((AppController) WithdrawalActivity.this.getApplication()).getUserCoin()) * Float.valueOf(((AppController) WithdrawalActivity.this.getApplication()).getReward_coin_price_of_each_coin());
                        txt_use_coin.setText(getString(R.string.txt_your_balance)+" "+jsonObject.getString("user_coin")+" "+getString(R.string.txt_coins)+" = "+ getString(R.string.txt_currency)+" "+new DecimalFormat("0.00").format(your_cash));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                //Toast.makeText(WithdrawalActivity.this, R.string.txt_error+" "+error, Toast.LENGTH_SHORT).show();
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    //============================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }


    //============================================================================//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
