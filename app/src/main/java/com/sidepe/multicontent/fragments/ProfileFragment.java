package com.sidepe.multicontent.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.activities.AccountUpgrade;
import com.sidepe.multicontent.activities.UploadProfilePhotoActivity;
import com.sidepe.multicontent.activities.WithdrawalActivity;
import com.sidepe.multicontent.utils.AppController;

import com.sidepe.multicontent.R;

import com.sidepe.multicontent.utils.Tools;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {
    RelativeLayout userProfileRelativeLayout;
    ConstraintLayout cl_referral_user;
    String userId;
    String firstname;
    String lastname;
    String email;
    String username;
    String roleTitle;
    String coin;
    String referral;
    String user_image;
    ImageView user_profile_photo;
    TextView userReferral, profileCredit;
    private SearchFragment searchFragment = new SearchFragment();
    private UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
    public ProfileFragment() { }
    private ProgressWheel progressProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //Set ActionBar Title
        getActivity().setTitle(R.string.txt_my_profile);

        //Material ProgressWheel
        progressProfile = (ProgressWheel) view.findViewById(R.id.profile_progress_wheel);

        userProfileRelativeLayout = (RelativeLayout) view.findViewById(R.id.userProfileRelativeLayout);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(userProfileRelativeLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh fragment
                            /*getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frmMain, new MainFragment())
                                    .commit();*/
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        getReferralCount();
        getUserCoin();

        //Get local variable from AppController
        userId = ((AppController) getActivity().getApplication()).getUserId();
        firstname = ((AppController) getActivity().getApplication()).getUserFirstName();
        lastname = ((AppController) getActivity().getApplication()).getUserLastName();
        email = ((AppController) getActivity().getApplication()).getUserEmail();
        username = ((AppController) getActivity().getApplication()).getUserUserName();
        roleTitle = ((AppController) getActivity().getApplication()).getUserRoleTitle();
        coin = ((AppController) getActivity().getApplication()).getUserCoin();
        referral = ((AppController) getActivity().getApplication()).getUserReferral();
        user_image = ((AppController) getActivity().getApplication()).getUserImage();

        user_profile_photo = (ImageView) view.findViewById(R.id.user_profile_photo);
        Glide.with(getActivity())
                .load(Config.USER_IMG_URL+user_image)
                //.apply(RequestOptions.circleCropTransform() //Circle Image
                //.apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(100)) //Rounded and Circle Image
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                        .placeholder(R.drawable.pre_loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate())
                .into(user_profile_photo);

        user_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadProfilePhotoActivity.class);
                startActivity(intent);
            }
        });

        TextView textViewArrow1 = (TextView) view.findViewById(R.id.textViewArrow1);
        textViewArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(getActivity());
                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_update_profile));
                builderCheckUpdate.setMessage(getResources().getString(R.string.txt_profile_web_login));
                builderCheckUpdate.setCancelable(false);

                builderCheckUpdate.setPositiveButton(
                        getResources().getString(R.string.txt_profile_web_login_btn),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_LOGIN_URL));
                                startActivity(browserIntent);
                            }
                        });

                builderCheckUpdate.setNegativeButton(
                        getResources().getString(R.string.txt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //getActivity().finish();
                            }
                        });

                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                alert1CheckUpdate.show();*/

                Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","UpdateProfile");
                bundle.putString("showTitle", getString(R.string.nav_update_my_profile));
                updateProfileFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .replace(R.id.frmMain, updateProfileFragment,"UPDATE_PROFILE_FRAGMENT_TAG")
                        .addToBackStack("UPDATE_PROFILE_FRAGMENT_TAG")
                        .commit();
            }
        });

        TextView textViewArrow2 = (TextView) view.findViewById(R.id.textViewArrow2);
        textViewArrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountUpgrade.class);
                startActivity(intent);
            }
        });

        //Hide if referral system is disable
        ConstraintLayout cl_referral_user = (ConstraintLayout) view.findViewById(R.id.cl_referral_user);
        if (((AppController) getActivity().getApplication()).getReward_coin_referral_friend().equals("0") && ((AppController) getActivity().getApplication()).getReward_coin_referral_user().equals("0")) {
            //cl_referral_user.setVisibility(View.GONE);
        }

        TextView textViewArrow3 = (TextView) view.findViewById(R.id.textViewArrow3);
        textViewArrow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(getActivity());
                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_referral_user));
                if (((AppController) getActivity().getApplication()).getReward_coin_referral_friend().equals("0") && ((AppController) getActivity().getApplication()).getReward_coin_referral_user().equals("0")) {
                    builderCheckUpdate.setMessage(getResources().getString(R.string.txt_your_referral_id_is)+" "+userId+"\n\n"+getResources().getString(R.string.txt_give_your_friends_the_above_no_reward));
                }else{
                    builderCheckUpdate.setMessage(getResources().getString(R.string.txt_your_referral_id_is)+" "+userId+"\n\n"+getResources().getString(R.string.txt_give_your_friends_the_above));
                }

                builderCheckUpdate.setCancelable(false);

                builderCheckUpdate.setPositiveButton(
                        getResources().getString(R.string.txt_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                alert1CheckUpdate.show();
            }
        });

        TextView textViewArrow4 = (TextView) view.findViewById(R.id.textViewArrow4);
        textViewArrow4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WithdrawalActivity.class);
                startActivity(intent);
            }
        });

        TextView textViewArrow5 = (TextView) view.findViewById(R.id.textViewArrow5);
        textViewArrow5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(getActivity());
                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_delete_account));
                builderCheckUpdate.setMessage(getResources().getString(R.string.txt_deleting_your_account_will_delete_all_your_data));
                builderCheckUpdate.setCancelable(false);

                builderCheckUpdate.setPositiveButton(
                        getResources().getString(R.string.txt_delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Second notice to delete account
                                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(getActivity());
                                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_confirm_deletion));
                                builderCheckUpdate.setMessage(getResources().getString(R.string.txt_are_sure_you_want_to_delete));
                                builderCheckUpdate.setCancelable(false);

                                builderCheckUpdate.setPositiveButton(
                                        getResources().getString(R.string.txt_yes_delete),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Perform delete account
                                                performDeleteAccount();
                                            }
                                        });

                                builderCheckUpdate.setNegativeButton(
                                        getResources().getString(R.string.txt_cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                                alert1CheckUpdate.show();
                            }
                        });

                builderCheckUpdate.setNegativeButton(
                        getResources().getString(R.string.txt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                alert1CheckUpdate.show();
            }
        });

        ConstraintLayout constraintlayoutUserCoin = (ConstraintLayout) view.findViewById(R.id.constraintlayoutUserCoin);
        constraintlayoutUserCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(userProfileRelativeLayout, getResources().getString(R.string.txt_your_coins), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        ConstraintLayout constraintlayoutReferral = (ConstraintLayout) view.findViewById(R.id.constraintlayoutReferral);
        constraintlayoutReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(userProfileRelativeLayout, getResources().getString(R.string.txt_all_the_users_that_you_are_referring_to), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        ConstraintLayout constraintlayoutUserRole = (ConstraintLayout) view.findViewById(R.id.constraintlayoutUserRole);
        constraintlayoutUserRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), getResources().getString(R.string.txt_all_the_users_that_you_are_referring_to), Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(userProfileRelativeLayout, getResources().getString(R.string.txt_your_user_role_is)+" "+roleTitle+".", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });


        TextView userProfileFullname = (TextView) view.findViewById(R.id.tv_user_profile_fullname);
        userProfileFullname.setText(firstname + " " + lastname);

        TextView userProfileEmail = (TextView) view.findViewById(R.id.tv_user_profile_email);
        userProfileEmail.setText(email);

        //TextView profileUsername = (TextView) view.findViewById(R.id.tv_profile_username);
        //profileUsername.setText(getResources().getString(R.string.txt_username) + ": " + username);

        userReferral = (TextView) view.findViewById(R.id.tv_profile_user_referral);
        userReferral.setText("... " + getResources().getString(R.string.txt_users));

        TextView userProfileRole = (TextView) view.findViewById(R.id.tv_profile_role);
        userProfileRole.setText(roleTitle);

        profileCredit = (TextView) view.findViewById(R.id.tv_profile_user_coin);
        profileCredit.setText(coin + " " + getResources().getString(R.string.txt_coins));

        ConstraintLayout constraintLayoutBookmark = (ConstraintLayout) view.findViewById(R.id.constraintlayoutBookmark);
        constraintLayoutBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to bookmark fragment
                Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","BookmarkContent");
                bundle.putString("showTitle", getString(R.string.nav_bookmark));
                searchFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .replace(R.id.frmMain, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }


    //======================================================//
    public void getReferralCount() {
        progressProfile.setVisibility(View.VISIBLE);
        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.GET_REFERRAL_COUNT + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        userReferral.setText(getAnswer+" " + getResources().getString(R.string.txt_users));
                        progressProfile.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //======================================================//
    public void getUserCoin() {
        progressProfile.setVisibility(View.VISIBLE);
        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.GET_ONLY_USER_COIN + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        profileCredit.setText(getAnswer + " " + getResources().getString(R.string.txt_coins));
                        progressProfile.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Error getUserCoin: "+error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //======================================================//
    public void performDeleteAccount() {
        progressProfile.setVisibility(View.VISIBLE);
        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.DELETE_USER_ACCOUNT + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String getAnswer = response.toString();
                        Toast.makeText(getActivity(),"Response: "+getAnswer,Toast.LENGTH_LONG).show();

                        if(getAnswer.equals("FormValidationError")) {
                            Toast.makeText(getActivity(),"Response: FormValidationError",Toast.LENGTH_LONG).show();

                        }else if(getAnswer.equals("YouCanNotDeleteSuperAdmin")) {
                            Toast.makeText(getActivity(),"Response: YouCanNotDeleteSuperAdmin",Toast.LENGTH_LONG).show();

                        }else if(getAnswer.equals("Failed")) {
                            Toast.makeText(getActivity(),"Response: Failed",Toast.LENGTH_LONG).show();

                        }else{
                            Toast.makeText(getActivity(),R.string.txt_your_account_has_been_deleted_successfully,Toast.LENGTH_LONG).show();
                            //Delete Account and Logout
                            SharedPreferences prefs = getActivity().getSharedPreferences("USER_LOGIN", Context.MODE_PRIVATE);
                            prefs.edit().clear().commit();
                            getActivity().finish();
                            System.exit(0);
                        }
                        progressProfile.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Error getUserCoin: "+error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }
}
