package com.sidepe.multicontent.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;

import java.util.HashMap;
import java.util.Map;

import static com.sidepe.multicontent.activities.MainActivity.login_user_id;


public class ExoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer player;
    String userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId;
    Context context = this;
    Handler handler;
    Runnable myRunnable;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fullscreen
        getSupportActionBar().hide();
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_exo_player);

        loadInterstitialAd();

        //Update user coin
        if(login_user_id.equals("Not Login")) {
            //No Action
        }else{
            //Update user coin from watching video after 30 sec
            handler = new Handler();
            myRunnable = new Runnable() {
                public void run() {
                    final String theUserId = login_user_id;
                    final String theUserCoin = ((AppController) ExoPlayerActivity.this.getApplication()).getReward_coin_watching_video();
                    final String updateCoinType = "playVideo";
                    final String expirationTime = ((AppController) ExoPlayerActivity.this.getApplication()).getReward_coin_watching_video_exp();
                    updateUserCoin(theUserId, theUserCoin, updateCoinType, expirationTime);
                }
            };
            handler.postDelayed(myRunnable,30000);
        }

        //Get Intent Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentId")) {
                userId = extras.getString("userId");
                contentId = extras.getString("contentId");
                contentTitle = extras.getString("contentTitle");
                contentImage = extras.getString("contentImage");
                contentUrl = extras.getString("contentUrl");
                contentTypeId = extras.getString("contentTypeId");
            }
        }

        //Exo Player New
        playerView = (PlayerView) findViewById(R.id.exoplayer);
        player = new SimpleExoPlayer.Builder(context).build();
        playerView.setPlayer(player);
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(contentUrl));
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }

    //============================================================================//
    private void loadInterstitialAd(){
        // Interstitial Ad
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, ((AppController) this.getApplication()).getAdmobSettingAppId());
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(((AppController) this.getApplication()).getAdmobSettingInterstitialUnitId());
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                if(login_user_id.equals("Not Login")) {
                    //No Action
                }else{
                    final String theUserId = login_user_id;
                    final String theUserCoin = ((AppController) ExoPlayerActivity.this.getApplication()).getReward_coin_interstitial_ad_click();
                    final String updateCoinType = "interstitialAd";
                    final String expirationTime = ((AppController) ExoPlayerActivity.this.getApplication()).getReward_coin_interstitial_ad_exp();
                    updateUserCoin(theUserId, theUserCoin, updateCoinType, expirationTime);
                }
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


    //============================================================================//
    private void updateUserCoin(final String theUserId, final String theUserCoin, final String updateCoinType, final String expirationTime){
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Total user_coin + user_coin
                if(!response.toString().equals(""))
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest requestView = new StringRequest(Request.Method.POST, Config.UPDATE_USER_COIN_URL + "?api_key=" + Config.API_KEY,listener,errorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("user_id",theUserId);
                params.put("user_coin",theUserCoin);
                params.put("update_coin_type",updateCoinType);
                params.put("expiration_time",expirationTime);
                return params;
            }
        };
        requestView.setRetryPolicy(new DefaultRetryPolicy(10000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestView);
    }


    //============================================================================//
    public void onBackPressed() {
        if(player.isPlaying()) {
            player.stop();
        }
        if(login_user_id.equals("Not Login")) {
            //No Action
        }else{
            handler.removeCallbacks(myRunnable);
        }

        // InterstitialAd
        if (mInterstitialAd.isLoaded()) {
            if (((AppController) this.getApplication()).getAdmobSettingInterstitialStatus().equals("1"))
            {
                if(login_user_id.equals("Not Login")) {
                    mInterstitialAd.show();

                }else{
                    if (((AppController) this.getApplication()).getUserHideInterstitialAd().equals("0")) //Check user hide ads or not
                    {
                        mInterstitialAd.show();
                    }
                }
            }
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlayer();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        startPlayer();
    }

    private void pausePlayer(){
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    private void startPlayer(){
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }
}