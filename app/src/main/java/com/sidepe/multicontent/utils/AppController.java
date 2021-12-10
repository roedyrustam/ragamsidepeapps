package com.sidepe.multicontent.utils;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import com.sidepe.multicontent.R;


public class AppController extends Application
{
	public static final String TAG = AppController.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private static AppController mInstance;

	private String userId;
	private String userUserName;
	private String userFirstName;
	private String userLastName;
	private String userImage;
	private String userMobile;
	private String userEmail;
	private String userCoin;
	private String userCredit;
	private String userReferral;
	private String userEmailVerified;
	private String userMobileVerified;
	private String userRoleID;
	private String userRoleTitle;
	private String userHideBannerAd;
	private String userHideInterstitialAd;
	private String settingAppName;
	private String settingWebsite;
	private String settingEmail;
	private String settingVersionCode;
	private String settingAndroidMaintenance;
	private String settingTextMaintenance;
	private String settingOneSignaAppId;
	private String settingYouTubeApiKey;
	private String admobSettingAppId;
	private String admobSettingBannerUnitId;
	private String admobSettingInterstitialUnitId;
	private String admobSettingBannerSize;
	private String admobSettingInterstitialClicks;
	private String admobSettingBannerStatus;
	private String admobSettingInterstitialStatus;
	private String reward_coin_banner_ad_exp, reward_coin_interstitial_ad_exp, reward_coin_rewarded_ad_exp, reward_coin_native_ad_exp, reward_coin_play_game_exp, reward_coin_banner_ad_coin_req, reward_coin_interstitial_ad_coin_req,
			reward_coin_rewarded_ad_coin_req, reward_coin_native_ad_coin_req, reward_coin_vip_user_coin_req, reward_coin_banner_ad_click, reward_coin_interstitial_ad_click, reward_coin_rewarded_ad_click,
			reward_coin_native_ad_click, reward_coin_write_review, reward_coin_play_game, reward_coin_referral_user, reward_coin_referral_friend, reward_coin_publish_game, reward_coin_withdrawal_coin_minimum_req,
            reward_coin_price_of_each_coin, reward_coin_watching_video_exp, reward_coin_watching_video;

    public String getSettingYouTubeApiKey() {
        return settingYouTubeApiKey;
    }

    public void setSettingYouTubeApiKey(String settingYouTubeApiKey) {
        this.settingYouTubeApiKey = settingYouTubeApiKey;
    }

    public String getReward_coin_watching_video_exp() {
        return reward_coin_watching_video_exp;
    }

    public void setReward_coin_watching_video_exp(String reward_coin_watching_video_exp) {
        this.reward_coin_watching_video_exp = reward_coin_watching_video_exp;
    }

    public String getReward_coin_watching_video() {
        return reward_coin_watching_video;
    }

    public void setReward_coin_watching_video(String reward_coin_watching_video) {
        this.reward_coin_watching_video = reward_coin_watching_video;
    }

    public String getReward_coin_withdrawal_coin_minimum_req() {
        return reward_coin_withdrawal_coin_minimum_req;
    }

    public void setReward_coin_withdrawal_coin_minimum_req(String reward_coin_withdrawal_coin_minimum_req) {
        this.reward_coin_withdrawal_coin_minimum_req = reward_coin_withdrawal_coin_minimum_req;
    }

    public String getReward_coin_price_of_each_coin() {
        return reward_coin_price_of_each_coin;
    }

    public void setReward_coin_price_of_each_coin(String reward_coin_price_of_each_coin) {
        this.reward_coin_price_of_each_coin = reward_coin_price_of_each_coin;
    }

    public String getReward_coin_banner_ad_exp() {
		return reward_coin_banner_ad_exp;
	}

	public String getReward_coin_play_game() {
		return reward_coin_play_game;
	}

	public void setReward_coin_play_game(String reward_coin_play_game) {
		this.reward_coin_play_game = reward_coin_play_game;
	}

	public String getReward_coin_play_game_exp() {
		return reward_coin_play_game_exp;
	}

	public void setReward_coin_play_game_exp(String reward_coin_play_game_exp) {
		this.reward_coin_play_game_exp = reward_coin_play_game_exp;
	}

	public void setReward_coin_banner_ad_exp(String reward_coin_banner_ad_exp) {
		this.reward_coin_banner_ad_exp = reward_coin_banner_ad_exp;
	}

	public String getReward_coin_interstitial_ad_exp() {
		return reward_coin_interstitial_ad_exp;
	}

	public void setReward_coin_interstitial_ad_exp(String reward_coin_interstitial_ad_exp) {
		this.reward_coin_interstitial_ad_exp = reward_coin_interstitial_ad_exp;
	}

	public String getReward_coin_rewarded_ad_exp() {
		return reward_coin_rewarded_ad_exp;
	}

	public void setReward_coin_rewarded_ad_exp(String reward_coin_rewarded_ad_exp) {
		this.reward_coin_rewarded_ad_exp = reward_coin_rewarded_ad_exp;
	}

