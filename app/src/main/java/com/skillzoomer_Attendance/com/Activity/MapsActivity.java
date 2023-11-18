package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skillzoomer_Attendance.com.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    GoogleMap googleMap;
    ImageView mapback_btn;
    private String[] locationPermissions;
    private static final int Location_Request_code = 200;
    private LocationManager locationManager;
    private double latitude, longitude, lat_dest, long_dest;
    int flag = 0;

    TextView txt_distance, txt_amount_old, txt_amount_new, txt_mode, durationhour_tv, durationminuite_tv, duration_tv;
    private ProgressDialog progressDialog;
    LatLng latlng;
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
    private long siteId;



    private List<Polyline> polylines = null;

    ApplicationInfo applicationInfo=null;
    String MAPS_API_KEY;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MapsActivity.this, R.color.statusbar));

        }

        try {
            applicationInfo=MapsActivity.this.getPackageManager().getApplicationInfo(this.getPackageName(),PackageManager.GET_META_DATA);
            MAPS_API_KEY=applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Initialize EditText

        txt_amount_new = findViewById(R.id.txt_amount_new);
        txt_distance = findViewById(R.id.txt_distance);
        txt_amount_old = findViewById(R.id.txt_amount_old);
        firebaseAuth=FirebaseAuth.getInstance();

        //durationhour_tv=findViewById(R.id.durationhour_tv);
        //durationminuite_tv=findViewById(R.id.durationminuite_tv);
        traveldetails_ll = findViewById(R.id.traveldetails_ll);
        duration_tv = findViewById(R.id.duration_tv);
        book_now = findViewById(R.id.book_now);
        progressBar = findViewById(R.id.progressBar);
        //txt_mode=findViewById(R.id.txt_mode);
        Places.initialize(getApplicationContext(), MAPS_API_KEY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent=getIntent();
        lat=intent.getDoubleExtra("latitude",0);
        lon=intent.getDoubleExtra("longitude",0);
        siteId=intent.getLongExtra("siteId",0);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapsActivity.this);


        //Permission Arrays
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //Check LocationPermission
//        if (checkLocationPermission()) {
//            progressDialog.setMessage("Please Wait");
//            progressDialog.show();
//            getLocation();
//        } else {
//            requestLocationPermission();
//        }


        mapback_btn = findViewById(R.id.mapback_btn);
        mapback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapsActivity.super.onBackPressed();
            }
        });


        book_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat>0 && lon>0&& siteId>0){
                    if(lat>0 && lon>0&& siteId>0){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("siteLatitude",lat);
                        hashMap.put("siteLongitude",lon);
                        hashMap.put("locationVerifyConfirm",true);
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MapsActivity.this, "Location Updated Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MapsActivity.this,timelineActivity.class));
                            }
                        });
                    }
                }
            }
        });

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("RequestCode", String.valueOf(requestCode));
        //Toast.makeText(this, "onactivity"+stype, Toast.LENGTH_SHORT).show();
        Log.e("ResultCode", "" + resultCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (stype == "Source") {
                if ("ram".equals("Shyam") ){

                } else {
                    flag++;
                }

                marker.remove();

                latlng = place.getLatLng();
                latitude = latlng.latitude;
                longitude = latlng.longitude;
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

            } else if (stype == "Destination") {
                flag++;

                //Toast.makeText(this, place.getAddress(), Toast.LENGTH_SHORT).show();
                latlng_destination = (place.getLatLng());
                lat = latlng_destination.latitude;
                lon = latlng_destination.longitude;

                /*latlng_destination = latlng_destination.replaceAll("lat/long:", "");
                latlng_destination = latlng_destination.replace("(", "");
                latlng_destination = latlng_destination.replace(")", "");*/
                //String[] split = latlng.split(",");
                //lat_dest = Double.parseDouble(split[0]);
                //long_dest = Double.parseDouble(split[1]);
                Log.e("Lat/lon", lat + "," + lon);

            }
            if (flag >= 1) {
                // Toast.makeText(this," "+latitude+" "+longitude+" "+lat+" "+lon,Toast.LENGTH_SHORT).show();
                //distance(latitude,longitude,lat,lon);

                traveldetails_ll.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            }
        } else if (resultCode == AutocompleteActivity.RESULT_OK) {
            Status status = Autocomplete.getStatusFromIntent(data);
            //Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void addDriverMarker() {



    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
    }

    //    private void detectLocation() {
//        Toast.makeText(this, "Detecting Location", Toast.LENGTH_LONG).show();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Location_Request_code: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //Permission Allowed
                        getLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;


    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        progressDialog.dismiss();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        currentlocation = location;

                        try {
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1


                            );
                            address = addresses.get(0).getAddressLine(0);
                            latitude = addresses.get(0).getLatitude();
                            longitude = addresses.get(0).getLongitude();
                            Log.e("SourceLat", "" + latitude);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                }
            });
        }else{
            Toast.makeText(this, "Your Location is Turned Off.", Toast.LENGTH_SHORT).show();
            getLocation();
        }
    }







    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.setMapType(1);

            /*googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));*/
        LatLng latLng;
        latLng = new LatLng(lat, lon);
        marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                .title("Site Location")
                .draggable(true));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker.setPosition(latLng);
                marker.setTitle("New Location");
                lat=latLng.latitude;
                lon=latLng.longitude;
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });


        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                marker.remove();
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                        .title("Site Location")
                        .draggable(true));
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }
        });
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

    public void Findroutes(LatLng Start, LatLng End) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}