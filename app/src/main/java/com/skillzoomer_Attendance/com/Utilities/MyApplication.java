package com.skillzoomer_Attendance.com.Utilities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

public class MyApplication extends Application
{
    @Override
    public void onCreate()
    {

        updateLanguage(this);
        super.onCreate();

    }

    public static void updateLanguage(Context ctx)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString("locale_override", "");
        updateLanguage(ctx, lang);
    }

    public static void updateLanguage(Context ctx, String lang)
    {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.setLocale(new Locale(lang));
        else
            cfg.setLocale(Locale.ENGLISH);

        ctx.getResources().updateConfiguration(cfg, null);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

    }
}