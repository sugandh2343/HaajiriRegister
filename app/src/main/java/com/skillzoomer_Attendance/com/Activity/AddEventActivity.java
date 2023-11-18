 package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelEventCategory;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddEventBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

 public class AddEventActivity extends AppCompatActivity {
     ActivityAddEventBinding binding;
     private ArrayList<ModelEventCategory> eventCategories;
     String startTime="",endTime="";
     private final Calendar myCalendar = Calendar.getInstance();
     private String toDate = "";
     long siteId;
     String siteName;
     long tempEventId=0;
     FirebaseAuth firebaseAuth;

     String id,name,date,start,end,category,addedBy,Activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");
        Activity=intent.getStringExtra("Activity");
        id=intent.getStringExtra("id");
        name=intent.getStringExtra("name");
        date=intent.getStringExtra("date");
        start=intent.getStringExtra("start");
        end=intent.getStringExtra("end");
        category=intent.getStringExtra("category");
        addedBy=intent.getStringExtra("addedBy");

        if(Activity!=null && Activity.equals("Update")){
            binding.etEventName.setText(name);
            binding.etStartTime.setText(start);
            binding.etEventDate.setText(date);
            binding.etEndTime.setText(end);
            binding.btnAddEvent.setText("Update Event");
            binding.txtHeading.setText("Update Event");
        }

        firebaseAuth=FirebaseAuth.getInstance();
        eventCategories=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Banner");
        reference.child("Event Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventCategories.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelEventCategory modelEventCategory=ds.getValue(ModelEventCategory.class);
                    eventCategories.add(modelEventCategory);
                }
                eventCategories.add(eventCategories.size(),new ModelEventCategory(eventCategories.size(),"Others"));
                eventCategories.add(0,new ModelEventCategory(0,"Select Category"));
                eventCategoryAdapter eventCategoryAdapter=new eventCategoryAdapter();
                binding.spinnerEventCategory.setAdapter(eventCategoryAdapter);
                if(Activity.equals("Update")){
                    for(int i=0;i<eventCategories.size();i++){
                        if(eventCategories.get(i).getName().equals(category)){
                            binding.spinnerEventCategory.setSelection(i);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd/MM/yyyy"; //In which you need put here
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);
            toDate = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + toDate);
            updateLabel1(binding.etEventDate);

        };
        binding.etEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy");

//            try {
//                Date fDate = dateFormat.parse(siteCreatedDate);
//                Log.e("Parse Success","Success");
//                datePickerDialog.getDatePicker().setMinDate(fDate.getTime());
//            } catch (ParseException e) {
//                Log.e("ParseException",e.getMessage());
//                e.printStackTrace();
//            }

                datePickerDialog.show();
                String myFormat = "dd/MM/yyyy"; //In which you need put here
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
                mTimePicker = new TimePickerDialog(AddEventActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etStartTime.setText(selectedHour + ":" + selectedMinute);
                        startTime=selectedHour + ":" + selectedMinute;

                        if(!TextUtils.isEmpty(startTime)){
//                            calculateTime(binding.etStartTime.getText().toString(),binding.etEndTime.getText().toString());
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
                mTimePicker = new TimePickerDialog(AddEventActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etEndTime.setText(selectedHour + ":" + selectedMinute);
                        endTime=selectedHour + ":" + selectedMinute;

                        if(!TextUtils.isEmpty(endTime)){
//                            calculateTime(binding.etStartTime.getText().toString(),binding.etEndTime.getText().toString());
                        }

                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        binding.btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etEventName.getText().toString())){
                    Toast.makeText(AddEventActivity.this, "Enter the event's name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etEventDate.getText().toString())){
                    Toast.makeText(AddEventActivity.this, "Select Event Date", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etStartTime.getText().toString())||TextUtils.isEmpty(binding.etEndTime.getText().toString())){
                    Toast.makeText(AddEventActivity.this, "Enter the duration of Event", Toast.LENGTH_SHORT).show();
                }else if(binding.spinnerEventCategory.getSelectedItemPosition()==0){
                    Toast.makeText(AddEventActivity.this, "Select Event Category", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Activity.equals("Add")){
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                                .child("Site").child(String.valueOf(siteId));
                        reference.child("Events").child("Event").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long eventId=snapshot.getChildrenCount()+1;
                                addEvent(reference,eventId);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else{
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                                .child("Site").child(String.valueOf(siteId));
                        reference.child("Events").child("Event").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("eventId",String.valueOf(id));
                                hashMap.put("eventName",binding.etEventName.getText().toString());
                                hashMap.put("eventDate",binding.etEventDate.getText().toString());
                                hashMap.put("eventStart",binding.etStartTime.getText().toString());
                                hashMap.put("eventEnd",binding.etEndTime.getText().toString());
                                hashMap.put("eventCategory",eventCategories.get(binding.spinnerEventCategory.getSelectedItemPosition()).getName());
                                hashMap.put("addedBy",firebaseAuth.getUid());
                                reference.child("Events").child("Event").child(String.valueOf(id)).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddEventActivity.this, "Event Updated Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent1=new Intent(AddEventActivity.this,EventnAnnouncementHome.class);
                                                intent1.putExtra("siteId",siteId);
                                                intent1.putExtra("siteName",siteName);
                                                startActivity(intent1);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }
        });
    }

     private void addEvent(DatabaseReference reference, long eventId) {
        tempEventId=eventId;
         reference.child("Events").child("Event").child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                    tempEventId=tempEventId+1;
                    addEvent(reference,tempEventId);
                 }else{
                     HashMap<String,Object> hashMap=new HashMap<>();
                     hashMap.put("eventId",String.valueOf(eventId));
                     hashMap.put("eventName",binding.etEventName.getText().toString());
                     hashMap.put("eventDate",binding.etEventDate.getText().toString());
                     hashMap.put("eventStart",binding.etStartTime.getText().toString());
                     hashMap.put("eventEnd",binding.etEndTime.getText().toString());
                     hashMap.put("eventCategory",eventCategories.get(binding.spinnerEventCategory.getSelectedItemPosition()).getName());
                     hashMap.put("addedBy",firebaseAuth.getUid());
                     reference.child("Events").child("Event").child(String.valueOf(eventId)).setValue(hashMap)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     Toast.makeText(AddEventActivity.this, "Event Added Successfully", Toast.LENGTH_SHORT).show();
                                     Intent intent1=new Intent(AddEventActivity.this,EventnAnnouncementHome.class);
                                     intent1.putExtra("siteId",siteId);
                                     intent1.putExtra("siteName",siteName);
                                     startActivity(intent1);
                                 }
                             })
                             .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(AddEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                 }
                             });
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
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
     private void updateLabel1(EditText fromdateEt) {
         String myFormat = "dd/MM/yyyy"; //In which you need put here
         java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);
         fromdateEt.setText(sdf.format(myCalendar.getTime()));
     }

     class eventCategoryAdapter extends BaseAdapter {

         @Override
         public int getCount() {
             return eventCategories.size();
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
             designationText.setText(eventCategories.get(position).getName());

             return row;
         }
     }

 }