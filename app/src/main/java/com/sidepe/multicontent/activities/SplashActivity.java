package com.sidepe.multicontent.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.utils.PrefManager;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.sidepe.multicontent.R;

public class SplashActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btn_next, btn_skip;
    LinearLayout linearLayout;
    private TextView[] dots;
    private PrefManager prefManager;
    private int[] layouts;

    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (!prefManager.IsFirstTimeLaunch()) {
            LaunchHomeScreen();
            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        btn_next = (Button) findViewById(R.id.next);
        btn_skip = (Button) findViewById(R.id.skip);

        layouts = new int[]{
                R.layout.screen_1,
                R.layout.screen_2,
                R.layout.screen_3,
                R.layout.screen_4
        };


        addBottom(0);
        changeStatusBarColor();


        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(changeListener);

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchHomeScreen();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current=getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    LaunchHomeScreen();
                }
            }
        });


    }


    private void addBottom(int currentpage) {

        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.light_active);
        int[] colorsInactive = getResources().getIntArray(R.array.dark_inactive);

        linearLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(colorsInactive[currentpage]);
            linearLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentpage].setTextColor(colorsActive[currentpage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }




    private void LaunchHomeScreen() {

        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(SplashActivity.this, OneSplashActivity.class));
        finish();
    }


    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottom(position);

            if (position == layouts.length - 1) {
                btn_next.setText(getString(R.string.start));
                btn_skip.setVisibility(View.GONE);


            } else {
                btn_next.setText(getString(R.string.next));
                btn_skip.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }


    };


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public class ViewPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}

