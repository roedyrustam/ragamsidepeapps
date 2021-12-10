package com.sidepe.multicontent.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.sidepe.multicontent.activities.MainActivity.user_role_id;

public class AccountUpgrade extends AppCompatActivity {

    private RadioGroup radioGroupAccountUpgrade;
    private RadioButton radioButton;
    private RadioButton radioButtonBannerAd, radioButtonInterstitialAd, radioButtonVIP;
    private Button btnAccountUpgrade;
    private ProgressWheel progressWheelInterpolated;
    private String upgradeTag, userId, mobileUserLogin;
    private TextView txt_use_coin, txt_after_upgrade, txtBannerAd, txtInterstitialAd, txtVIP;
    private ConstraintLayout constraintlayoutAccountUpgrade;
    private CardView cardViewAccountUpgrade, cardViewAccountUpgradeAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_upgrade);
        setTitle(R.string.txt_account_upgrade);

        if(user_role_id.equals("Not Login")) {
            Toast.makeText(AccountUpgrade.this, R.string.txt_please_login_first, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AccountUpgrade.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        txtBannerAd = (TextView) findViewById(R.id.txtBannerAd);
        txtBannerAd.setText(getString(R.string.txt_requires_coins)+" "+((AppController) this.getApplication()).getReward_coin_banner_ad_coin_req());

        txtInterstitialAd = (TextView) findViewById(R.id.txtInterstitialAd);
        txtInterstitialAd.setText(getString(R.string.txt_requires_coins)+" "+((AppController) this.getApplication()).getReward_coin_interstitial_ad_coin_req());

        txtVIP = (TextView) findViewById(R.id.txtVIP);
        txtVIP.setText(getString(R.string.txt_requires_coins)+" "+((AppController) this.getApplication()).getReward_coin_vip_user_coin_req());

        constraintlayoutAccountUpgrade = (ConstraintLayout) findViewById(R.id.constraintlayoutAccountUpgrade);
        cardViewAccountUpgrade = (CardView) findViewById(R.id.cardViewAccountUpgrade);
        cardViewAccountUpgradeAfter = (CardView) findViewById(R.id.cardViewAccountUpgradeAfter);

        radioButtonBannerAd = (RadioButton) findViewById(R.id.radioButtonBannerAd);
        radioButtonInterstitialAd = (RadioButton) findViewById(R.id.radioButtonInterstitialAd);
        radioButtonVIP = (RadioButton) findViewById(R.id.radioButtonVIP);

        txt_use_coin = (TextView) findViewById(R.id.txt_use_coin);
        txt_use_coin.setText(R.string.txt_loading);
        txt_after_upgrade = (TextView) findViewById(R.id.txt_after_upgrade);

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.account_upgrade_progress_wheel);

        userId = ((AppController) this.getApplication()).getUserId();

        //Check user login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        //Get user coin
        getUserCoin();

        radioGroupAccountUpgrade = (RadioGroup) findViewById(R.id.radioGroupAccountUpgrade);
        btnAccountUpgrade = (Button) findViewById(R.id.btnAccountUpgrade);
        btnAccountUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get selected radio button from radioGroup
                int selectedId = radioGroupAccountUpgrade.getCheckedRadioButtonId();
                //Find the radiobutton by returned id
                //Toast.makeText(AccountUpgrade.this, ""+radioButton.getText(), Toast.LENGTH_LONG).show();
                //Toast.makeText(AccountUpgrade.this, ""+radioButton.getTag(), Toast.LENGTH_LONG).show();

                if (selectedId == -1) {
                    Snackbar snackbar = Snackbar.make(constraintlayoutAccountUpgrade, R.string.txt_please_select_an_option, Snackbar.LENGTH_LONG);
                    snackbar.show();

                }else{
                    radioButton = (RadioButton) findViewById(selectedId);
                    upgradeTag = ""+radioButton.getTag();
                    accountUpgrade(upgradeTag);
                }
            }
        });

    }


    //======================================================//
    public void accountUpgrade(final String upgradeTag) {
        btnAccountUpgrade.setEnabled(false);
        btnAccountUpgrade.setText(R.string.txt_please_wait);
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.ACCOUNT_UPGRADE_URL+"?api_key="+Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        if (getAnswer.equals("SuccessHideBannerAds")) {
                            Toast.makeText(AccountUpgrade.this, getString(R.string.txt_banner_ads_has_been_hidden), Toast.LENGTH_LONG).show();
                            txt_use_coin.setText(getString(R.string.txt_banner_ads_has_been_hidden));
                            cardViewAccountUpgrade.setVisibility(View.GONE);
                            cardViewAccountUpgradeAfter.setVisibility(View.VISIBLE);
                            txt_after_upgrade.setText(R.string.txt_banner_ads_has_been_hidden_description);

                        }else if (getAnswer.equals("SuccessHideInterstitialAds")) {
                            Toast.makeText(AccountUpgrade.this, getString(R.string.txt_interstitial_ads_has_been_hidden), Toast.LENGTH_LONG).show();
                            txt_use_coin.setText(getString(R.string.txt_interstitial_ads_has_been_hidden));
                            cardViewAccountUpgrade.setVisibility(View.GONE);
                            cardViewAccountUpgradeAfter.setVisibility(View.VISIBLE);
                            txt_after_upgrade.setText(R.string.txt_interstitial_ads_has_been_hidden_description);
                            btnAccountUpgrade.setEnabled(true);
                            btnAccountUpgrade.setText(R.string.txt_account_upgrade);

                        }else if (getAnswer.equals("SuccessVIP")) {
                            Toast.makeText(AccountUpgrade.this, getString(R.string.txt_your_account_has_changed_to_vip), Toast.LENGTH_LONG).show();
                            txt_use_coin.setText(getString(R.string.txt_your_account_has_changed_to_vip));
                            cardViewAccountUpgrade.setVisibility(View.GONE);
                            cardViewAccountUpgradeAfter.setVisibility(View.VISIBLE);
                            txt_after_upgrade.setText(R.string.txt_your_account_has_changed_to_vip_description);
                            btnAccountUpgrade.setEnabled(true);
                            btnAccountUpgrade.setText(R.string.txt_account_upgrade);

                        }else if (getAnswer.equals("NotEnoughCoin")) {
                            Toast.makeText(AccountUpgrade.this, getString(R.string.txt_not_enough_coin), Toast.LENGTH_LONG).show();
                            btnAccountUpgrade.setEnabled(true);
                            btnAccountUpgrade.setText(R.string.txt_account_upgrade);

                        }else if (getAnswer.equals("Failed")) {
                            Toast.makeText(AccountUpgrade.this, "Failed: "+getAnswer, Toast.LENGTH_LONG).show();
                            btnAccountUpgrade.setEnabled(true);
                            btnAccountUpgrade.setText(R.string.txt_account_upgrade);
                        }

                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("upgrade_tag",upgradeTag);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(30000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
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
                        ((AppController) AccountUpgrade.this.getApplication()).setUserCoin(jsonObject.getString("user_coin"));
                        txt_use_coin.setText(getString(R.string.txt_your_balance)+" "+jsonObject.getString("user_coin")+" "+getString(R.string.txt_coins));

                        if(Integer.valueOf(((AppController) AccountUpgrade.this.getApplication()).getReward_coin_banner_ad_coin_req()) > Integer.valueOf(jsonObject.getString("user_coin")))
                            radioButtonBannerAd.setEnabled(false);
                        if(Integer.valueOf(((AppController) AccountUpgrade.this.getApplication()).getReward_coin_interstitial_ad_coin_req()) > Integer.valueOf(jsonObject.getString("user_coin")))
                            radioButtonInterstitialAd.setEnabled(false);
                        if(Integer.valueOf(((AppController) AccountUpgrade.this.getApplication()).getReward_coin_vip_user_coin_req()) > Integer.valueOf(jsonObject.getString("user_coin")))
                            radioButtonVIP.setEnabled(false);

                        if(jsonObject.getString("user_hide_banner_ad").equals("1"))
                            radioButtonBannerAd.setEnabled(false);
                        if(jsonObject.getString("user_hide_interstitial_ad").equals("1"))
                            radioButtonInterstitialAd.setEnabled(false);
                        if(jsonObject.getString("user_role_id").equals("6"))
                            radioButtonVIP.setEnabled(false);

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
                //Toast.makeText(AccountUpgrade.this, R.string.txt_error+" "+error, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }
}
