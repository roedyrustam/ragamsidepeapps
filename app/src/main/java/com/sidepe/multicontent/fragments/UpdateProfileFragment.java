package com.sidepe.multicontent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.R;
import com.sidepe.multicontent.activities.UploadProfilePhotoActivity;
import com.sidepe.multicontent.utils.AppController;
import com.sidepe.multicontent.utils.Tools;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;


public class UpdateProfileFragment extends Fragment {
    public UpdateProfileFragment() { }
    CoordinatorLayout updateProfileCoordinatorLayout;
    private ProgressWheel progressWheelInterpolated;
    TextInputEditText firstnameUpdate;
    TextInputEditText lastnameUpdate;
    TextInputEditText usernameUpdate;
    TextInputEditText emailUpdate;
    TextInputEditText passwordUpdate;
    ImageButton editProfilePhoto;
    Button btnUpdate;
    final int READ_WRITE_CAMERA_EXTERNAL_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //String myValue = this.getArguments().getString("message"); // Get variable
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        //Set ActionBar Title
        getActivity().setTitle(R.string.nav_update_my_profile);

        updateProfileCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.updateProfileCoordinatorLayout);

        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) view.findViewById(R.id.update_profile_progress_wheel);

        editProfilePhoto = (ImageButton) view.findViewById(R.id.edit_profile_photo);
        firstnameUpdate = (TextInputEditText)view.findViewById(R.id.et_update_firstname);
        lastnameUpdate = (TextInputEditText)view.findViewById(R.id.et_update_lastname);
        usernameUpdate = (TextInputEditText)view.findViewById(R.id.et_update_username);
        emailUpdate = (TextInputEditText)view.findViewById(R.id.et_update_email);
        passwordUpdate = (TextInputEditText)view.findViewById(R.id.et_update_password);

        Glide.with(getActivity())
                .load(Config.USER_IMG_URL+((AppController) getActivity().getApplication()).getUserImage())
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                        .placeholder(R.drawable.pre_loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate())
                .into(editProfilePhoto);

        editProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadProfilePhotoActivity.class);
                startActivity(intent);
            }
        });


        firstnameUpdate.setText(((AppController) getActivity().getApplication()).getUserFirstName());
        lastnameUpdate.setText(((AppController) getActivity().getApplication()).getUserLastName());
        usernameUpdate.setText(((AppController) getActivity().getApplication()).getUserUserName());
        emailUpdate.setText(((AppController) getActivity().getApplication()).getUserEmail());

        btnUpdate = (Button) view.findViewById(R.id.btn_update_profile);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hidden the keyboard
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(updateProfileCoordinatorLayout.getWindowToken(), 0);

                performUpdate();
            }
        });

        //Show done button on keyboard
        passwordUpdate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hidden the keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(updateProfileCoordinatorLayout.getWindowToken(), 0);

                    performUpdate();
                    return true;
                }
                return false;
            }
        });


        return view;
    }


    //==========================================================================//
    public void performUpdate() {
        final String username = usernameUpdate.getText().toString();
        final String firstname = firstnameUpdate.getText().toString();
        final String lastname = lastnameUpdate.getText().toString();
        final String email = emailUpdate.getText().toString();
        final String password = passwordUpdate.getText().toString();

        if (firstname.equals("")) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_empty_fullname, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (firstname.length() < 3) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_fullname_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (lastname.equals("")) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_empty_fullname, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (lastname.length() < 3) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_fullname_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (email.equals("")) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_empty_email, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (email.length() < 8) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_email_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (!Config.isEmailValid(email)) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_email_not_valid, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (username.equals("")) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_empty_username, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else if (!password.equals("") && password.length() < 8) {
            Snackbar snackbar = Snackbar.make(updateProfileCoordinatorLayout, R.string.txt_password_length_error, Snackbar.LENGTH_LONG);
            snackbar.show();

        }else {
            btnUpdate.setEnabled(false);
            btnUpdate.setText(R.string.txt_please_wait);
            //Send Update Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.UPDATE_REQUEST_URL + "?api_key=" + Config.API_KEY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            //Toast.makeText(getApplicationContext(),getAnswer,Toast.LENGTH_LONG).show();
                            if (getAnswer.equals("Success")) {
                                Toast.makeText(getActivity(),R.string.txt_update_profile_success,Toast.LENGTH_LONG).show();
                                btnUpdate.setEnabled(true);
                                btnUpdate.setText(R.string.txt_update_profile);

                                //Restart App
                                Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                getActivity().finish();

                            }else {
                                Toast.makeText(getActivity(), getAnswer, Toast.LENGTH_LONG).show();
                                btnUpdate.setEnabled(true);
                                btnUpdate.setText(R.string.txt_update_profile);
                            }

                            progressWheelInterpolated.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(),"Error: "+error,Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            btnUpdate.setEnabled(true);
                            btnUpdate.setText(R.string.txt_update_profile);
                        }
                    }
            ){
                //To send our parametrs
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("user_username",username);
                    params.put("user_firstname",firstname);
                    params.put("user_lastname",lastname);
                    params.put("user_email",email);
                    params.put("user_password",password);

                    return params;
                }
            };

            //To avoid send twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(requestPostResponse);
        }
    }

}