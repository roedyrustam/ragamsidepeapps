package com.sidepe.multicontent.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sidepe.multicontent.BuildConfig;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OneSplashActivity extends AppCompatActivity {

    String mobileUserLogin;
    String settingVersionCode;
    Button btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_splash);

        // Full Screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide Toolabr
        getSupportActionBar().hide();

        btnTryAgain = (Button)findViewById(R.id.btnTryAgain);

        //Check user login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);
        if (mobileUserLogin != null) { //User is Login
            //Get all information after login
            getAllAfterLogin();

        }else { //User Not Login
            //Get all information before login
            getAllBeforeLogin();
        }
    }


    //==========================================================================//
    public void getAllAfterLogin() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_ALL_AFTER_LOGIN_URL + "?user_username="+mobileUserLogin+"&api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        //Set local variable and use it in whole application
                        ((AppController) OneSplashActivity.this.getApplication()).setUserId(jsonObject.getString("user_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserUserName(jsonObject.getString("user_username"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserFirstName(jsonObject.getString("user_firstname"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserLastName(jsonObject.getString("user_lastname"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserMobile(jsonObject.getString("user_mobile"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserEmail(jsonObject.getString("user_email"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserCredit(jsonObject.getString("user_credit"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserCoin(jsonObject.getString("user_coin"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserReferral(jsonObject.getString("user_referral"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserEmailVerified(jsonObject.getString("user_email_verified"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserMobileVerified(jsonObject.getString("user_mobile_verified"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserRoleID(jsonObject.getString("user_role_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserRoleTitle(jsonObject.getString("user_role_title"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingAppName(jsonObject.getString("setting_app_name"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingEmail(jsonObject.getString("setting_email"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingWebsite(jsonObject.getString("setting_website"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingVersionCode(jsonObject.getString("setting_version_code"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingAndroidMaintenance(jsonObject.getString("setting_android_maintenance"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingTextMaintenance(jsonObject.getString("setting_text_maintenance"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserImage(jsonObject.getString("user_image"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserHideBannerAd(jsonObject.getString("user_hide_banner_ad"));
                        ((AppController) OneSplashActivity.this.getApplication()).setUserHideInterstitialAd(jsonObject.getString("user_hide_interstitial_ad"));

                        ((AppController) OneSplashActivity.this.getApplication()).setSettingOneSignaAppId(jsonObject.getString("setting_one_signal_app_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingYouTubeApiKey(jsonObject.getString("setting_youtube_api_key"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingAppId(jsonObject.getString("admob_setting_app_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingBannerUnitId(jsonObject.getString("admob_setting_banner_unit_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingInterstitialUnitId(jsonObject.getString("admob_setting_interstitial_unit_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingBannerSize(jsonObject.getString("admob_setting_banner_size"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingInterstitialClicks(jsonObject.getString("admob_setting_interstitial_clicks"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingBannerStatus(jsonObject.getString("admob_setting_banner_status"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingInterstitialStatus(jsonObject.getString("admob_setting_interstitial_status"));

                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_banner_ad_exp(jsonObject.getString("reward_coin_banner_ad_exp"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_interstitial_ad_exp(jsonObject.getString("reward_coin_interstitial_ad_exp"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_rewarded_ad_exp(jsonObject.getString("reward_coin_rewarded_ad_exp"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_native_ad_exp(jsonObject.getString("reward_coin_native_ad_exp"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_play_game_exp(jsonObject.getString("reward_coin_play_game_exp"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_banner_ad_coin_req(jsonObject.getString("reward_coin_banner_ad_coin_req"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_interstitial_ad_coin_req(jsonObject.getString("reward_coin_interstitial_ad_coin_req"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_rewarded_ad_coin_req(jsonObject.getString("reward_coin_rewarded_ad_coin_req"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_native_ad_coin_req(jsonObject.getString("reward_coin_native_ad_coin_req"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_vip_user_coin_req(jsonObject.getString("reward_coin_vip_user_coin_req"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_banner_ad_click(jsonObject.getString("reward_coin_banner_ad_click"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_interstitial_ad_click(jsonObject.getString("reward_coin_interstitial_ad_click"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_rewarded_ad_click(jsonObject.getString("reward_coin_rewarded_ad_click"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_native_ad_click(jsonObject.getString("reward_coin_native_ad_click"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_write_review(jsonObject.getString("reward_coin_write_review"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_play_game(jsonObject.getString("reward_coin_play_game"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_referral_user(jsonObject.getString("reward_coin_referral_user"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_referral_friend(jsonObject.getString("reward_coin_referral_friend"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_publish_game(jsonObject.getString("reward_coin_publish_game"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_withdrawal_coin_minimum_req(jsonObject.getString("reward_coin_withdrawal_coin_minimum_req"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_price_of_each_coin(jsonObject.getString("reward_coin_price_of_each_coin"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_watching_video(jsonObject.getString("reward_coin_watching_video"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_watching_video_exp(jsonObject.getString("reward_coin_watching_video_exp"));

                        //Check user_status
                        String user_status = jsonObject.getString("user_status");
                        if (!user_status.equals("1")) {
                            Toast.makeText(getApplicationContext(), R.string.txt_your_account_has_been_blocked_please_contact_administrator, Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            //Check App Version
                            versionCheck();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                //Toast.makeText(MainActivity.this, R.string.txt_error, Toast.LENGTH_SHORT).show();
                //No Internet Connection
                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(OneSplashActivity.this);
                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_whoops));
                builderCheckUpdate.setMessage(getResources().getString(R.string.txt_no_network_connection_found));
                builderCheckUpdate.setCancelable(false);

                builderCheckUpdate.setPositiveButton(
                        getResources().getString(R.string.txt_try_again),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(getIntent());
                            }
                        });

                builderCheckUpdate.setNegativeButton(
                        getResources().getString(R.string.txt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                alert1CheckUpdate.show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    //==========================================================================//
    public void getAllBeforeLogin() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_ALL_BEFORE_LOGIN_URL + "?api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        //Set local variable and use it in whole application
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingAppName(jsonObject.getString("setting_app_name"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingEmail(jsonObject.getString("setting_email"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingWebsite(jsonObject.getString("setting_website"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingVersionCode(jsonObject.getString("setting_version_code"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingAndroidMaintenance(jsonObject.getString("setting_android_maintenance"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingTextMaintenance(jsonObject.getString("setting_text_maintenance"));

                        ((AppController) OneSplashActivity.this.getApplication()).setSettingOneSignaAppId(jsonObject.getString("setting_one_signal_app_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setSettingYouTubeApiKey(jsonObject.getString("setting_youtube_api_key"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingAppId(jsonObject.getString("admob_setting_app_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingBannerUnitId(jsonObject.getString("admob_setting_banner_unit_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingInterstitialUnitId(jsonObject.getString("admob_setting_interstitial_unit_id"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingBannerSize(jsonObject.getString("admob_setting_banner_size"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingInterstitialClicks(jsonObject.getString("admob_setting_interstitial_clicks"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingBannerStatus(jsonObject.getString("admob_setting_banner_status"));
                        ((AppController) OneSplashActivity.this.getApplication()).setAdmobSettingInterstitialStatus(jsonObject.getString("admob_setting_interstitial_status"));

                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_referral_friend(jsonObject.getString("reward_coin_referral_friend"));
                        ((AppController) OneSplashActivity.this.getApplication()).setReward_coin_referral_user(jsonObject.getString("reward_coin_referral_user"));

                        //Check App Version
                        versionCheck();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                //Toast.makeText(MainActivity.this, R.string.txt_error, Toast.LENGTH_SHORT).show();
                //No Internet Connection
                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(OneSplashActivity.this);
                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_whoops));
                builderCheckUpdate.setMessage(getResources().getString(R.string.txt_no_network_connection_found));
                builderCheckUpdate.setCancelable(false);

                builderCheckUpdate.setPositiveButton(
                        getResources().getString(R.string.txt_try_again),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(getIntent());
                            }
                        });

                builderCheckUpdate.setNegativeButton(
                        getResources().getString(R.string.txt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                alert1CheckUpdate.show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    //==========================================================================//
    public void versionCheck() {
        //Get versionCode from local variable
        settingVersionCode = ((AppController) this.getApplication()).getSettingVersionCode();
        int appVersionCode = BuildConfig.VERSION_CODE;
        if (appVersionCode < Integer.parseInt(settingVersionCode))
        {
            //Your app is old
            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(OneSplashActivity.this);
            builderCheckUpdate.setTitle(getResources().getString(R.string.txt_check_update_title));
            builderCheckUpdate.setMessage(getResources().getString(R.string.txt_check_update_msg));
            builderCheckUpdate.setCancelable(false);

            builderCheckUpdate.setPositiveButton(
                    getResources().getString(R.string.txt_get_update),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.txt_update_url)));
                            startActivity(browserIntent);

                            //Visible btnTryAgain
                            btnTryAgain.setVisibility(View.VISIBLE);
                            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    });

            /*builderCheckUpdate.setNegativeButton(
                    getResources().getString(R.string.txt_later),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //Check Maintenance Status
                            maintenanceCheck();
                        }
                    });*/

            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
            alert1CheckUpdate.show();

        }else{
            //Check Maintenance Status
            maintenanceCheck();
        }
    }


    //==========================================================================//
    public void maintenanceCheck() {
        //Get versionCode from local variable
        String settingMaintenance = ((AppController) this.getApplication()).getSettingAndroidMaintenance();
        String settingMaintenanceMessage = ((AppController) this.getApplication()).getSettingTextMaintenance();
        if (settingMaintenance.equals("1"))
        {
            //Maintenance mode is enable
            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(OneSplashActivity.this);
            builderCheckUpdate.setTitle(getResources().getString(R.string.txt_maintenance_title));
            builderCheckUpdate.setMessage(settingMaintenanceMessage);
            builderCheckUpdate.setCancelable(false);

            builderCheckUpdate.setPositiveButton(
                    getResources().getString(R.string.txt_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

            /*builderCheckUpdate.setNegativeButton(
                    getResources().getString(R.string.txt_later),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });*/

            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
            alert1CheckUpdate.show();

        }else{
            //Go to MainActivity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(OneSplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            },0);
        }
    }
}