	public String getReward_coin_native_ad_exp() {
		return reward_coin_native_ad_exp;
	}

	public void setReward_coin_native_ad_exp(String reward_coin_native_ad_exp) {
		this.reward_coin_native_ad_exp = reward_coin_native_ad_exp;
	}

	public String getReward_coin_banner_ad_coin_req() {
		return reward_coin_banner_ad_coin_req;
	}

	public void setReward_coin_banner_ad_coin_req(String reward_coin_banner_ad_coin_req) {
		this.reward_coin_banner_ad_coin_req = reward_coin_banner_ad_coin_req;
	}

	public String getReward_coin_interstitial_ad_coin_req() {
		return reward_coin_interstitial_ad_coin_req;
	}

	public void setReward_coin_interstitial_ad_coin_req(String reward_coin_interstitial_ad_coin_req) {
		this.reward_coin_interstitial_ad_coin_req = reward_coin_interstitial_ad_coin_req;
	}

	public String getReward_coin_rewarded_ad_coin_req() {
		return reward_coin_rewarded_ad_coin_req;
	}

	public void setReward_coin_rewarded_ad_coin_req(String reward_coin_rewarded_ad_coin_req) {
		this.reward_coin_rewarded_ad_coin_req = reward_coin_rewarded_ad_coin_req;
	}

	public String getReward_coin_native_ad_coin_req() {
		return reward_coin_native_ad_coin_req;
	}

	public void setReward_coin_native_ad_coin_req(String reward_coin_native_ad_coin_req) {
		this.reward_coin_native_ad_coin_req = reward_coin_native_ad_coin_req;
	}

	public String getReward_coin_vip_user_coin_req() {
		return reward_coin_vip_user_coin_req;
	}

	public void setReward_coin_vip_user_coin_req(String reward_coin_vip_user_coin_req) {
		this.reward_coin_vip_user_coin_req = reward_coin_vip_user_coin_req;
	}

	public String getReward_coin_banner_ad_click() {
		return reward_coin_banner_ad_click;
	}

	public void setReward_coin_banner_ad_click(String reward_coin_banner_ad_click) {
		this.reward_coin_banner_ad_click = reward_coin_banner_ad_click;
	}

	public String getReward_coin_interstitial_ad_click() {
		return reward_coin_interstitial_ad_click;
	}

	public void setReward_coin_interstitial_ad_click(String reward_coin_interstitial_ad_click) {
		this.reward_coin_interstitial_ad_click = reward_coin_interstitial_ad_click;
	}

	public String getReward_coin_rewarded_ad_click() {
		return reward_coin_rewarded_ad_click;
	}

	public void setReward_coin_rewarded_ad_click(String reward_coin_rewarded_ad_click) {
		this.reward_coin_rewarded_ad_click = reward_coin_rewarded_ad_click;
	}

	public String getReward_coin_native_ad_click() {
		return reward_coin_native_ad_click;
	}

	public void setReward_coin_native_ad_click(String reward_coin_native_ad_click) {
		this.reward_coin_native_ad_click = reward_coin_native_ad_click;
	}

	public String getReward_coin_write_review() {
		return reward_coin_write_review;
	}

	public void setReward_coin_write_review(String reward_coin_write_review) {
		this.reward_coin_write_review = reward_coin_write_review;
	}

	public String getReward_coin_referral_user() {
		return reward_coin_referral_user;
	}

	public void setReward_coin_referral_user(String reward_coin_referral_user) {
		this.reward_coin_referral_user = reward_coin_referral_user;
	}

	public String getReward_coin_referral_friend() {
		return reward_coin_referral_friend;
	}

	public void setReward_coin_referral_friend(String reward_coin_referral_friend) {
		this.reward_coin_referral_friend = reward_coin_referral_friend;
	}

	public String getReward_coin_publish_game() {
		return reward_coin_publish_game;
	}

	public void setReward_coin_publish_game(String reward_coin_publish_game) {
		this.reward_coin_publish_game = reward_coin_publish_game;
	}

	public String getUserHideBannerAd() {
		return userHideBannerAd;
	}

	public void setUserHideBannerAd(String userHideBannerAd) {
		this.userHideBannerAd = userHideBannerAd;
	}

	public String getUserHideInterstitialAd() {
		return userHideInterstitialAd;
	}

