package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddWorkPlaceBinding;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddWorkPlaceActivity extends FragmentActivity implements OnMapReadyCallback {
    ActivityAddWorkPlaceBinding binding;
    ApplicationInfo applicationInfo=null;
    String MAPS_API_KEY;
    LatLng latlng;
    double latitude=0,longitude=0;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    //    private ArrayList<String> workplaceType=("Select Type","Fixed");
    private String[] search = {"Select workplace type", "Fixed", "Tentative"};
    private String[] term = {"Select term", "Yearly", "Half Yearly","Quaterly","Monthly"};
    String currentDate;
    private final Calendar myCalendar = Calendar.getInstance();
    String startTime="",endTime="";
    long siteId;

    GoogleMap googleMap;
    ImageView mapback_btn;
    private String[] locationPermissions;
    private static final int Location_Request_code = 200;
    private LocationManager locationManager;
    private double  lat_dest, long_dest;
    int flag = 0;

    TextView txt_distance, txt_amount_old, txt_amount_new, txt_mode, durationhour_tv, durationminuite_tv, duration_tv;


    LatLng latlng_destination;
    String stype = "";
    FusedLocationProviderClient fusedLocationProviderClient;
    public Location currentlocation;
    String address;
    Double lat, lon, lat1, lon1;
    LinearLayout traveldetails_ll;
    MaterialButton book_now;
    int statuscode;
    ProgressBar progressBar;
    Marker marker;

    private Circle circle;
    int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddWorkPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        firebaseAuth=FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        binding.rbCurrentLocation.setChecked(true);
        binding.rbMap.setChecked(false);
        binding.etSearch.setVisibility(View.GONE);
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        binding.llShortLeave.setVisibility(View.GONE);
        binding.llLate.setVisibility(View.GONE);
        binding.llHalfDay.setVisibility(View.GONE);
        binding.llCl.setVisibility(View.GONE);
        binding.llEl.setVisibility(View.GONE);
        binding.llSl.setVisibility(View.GONE);
        binding.llMl.setVisibility(View.GONE);
        binding.llPl.setVisibility(View.GONE);
        if (checkLocationPermission()) {
            progressDialog.show();
            detectLocation();
        } else {
            requestLocationPermission();
        }
        binding.llFirst.setVisibility(View.VISIBLE);
        binding.llSecond.setVisibility(View.GONE);
        binding.llThird.setVisibility(View.GONE);
        binding.llFourth.setVisibility(View.GONE);
        binding.rbCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.rbMap.isChecked()){
                    binding.rbCurrentLocation.setChecked(true);
                    googleMap.clear();
                    binding.rbMap.setChecked(false);
                    binding.etSearch.setVisibility(View.GONE);
                    if (checkLocationPermission()) {
                        progressDialog.show();
                        detectLocation();
                    } else {
                        requestLocationPermission();
                    }
                }
            }
        });
        binding.rbMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.rbCurrentLocation.isChecked()){
                    binding.rbMap.setChecked(true);
                    googleMap.clear();
                    binding.rbCurrentLocation.setChecked(false);
                    binding.etSearch.setVisibility(View.VISIBLE);
                    binding.etLatitude.setEnabled(true);
                    binding.etLongitude.setEnabled(true);
                    binding.etLongitude.getText().clear();
                    binding.etLatitude.getText().clear();
                    binding.etAddress.getText().clear();
                }
            }
        });
        SpinnerTermAdapter spinnerTermAdapter=new SpinnerTermAdapter();
        binding.spinnerTermCl.setAdapter(spinnerTermAdapter);
        binding.spinnerTermEl.setAdapter(spinnerTermAdapter);
        binding.spinnerTermMl.setAdapter(spinnerTermAdapter);
        binding.spinnerTermPl.setAdapter(spinnerTermAdapter);
        binding.spinnerTermSl.setAdapter(spinnerTermAdapter);

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etSiteName.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the workplace name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etLatitude.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the location of workplace", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.VISIBLE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                }

            }
        });
        try {
            applicationInfo=this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            MAPS_API_KEY=applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Places.initialize(getApplicationContext(), MAPS_API_KEY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        binding.etSearch.setFocusable(false);
        binding.etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etSearch.setFocusable(false);
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                            , Place.Field.LAT_LNG, Place.Field.NAME);
                try{
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                            .build(getApplicationContext());
                    startActivityForResult(intent, 100);
                }catch (Exception e){
                    Log.e("EXCSDF",e.getMessage());
                }



            }
        });
        binding.btnHalfDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llFirst.setVisibility(View.GONE);
                binding.llSecond.setVisibility(View.GONE);
                binding.llThird.setVisibility(View.GONE);
                binding.llFourth.setVisibility(View.VISIBLE);

            }
        });

        AppCompatSeekBar progress = (AppCompatSeekBar) findViewById(R.id.simpleSeekBar);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius=progress;


                LatLng latLng;
                googleMap.clear();
                latLng = new LatLng(latitude, longitude);
