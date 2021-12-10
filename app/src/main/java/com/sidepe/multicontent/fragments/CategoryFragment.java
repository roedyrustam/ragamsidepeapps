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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.adapters.CategoryAdapter;
import com.sidepe.multicontent.models.CategoryModel;
import com.sidepe.multicontent.utils.Tools;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.sidepe.multicontent.R;

public class CategoryFragment extends Fragment {
    public CategoryFragment() { }
    CoordinatorLayout categoryCoordinatorLayout;
    RecyclerView categoryRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<CategoryModel> categoryModelsList;
    RequestQueue rqCat;
    private ProgressWheel progressWheelInterpolated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //String myValue = this.getArguments().getString("message"); // Get variable
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        //Set ActionBar Title
        getActivity().setTitle(R.string.nav_category);

        categoryCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.categoryCoordinatorLayout);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(categoryCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
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
        progressWheelInterpolated = (ProgressWheel) view.findViewById(R.id.category_progress_wheel);

        //categoryRecyclerView start
        //Vertical one column
        rqCat = Volley.newRequestQueue(getActivity());
        categoryRecyclerView = (RecyclerView) view.findViewById(R.id.recycleViewCategory);
        categoryRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryModelsList = new ArrayList<>();
        sendCategoryRequest();

        return view;
    }


    //==========================================================================//
    public void sendCategoryRequest(){
        //Show ProgressDialog
        //final ProgressDialog pDialogCat;
        //pDialogCat = new ProgressDialog(getActivity());
        //pDialogCat.setMessage(this.getResources().getString(R.string.txt_loading_category));
        //pDialogCat.setCancelable(true);
        //pDialogCat.show();
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
                    categoryModelsList.add(categoryModel);
                }

                mAdapter = new CategoryAdapter(getActivity(), categoryModelsList);
                categoryRecyclerView.setAdapter(mAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqCat.add(jsonArrayRequest);
    }

}