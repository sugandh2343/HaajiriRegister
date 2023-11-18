package com.skillzoomer_Attendance.com.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelDayBookClass;
import com.skillzoomer_Attendance.com.Model.ModelExpenseLabourByData;
import com.skillzoomer_Attendance.com.Model.ModelReceiveCash;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterDayBook extends RecyclerView.Adapter<AdapterDayBook.HolderDayBook> {
    private Context context;
    private ArrayList<ModelDayBookClass> dayBookClassArrayList;
    private int selected_option;
    private String memberStatus;
    private long siteId;
    private ArrayList<ModelReceiveCash> receiveCashArrayList;
    private ArrayList<ModelExpenseLabourByData> expenseArrayList;

    public AdapterDayBook(Context context, ArrayList<ModelDayBookClass> dayBookClassArrayList, int selected_option) {
        this.context = context;
        this.dayBookClassArrayList = dayBookClassArrayList;
        this.selected_option = selected_option;
    }

    public AdapterDayBook(Context context, ArrayList<ModelDayBookClass> dayBookClassArrayList, int selected_option, String memberStatus) {
        this.context = context;
        this.dayBookClassArrayList = dayBookClassArrayList;
        this.selected_option = selected_option;
        this.memberStatus = memberStatus;
    }

    public AdapterDayBook(Context context, ArrayList<ModelDayBookClass> dayBookClassArrayList, int selected_option, String memberStatus, long siteId) {
        this.context = context;
        this.dayBookClassArrayList = dayBookClassArrayList;
        this.selected_option = selected_option;
        this.memberStatus = memberStatus;
        this.siteId = siteId;
    }

    @NonNull
    @Override
    public AdapterDayBook.HolderDayBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_day_book_list_single_row,parent,false);
        return new AdapterDayBook.HolderDayBook(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDayBook holder, int position) {
        holder.setIsRecyclable(false);
        receiveCashArrayList=new ArrayList<>();
        expenseArrayList=new ArrayList<>();
        ModelDayBookClass modelDayBookClass=dayBookClassArrayList.get(position);
        if(selected_option==2){
            holder.ll_main.setWeightSum((float) 3.04);
            holder.rec_amount.setVisibility(View.GONE);
            holder.rec_from.setVisibility(View.GONE);
            holder.closingBalance.setVisibility(View.GONE);

            holder.date.setText(modelDayBookClass.getDate());
            holder.expAmt.setText(modelDayBookClass.getExpAmt());
            holder.remark_expense.setText(modelDayBookClass.getExpRemark());
        }else if(selected_option==1){
            holder.ll_main.setWeightSum((float) 6.05);
            holder.rec_amount.setVisibility(View.VISIBLE);
            holder.rec_from.setVisibility(View.VISIBLE);
            holder.closingBalance.setVisibility(View.VISIBLE);
            holder.rec_from.setText(modelDayBookClass.getRec_from());
            holder.rec_amount.setText(modelDayBookClass.getRecAmt());
            int Balance=Integer.parseInt(modelDayBookClass.getRecAmt())-Integer.parseInt(modelDayBookClass.getExpAmt());
            holder.closingBalance.setText(String.valueOf(Balance));
            holder.remark_expense.setText(modelDayBookClass.getExpRemark());

            holder.date.setText(modelDayBookClass.getDate());
            holder.expAmt.setText(modelDayBookClass.getExpAmt());
        }else if(selected_option==3){
            if(memberStatus.equals("Registered")){
                holder.ll_main.setWeightSum((float) 6.05);
                holder.rec_amount.setVisibility(View.VISIBLE);

                holder.rec_from.setVisibility(View.VISIBLE);
                holder.closingBalance.setVisibility(View.VISIBLE);
                holder.rec_from.setText(modelDayBookClass.getRec_from());
                holder.rec_amount.setText(modelDayBookClass.getRecAmt());
                if(modelDayBookClass.getRecAmt().equals("N/R")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.rec_amount.setTextColor(context.getColor(R.color.red));
                    }
                }
                int Balance=Integer.parseInt(modelDayBookClass.getRecAmt())-Integer.parseInt(modelDayBookClass.getExpAmt());
                holder.closingBalance.setText(String.valueOf(Balance));
                holder.date.setText(modelDayBookClass.getDate());
                holder.expAmt.setText(modelDayBookClass.getExpAmt());
                if(modelDayBookClass.getExpAmt().equals("N/R")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.expAmt.setTextColor(context.getColor(R.color.red));
                    }
                }
                Log.e("Array","Remark"+modelDayBookClass.getExpRemark());
                holder.remark_expense.setText(modelDayBookClass.getExpRemark());

            }else if(memberStatus.equals("Pending")){
                holder.ll_main.setWeightSum((float) 3.04);
                holder.rec_amount.setVisibility(View.GONE);
                holder.rec_from.setVisibility(View.GONE);
                holder.closingBalance.setVisibility(View.GONE);

                holder.date.setText(modelDayBookClass.getDate());
                holder.expAmt.setText(modelDayBookClass.getExpAmt());
                if(modelDayBookClass.getExpAmt().equals("N/R")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.expAmt.setTextColor(context.getColor(R.color.red));
                    }
                }
                holder.remark_expense.setText(modelDayBookClass.getExpRemark());
            }
        }else{
            holder.ll_main.setWeightSum((float) 6.05);
            holder.rec_amount.setVisibility(View.VISIBLE);
            holder.rec_from.setVisibility(View.VISIBLE);
            holder.closingBalance.setVisibility(View.VISIBLE);
            holder.rec_from.setText(modelDayBookClass.getRec_from());
            holder.rec_amount.setText(modelDayBookClass.getRecAmt());
            if(modelDayBookClass.getRecAmt().equals("N/R")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.rec_amount.setTextColor(context.getColor(R.color.red));
                }
            }
            int Balance=Integer.parseInt(modelDayBookClass.getRecAmt())-Integer.parseInt(modelDayBookClass.getExpAmt());
            holder.closingBalance.setText(String.valueOf(Balance));

            holder.date.setText(modelDayBookClass.getDate());
            holder.expAmt.setText(modelDayBookClass.getExpAmt());
            holder.remark_expense.setText(modelDayBookClass.getExpRemark());
            Log.e("Array","Remark"+modelDayBookClass.getExpRemark());

        }
        if(holder.rec_from.getText().toString().equals("Multiple")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.rec_from.setElevation(22);
            }
            holder.rec_from.setTextColor(context.getResources().getColor(R.color.red));
            holder.rec_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showReceivedData(modelDayBookClass);
                }
            });
        }
        if(holder.remark_expense.getText().toString().equals("Multiple")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.remark_expense.setElevation(22);
            }
            if(modelDayBookClass.getExpAmt().equals("N/R")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.expAmt.setTextColor(context.getColor(R.color.red));
                }
            }
            holder.remark_expense.setTextColor(context.getResources().getColor(R.color.red));
            holder.remark_expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExpenseData(modelDayBookClass);
                }
            });
        }



    }

    private void showExpenseData(ModelDayBookClass modelDayBookClass) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_expense, null);
        RecyclerView rv_receive;
        rv_receive=mView.findViewById(R.id.rv_receive);
        alert.setView(mView);
        String uid;
        if(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(modelDayBookClass.getDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelExpenseLabourByData modelReceiveCash=ds.getValue(ModelExpenseLabourByData.class);
                    expenseArrayList.add(modelReceiveCash);
                }
                AdapterExpense adapterReceiveCash=new AdapterExpense(context,expenseArrayList);
                rv_receive.setAdapter(adapterReceiveCash);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.show();
    }

    private void showReceivedData(ModelDayBookClass modelDayBookClass) {
        String uid;
        if(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_receive_cash, null);
        RecyclerView rv_receive;
        rv_receive=mView.findViewById(R.id.rv_receive);
        alert.setView(mView);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Receive").child(modelDayBookClass.getDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiveCashArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelReceiveCash modelReceiveCash=ds.getValue(ModelReceiveCash.class);
                    receiveCashArrayList.add(modelReceiveCash);
                }
                AdapterReceiveCash adapterReceiveCash=new AdapterReceiveCash(context,receiveCashArrayList);
                rv_receive.setAdapter(adapterReceiveCash);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return dayBookClassArrayList.size();
    }

    public class HolderDayBook extends RecyclerView.ViewHolder {
        LinearLayout ll_main;
        TextView date,rec_amount,rec_from,expAmt,closingBalance,remark_expense;

        public HolderDayBook(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            rec_amount=itemView.findViewById(R.id.rec_amount);
            rec_from=itemView.findViewById(R.id.rec_from);
            expAmt=itemView.findViewById(R.id.expAmt);
            closingBalance=itemView.findViewById(R.id.closingBalance);
            ll_main=itemView.findViewById(R.id.ll_main);
            remark_expense=itemView.findViewById(R.id.remark_expense);


        }
    }
}
