package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAdvancesHomeBinding;
import com.skillzoomer_Attendance.com.databinding.ActivityProfileBinding;

import java.util.ArrayList;

public class AdvancesHomeActivity extends AppCompatActivity {
    ActivityAdvancesHomeBinding binding;
    long siteId;
    long cashInHand;
    private String userType;
    String timestamp,siteName,companyName;
    Boolean siteSpinner;
    private ArrayList<ModelSite> siteAdminList;
    FirebaseAuth firebaseAuth;
    private int selected_option=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdvancesHomeBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent=getIntent();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType=sharedpreferences.getString("userDesignation","");
        selected_option=sharedpreferences.getInt("workOption",0);
        siteAdminList=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();
        Log.e("Value",""+sharedpreferences.getBoolean("cashManagement",true));
        Log.e("Value",""+sharedpreferences.getBoolean("expenseManagement",true));
        Log.e("Value",""+sharedpreferences.getBoolean("attendanceManagement",true));

        if(userType.equals("Supervisor")){
            if(sharedpreferences.getBoolean("cashManagement",true)){
                if(!sharedpreferences.getBoolean("expenseManagement",true)){
                    binding.llDayBook.setVisibility(View.VISIBLE);
                    binding.llPaymentDo.setVisibility(View.GONE);
                    binding.llAdvancesList.setVisibility(View.VISIBLE);
                    binding.btnDayBookList.setVisibility(View.VISIBLE);
                    binding.btnFundrequest.setVisibility(View.VISIBLE);
                }


            }else if(!sharedpreferences.getBoolean("cashManagement",true)){
                if(sharedpreferences.getBoolean("expenseManagement",true)){
                    binding.llDayBook.setVisibility(View.GONE);
                    binding.llPaymentDo.setVisibility(View.VISIBLE);
                    binding.llAdvancesList.setVisibility(View.VISIBLE);
                    binding.btnDayBookList.setVisibility(View.GONE);
                    binding.btnFundrequest.setVisibility(View.GONE);
                }
            }
            siteId=sharedpreferences.getLong("siteId",0);
            userType=sharedpreferences.getString("userDesignation","");
            binding.llSelectSite.setVisibility(View.GONE);
            binding.llCashInHand.setVisibility(View.VISIBLE);
            getCashInHand();
        }else{

            if(intent.getBooleanExtra("SiteSpinner",false)){
                if(selected_option==1){
                    binding.llPaymentDo.setVisibility(View.GONE);
                    binding.llManpower.setVisibility(View.GONE);
                    binding.llWorkActivity.setVisibility(View.VISIBLE);
                    binding.llCashInHand.setVisibility(View.VISIBLE);
                    binding.llDayBook.setVisibility(View.GONE);
                    binding.llDayBookList.setVisibility(View.VISIBLE);
                    getCashInHand();
                }else if(selected_option==2){
                    binding.llCashInHand.setVisibility(View.GONE);
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.llAdvancesList.setVisibility(View.GONE);
                    binding.llDayBookList.setVisibility(View.GONE);
                    binding.btnReceiveCash.setText(getString(R.string.day_book_list));
                    binding.llDayBook.setVisibility(View.VISIBLE);
                    getSiteListAdministrator();


                }else if(selected_option==3){
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                   binding.llAdvancesList.setVisibility(View.GONE);
                   getSiteListAdministrator();
                   Log.e("SpinnerPosition",""+binding.spinnerSelectSite.getSelectedItemPosition());
                   if(binding.spinnerSelectSite.getSelectedItemPosition()>0){
                      getSiteMemberStatus(siteId);
                   }



                }


            }else{
                binding.llSelectSite.setVisibility(View.GONE);
                binding.llCashInHand.setVisibility(View.VISIBLE);
                siteId=intent.getLongExtra("siteId",0);
                siteName=intent.getStringExtra("siteName");
                getCashInHand();
            }


            Log.e("SiteID7860",""+siteName+"::::"+siteId);
            binding.btnFundrequest.setVisibility(View.GONE);
        }
        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("select","SpinnerSite"+selected_option);
                if(i>0){
                    if(selected_option==2){
                        binding.llAdvancesList.setVisibility(View.VISIBLE);
                        siteId = siteAdminList.get(i).getSiteId();
                        siteName = siteAdminList.get(i).getSiteName();
                    }else if(selected_option==3){
                        binding.llAdvancesList.setVisibility(View.VISIBLE);
                        siteId = siteAdminList.get(i).getSiteId();
                        siteName = siteAdminList.get(i).getSiteName();
                        getSiteMemberStatus(siteId);
                        getCashInHand();
                    }
                }else{
                    if(userType.equals("Supervisor")){
                        binding.llCashInHand.setVisibility(View.VISIBLE);
                    }else{
                        binding.llCashInHand.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnDayBookList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(AdvancesHomeActivity.this,DayBookListActivity.class);
                intent1.putExtra("Activity","ShowAttendance");
                startActivity(intent1);
            }
        });


        binding.btnSkilledPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdvancesHomeActivity.this, PaymentActivity.class);
                intent.putExtra("siteName",siteName);
                intent.putExtra("siteId",siteId);
                intent.putExtra("WorkerType","Skilled");
                startActivity(intent);
        }
    });
        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdvancesHomeActivity.this, ViewMasterDataSheet.class));
            }
        });
    binding.btnUnskilledPayment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(AdvancesHomeActivity.this,PaymentActivity.class);
            intent.putExtra("WorkerType","Unskilled");
            intent.putExtra("siteName",siteName);
            intent.putExtra("siteId",siteId);
            startActivity(intent);

        }
    });
    binding.btnViewAdvancesReprt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(AdvancesHomeActivity.this, ShowPaymentActivity.class);
            intent.putExtra("Activity","ShowAttendance");
            startActivity(intent);
        }
    });
    binding.downloadAdvances.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                Intent intent=new Intent(AdvancesHomeActivity.this,ShowPaymentActivity.class);
                intent.putExtra("Activity","DownloadAttendance");
                startActivity(intent);

            }else{
                Toast.makeText(AdvancesHomeActivity.this,getString(R.string.cannot_download_report) , Toast.LENGTH_SHORT).show();
            }
        }
    });
    binding.btnFundrequest.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(AdvancesHomeActivity.this, FundRequestActivity.class));
        }
    });
    binding.llHome.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(userType.equals("Supervisor")) {
                Intent intent = new Intent(AdvancesHomeActivity.this, MemberTimelineActivity.class);
                intent.putExtra("type", "Skilled");
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteId", siteId);
                startActivity(intent);
            }else {
                startActivity(new Intent(AdvancesHomeActivity.this, timelineActivity.class));
            }
        }
    });
    binding.llManpower.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(sharedpreferences.getBoolean("attendanceManagement",true)){
                Intent intent=new Intent(AdvancesHomeActivity.this, ManpowerHomeActivity.class);
                intent.putExtra("type","Skilled");
                intent.putExtra("siteName",siteName);
                intent.putExtra("siteId",siteId);
                startActivity(intent);
            }else{
                Toast.makeText(AdvancesHomeActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
            }


        }
    });
    binding.btnReceiveCash.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!userType.equals("Supervisor")){
                Intent intent=new Intent(AdvancesHomeActivity.this,DayBookListActivity.class);
                intent.putExtra("position",binding.spinnerSelectSite.getSelectedItemPosition());
                intent.putExtra("Activity","ShowAttendance");

                startActivity(intent);

            }else{
                Intent intent=new Intent(AdvancesHomeActivity.this, ReceiveCashActivity.class);
                intent.putExtra("siteId",siteId);
                intent.putExtra("siteName",siteName);
                startActivity(intent);
            }

        }
    });
    binding.btnExpense.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(AdvancesHomeActivity.this,ExpensesActivity.class);
            intent.putExtra("siteId",siteId);
            intent.putExtra("siteName",siteName);
            startActivity(intent);
        }
    });

    }
    private void getSiteMemberStatus(long siteId) {
        String uid;
        if(getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("memberStatus").getValue(String.class).equals("Pending")) {
                    binding.llAdvancesList.setVisibility(View.VISIBLE);
                    binding.btnDayBookList.setVisibility(View.GONE);
                    binding.llPaymentDo.setVisibility(View.VISIBLE);
                    binding.btnReceiveCash.setText(getString(R.string.day_book_list));
                    binding.llDayBook.setVisibility(View.VISIBLE);
                    binding.llCashInHand.setVisibility(View.GONE);
                    binding.btnHeadingPay.setVisibility(View.VISIBLE);
                    binding.llDayBook.setWeightSum(3.3f);
                    binding.btnReceiveCash.setVisibility(View.VISIBLE);

                }else{
                    binding.llAdvancesList.setVisibility(View.VISIBLE);
                    binding.llPaymentDo.setVisibility(View.GONE);
                    binding.btnReceiveCash.setText(getString(R.string.receiving));
                    binding.btnDayBookList.setVisibility(View.VISIBLE);
                    binding.llDayBook.setVisibility(View.VISIBLE);
                    binding.btnReceiveCash.setVisibility(View.GONE);
                    binding.llCashInHand.setVisibility(View.VISIBLE);
                    binding.btnHeadingPay.setVisibility(View.GONE);
                    binding.llDayBook.setWeightSum(1.15f);

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


    private void getCashInHand() {
        String uid;
        if(getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("cashInHand")){
                    cashInHand=snapshot.child("cashInHand").getValue(long.class);
//                    cashInHand=Math.abs(cashInHand);
                    binding.txtCashInHand.setText(String.valueOf(cashInHand));
                }else{
                    binding.txtCashInHand.setText("0");
                }
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

    @Override
    public void onBackPressed() {

    }
}