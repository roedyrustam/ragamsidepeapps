package com.sidepe.multicontent.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.utils.AppController;
import com.sidepe.multicontent.utils.ImagePickerUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class UploadProfilePhotoActivity extends RuntimePermissionsActivity {

    final int READ_WRITE_EXTERNAL_REQUEST_CODE = 1;
    final int REQUEST_CHOOSE_IMAGE_GALLERY = 2;
    ImageView iv_upload_profile_photo;
    Button btn_upload_profile_photo;
    private String codedImage = "";
    private ProgressWheel progressWheelInterpolated;
    String userId, oldUserImage, imgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_photo);

        setTitle(R.string.txt_upload_new_photo);

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.upload_profile_progress_wheel);

        userId = ((AppController) UploadProfilePhotoActivity.this.getApplication()).getUserId();

        if(userId.equals(null)) {
            Intent intent = new Intent(UploadProfilePhotoActivity.this, MainActivity.class);
            startActivity(intent);
        }

        oldUserImage = ((AppController) UploadProfilePhotoActivity.this.getApplication()).getUserImage();
        Random rand = new Random();
        int value = rand.nextInt(100000);
        imgName = "img_"+value+userId;

        //Add permission from activity
        UploadProfilePhotoActivity.super.requestAppPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_WRITE_EXTERNAL_REQUEST_CODE);

        String user_image = ((AppController) UploadProfilePhotoActivity.this.getApplication()).getUserImage();
        iv_upload_profile_photo = (ImageView) findViewById(R.id.iv_upload_profile_photo);
        Glide.with(UploadProfilePhotoActivity.this)
                .load(Config.USER_IMG_URL+user_image)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                        .placeholder(R.drawable.pre_loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate())
                .into(iv_upload_profile_photo);


        iv_upload_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add permission from activity
                UploadProfilePhotoActivity.super.requestAppPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_WRITE_EXTERNAL_REQUEST_CODE);
            }
        });


        btn_upload_profile_photo = (Button) findViewById(R.id.btn_upload_profile_photo);
        btn_upload_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codedImage.equals(""))
                    Toast.makeText(UploadProfilePhotoActivity.this,R.string.txt_please_select_an_image,Toast.LENGTH_LONG).show();
                else
                    uploadImageRequest();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHOOSE_IMAGE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Toast.makeText(UploadProfilePhotoActivity.this,"Request Code: "+requestCode+" resultCode: "+resultCode+ "Data: "+data.getData(),Toast.LENGTH_LONG).show();
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                codedImage = ImagePickerUtil.getStringImage(bitmap,400);
                //iv_upload_profile_photo.setImageBitmap(bitmap);
                Glide.with(UploadProfilePhotoActivity.this)
                        .load(bitmap)
                        .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                                .placeholder(R.drawable.pre_loading)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate())
                        .into(iv_upload_profile_photo);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void uploadImageRequest() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        btn_upload_profile_photo.setEnabled(false);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.UPLOAD_IMAGE_REQUEST_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String getAnswer = response.toString();

                        //Toast.makeText(UploadProfilePhotoActivity.this, "Response: "+getAnswer,Toast.LENGTH_LONG).show();

                        if (getAnswer.equals("Success")) {
                            Toast.makeText(UploadProfilePhotoActivity.this, R.string.txt_your_profile_photo_has_been_changed_successfully,Toast.LENGTH_LONG).show();
                            ((AppController) UploadProfilePhotoActivity.this.getApplication()).setUserImage(imgName);

                            //Restart App
                            Intent i = UploadProfilePhotoActivity.this.getPackageManager().getLaunchIntentForPackage(UploadProfilePhotoActivity.this.getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            UploadProfilePhotoActivity.this.finish();

                        }else {
                            Toast.makeText(UploadProfilePhotoActivity.this, getAnswer, Toast.LENGTH_LONG).show();
                            btn_upload_profile_photo.setEnabled(true);
                        }

                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadProfilePhotoActivity.this,"Error: "+error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        btn_upload_profile_photo.setEnabled(true);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("coded_image",codedImage);
                params.put("user_id",userId);
                params.put("img_name",imgName);
                params.put("old_user_image",oldUserImage);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);

    }


    @Override
    public void onPermissionsGranted(int requestCode) {
        if(requestCode == READ_WRITE_EXTERNAL_REQUEST_CODE) {
            ImagePickerUtil.showImagePicker(UploadProfilePhotoActivity.this, REQUEST_CHOOSE_IMAGE_GALLERY);
        }
    }


    @Override
    public void onPermissionsDeny(int requestCode) {
        Toast.makeText(getApplicationContext(),getString(R.string.txt_read_and_write_external_request_is_not_granted),Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }


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