	public void setUserHideInterstitialAd(String userHideInterstitialAd) {
		this.userHideInterstitialAd = userHideInterstitialAd;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getUserRoleID() {
		return userRoleID;
	}

	public void setUserRoleID(String userRoleID) {
		this.userRoleID = userRoleID;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserRoleTitle() {
		return userRoleTitle;
	}

	public void setUserRoleTitle(String userRoleTitle) {
		this.userRoleTitle = userRoleTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static String getTAG() {
		return TAG;
	}

	public String getUserUserName() {
		return userUserName;
	}

	public void setUserUserName(String userUserName) {
		this.userUserName = userUserName;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserCoin() {
		return userCoin;
	}

	public void setUserCoin(String userCoin) {
		this.userCoin = userCoin;
	}

	public String getUserCredit() {
		return userCredit;
	}

	public void setUserCredit(String userCredit) {
		this.userCredit = userCredit;
	}

	public String getUserReferral() {
		return userReferral;
	}

	public void setUserReferral(String userReferral) {
		this.userReferral = userReferral;
	}

	public String getUserEmailVerified() {
		return userEmailVerified;
	}

	public void setUserEmailVerified(String userEmailVerified) {
		this.userEmailVerified = userEmailVerified;
	}

	public String getUserMobileVerified() {
		return userMobileVerified;
	}

	public void setUserMobileVerified(String userMobileVerified) {
		this.userMobileVerified = userMobileVerified;
	}

	public String getSettingAppName() {
		return settingAppName;
	}

	public void setSettingAppName(String settingAppName) {
		this.settingAppName = settingAppName;
	}

    public String getSettingWebsite() {
        return settingWebsite;
    }

    public void setSettingWebsite(String settingWebsite) {
        this.settingWebsite = settingWebsite;
    }

    public String getSettingEmail() {
		return settingEmail;
	}

	public void setSettingEmail(String settingEmail) {
		this.settingEmail = settingEmail;
	}

	public String getSettingVersionCode() {
		return settingVersionCode;
	}

	public void setSettingVersionCode(String settingVersionCode) {
		this.settingVersionCode = settingVersionCode;
	}

	public String getSettingAndroidMaintenance() {
		return settingAndroidMaintenance;
	}

	public void setSettingAndroidMaintenance(String settingAndroidMaintenance) {
		this.settingAndroidMaintenance = settingAndroidMaintenance;
	}

	public String getSettingTextMaintenance() {
		return settingTextMaintenance;
	}

	public void setSettingTextMaintenance(String settingTextMaintenance) {
		this.settingTextMaintenance = settingTextMaintenance;
	}

    public String getSettingOneSignaAppId() {
        return settingOneSignaAppId;
    }

    public void setSettingOneSignaAppId(String settingOneSignaAppId) {
        this.settingOneSignaAppId = settingOneSignaAppId;
    }

    public String getAdmobSettingAppId() {
        return admobSettingAppId;
    }

    public void setAdmobSettingAppId(String admobSettingAppId) {
        this.admobSettingAppId = admobSettingAppId;
    }

    public String getAdmobSettingBannerUnitId() {
        return admobSettingBannerUnitId;
    }

    public void setAdmobSettingBannerUnitId(String admobSettingBannerUnitId) {
        this.admobSettingBannerUnitId = admobSettingBannerUnitId;
    }

    public String getAdmobSettingInterstitialUnitId() {
        return admobSettingInterstitialUnitId;
    }

    public void setAdmobSettingInterstitialUnitId(String admobSettingInterstitialUnitId) {
        this.admobSettingInterstitialUnitId = admobSettingInterstitialUnitId;
    }

    public String getAdmobSettingBannerSize() {
        return admobSettingBannerSize;
    }

    public void setAdmobSettingBannerSize(String admobSettingBannerSize) {
        this.admobSettingBannerSize = admobSettingBannerSize;
    }

    public String getAdmobSettingInterstitialClicks() {
        return admobSettingInterstitialClicks;
    }

    public void setAdmobSettingInterstitialClicks(String admobSettingInterstitialClicks) {
        this.admobSettingInterstitialClicks = admobSettingInterstitialClicks;
    }

    public String getAdmobSettingBannerStatus() {
        return admobSettingBannerStatus;
    }

    public void setAdmobSettingBannerStatus(String admobSettingBannerStatus) {
        this.admobSettingBannerStatus = admobSettingBannerStatus;
    }

    public String getAdmobSettingInterstitialStatus() {
        return admobSettingInterstitialStatus;
    }

    public void setAdmobSettingInterstitialStatus(String admobSettingInterstitialStatus) {
        this.admobSettingInterstitialStatus = admobSettingInterstitialStatus;
    }

    //Set and Get local variable
	// To Set
	//((AppController) this.getApplication()).setSomeVariable("foo");
	//((AppController) MainActivity.this.getApplication()).setUserId(jsonObject.getString("user_id"));

	// To Get
	//String s = ((AppController) this.getApplication()).getSomeVariable();

	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		//For Custom Font
		ViewPump.init(ViewPump.builder()
				.addInterceptor(new CalligraphyInterceptor(
						new CalligraphyConfig.Builder()
								.setDefaultFontPath("fonts/Roboto-Regular.ttf")
								.setFontAttrId(R.attr.fontPath)
								.build()))
				.build());
	}

	public static synchronized AppController getInstance()
	{
		return mInstance;
	}

	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag)
	{
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req)
	{
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag)
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(tag);
		}
	}

	public void cancelPendingRequests()
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(TAG);
		}
	}
}