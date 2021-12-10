package com.sidepe.multicontent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.activities.OneContentDownloadActivity;
import com.sidepe.multicontent.activities.OneContentLinkActivity;
import com.sidepe.multicontent.activities.OneContentMusicActivity;
import com.sidepe.multicontent.activities.OneContentProductActivity;
import com.sidepe.multicontent.activities.OneContentTextActivity;
import com.sidepe.multicontent.activities.OneContentVideoActivity;
import com.sidepe.multicontent.adapters.CategoryHorizentalAdapter;
import com.sidepe.multicontent.adapters.ContentHorizentalAdapter;
import com.sidepe.multicontent.models.CategoryModel;
import com.sidepe.multicontent.models.ContentModel;
import com.sidepe.multicontent.utils.AppController;
import com.sidepe.multicontent.utils.Tools;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sidepe.multicontent.R;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

public class MainFragment extends Fragment {
    CoordinatorLayout mainCoordinatorLayout;
    SliderLayout topSliderLayout;
    String sliderId;
    String sliderTitle;
    String sliderContentId;
    String sliderContentTypeId;
    String sliderImage;
    String click;
    private Context context;
    ImageButton btn_f_main_featured_show_all, btn_f_main_best_rated_show_all, btn_f_main_latest_show_all, btn_f_main_special_show_all;

    //Category Horizental Variable for volley
    RecyclerView horizentalCategoryRecyclerView;
    RecyclerView.Adapter horCatAdapter;
    RecyclerView.LayoutManager horCatLayoutManager;
    List<CategoryModel> horCatItemModelsList;
    RequestQueue rqHorCatItem;

    //Featured Content Variable for volley
    RecyclerView featuredContentRecyclerView;
    RecyclerView.Adapter featuredAdapter;
    RecyclerView.LayoutManager featuredLayoutManager;
    List<ContentModel> featuredContentModelsList;
    RequestQueue rqFeaturedContent;

    //Best Rated Content Variable for volley
    RecyclerView bestRatedContentRecyclerView;
    RecyclerView.Adapter bestRatedAdapter;
    RecyclerView.LayoutManager bestRatedLayoutManager;
    List<ContentModel> bestRatedContentModelsList;
    RequestQueue rqBestRatedContent;

    //Latest Content Variable for volley
    RecyclerView latestContentRecyclerView;
    RecyclerView.Adapter latestAdapter;
    RecyclerView.LayoutManager latestLayoutManager;
    List<ContentModel> latestContentModelsList;
    RequestQueue rqLatestContent;

    //Special Content Variable for volley
    RecyclerView specialContentRecyclerView;
    RecyclerView.Adapter specialAdapter;
    RecyclerView.LayoutManager specialLayoutManager;
    List<ContentModel> specialContentModelsList;
    RequestQueue rqSpecialContent;

    private ProgressWheel progressWheelInterpolated;

    public MainFragment() { }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Set ActionBar Title
        getActivity().setTitle(R.string.app_name);

        mainCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.mainCoordinatorLayout);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(mainCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Go to last fragment/activity
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) view.findViewById(R.id.main_progress_wheel);

        //Featured Content List
        btn_f_main_featured_show_all = (ImageButton) view.findViewById(R.id.btn_f_main_featured_show_all);
        btn_f_main_featured_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass data from Fragment to Fragment
                Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","FeaturedContent");
                bundle.putString("showTitle",getActivity().getString(R.string.txt_featured_title));
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .replace(R.id.mainCoordinatorLayout, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Best Rated Content List
        btn_f_main_best_rated_show_all = (ImageButton) view.findViewById(R.id.btn_f_main_best_rated_show_all);
        btn_f_main_best_rated_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass data from Fragment to Fragment
                Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","BestRatedContent");
                bundle.putString("showTitle",getActivity().getString(R.string.txt_best_rated_title));
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .replace(R.id.mainCoordinatorLayout, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Latest Content List
        btn_f_main_latest_show_all = (ImageButton) view.findViewById(R.id.btn_f_main_latest_show_all);
        btn_f_main_latest_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass data from Fragment to Fragment
                Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","LatestContent");
                bundle.putString("showTitle", getActivity().getString(R.string.txt_latest_title));
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .replace(R.id.mainCoordinatorLayout, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Special Content List
        btn_f_main_special_show_all = (ImageButton) view.findViewById(R.id.btn_f_main_special_show_all);
        btn_f_main_special_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass data from Fragment to Fragment
                Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","SpecialContent");
                bundle.putString("showTitle", getActivity().getString(R.string.txt_special_title));
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .replace(R.id.mainCoordinatorLayout, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Top slider start
        topSliderLayout = (SliderLayout) view.findViewById(R.id.topSlider);
        final Map<String, String> urlImageMap = new TreeMap<>();
        //Start get slider information from server via Volley
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
                        sliderId = object.getString("slider_id");
                        sliderTitle = object.getString("slider_title");
                        sliderContentId = object.getString("slider_content_id");
                        sliderContentTypeId = object.getString("slider_content_type_id");
                        sliderImage = object.getString("slider_image");
                        sliderImage = Config.GET_SLIDER_IMG_URL+sliderImage;

                        //Add sliderTitle and sliderImage to SliderLayout
                        TextSliderView textSliderView = new TextSliderView(getActivity());
                        textSliderView
                                .description(sliderTitle)
                                //.empty(R.drawable.slider_pre_loadin)
                                //.error(R.drawable.slider_pre_loadin)
                                .errorDisappear(true)
                                .image(sliderImage)
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                //.descriptionVisibility(BaseSliderView.Visibility.VISIBLE)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView baseSliderView) {
                                        click = baseSliderView.getBundle().getString("click");
                                        String contentTypeId = click.substring(0,2);
                                        String contentId = click.substring(2);

                                        if(contentTypeId.equals("11")) { //Video & Movie
                                            Intent intent = new Intent(getActivity(), OneContentVideoActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_play_video));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("12")) { //Music & Audio
                                            Intent intent = new Intent(getActivity(), OneContentMusicActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_play_music));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("13")) { //HTML5 Game
                                            Intent intent = new Intent(getActivity(), OneContentLinkActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_play_game));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("14")) { //Text & Article
                                            Intent intent = new Intent(getActivity(), OneContentTextActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_open));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("15")) { //PDF Reader
                                            Intent intent = new Intent(getActivity(), OneContentLinkActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("contentTypeId", 15); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_open_pdf));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("16")) { //News
                                            Intent intent = new Intent(getActivity(), OneContentTextActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_open));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("17")) { //Product
                                            Intent intent = new Intent(getActivity(), OneContentProductActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_show));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("18")) { //Buy & Sell
                                            Intent intent = new Intent(getActivity(), OneContentProductActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_show));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("19")) { //City Guide
                                            Intent intent = new Intent(getActivity(), OneContentProductActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_show));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("20")) { //Download
                                            Intent intent = new Intent(getActivity(), OneContentDownloadActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_open));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("21")) { //Hyperlink
                                            Intent intent = new Intent(getActivity(), OneContentLinkActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_open));
                                            startActivity(intent);

                                        }else if(contentTypeId.equals("22")) { //Images Gallery
                                            /*Intent intent = new Intent(getActivity(), OneContentProductActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_show));
                                            startActivity(intent);*/

                                        }else {
                                            /*Intent intent = new Intent(getActivity(), OneContentLinkActivity.class);
                                            intent.putExtra("contentId", contentId); //click has content_id value
                                            intent.putExtra("buttonText", getString(R.string.txt_button_open));
                                            startActivity(intent);*/
                                        }
                                    }
                                });
                        //.descriptionLayoutColor(Color.parseColor("#99999999"));

                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle().putString("click", sliderContentTypeId+sliderContentId);

                        topSliderLayout.addSlider(textSliderView);
                        topSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
                        topSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        topSliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
                        topSliderLayout.setDuration(4000);
                    }
                }
                catch (Exception e)
                {

                }
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressWheelInterpolated.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.txt_no_slider_found, Toast.LENGTH_LONG).show();
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_SLIDER_URL + "?api_key=" + Config.API_KEY, null, listener, errorListener);
        AppController.getInstance().addToRequestQueue(request);
        //.End get slider information from server via Volley
        //.Top slider end

        //Category Horizontal RecyclerView start
        /*rqHorCatItem = Volley.newRequestQueue(getActivity());
        horizentalCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.rv_horizental_category);
        horizentalCategoryRecyclerView.setHasFixedSize(true);
        horCatLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizentalCategoryRecyclerView.setLayoutManager(horCatLayoutManager);
        horCatItemModelsList = new ArrayList<>();
        sendCategoryHorizentalRequest();*/

        //Category Grid RecyclerView start
        rqHorCatItem = Volley.newRequestQueue(getActivity());
        horizentalCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.rv_horizental_category);
        horizentalCategoryRecyclerView.setHasFixedSize(true);
        //horCatLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horCatLayoutManager = new GridLayoutManager(getActivity(), 3); // 2 Grid
        horizentalCategoryRecyclerView.setLayoutManager(horCatLayoutManager);
        horCatItemModelsList = new ArrayList<>();
        sendCategoryHorizentalRequest();

        //Featured contentRecyclerView start
        //Horizontal list content for Featured
        rqFeaturedContent = Volley.newRequestQueue(getActivity());
        featuredContentRecyclerView = (RecyclerView) view.findViewById(R.id.rv_f_main_featured_content);
        featuredContentRecyclerView.setHasFixedSize(true);
        featuredLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        featuredContentRecyclerView.setLayoutManager(featuredLayoutManager);
        featuredContentModelsList = new ArrayList<>();
        sendFeaturedContentRequest();

        //Best Rated contentRecyclerView start
        //Horizontal list content for Featured
        rqBestRatedContent = Volley.newRequestQueue(getActivity());
        bestRatedContentRecyclerView = (RecyclerView) view.findViewById(R.id.rv_f_main_best_rated_content);
        bestRatedContentRecyclerView.setHasFixedSize(true);
        bestRatedLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bestRatedContentRecyclerView.setLayoutManager(bestRatedLayoutManager);
        bestRatedContentModelsList = new ArrayList<>();
        sendBestRatedContentRequest();

        //Latest contentRecyclerView start
        //Horizontal list content by Latest
        rqLatestContent = Volley.newRequestQueue(getActivity());
        latestContentRecyclerView = (RecyclerView) view.findViewById(R.id.rv_f_main_latest_content);
        latestContentRecyclerView.setHasFixedSize(true);
        latestLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        latestContentRecyclerView.setLayoutManager(latestLayoutManager);
        latestContentModelsList = new ArrayList<>();
        sendLatestContentRequest();

        //Special contentRecyclerView start
        //Horizontal list content by Special
        rqSpecialContent = Volley.newRequestQueue(getActivity());
        specialContentRecyclerView = (RecyclerView) view.findViewById(R.id.rv_f_main_special_content);
        specialContentRecyclerView.setHasFixedSize(true);
        specialLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        specialContentRecyclerView.setLayoutManager(specialLayoutManager);
        specialContentModelsList = new ArrayList<>();
        sendSpecialContentRequest();

        return view;
    }


    //==========================================================================//
    public void sendCategoryHorizentalRequest() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_MAIN_CATEGORY_URL + "?api_key=" + Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                {
                    //No result found!
                    Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    CategoryModel categoryModel = new CategoryModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        categoryModel.setCategoryId(jsonObject.getString("category_id"));
                        categoryModel.setCategoryImage(jsonObject.getString("category_image"));
                        categoryModel.setCategoryTitle(jsonObject.getString("category_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    horCatItemModelsList.add(categoryModel);

                }

                horCatAdapter = new CategoryHorizentalAdapter(getActivity(), horCatItemModelsList);
                horizentalCategoryRecyclerView.setAdapter(horCatAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqHorCatItem.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendFeaturedContentRequest() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_FEATURED_CONTENT_URL+"?limit=15&last_id=0&api_key=" + Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    featuredContentModelsList.add(contentModel);
                }

                featuredAdapter = new ContentHorizentalAdapter(getActivity(), featuredContentModelsList);
                featuredContentRecyclerView.setAdapter(featuredAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqFeaturedContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendBestRatedContentRequest() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_BEST_RATED_CONTENT_URL+"?limit=15&last_id=0&api_key=" + Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bestRatedContentModelsList.add(contentModel);
                }

                bestRatedAdapter = new ContentHorizentalAdapter(getActivity(), bestRatedContentModelsList);
                bestRatedContentRecyclerView.setAdapter(bestRatedAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqBestRatedContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendLatestContentRequest() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_LATEST_CONTENT_URL+"?limit=15&last_id=0&api_key=" + Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    latestContentModelsList.add(contentModel);

                }

                latestAdapter = new ContentHorizentalAdapter(getActivity(), latestContentModelsList);
                latestContentRecyclerView.setAdapter(latestAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqLatestContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendSpecialContentRequest() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_SPECIAL_CONTENT_URL+"?limit=15&last_id=0&api_key=" + Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    specialContentModelsList.add(contentModel);

                }

                specialAdapter = new ContentHorizentalAdapter(getActivity(), specialContentModelsList);
                specialContentRecyclerView.setAdapter(specialAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqSpecialContent.add(jsonArrayRequest);
    }
}
