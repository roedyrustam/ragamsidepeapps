package com.sidepe.multicontent.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import static com.sidepe.multicontent.Config.DIRECTION;
import static com.sidepe.multicontent.activities.MainActivity.login_user_id;

public class OneContentLinkActivity extends AppCompatActivity {
    private String buttonText;
    private String contentId;
    private String userUsername;
    private String contentTitle;
    private String categoryTitle;
    private String contentImage;
    private String contentUrl;
    private String content_open_url_inside_app;
    private String contentDuration;
    private String contentViewed;
    private String contentPublishDate;
    private String contentTypeTitle;
    private String contentTypeId;
    private String contentUserRoleId;
    private String contentOrientation;
    private String contentCached;
    private WebView wvOneContent;
    Context context = this;
    String contentBookmark;
    String contentDescription;
    String mobileUserLogin;
    String user_role_id;
    String userId;
    String total_rate, row_rate, rate_average;
    Button showContent;
    FloatingActionButton floatingActionButtonShowLink;
    CoordinatorLayout coordinatorLayoutOneContentLink;
    CardView cardViewAD;
    ImageButton imageButtonAdClose, btnArrowRating;
    private ProgressWheel progressWheelInterpolated;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu floating_action_menu_one_link;
    Animation show_main_fab;
    RatingBar ratingBarAverage;
    ProgressBar progressBarFiveStar, progressBarFourStar, progressBarThreeStar, progressBarTwoStar, progressBarOneStar;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_add_comment;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_show_comment;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_bookmark;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_share;

    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_content_link);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayoutOneContentLink = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutOneContentLink);
        cardViewAD = (CardView) findViewById(R.id.cv_ad);
        ratingBarAverage = (RatingBar) findViewById(R.id.ratingBarAverage);
        progressBarFiveStar = (ProgressBar) findViewById(R.id.progressBarFiveStar);
        progressBarFourStar = (ProgressBar) findViewById(R.id.progressBarFourStar);
        progressBarThreeStar = (ProgressBar) findViewById(R.id.progressBarThreeStar);
        progressBarTwoStar = (ProgressBar) findViewById(R.id.progressBarTwoStar);
        progressBarOneStar = (ProgressBar) findViewById(R.id.progressBarOneStar);

        imageButtonAdClose = (ImageButton) findViewById(R.id.btn_ad_close);
        imageButtonAdClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUpgrade = new Intent(OneContentLinkActivity.this, AccountUpgrade.class);
                startActivity(intentUpgrade);
            }
        });

        loadBannerAd();
        loadInterstitialAd();

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.one_item_progress_wheel);

        //Check user is login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }


        //Get Intent Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentId")) {
                contentId = extras.getString("contentId");
                buttonText = getString(R.string.txt_loading);
                sendJsonArrayRequest();
                buttonText = extras.getString("buttonText");
            }
        }

        btnArrowRating = (ImageButton) findViewById(R.id.btn_arrow_rating);
        btnArrowRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowComment.class);
                intent.putExtra("contentId", contentId);
                intent.putExtra("contentTitle", contentTitle);
                startActivity(intent);
            }
        });

        //Set ActionBar Title
        setTitle("");

        //Get userID from local variable
        userId = ((AppController) this.getApplication()).getUserId();

        wvOneContent = (WebView) findViewById(R.id.wv_one_content);
        String text = "<html dir='"+DIRECTION+"'><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:"+DIRECTION+"; line-height:23px;}"
                + "</style></head>"
                + "<body>"
                + getString(R.string.txt_loading)
                + "</body></html>";
        wvOneContent.loadDataWithBaseURL(null,text, "text/html; charset=UTF-8", "utf-8", null);

        //Total View + 1 //
        totalContentView();

        floatingActionButtonShowLink = (FloatingActionButton) findViewById(R.id.fabShowVideo);
        floatingActionButtonShowLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send contentURL to ShowWebViewContentActivity
                Intent intent = new Intent(context, ShowWebViewContentActivity.class);
                intent.putExtra("contentTitle", contentTitle);
                intent.putExtra("contentUrl", contentUrl);
                intent.putExtra("contentOrientation", contentOrientation);
                startActivity(intent);
            }
        });

        showContent = (Button)findViewById(R.id.btn_show_content);

        //Floating Action Menu
        floating_action_menu_one_link = findViewById(R.id.floating_action_menu_one_link);
        fab_add_comment = findViewById(R.id.fab_add_comment);
        fab_add_comment.setImageResource(R.drawable.ic_rate_review_white_24dp);
        fab_show_comment = findViewById(R.id.fab_show_comment);
        fab_show_comment.setImageResource(R.drawable.ic_insert_comment_white_24dp);
        fab_bookmark = findViewById(R.id.fab_bookmark);
        fab_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        fab_share = findViewById(R.id.fab_share);
        fab_share.setImageResource(R.drawable.ic_baseline_share_24);

        //Main Fab Animation
        show_main_fab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_anim);
        floating_action_menu_one_link.startAnimation(show_main_fab);

        //Add CommentModel
        fab_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileUserLogin != null) {
                    //contentId userId
                    Intent intent = new Intent(getBaseContext(), AddCommentActivity.class);
                    intent.putExtra("contentId", contentId);
                    intent.putExtra("contentTitle", contentTitle);
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                }else{
                    //Please login first
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutOneContentLink, R.string.txt_please_login_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.txt_bookmark_login, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(OneContentLinkActivity.this, LoginActivity.class));
                                }
                            });
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
                    snackbar.show();
                }
            }
        });

        //Show Comments
        fab_show_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowComment.class);
                intent.putExtra("contentId", contentId);
                intent.putExtra("contentTitle", contentTitle);
                startActivity(intent);
            }
        });

        //Get bookmark status
        if (mobileUserLogin != null) {
            getBookmarkStatus();

        }else{
            fab_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Please login first then click on the bookmark
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutOneContentLink, R.string.txt_please_login_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.txt_bookmark_login, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(OneContentLinkActivity.this, LoginActivity.class));
                                }
                            });
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
                    snackbar.show();
                }
            });
        }

        //Share content
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCompat.IntentBuilder.from(OneContentLinkActivity.this)
                        .setType("text/plain")
                        .setChooserTitle(getString(R.string.txt_share_content))
                        //.setText("http://play.google.com/store/apps/details?id=" + OneContentVideoActivity.this.getPackageName())
                        .setText(getString(R.string.txt_i_like_to_share_this_with_you)+Config.BASE_URL+"Web/content/"+contentId)
                        .startChooser();
            }
        });

        //Get Rating Average
        getRatingAverage();
    }

    //============================================================================//
    private void getBookmarkStatus()
    {
        StringRequest requestPostResponse = new StringRequest(Request.Method.GET, Config.GET_BOOKMARK_STATUS+"?user_id="+userId+"&content_id="+contentId+"&api_key="+ Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        if (getAnswer.equals("ContentIsBookmark")) {
                            //Toast.makeText(getApplicationContext(),"ItemIsBookmark",Toast.LENGTH_LONG).show();
                            fab_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                            fab_bookmark.setTitle(getString(R.string.txt_remove_bookmark));
                            fab_bookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change status to unBookmark
                                    fab_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                                    fab_bookmark.setTitle(getString(R.string.txt_add_bookmark));
                                    removeFromBookmark();
                                }
                            });

                        }else if (getAnswer.equals("ContentIsNotBookmark")) {
                            //Toast.makeText(getApplicationContext(),"ItemIsNotBookmark",Toast.LENGTH_LONG).show();
                            fab_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                            fab_bookmark.setTitle(getString(R.string.txt_add_bookmark));
                            fab_bookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change status to Bookmark
                                    fab_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                                    fab_bookmark.setTitle(getString(R.string.txt_remove_bookmark));
                                    addToBookmark();
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //============================================================================//
    private void removeFromBookmark()
    {
        //Remove From Bookmark
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.REMOVE_FROM_BOOKMARK_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        Toast.makeText(getApplicationContext(), getAnswer ,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.txt_error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("content_id",contentId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //============================================================================//
    private void addToBookmark()
    {
        //Remove From Bookmark
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.ADD_TO_BOOKMARK_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        Toast.makeText(getApplicationContext(), getAnswer ,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.txt_error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("content_id",contentId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);

    }


    /*============================================================================*/
    private void sendJsonArrayRequest()
    {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject object = response.getJSONObject(i);
                        userUsername = object.getString("user_username");
                        contentDescription = object.getString("content_description");
                        contentBookmark = object.getString("content_description");
                        contentTitle = object.getString("content_title");
                        categoryTitle = object.getString("category_title");
                        contentImage = object.getString("content_image");
                        contentUrl = object.getString("content_url");
                        content_open_url_inside_app = object.getString("content_open_url_inside_app");
                        contentDuration = object.getString("content_duration");
                        contentViewed = object.getString("content_viewed");
                        contentPublishDate = object.getString("content_publish_date");
                        contentTypeTitle = object.getString("content_title");
                        contentTypeId = object.getString("content_type_id");
                        contentUserRoleId = object.getString("content_user_role_id");
                        contentOrientation = object.getString("content_orientation");
                        contentCached = object.getString("content_cached");

                        String contentImageURL = Config.CONTENT_IMG_URL+contentImage;
                        ImageView contentImageView = (ImageView) findViewById(R.id.iv_one_content_image);
                        Glide
                                .with(OneContentLinkActivity.this)
                                .load(contentImageURL)
                                .apply(new RequestOptions()
                                        .fitCenter()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(contentImageView);

                        TextView oneContentTitle = (TextView)findViewById(R.id.tv_one_content_title);
                        oneContentTitle.setText(contentTitle);

                        TextView oneContentCategory = (TextView)findViewById(R.id.tv_one_content_category);
                        oneContentCategory.setText(categoryTitle);

                        TextView oneContentDate = (TextView)findViewById(R.id.tv_one_content_date);
                        oneContentDate.setText(Config.TimeAgo(contentPublishDate));

                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String contentViewed_formatted = formatter.format(Integer.parseInt(contentViewed));
                        TextView oneContentViewed = (TextView)findViewById(R.id.tv_one_content_viewed);
                        oneContentViewed.setText(contentViewed_formatted);

                        showContent.setText(buttonText);
                        showContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(contentTypeId.equals("21")) //Hyperlink
                                {
                                    if(content_open_url_inside_app.equals("1"))
                                    {
                                        //Send contentURL to ShowWebViewContentActivity
                                        Intent intent = new Intent(context, ShowWebViewContentActivity.class);
                                        intent.putExtra("contentTitle", contentTitle);
                                        intent.putExtra("contentCached", contentCached);
                                        intent.putExtra("contentUrl", contentUrl);
                                        intent.putExtra("contentOrientation", contentOrientation);
                                        intent.putExtra("contentTypeId", contentTypeId);
                                        startActivity(intent);

                                    }else{
                                        //Go to browser
                                        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(contentUrl));
                                        startActivity(browser);
                                    }

                                }else{
                                    // Code to be executed when the user clicks on an ad.
                                    /*if(login_user_id.equals("Not Login")) {
                                        //No Action
                                    }else{
                                        final String theUserId = login_user_id;
                                        final String theUserCoin = ((AppController) OneContentLinkActivity.this.getApplication()).getReward_coin_play_game();
                                        final String updateCoinType = "playGame";
                                        final String expirationTime = ((AppController) OneContentLinkActivity.this.getApplication()).getReward_coin_play_game_exp();
                                        updateUserCoin(theUserId, theUserCoin, updateCoinType, expirationTime);
                                    }*/

                                    //Send contentURL to ShowWebViewContentActivity
                                    Intent intent = new Intent(context, ShowWebViewContentActivity.class);
                                    intent.putExtra("contentTitle", contentTitle);
                                    intent.putExtra("contentCached", contentCached);
                                    intent.putExtra("contentUrl", contentUrl);
                                    intent.putExtra("contentOrientation", contentOrientation);
                                    intent.putExtra("contentTypeId", contentTypeId);
                                    startActivity(intent);
                                }
                            }
                        });

                        //To format as HTML
                        String formattedPageContent = android.text.Html.fromHtml(contentDescription).toString(); //Just for ckEditor

                        wvOneContent.getSettings().setJavaScriptEnabled(true);
                        wvOneContent.setFocusableInTouchMode(false);
                        wvOneContent.setFocusable(false);
                        WebSettings webSettings = wvOneContent.getSettings();
                        webSettings.setDefaultFontSize(Config.Font_Size);
                        wvOneContent.getSettings().setDefaultTextEncodingName("UTF-8");
                        String mimeType = "text/html; charset=UTF-8";
                        String encoding = "utf-8";

                        String text = "<html dir='"+DIRECTION+"'><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:"+DIRECTION+"; line-height:23px;}"
                                + "</style></head>"
                                + "<body>"
                                //+ formattedPageContent
                                + contentDescription
                                + "</body></html>";
                        wvOneContent.loadDataWithBaseURL(null,text, mimeType, encoding, null);
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressWheelInterpolated.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.txt_error, Toast.LENGTH_LONG).show();
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_ONE_CONTENT_URL+contentId+"/?api_key=" + Config.API_KEY, null, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }


    //============================================================================//
    private void totalContentView(){
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Total itemView + 1
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest requestView = new StringRequest(Request.Method.POST, Config.TOTAL_CONTENT_VIEWED_URL + "?api_key=" + Config.API_KEY,listener,errorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("content_id",contentId);
                return params;
            }
        };
        requestView.setRetryPolicy(new DefaultRetryPolicy(9000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestView);
    }


   /*============================================================================*/
    private void getRatingAverage()
    {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject object = response.getJSONObject(i);
                        total_rate = object.getString("total_rate");
                        row_rate = object.getString("row_rate");
                        rate_average = object.getString("rate_average");
                        String one_star_average = object.getString("one_star_average");
                        String two_star_average = object.getString("two_star_average");
                        String three_star_average = object.getString("three_star_average");
                        String four_star_average = object.getString("four_star_average");
                        String five_star_average = object.getString("five_star_average");

                        ratingBarAverage.setRating(Float.parseFloat(rate_average));
                        progressBarFiveStar.setProgress(Integer.parseInt(five_star_average));
                        progressBarFourStar.setProgress(Integer.parseInt(four_star_average));
                        progressBarThreeStar.setProgress(Integer.parseInt(three_star_average));
                        progressBarTwoStar.setProgress(Integer.parseInt(two_star_average));
                        progressBarOneStar.setProgress(Integer.parseInt(one_star_average));

                        TextView rateAverage = (TextView)findViewById(R.id.txt_rate_average);
                        rateAverage.setText(rate_average);

                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String total_rate_formated = formatter.format(Integer.parseInt(row_rate));

                        TextView rowRate = (TextView)findViewById(R.id.txt_row_rate);
                        rowRate.setText(total_rate_formated);

                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressWheelInterpolated.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), R.string.txt_error, Toast.LENGTH_LONG).show();
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_RATING_AVERAGE+"?content_id="+contentId+"&api_key="+ Config.API_KEY, null, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }


    //============================================================================//
    private void loadBannerAd(){
        // Banner Ad
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, ((AppController) this.getApplication()).getAdmobSettingAppId());
        View adContainer = findViewById(R.id.adMobBannerView);
        mAdView = new AdView(OneContentLinkActivity.this);
        //Banner Size
        String bannerAdSize = ((AppController) this.getApplication()).getAdmobSettingBannerSize();
        if(bannerAdSize.equals("BANNER"))
            mAdView.setAdSize(AdSize.BANNER);
        else if(bannerAdSize.equals("LARGE_BANNER"))
            mAdView.setAdSize(AdSize.LARGE_BANNER);
        else if(bannerAdSize.equals("MEDIUM_RECTANGLE"))
            mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        else if(bannerAdSize.equals("FULL_BANNER"))
            mAdView.setAdSize(AdSize.FULL_BANNER);
        else if(bannerAdSize.equals("LEADERBOARD"))
            mAdView.setAdSize(AdSize.LEADERBOARD);
        else if(bannerAdSize.equals("SMART_BANNER"))
            mAdView.setAdSize(AdSize.SMART_BANNER);
        else
            mAdView.setAdSize(AdSize.SMART_BANNER);

        mAdView.setAdUnitId(((AppController) this.getApplication()).getAdmobSettingBannerUnitId());
        ((RelativeLayout)adContainer).addView(mAdView);
        if (((AppController) this.getApplication()).getAdmobSettingBannerStatus().equals("1"))
        {
            if(login_user_id.equals("Not Login")) {
                cardViewAD.setVisibility(View.VISIBLE);
                adContainer.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            }else{
                if (((AppController) this.getApplication()).getUserHideBannerAd().equals("0")) //Check user hide ads or not
                {
                    cardViewAD.setVisibility(View.VISIBLE);
                    adContainer.setVisibility(View.VISIBLE);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
            }
        }

        mAdView.setAdListener(new AdListener() {
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
                // Code to be executed when an ad opens an overlay that covers the screen.
                if(login_user_id.equals("Not Login")) {
                    //No Action
                }else{
                    final String theUserId = login_user_id;
                    final String theUserCoin = ((AppController) OneContentLinkActivity.this.getApplication()).getReward_coin_banner_ad_click();;
                    final String updateCoinType = "bannerAd";
                    final String expirationTime = ((AppController) OneContentLinkActivity.this.getApplication()).getReward_coin_banner_ad_exp();
                    updateUserCoin(theUserId, theUserCoin, updateCoinType, expirationTime);
                }
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
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
                    final String theUserCoin = ((AppController) OneContentLinkActivity.this.getApplication()).getReward_coin_interstitial_ad_click();
                    final String updateCoinType = "interstitialAd";
                    final String expirationTime = ((AppController) OneContentLinkActivity.this.getApplication()).getReward_coin_interstitial_ad_exp();
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
            // Show Interstitial Ad
            if (mInterstitialAd.isLoaded()) {
                if (((AppController) this.getApplication()).getAdmobSettingInterstitialStatus().equals("1")) {
                    if(login_user_id.equals("Not Login")) {
                        //mInterstitialAd.show();

                    }else{
                        if (((AppController) this.getApplication()).getUserHideInterstitialAd().equals("0")) //Check user hide ads or not
                        {
                            //mInterstitialAd.show();
                        }
                    }
                }
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //============================================================================//
    public void onBackPressed() {
        // Show Interstitial Ad
        if (mInterstitialAd.isLoaded()) {
            if (((AppController) this.getApplication()).getAdmobSettingInterstitialStatus().equals("1"))
            {
                if(login_user_id.equals("Not Login")) {
                    //mInterstitialAd.show();

                }else{
                    if (((AppController) this.getApplication()).getUserHideInterstitialAd().equals("0")) //Check user hide ads or not
                    {
                        //mInterstitialAd.show();
                    }
                }
            }
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        finish();
    }


    //============================================================================//
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    //============================================================================//
    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }


    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}