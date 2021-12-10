package com.sidepe.multicontent.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.ads.InterstitialAd;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.fragments.AboutFragment;
import com.sidepe.multicontent.fragments.CategoryFragment;
import com.sidepe.multicontent.fragments.MainFragment;
import com.sidepe.multicontent.fragments.ProfileFragment;
import com.sidepe.multicontent.fragments.SearchFragment;
import com.sidepe.multicontent.utils.AppController;
import com.sidepe.multicontent.utils.PrefManager;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.sidepe.multicontent.R;

import com.sidepe.multicontent.fragments.WebViewFragment;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //Create object for FrameLayout and Fragments
    private FrameLayout frmMain;
    private DrawerLayout drawerLayout;
    private MainFragment mainFragment = new MainFragment();
    private CategoryFragment categoryFragment = new CategoryFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private WebViewFragment webViewFragmentAbout = new WebViewFragment();
    private WebViewFragment webViewFragmentContact = new WebViewFragment();
    private AboutFragment aboutAppFragment = new AboutFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private PrefManager prefManager;
    String mobileUserLogin;
    RequestQueue rqGetAllAfterLogin;
    String settingVersionCode;
    private InterstitialAd mInterstitialAd;
    public static String login_user_id;
    public static String user_role_id;
    public static String setting_email;
    public static String setting_website;
    TextView tvNavTitle, tvNavSubTitle;
    LinearLayout navLinearLayout;
    ImageView navImageView;
    final int READ_WRITE_CAMERA_EXTERNAL_REQUEST_CODE = 1;


    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        setting_email = ((AppController) this.getApplication()).getSettingEmail();
        setting_website = ((AppController) this.getApplication()).getSettingWebsite();

        //Access the FrameLayout
        frmMain = (FrameLayout) findViewById(R.id.frmMain);

        //Load Default and Main fragment in MainActivity
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frmMain, mainFragment);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Check user login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        //Hide login nav menu if user logged in
        Menu nav_Menu = navigationView.getMenu();
        if (mobileUserLogin != null) { //User is Login
            //Set menu
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_register).setVisible(false);
            nav_Menu.findItem(R.id.nav_bookmark).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
            nav_Menu.findItem(R.id.nav_account_upgrade).setVisible(true);
            login_user_id = ((AppController) this.getApplication()).getUserId();
            user_role_id = ((AppController) this.getApplication()).getUserRoleID();

            //Set Navigation Menu title and sub title and background color
            View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
            tvNavTitle = (TextView) header.findViewById(R.id.navTitle);
            tvNavTitle.setText(((AppController) this.getApplication()).getUserUserName());
            tvNavSubTitle = (TextView) header.findViewById(R.id.tvNavSubTitle);
            tvNavSubTitle.setText(((AppController) this.getApplication()).getUserEmail());
            navImageView = (ImageView) header.findViewById(R.id.navImageView);
            Glide.with(MainActivity.this)
                    .load(Config.USER_IMG_URL+((AppController) this.getApplication()).getUserImage())
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                            .placeholder(R.drawable.pre_loading)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate())
                    .into(navImageView);
            navImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, UploadProfilePhotoActivity.class);
                    startActivity(intent);
                }
            });

        }else { //User Not Login
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_register).setVisible(true);
            nav_Menu.findItem(R.id.nav_bookmark).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_account_upgrade).setVisible(false);
            login_user_id = "Not Login";
            user_role_id = "Not Login";

            //Set Navigation Menu title and sub title and background color
            View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
            tvNavTitle = (TextView) header.findViewById(R.id.navTitle);
            tvNavTitle.setText(R.string.app_name);
            tvNavSubTitle = (TextView) header.findViewById(R.id.tvNavSubTitle);
            tvNavSubTitle.setText(R.string.app_sub_name);
            navImageView = (ImageView) header.findViewById(R.id.navImageView);
            Glide.with(MainActivity.this)
                    .load(R.mipmap.ic_launcher)
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                            .placeholder(R.drawable.pre_loading)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate())
                    .into(navImageView);
        }
    }


    //==========================================================================//
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
            //To fix title bug.
            if (mainFragment != null && mainFragment.isVisible()) {
                setTitle(R.string.app_name);
            }

        }
    }


    //==========================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        if (id == R.id.action_settings) {
            return true;

        }if (id == R.id.action_search) {
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","");
            bundle.putString("showTitle", getString(R.string.menu_search));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment, "SEARCH_FRAGMENT")
                    .addToBackStack(null)
                    .commit();


            //To refresh searchFragment if clicked on the search toolbar again to show search form
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(searchFragment).attach(searchFragment).commit();
                //Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","");
                bundle.putString("showTitle", getString(R.string.menu_search));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    //==========================================================================//
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
            transaction.replace(R.id.frmMain, mainFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (mainFragment != null && mainFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(mainFragment).attach(mainFragment).commit();
            }

        } else if (id == R.id.nav_category) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            /*Bundle bundle = new Bundle();
            String myMessage = "Armin MSG";
            bundle.putString("message", myMessage);
            categoryFragment.setArguments(bundle);*/

            transaction.replace(R.id.frmMain, categoryFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (categoryFragment != null && categoryFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(categoryFragment).attach(categoryFragment).commit();
            }

        } else if (id == R.id.nav_bookmark) {
            //Pass data from Fragment to Fragment
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","BookmarkContent");
            bundle.putString("showTitle", getString(R.string.nav_bookmark));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment)
                    .addToBackStack(null)
                    .commit();

            //To refresh current fragment if clicked
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(bookmarkFragment).attach(bookmarkFragment).commit();
                bundle.putString("showWhichContent","BookmarkContent");
                bundle.putString("showTitle", getString(R.string.nav_bookmark));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }

        } else if (id == R.id.nav_contact) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            Bundle bundle = new Bundle();
            String theTitle = getString(R.string.nav_contact);
            String theSubTitle = getString(R.string.txt_ways_to_contact_us);
            String theUrl = Config.PAGE_CONTACT_US + "?api_key=" + Config.API_KEY;
            bundle.putString("title", theTitle);
            bundle.putString("sub_title", theSubTitle);
            bundle.putString("url", theUrl);
            webViewFragmentContact.setArguments(bundle);

            transaction.replace(R.id.frmMain, webViewFragmentContact);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (webViewFragmentContact != null && webViewFragmentContact.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(webViewFragmentContact).attach(webViewFragmentContact).commit();
            }

        } else if (id == R.id.nav_help) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            Bundle bundle = new Bundle();
            String theTitle = getString(R.string.nav_reward_coin);
            String theSubTitle = getString(R.string.txt_how_to_reward_coin);
            String theUrl = Config.PAGE_HELP + "?api_key=" + Config.API_KEY;
            bundle.putString("title", theTitle);
            bundle.putString("sub_title", theSubTitle);
            bundle.putString("url", theUrl);
            webViewFragmentContact.setArguments(bundle);

            transaction.replace(R.id.frmMain, webViewFragmentContact);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (webViewFragmentContact != null && webViewFragmentContact.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(webViewFragmentContact).attach(webViewFragmentContact).commit();
            }

        }else if (id == R.id.nav_account_upgrade) {
            Intent intentUpgrade = new Intent(MainActivity.this, AccountUpgrade.class);
            startActivity(intentUpgrade);

        } else if (id == R.id.nav_about_app) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
            transaction.replace(R.id.frmMain, aboutAppFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (aboutAppFragment != null && aboutAppFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(aboutAppFragment).attach(aboutAppFragment).commit();
            }

        } else if (id == R.id.nav_share_app) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.txt_share);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        } else if (id == R.id.nav_login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        } else if (id == R.id.nav_register) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));

        } else if (id == R.id.nav_profile) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            transaction.replace(R.id.frmMain, profileFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (profileFragment != null && profileFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).attach(profileFragment).commit();
            }

        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
            prefs.edit().clear().commit();
            //SharedPreferences users = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
            //users.edit().clear().commit();
            Toast.makeText(getApplicationContext(),R.string.txt_logout_successfully,Toast.LENGTH_SHORT).show();

            Intent refresh = new Intent(MainActivity.this, OneSplashActivity.class);
            startActivity(refresh);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