//                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                supportMapFragment.getMapAsync(AddWorkPlaceActivity.this);
                marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                        .title("Site Location")
                        .draggable(true));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                circle =googleMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(progress)
                        .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(2.0f));

            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
                Log.e("SEEKBAR",""+seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Log.e("SEEKBAR","1:::::::"+seekBar.getProgress());
                binding.txtSeekBar.setText(String.valueOf(seekBar.getProgress())+" m");
            }
        });
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(AddWorkPlaceActivity.this);
        SpinnerAdapter spinnerAdapter=new SpinnerAdapter();
//        binding.spinnerWorkPlaceType.setAdapter(spinnerAdapter);
//        binding.spinnerWorkPlaceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i== search.length-1){
//                    binding.llToWhom.setVisibility(View.VISIBLE);
//                }else{
//                    binding.llToWhom.setVisibility(View.GONE);
//                }
//            }

//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        binding.btnNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etSiteName.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, getString(R.string.enter_site_name), Toast.LENGTH_SHORT).show();
                }
//                else if(TextUtils.isEmpty(binding.etCity.getText().toString())){
//                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the nick name of workplace", Toast.LENGTH_SHORT).show();
//                }
                else if(TextUtils.isEmpty(binding.etAddress.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the address of workplace", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etStartTime.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the start time of workplace", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(binding.etEndTime.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the end time of workplace", Toast.LENGTH_SHORT).show();
                }else if(!binding.cbSunday.isChecked()&& !binding.cbMonday.isChecked() &&
                        !binding.cbTuesday.isChecked()&& !binding.cbWednesday.isChecked() &&
                        !binding.cbThursday.isChecked()&& !binding.cbFriday.isChecked() &&
                        !binding.cbSaturday.isChecked()){
                    Toast.makeText(AddWorkPlaceActivity.this, "Select at least one weekly off", Toast.LENGTH_SHORT).show();

                }
//                else if(binding.spinnerWorkPlaceType.getSelectedItemPosition()==0){
//                    Toast.makeText(AddWorkPlaceActivity.this, "Select Workplace Type", Toast.LENGTH_SHORT).show();
//                }
                else{
                    progressDialog.show();
                    addWorkPlace();
//                    binding.llFirst.setVisibility(View.GONE);
//                    binding.llSecond.setVisibility(View.GONE);
//                    binding.llThird.setVisibility(View.VISIBLE);
//                    binding.llFourth.setVisibility(View.GONE);

                }
            }
        });
        TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view , int hourOfDay , int minute) {
                myCalendar.set(Calendar.HOUR , hourOfDay);
                myCalendar.set(Calendar.MINUTE , minute);
                String myFormat = "hh:mm"; //In which you need put here
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat(myFormat , Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time" , "Start:" + startTime);
                updateLabel(binding.etStartTime);

            }
        };
        TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view , int hourOfDay , int minute) {
                myCalendar.set(Calendar.HOUR , hourOfDay);
                myCalendar.set(Calendar.MINUTE , minute);
                String myFormat = "hh:mm"; //In which you need put here
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat(myFormat , Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    endTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time" , "Start:" + startTime);
                updateLabel(binding.etEndTime);

            }
        };


        binding.etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddWorkPlaceActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etStartTime.setText(selectedHour + ":" + selectedMinute);
                        endTime=selectedHour + ":" + selectedMinute;
                        Log.e("StartTime",endTime);
                        if(!TextUtils.isEmpty(startTime)){
                            calculateTime(binding.etStartTime.getText().toString(),binding.etEndTime.getText().toString());
                        }

                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        binding.etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddWorkPlaceActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etEndTime.setText(selectedHour + ":" + selectedMinute);
                        startTime=selectedHour + ":" + selectedMinute;
                        Log.e("endTime",startTime);
                        if(!TextUtils.isEmpty(endTime)){
                            calculateTime(binding.etStartTime.getText().toString(),binding.etEndTime.getText().toString());
                        }

                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        binding.cbShortLeave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llShortLeave.setVisibility(View.VISIBLE);

                }else{
                    binding.llShortLeave.setVisibility(View.GONE);

                }
            }
        });
        binding.cbLate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llLate.setVisibility(View.VISIBLE);

                }else{
                    binding.llLate.setVisibility(View.GONE);

                }
            }
        });

        binding.cbHalfDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llHalfDay.setVisibility(View.VISIBLE);
                }else{
                    binding.llHalfDay.setVisibility(View.GONE);
                }
            }
        });
        binding.cbCl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llCl.setVisibility(View.VISIBLE);
                }else{
                    binding.llCl.setVisibility(View.GONE);
                }
            }
        });
        binding.btnAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etSiteName.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the workplace name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etLatitude.getText().toString())){
                    Toast.makeText(AddWorkPlaceActivity.this, "Enter the location of workplace", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.VISIBLE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                }
            }
        });

        binding.cbEl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llEl.setVisibility(View.VISIBLE);
                }else{
                    binding.llEl.setVisibility(View.GONE);
                }
            }
        });
        binding.cbSl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llSl.setVisibility(View.VISIBLE);
                }else{
                    binding.llSl.setVisibility(View.GONE);
                }
            }
        });
        binding.cbMl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llMl.setVisibility(View.VISIBLE);
                }else{
                    binding.llMl.setVisibility(View.GONE);
                }
            }
        });
        binding.cbPl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llPl.setVisibility(View.VISIBLE);
                }else{
                    binding.llPl.setVisibility(View.GONE);
                }
            }
        });
    }

    private void calculateTime(String startTime, String endTime) {
        SimpleDateFormat simpleDateFormat = null;
        Date date1=null,date2=null;
        int days,hours,min;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("hh:mm");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                date1 = simpleDateFormat.parse(startTime);
                date2 = simpleDateFormat.parse(endTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }


        long difference = Math.abs((int)(date2.getTime() - date1.getTime())) ;
        if(difference<0){

        }
        days = (int) (difference / (1000*60*60*24));
        hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        binding.etHours.setText(String.valueOf(hours));
        binding.etMin.setText(String.valueOf(min));
        Log.i("======= Hours"," :: "+hours);
    }

    private void updateLabel(EditText fromdateEt) {
        String myFormat = "hh:mm"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat, Locale.US);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fromdateEt.setText(sdf.format(myCalendar.getTime()));
        }
    }

    private void addWorkPlace() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                        .child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    siteId=(snapshot.getChildrenCount()+1)*100;

                    Log.e("WorkplaceId",""+siteId);
                    addWorkData();


                }else{
                    siteId=100;
                    addWorkData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private Boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);

    }
    private void detectLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            progressDialog.dismiss();
            return;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        try{
                            Location location = task.getResult();
                            if (location != null) {

                                try {
                                    Geocoder geocoder = new Geocoder(AddWorkPlaceActivity.this, Locale.ENGLISH);
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location.getLatitude(), location.getLongitude(), 1



                                    );
                                    binding.etAddress.setText(addresses.get(0).getAddressLine(0));
                                    latitude = addresses.get(0).getLatitude();
                                    longitude = addresses.get(0).getLongitude();
                                    binding.etLatitude.setText(String.valueOf(latitude));
                                    binding.etLongitude.setText(String.valueOf(longitude));
                                    binding.etLongitude.setEnabled(false);
                                    binding.etLatitude.setEnabled(false);
                                    Log.e("SourceLat", "Latitude" + latitude);
                                    Log.e("SourceLat", "Longitude" + latitude);
                                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                    supportMapFragment.getMapAsync(AddWorkPlaceActivity.this);
                                    progressDialog.dismiss();
//                                if(latitude>0 && longitude>0){
//                                    fusedLocationProviderClient.removeLocationUpdates((LocationListener) locationManager);
//                                }


                                } catch (IOException e) {
                                    e.printStackTrace();

                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AddWorkPlaceActivity.this, "Unable to detect Your Location", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){


                        }



                    }
                });
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Your Location is Turned Off.", Toast.LENGTH_SHORT).show();
            detectLocation();
        }
    }

    private void addWorkData() {
        Log.e("Workplaceid",""+siteId);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Industry").child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    siteId = siteId + 100;
                    addWorkData();
                }else{
                    String timestamp=""+System.currentTimeMillis();
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("siteId",siteId);
                    hashMap.put("siteName",binding.etSiteName.getText().toString());
                    hashMap.put("siteCity",binding.etSiteName.getText().toString());
                    hashMap.put("siteAddress",binding.etAddress.getText().toString());
                    hashMap.put("timestamp",timestamp);
                    hashMap.put("hrUid",firebaseAuth.getUid());
                    hashMap.put("online",false);
                    hashMap.put("startTime",binding.etEndTime.getText().toString());
                    hashMap.put("endTime",binding.etStartTime.getText().toString());
                    hashMap.put("siteCreatedDate",currentDate);
//        hashMap.put("forceOpt",forceLogout);
//        hashMap.put("workOpt",picActivity);
                    hashMap.put("memberStatus","Pending");
                    if(TextUtils.isEmpty(binding.etLatitude.getText().toString())){
                        hashMap.put("siteLatitude",0.0);
                    }else{
                        hashMap.put("siteLatitude",Float.parseFloat(binding.etLatitude.getText().toString()));
                    }
                    if(TextUtils.isEmpty(binding.etLongitude.getText().toString())){
                        hashMap.put("siteLongitude",0.0);
                    }else{
                        hashMap.put("siteLongitude",Float.parseFloat(binding.etLongitude.getText().toString()));
                    }

//                    hashMap.put("workplaceType",search[binding.spinnerWorkPlaceType.getSelectedItemPosition()]);
                    hashMap.put("industryPosition",getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));

                    hashMap.put("Sunday",binding.cbSunday.isChecked());
                    hashMap.put("Monday",binding.cbMonday.isChecked());
                    hashMap.put("Tuesday",binding.cbTuesday.isChecked());
                    hashMap.put("Wednesday",binding.cbWednesday.isChecked());
                    hashMap.put("Thursday",binding.cbThursday.isChecked());
                    hashMap.put("Friday",binding.cbFriday.isChecked());
                    hashMap.put("Saturday",binding.cbSaturday.isChecked());
                    hashMap.put("circleRadius",circle.getRadius());
                    hashMap.put("circleCenterLat",circle.getCenter().latitude);
                    hashMap.put("circleCenterLon",circle.getCenter().longitude);
                    hashMap.put("workingHours",Integer.parseInt(String.valueOf(binding.etHours.getText().toString())));
                    hashMap.put("workingMins",Integer.parseInt(String.valueOf(binding.etMin.getText().toString())));
                    hashMap.put("policy",false);
                    hashMap.put("policyCount",0);

                    reference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddWorkPlaceActivity.this, "Your Work Place is registered.", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(AddWorkPlaceActivity.this,TimelineOtherIndustry1.class);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Log.e("WorkData",""+circle.getRadius());
