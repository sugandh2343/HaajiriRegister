package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityManpowerHomeBinding;

import java.util.ArrayList;

public class ManpowerHomeActivity extends AppCompatActivity {
    ActivityManpowerHomeBinding binding;
    String timestamp,siteName,companyName;
    private String userType;

    long siteId;
    private ProgressDialog progressDialog;
    private ArrayList<ModelSite> siteAdminList;
    FirebaseAuth firebaseAuth;
    String uid;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityManpowerHomeBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        Intent intent=getIntent();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType=sharedpreferences.getString("userDesignation","");

        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        MyApplication my = new MyApplication( );
        firebaseAuth= FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        siteAdminList=new ArrayList<>();
        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));
        if(userType.equals("Supervisor")){
            siteId=sharedpreferences.getLong("siteId",0);
            userType=sharedpreferences.getString("userDesignation","");
            binding.llSelectSite.setVisibility(View.GONE);
            binding.llMain.setVisibility(View.VISIBLE);
        }else{
            siteId=intent.getLongExtra("siteId",0);
            siteName=intent.getStringExtra("siteName");
            Log.e("SiteID7860",""+siteName+"::::"+siteId);
            binding.llSelectSite.setVisibility(View.VISIBLE);
            binding.llMain.setVisibility(View.GONE);
            getSiteListAdministrator();
        }

        binding.toolbar.inflateMenu(R.menu.menu_navigationbar);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return false;
            }
        });

        binding.btnSkilledViewMasterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManpowerHomeActivity.this, ViewMasterDataSheet.class);
                intent.putExtra("siteId",String.valueOf(siteId));
                intent.putExtra("siteName",siteName);
                intent.putExtra("userType","Unskilled");
                startActivity(intent);
                finish();

            }
        });
        binding.btnUnskilledViewMasterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManpowerHomeActivity.this,ViewMasterDataSheet.class);
                intent.putExtra("siteId",String.valueOf(siteId));
                intent.putExtra("userType","Unskilled");
                startActivity(intent);
                finish();
            }
        });
        binding.btnSkilledTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManpowerHomeActivity.this, MainActivity.class);
                intent.putExtra("type","Skilled");
                intent.putExtra("siteName",siteName);
                intent.putExtra("siteId",siteId);
                startActivity(intent);
                finish();
            }
        });
        binding.btnUnskilledTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManpowerHomeActivity.this,MainActivity.class);
                intent.putExtra("type","Unskilled");
                intent.putExtra("siteName",siteName);
                intent.putExtra("siteId",siteId);
                startActivity(intent);
                finish();

            }
        });
        binding.btnViewAllAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManpowerHomeActivity.this, ShowAttendanceActivity.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);
                finish();
            }
        });
        binding.downloadAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                    Intent intent=new Intent(ManpowerHomeActivity.this,ShowAttendanceActivity.class);
                    intent.putExtra("Activity","DownloadAttendance");
                    startActivity(intent);

                }else{
                    Toast.makeText(ManpowerHomeActivity.this, getString(R.string.cannot_download_report), Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equals("Supervisor")) {
                    Intent intent = new Intent(ManpowerHomeActivity.this, MemberTimelineActivity.class);
                    intent.putExtra("type", "Skilled");
                    intent.putExtra("siteName", siteName);
                    intent.putExtra("siteId", siteId);
                    startActivity(intent);
                    finish();
                }else {
                    startActivity(new Intent(ManpowerHomeActivity.this, timelineActivity.class));
                    finish();
                }
            }
        });
        binding.llPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getBoolean("cashManagement",true)||sharedpreferences.getBoolean("expenseManagement",true)){
                    Intent intent=new Intent(ManpowerHomeActivity.this, AdvancesHomeActivity.class);
                    intent.putExtra("type","Skilled");
                    intent.putExtra("SiteSpinner", true);
                    intent.putExtra("siteName",siteName);
                    intent.putExtra("siteId",siteId);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(ManpowerHomeActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    siteId = siteAdminList.get(i).getSiteId();
                    siteName = siteAdminList.get(i).getSiteName();
                    getSiteMemberStatus(siteId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getSiteMemberStatus(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("memberStatus").getValue(String.class).equals("Pending")) {
                    binding.llAttendance.setVisibility(View.VISIBLE);
                    binding.llMain.setVisibility(View.VISIBLE);
                }else{
                    binding.llAttendance.setVisibility(View.GONE);
                    binding.llMain.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteAdminList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    if(modelSite.getSiteStatus().equals("Active")){
                        siteAdminList.add(modelSite);
                    }

                }
                siteAdminList.add(0,new ModelSite(getString(R.string.select_site),0));
                Log.e("siteAdminList", "" + siteAdminList.size());
               SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                siteId = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId();
                siteName = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteCity();
                binding.llSelectSite.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    class SiteSpinnerAdapter
            extends BaseAdapter {
        SiteSpinnerAdapter() {
        }

        public int getCount() {
            return siteAdminList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
            textView.setText(siteAdminList.get(n).getSiteCity());
            return view2;
        }
    }
}