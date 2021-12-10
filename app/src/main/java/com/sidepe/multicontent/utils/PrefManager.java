package com.sidepe.multicontent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context mContext;


    private static final String pref_name = "PREF";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context){

        this.mContext=context;
        pref=context.getSharedPreferences(pref_name,0);
        editor=pref.edit();

    }

    public void setFirstTimeLaunch(Boolean isFirstTimeLaunch) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTimeLaunch);
        editor.commit();

    }

    public boolean IsFirstTimeLaunch() {
        return  pref.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

}
