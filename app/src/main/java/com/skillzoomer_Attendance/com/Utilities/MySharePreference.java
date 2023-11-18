package com.skillzoomer_Attendance.com.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharePreference
{
    private static MySharePreference instance;
    private static SharedPreferences pref;

    private MySharePreference(Context context)
    {
        if (context != null)
        {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        else
        {
            pref = PreferenceManager.getDefaultSharedPreferences(AppClass.getintance());
        }
    }

    public static MySharePreference getInstance(Context context)
    {
        if (instance == null || pref == null)
        {
            instance = new MySharePreference(context);
        }
        return instance;
    }
    public String getLanguage()
    {
        return pref.getString("appLanguage", "hi-rIN");
    }

    public void setLanguage(String b)
    {
        pref.edit().putString("appLanguage", b).apply();
    }
}