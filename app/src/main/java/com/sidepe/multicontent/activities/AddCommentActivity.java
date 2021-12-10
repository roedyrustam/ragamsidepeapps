package com.sidepe.multicontent.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

public class AddCommentActivity extends AppCompatActivity {

    String contentId, userId, userRate, contentTitle;
    Button btn_add_comment;
    TextInputEditText textInputEditText;
    RatingBar ratingBar;
    private ProgressWheel progressWheelInterpolated;
    TextView tv_add_comment_sub_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        setTitle(R.string.txt_add_comment);

        contentId = getIntent().getStringExtra("contentId");
        userId = getIntent().getStringExtra("userId");
        contentTitle = getIntent().getStringExtra("contentTitle");

        tv_add_comment_sub_title = (TextView) findViewById(R.id.tv_add_comment_sub_title);
        tv_add_comment_sub_title.setText(contentTitle);

        textInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                userRate = String.valueOf(rating);
            }
        });
        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.add_comment_progress_wheel);

        btn_add_comment = findViewById(R.id.btn_add_comment);
        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_comment = textInputEditText.getText().toString();
                if(userRate == null )
                    Toast.makeText(AddCommentActivity.this, getString(R.string.txt_please_select_your_rating), Toast.LENGTH_LONG).show();
                else
                    //Hide keyboard
                    ((InputMethodManager) getSystemService(WithdrawalActivity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    addComment(user_comment, userRate);
            }
        });
    }


    //======================================================//
    public void addComment(final String user_comment, final String user_rate) {
        //Send Login Request
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.ADD_COMMENT_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        if (getAnswer.equals("Success")) {
                            Toast.makeText(AddCommentActivity.this, getString(R.string.txt_your_comment_sent_successfully), Toast.LENGTH_LONG).show();
                            //Toast.makeText(AddCommentActivity.this, getString(R.string.txt_you_earned_reward_coin), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddCommentActivity.this, ShowComment.class);
                            intent.putExtra("contentId", contentId);
                            finish();
                            startActivity(intent);

                        }else if (getAnswer.equals("YouSetReviewRecently")) {
                            Toast.makeText(AddCommentActivity.this, getString(R.string.txt_you_sent_review_recently), Toast.LENGTH_LONG).show();

                        }else if (getAnswer.equals("Failed")) {
                            Toast.makeText(AddCommentActivity.this, "Error: "+getAnswer, Toast.LENGTH_LONG).show();

                        }else
                            Toast.makeText(AddCommentActivity.this, "Error: "+getAnswer, Toast.LENGTH_LONG).show();

                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parameters
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("comment_user_id",userId);
                params.put("comment_content_id",contentId);
                params.put("comment_text",user_comment);
                params.put("comment_rate",user_rate);
                params.put("comment_device_type_id","2"); // Web:1 Android: 2 iOS: 3 Other: 4

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(30000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
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
