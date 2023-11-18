package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skillzoomer_Attendance.com.Model.MopdelOtherExpenses;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterOtherExpenses extends RecyclerView.Adapter<AdapterOtherExpenses.HolderOtherExpenses>{

    private Context context;
    private ArrayList<MopdelOtherExpenses> otherExpensesArrayList;
    private long siteId;

    public AdapterOtherExpenses(Context context, ArrayList<MopdelOtherExpenses> otherExpensesArrayList, long siteId) {
        this.context = context;
        this.otherExpensesArrayList = otherExpensesArrayList;
        this.siteId = siteId;
    }

    @NonNull
    @Override
    public AdapterOtherExpenses.HolderOtherExpenses onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.other_expenses_settled,parent,false);
        return new AdapterOtherExpenses.HolderOtherExpenses(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOtherExpenses.HolderOtherExpenses holder, int position) {
        MopdelOtherExpenses otherExpenses=otherExpensesArrayList.get(holder.getAdapterPosition());
        holder.txt_container.setText(otherExpenses.getAmount());
        holder.txt_comment.setText(otherExpenses.getExpRemark());
        holder.btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.setVisibility(View.GONE);
            }
        });
        holder.btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                reference.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("OtherExpense").child(otherExpenses.getExpId()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                              Log.e("Remove","Success");
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return otherExpensesArrayList.size();
    }

    public class HolderOtherExpenses extends RecyclerView.ViewHolder {
        TextView txt_container,txt_comment;
        Button btn_yes,btn_no;
        public HolderOtherExpenses(@NonNull View itemView) {
            super(itemView);
            txt_container=itemView.findViewById(R.id.txt_container);
            btn_yes=itemView.findViewById(R.id.btn_yes);
            btn_no=itemView.findViewById(R.id.btn_no);
            txt_comment=itemView.findViewById(R.id.txt_comment);

        }
    }
}
