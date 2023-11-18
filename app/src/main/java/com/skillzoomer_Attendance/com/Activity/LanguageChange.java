package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skillzoomer_Attendance.com.Utilities.LocaleHelper;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityLanguageChangeBinding;

public class LanguageChange extends AppCompatActivity {
    ActivityLanguageChangeBinding binding;
    String[] language={"Hindi","English"};

    private String selectedLanguage;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorL;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLanguageChangeBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferencesL = getSharedPreferences("Language", Context.MODE_PRIVATE);
        editor=sharedpreferences.edit();
        editorL=sharedpreferencesL.edit();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SpinnerAdapter spinnerAdapter=new SpinnerAdapter();
        binding.spinnerLanguage.setAdapter(spinnerAdapter);
        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             selectedLanguage=language[i];   
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication my = new MyApplication( );
                if(selectedLanguage.equals(language[0])){
                    editor.putString("Language","hi");
                    editorL.putString("Language","hi");
                    editor.apply();
                    editor.commit();
                    editorL.commit();
                    editorL.commit();
                    context = LocaleHelper.setLocale(LanguageChange.this, "hi");
                    my.updateLanguage(LanguageChange.this, sharedpreferences.getString("Language","hi"));


                }else{
                    editor.putString("Language","en");
                    editorL.putString("Language","en");
                    editor.apply();
                    editor.commit();
                    editorL.commit();
                    editorL.commit();
                    context = LocaleHelper.setLocale(LanguageChange.this, "en");
                    my.updateLanguage(LanguageChange.this, sharedpreferences.getString("Language","en"));


                }
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LanguageChange.this);
                builder.setTitle(R.string.success)
                        .setMessage(R.string.language_change_successfully)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            if(sharedpreferences.getString("userDesignation","").equals("Supervisor")){
                                Intent intent = new Intent(LanguageChange.this, MemberTimelineActivity.class);
                                LanguageChange.this.startActivity(intent);
                                LanguageChange.this.finishAffinity();
                            }else{
                                Intent intent = new Intent(LanguageChange.this, timelineActivity.class);
                                LanguageChange.this.startActivity(intent);
                                LanguageChange.this.finishAffinity();
                            }

                        });

                builder.show();
            }
        });
    }
    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return language.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(language[position]);

            return row;
        }
    }
}