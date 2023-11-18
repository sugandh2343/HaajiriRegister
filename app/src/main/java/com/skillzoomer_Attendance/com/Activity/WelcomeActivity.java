package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
    ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWelcomeBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.btnLetsStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDynamicLink();
            }
        });
    }

    private void getDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        Toast.makeText(LoginActivity.this, "success get", Toast.LENGTH_SHORT).show();
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        Log.e("DeepLink",""+(pendingDynamicLinkData==null));
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if (deepLink != null) {
                            int site_id = 0;
                            String admin = "", role = "";
//                            System.out.println("=============data=====");
//                            System.out.println(deepLink.toString());
                            String mob = deepLink.getQueryParameter("mobile");
                            site_id = Integer.parseInt(deepLink.getQueryParameter("site_id"));
                            admin = deepLink.getQueryParameter("admin");
                            String companyName = deepLink.getQueryParameter("company");
                            String userName = deepLink.getQueryParameter("memberName");
                            Log.e("companyName22", companyName);

                            Intent i = new Intent(WelcomeActivity.this, RegisterActivity.class);
                            i.putExtra("mob", mob);
                            i.putExtra("admin", admin);
                            i.putExtra("site_id", site_id);
                            i.putExtra("role", "Supervisor");
                            i.putExtra("company", companyName);
                            i.putExtra("name", userName);

                            System.out.println("=================================data from link");
                            System.out.println("admin" + admin);
                            System.out.println("site" + site_id);
                            //Toast.makeText(getApplicationContext(), "site="+site_id, Toast.LENGTH_SHORT).show();
//                            System.out.println(mob);
                            startActivity(i);
                            finish();
                        }else{
                            startActivity(new Intent(WelcomeActivity.this,RegisterActivity.class));
                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}