package com.sidepe.multicontent.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.adapters.CategoryAdapter;
import com.sidepe.multicontent.adapters.ContentAdapter;
import com.sidepe.multicontent.models.CategoryModel;
import com.sidepe.multicontent.models.ContentModel;
import com.sidepe.multicontent.utils.Tools;

import static com.sidepe.multicontent.Config.API_KEY;
import static com.sidepe.multicontent.Config.GET_CONTENT_BY_CATEGORY_URL;
import static com.sidepe.multicontent.Config.GET_SUB_CATEGORY_URL;

public class SubCategoryFragment extends Fragment {
    public SubCategoryFragment() { }
    CoordinatorLayout subCategoryCoordinatorLayout;
    TextView tvFSC;
    RecyclerView subCategoryRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<CategoryModel> categoryModelsList;
    RequestQueue rqSubCat;
    String categoryId;
    //Content Variable for volley
    RecyclerView contentRecyclerView;
    RecyclerView.Adapter cAdapter;
    RecyclerView.LayoutManager cLayoutManager;
    List<ContentModel> contentModelsList;
    RequestQueue rqContent;
    private ProgressWheel progressWheelInterpolated;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    boolean noCategoryFound = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String theMainCategoryTitle = this.getArguments().getString("theMainCategoryTitle"); // Get variable
        categoryId = this.getArguments().getString("id"); // Get variable
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        //Set ActionBar Title
        getActivity().setTitle(theMainCategoryTitle);

        tvFSC = (TextView)view.findViewById(R.id.tv_fsc);
        tvFSC.setText("Sub Category "+theMainCategoryTitle);
        tvFSC.setVisibility(View.GONE);

        subCategoryCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.subCategoryCoordinatorLayout);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(subCategoryCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh fragment
                            /*getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frmMain, new CategoryFragment())
                                    .commit();*/
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) view.findViewById(R.id.sub_category_progress_wheel);

        //subCategoryRecyclerView start
        //Vertical one column
        rqSubCat = Volley.newRequestQueue(getActivity());
        subCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.recycleViewSubCategory);
        subCategoryRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        subCategoryRecyclerView.setLayoutManager(layoutManager);
        categoryModelsList = new ArrayList<>();
        mAdapter = new CategoryAdapter(getActivity(), categoryModelsList);
        subCategoryRecyclerView.setAdapter(mAdapter);
        subCategoryRecyclerView.setNestedScrollingEnabled(true);

        sendSubCategoryRequest();

        //contentRecyclerView start
        //List gamebox by category
        rqContent = Volley.newRequestQueue(getActivity());
        contentRecyclerView = (RecyclerView) view.findViewById(R.id.recycleViewContent);
        contentRecyclerView.setHasFixedSize(true);
        cLayoutManager = new LinearLayoutManager(getActivity());
        contentRecyclerView.setLayoutManager(cLayoutManager);
        contentModelsList = new ArrayList<>();
        cAdapter = new ContentAdapter(getActivity(), contentModelsList);
        contentRecyclerView.setAdapter(cAdapter);
        contentRecyclerView.setNestedScrollingEnabled(true);

        //Show gamebox for first time
        sendContentRequestFirstTime();

        //For Load More
        contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //Toast.makeText(getActivity(), "dy > 0", Toast.LENGTH_SHORT).show();
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) {
                            sendContentRequestLoadMore();
                        }
                    }
                }
            }
        });

        return view;
    }


    //==========================================================================//
    public void sendSubCategoryRequest(){
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_SUB_CATEGORY_URL+categoryId+"?api_key=" + API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                    noCategoryFound = true;
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
                    categoryModelsList.add(categoryModel);
                    mAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                noCategoryFound = true;
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqSubCat.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendContentRequestFirstTime() {
        lastId = "0";
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free
        //Toast.makeText(getActivity(), "First Time", Toast.LENGTH_SHORT).show();
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_CONTENT_BY_CATEGORY_URL+"?category_id="+categoryId+"&last_id="+lastId+"&limit="+Config.LOAD_LIMIT+"&api_key="+API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");

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
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                if(noCategoryFound == true) {
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                    tvFSC.setText(R.string.txt_no_result);
                    tvFSC.setVisibility(View.VISIBLE);
                }
                itShouldLoadMore = true;
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendContentRequestLoadMore() {
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        //Toast.makeText(getActivity(), "Load More", Toast.LENGTH_SHORT).show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_CONTENT_BY_CATEGORY_URL+"?category_id="+categoryId+"&last_id="+lastId+"&limit="+Config.LOAD_LIMIT+"&api_key="+API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0)
                {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");
                        //Toast.makeText(getActivity(), "Last ID LoadMore: "+lastId, Toast.LENGTH_SHORT).show();

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
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                itShouldLoadMore = true;
                Snackbar snackbar = Snackbar.make(subCategoryCoordinatorLayout, R.string.txt_no_more_result, Snackbar.LENGTH_LONG);
                snackbar.show();
                tvFSC.setText(R.string.txt_no_result);
                tvFSC.setVisibility(View.GONE);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }

}
