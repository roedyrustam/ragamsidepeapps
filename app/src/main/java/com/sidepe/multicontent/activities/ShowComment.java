package com.sidepe.multicontent.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.adapters.CommentAdapter;
import com.sidepe.multicontent.models.CommentModel;
import com.sidepe.multicontent.utils.Tools;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowComment extends AppCompatActivity {

    ConstraintLayout showCommentConstraintlayout;
    RecyclerView commentRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<CommentModel> commentModelsList;
    RequestQueue rqComment;
    private ProgressWheel progressWheelInterpolated;
    String contentId;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);
        setTitle(R.string.txt_show_comment);

        showCommentConstraintlayout = (ConstraintLayout) findViewById(R.id.showCommentConstraintlayout);

        contentId = getIntent().getStringExtra("contentId");

        //Check internet connection start
        if (!Tools.isNetworkAvailable(ShowComment.this)) {
            Snackbar snackbar = Snackbar.make(showCommentConstraintlayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh
                            finish();
                            startActivity(getIntent());
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.comment_progress_wheel);

        //categoryRecyclerView start
        //Vertical one column
        rqComment = Volley.newRequestQueue(ShowComment.this);
        commentRecyclerView = (RecyclerView) findViewById(R.id.rv_comments);
        commentRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ShowComment.this);
        commentRecyclerView.setLayoutManager(layoutManager);
        commentModelsList = new ArrayList<>();
        mAdapter = new CommentAdapter(ShowComment.this, commentModelsList);
        commentRecyclerView.setAdapter(mAdapter);
        commentRecyclerView.setNestedScrollingEnabled(true);

        sendCommentRequestFirstTime();

        //For Load More
        commentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            sendCommentRequestLoadMore();
                        }
                    }
                }
            }
        });
    }


    //==========================================================================//
    public void sendCommentRequestFirstTime(){
        lastId = "0";
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_COMMENT_URL+"?limit="+Config.LOAD_LIMIT+"&content_id="+contentId+"&last_id="+lastId+"&api_key="+Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0)
                {
                    //No result found!
                    Toast.makeText(ShowComment.this, R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    CommentModel commentModel = new CommentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("comment_id");

                        commentModel.setUser_username(jsonObject.getString("user_username"));
                        commentModel.setComment_user_id(jsonObject.getString("comment_user_id"));
                        commentModel.setComment_text(jsonObject.getString("comment_text"));
                        commentModel.setComment_time(jsonObject.getString("comment_time"));
                        commentModel.setComment_rate(jsonObject.getString("comment_rate"));
                        commentModel.setUser_image(jsonObject.getString("user_image"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commentModelsList.add(commentModel);
                    mAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                itShouldLoadMore = true;
                Log.i("BlueDev Volley Error: ", error+"");
                Snackbar snackbar = Snackbar.make(showCommentConstraintlayout, R.string.txt_no_result, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqComment.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void sendCommentRequestLoadMore(){
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_COMMENT_URL+"?limit=" + Config.LOAD_LIMIT + "&last_id=" + lastId + "&content_id=" + contentId + "&api_key=" + Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0)
                {
                    //No result found!
                    Toast.makeText(ShowComment.this, R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for(int i = 0; i < response.length(); i++){
                    CommentModel commentModel = new CommentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("comment_id");

                        commentModel.setUser_username(jsonObject.getString("user_username"));
                        commentModel.setComment_user_id(jsonObject.getString("comment_user_id"));
                        commentModel.setComment_text(jsonObject.getString("comment_text"));
                        commentModel.setComment_time(jsonObject.getString("comment_time"));
                        commentModel.setComment_rate(jsonObject.getString("comment_rate"));
                        commentModel.setUser_image(jsonObject.getString("user_image"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commentModelsList.add(commentModel);
                    mAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                itShouldLoadMore = true;
                Log.i("BlueDev Volley Error: ", error+"");
                Snackbar snackbar = Snackbar.make(showCommentConstraintlayout, R.string.txt_no_more_result, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqComment.add(jsonArrayRequest);
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
}