//        Log.e("WorkData",""+circle.getCenter().latitude);
//        Log.e("WorkData",""+circle.getRadius());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("RequestCode", String.valueOf(requestCode));
        //Toast.makeText(this, "onactivity"+stype, Toast.LENGTH_SHORT).show();
        Log.e("ResultCode", "" + resultCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

//                marker.remove();
            binding.etSearch.setText(place.getAddress());
            latlng = place.getLatLng();
            latitude = latlng.latitude;
            longitude = latlng.longitude;
                Geocoder geocoder = new Geocoder(AddWorkPlaceActivity.this, Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(
                        latitude, longitude, 1



                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.etAddress.setText(addresses.get(0).getAddressLine(0));
                binding.etLatitude.setText(String.valueOf(latitude));
                binding.etLongitude.setText(String.valueOf(longitude));
                binding.etLongitude.setEnabled(false);
                binding.etLatitude.setEnabled(false);
                Log.e("SourceLat", "Latitude" + latitude);
                Log.e("SourceLat", "Longitude" + latitude);
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(AddWorkPlaceActivity.this);
                progressDialog.dismiss();
                /*LatLng markerLoc = new LatLng(latitude, longitude);
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerLoc)      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   //

                googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_location1)));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        return true;
                    }
                });*/

//                latlng = (String.valueOf(place.getLatLng()));
//                latlng = latlng.replaceAll("lat/long:", "");
//                latlng = latlng.replace("(", "");
//                latlng = latlng.replace(")", "");
//                String[] split = latlng.split(",");
//                latitude = Double.parseDouble(split[0]);
//                longitude = Double.parseDouble(split[1]);

        }else if (resultCode == AutocompleteActivity.RESULT_OK) {
            Status status = Autocomplete.getStatusFromIntent(data);
            //Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.setMapType(1);

            /*googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));*/
        LatLng latLng;
        latLng = new LatLng(latitude, longitude);
        marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                .title("Site Location")
                .draggable(true));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        circle=googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));



        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        if(binding.rbMap.isChecked()){
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    googleMap.clear();
                    marker.setPosition(latLng);
                    marker.setTitle("New Location");

                    lat=latLng.latitude;
                    lon=latLng.longitude;
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("New Location"));
                    Geocoder geocoder = new Geocoder(AddWorkPlaceActivity.this, Locale.ENGLISH);
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(
                                lat, lon, 1



                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    binding.etAddress.setText(addresses.get(0).getAddressLine(0));
                    binding.etLatitude.setText(String.valueOf(lat));
                    binding.etLongitude.setText(String.valueOf(lon));
                    binding.etLongitude.setEnabled(false);
                    binding.etLatitude.setEnabled(false);


                  circle=  googleMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(500)
                            .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));

                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                }
            });

        }


