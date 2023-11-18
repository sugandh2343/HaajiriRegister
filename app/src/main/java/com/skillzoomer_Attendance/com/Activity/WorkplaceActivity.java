package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityWorkplaceBinding;

public class WorkplaceActivity extends AppCompatActivity {
    ActivityWorkplaceBinding binding;
    long siteId;
    private String siteName,companyName;
    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWorkplaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteName=intent.getStringExtra("siteName");
        siteId=intent.getLongExtra("siteId",0);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        sharedPreferences=getSharedPreferences("UserDetails", MODE_PRIVATE);

        editor=sharedPreferences.edit();
        companyName=sharedPreferences.getString("companyName","");
        binding.txtCompanyName.setText(companyName);
        binding.txtSiteName.setText(siteName);
        binding.txtSiteId.setText(""+siteId);
        firebaseAuth=FirebaseAuth.getInstance();
        binding.toolbar.inflateMenu(R.menu.menu_navigationbar1);

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return false;
            }
        });
        if(sharedPreferences.getString("userDesignation","").equals("HR Manager")){
            binding.btnAttendance.setVisibility(View.GONE);
        }else{
            binding.btnAttendance.setVisibility(View.VISIBLE);
        }
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId)).child("LeaveRequest").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean showLeaveRequest=false;
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("leaveStatus").getValue(String.class).equals("Pending")){
                                showLeaveRequest=true;

                            }
                            if(showLeaveRequest){
                                binding.ll5.setVisibility(View.VISIBLE);
                                binding.txtHeading.setText("Pending Leave Request");
                                binding.txtEvents.setText("You have pending leave Request. Click to View");
                                binding.ivAssociateNotify.setImageResource(R.drawable.ic_request);
                                binding.ll5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent1=new Intent(WorkplaceActivity.this,ShowLeaveRequestActivity.class);
                                        intent1.putExtra("siteId",siteId);
                                        intent1.putExtra("siteName",siteName);
                                        startActivity(intent1);

                                    }
                                });

                            }else{
                                binding.ll5.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.btnAddNewWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                        .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                        .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                        .child("Site").child(String.valueOf(siteId));
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean policy=snapshot.child("policy").getValue(Boolean.class);
                        if(policy!=null && policy){
                            progressDialog.dismiss();
                            Intent intent1=new Intent(WorkplaceActivity.this,EmployeeRegistration.class);

                            intent1.putExtra("siteId",siteId);
                            intent1.putExtra("siteName",siteName);
                            startActivity(intent1);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(WorkplaceActivity.this, "Add a leave policy to register employee", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        binding.btnEmpMasterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(WorkplaceActivity.this,EmpMasterData.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });

        binding.workActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(WorkplaceActivity.this,PolicyHome.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
        binding.btnEventnannouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(WorkplaceActivity.this,EventnAnnouncementHome.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
        binding.btnSkilledPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(WorkplaceActivity.this,EmployeeReportHome.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_navigationbar1, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Action menu
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:
                Log.e("Clicked", "Profile");
                startActivity(new Intent(WorkplaceActivity.this, AboutActivity.class));

                return true;
            case R.id.language:
                startActivity(new Intent(WorkplaceActivity.this, LanguageChange.class));

//                finish();
                return true;
//                case R.id.share:
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.project.skill"); // url for share the app
//                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
//                    return true;
            case R.id.youtube:
                // rate implementation here
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLasxv4S2lb-Lz_4dJ9E5GOIT3NqJk_BX-")));
                // rate the app
                return true;
            case R.id.share:
                // rate implementation here
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Hi!I am using this app called हाज़िरी Register and would like to share it with you. Download now for free from Playstore. Click on this link \n" + "https://play.google.com/store/apps/details?id=com.skillzoomer_Attendance.com";
                String sub = "Invite";
                System.out.println("++++++++++++++++++++body" + body);
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                // rate the app
                return true;
            case R.id.transaction:
                startActivity(new Intent(WorkplaceActivity.this, TransactionListActivity.class));
                return true;
            case R.id.logout:
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(WorkplaceActivity.this);
                builder.setTitle(R.string.log_out).setMessage(R.string.are_you_sure_you).setCancelable(false).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    firebaseAuth.signOut();
                    editor.clear();
                    editor.commit();
                    dialogInterface.dismiss();
//                                    startActivity(new Intent(timelineActivity.this,SplashActivity.class));
                    this.finishAffinity();
                }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();


//                myRef = myRef.child(session.getUserDetails( ).getAdminId( ));
//                myRef.child(session.getUserDetails( ).getId( )).child("attendance").setValue("false");
//                session.logoutUser( );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}