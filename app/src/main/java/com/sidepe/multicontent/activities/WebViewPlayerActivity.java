package com.sidepe.multicontent.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class WebViewPlayerActivity extends AppCompatActivity {

    private String contentUrl, contentTypeId, contentTitle, userId, contentId, contentImage, contentPlayerTypeId;
    private WebView showContentWebView;
    public ProgressDialog pd;
    Handler handler;
    Runnable myRunnable;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_show_webview_content);

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
                    final String theUserCoin = ((AppController) WebViewPlayerActivity.this.getApplication()).getReward_coin_watching_video();
                    final String updateCoinType = "playVideo";
                    final String expirationTime = ((AppController) WebViewPlayerActivity.this.getApplication()).getReward_coin_watching_video_exp();
                    updateUserCoin(theUserId, theUserCoin, updateCoinType, expirationTime);
                }
            };
            handler.postDelayed(myRunnable,30000);
        }


        // Makes Progress bar Visible
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        // Prevent to turn off the screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        //Get item url
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentUrl")) {
                userId = extras.getString("userId");
                contentId = extras.getString("contentId");
                contentTitle = extras.getString("contentTitle");
                contentImage = extras.getString("contentImage");
                contentUrl = extras.getString("contentUrl");
                contentTypeId = extras.getString("contentTypeId");
                contentPlayerTypeId = extras.getString("contentPlayerTypeId");
            }
        }


        showContentWebView = (WebView) findViewById(R.id.wv_show_video);
        showContentWebView.setWebViewClient(new MyWebViewClient());
        WebSettings settingWebView = showContentWebView.getSettings();
        settingWebView.setJavaScriptEnabled(true);
        settingWebView.setAllowFileAccess(true);
        settingWebView.setDomStorageEnabled(true);

        enableHTML5AppCache();

        // -------------------- LOADER ------------------------
        pd = new ProgressDialog(WebViewPlayerActivity.this);
        pd.setMessage(WebViewPlayerActivity.this.getResources().getString(R.string.txt_loading));
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

        showContentWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle(R.string.txt_loading);
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100)
                    setTitle(contentTitle);
            }

            //Added for fullscreen --> Start
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(WebViewPlayerActivity.this.getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView()
            {
                ((FrameLayout)WebViewPlayerActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                WebViewPlayerActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                WebViewPlayerActivity.this.setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = WebViewPlayerActivity.this.getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = WebViewPlayerActivity.this.getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)WebViewPlayerActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                WebViewPlayerActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846);
            }
            //Added for fullscreen --> End
        });


        if(contentPlayerTypeId.equals("3")) { // 3 : Regular Direct URL
            showContentWebView.loadUrl(contentUrl);

        }else if(contentPlayerTypeId.equals("4")) { // 4 : YouTub Embed ID
            String videoUrl = "https://www.youtube.com/embed/"+contentUrl;
            showContentWebView.loadUrl(videoUrl);

        }else if(contentPlayerTypeId.equals("5")) { // 5 : YouTub Embed ID
            String videoUrl = "https://www.youtube.com/embed/"+contentUrl;
            showContentWebView.loadUrl(videoUrl);

        }else if(contentPlayerTypeId.equals("6")) { // 6 : Vimeo Embed ID
            String videoUrl = "https://player.vimeo.com/video/"+contentUrl;
            showContentWebView.loadUrl(videoUrl);

        }else{
            showContentWebView.loadUrl(contentUrl);
        }
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
                    final String theUserCoin = ((AppController) WebViewPlayerActivity.this.getApplication()).getReward_coin_interstitial_ad_click();
                    final String updateCoinType = "interstitialAd";
                    final String expirationTime = ((AppController) WebViewPlayerActivity.this.getApplication()).getReward_coin_interstitial_ad_exp();
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
            showContentWebView.stopLoading();
            showContentWebView.loadUrl("");
            showContentWebView.reload();
            if(login_user_id.equals("Not Login")) {
                //No Action
            }else{
                handler.removeCallbacks(myRunnable);
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    //==========================================================================//
    public void onBackPressed() {
        showContentWebView.stopLoading();
        showContentWebView.loadUrl("");
        showContentWebView.reload();
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


    //==========================================================================//
    private void enableHTML5AppCache() {
        showContentWebView.getSettings().setDomStorageEnabled(true);
        showContentWebView.getSettings().setAppCachePath("/data/data/" + WebViewPlayerActivity.this.getPackageName() + "/cache");
        showContentWebView.getSettings().setAppCacheEnabled(true);
        showContentWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }


    //==========================================================================//
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (!pd.isShowing()) {
                pd.show();
            }

            // Start intent for "tel:" links
            if (url != null && url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(WebViewPlayerActivity.this, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "sms:" links
            if (url != null && url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(WebViewPlayerActivity.this, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "mailto:" links
            if (url != null && url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(WebViewPlayerActivity.this, R.string.txt_this_function_is_not_support_here, Toast.LENGTH_SHORT).show();
                }
                view.reload();
                return true;
            }

            // Start intent for "https://www.instagram.com/YourUsername" links
            if (url != null && url.contains("instagram.com")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
                view.reload();
                return true;
            }

            // Start intent for "https://www.t.me/YourUsername" links
            if (url != null && url.contains("t.me")) {
                Uri uri = Uri.parse(url);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("org.telegram.messenger");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
                view.reload();
                return true;
            }

            if (url != null && url.startsWith("external:http")) {
                url = url.replace("external:", "");
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            if (url != null && url.startsWith("file:///android_asset/external:http")) {
                url = url.replace("file:///android_asset/external:", "");
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                view.loadUrl(url);
            }

            return true;
        }

        //Added for fullscreen video
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            //Added for fullscreen video
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showContentWebView.loadUrl("file:///android_asset/error.html");
        }
    }
}