//        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//            @Override
//            public void onMarkerDrag(@NonNull Marker marker) {
//                marker.remove();
//            }
//
//            @Override
//            public void onMarkerDragEnd(@NonNull Marker marker) {
//                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
//                marker = googleMap.addMarker(new MarkerOptions().position(latLng)
//
//
//                        .title("Site Location")
//                        .draggable(true));
//            }
//
//            @Override
//            public void onMarkerDragStart(@NonNull Marker marker) {
//
//            }
//        });
//            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
//                    .title("Your Current Location");

        Log.e("HEllo","going to call");
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull CameraPosition cameraPosition) {
                Log.e("Latitude","CL"+cameraPosition.target.latitude);
                Log.e("Latitude","CLO"+cameraPosition.target.longitude);
            }
        });

    }

    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return search.length;
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
            designationText.setText(search[position]);

            return row;
        }
    }

    class SpinnerTermAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return term.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(term[position]);

            return row;
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.llSecond.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.VISIBLE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
        }else if(binding.llThird.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.VISIBLE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
        }else if(binding.llFourth.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.VISIBLE);
            binding.llFourth.setVisibility(View.GONE);
        } else{
            startActivity(new Intent(AddWorkPlaceActivity.this,TimelineOtherIndustry1.class));
        }
    }
}