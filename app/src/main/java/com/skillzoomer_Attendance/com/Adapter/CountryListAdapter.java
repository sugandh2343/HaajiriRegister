package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends ArrayAdapter<ModelSite> {

    private final Context context;
    private final int resource;
    private final int textViewResourceId;
    private final List<ModelSite> items;
    private final List<ModelSite> tempItems;
    private final List<ModelSite> suggestions;

    public CountryListAdapter(@NonNull Context context,
                              int resource,
                              int textViewResourceId,
                              List<ModelSite> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<ModelSite>(items); // this makes the difference.
        suggestions = new ArrayList<ModelSite>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_of_country_row,parent,false);
        }
        ModelSite state = items.get(position);
        if (state!=null){
            TextView lblName = (TextView) view.findViewById(R.id.spinner_text);
            if (lblName != null)
                lblName.setText(state.getSiteName());
            lblName.setTextSize(16);
        }
        return view;
    }
    @Override
    public Filter getFilter() {
        return nameFilter;
    }
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((ModelSite) resultValue).getSiteName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ModelSite occupation  : tempItems) {
                    if (occupation.getSiteName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(occupation);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<ModelSite> filterList = (ArrayList<ModelSite>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ModelSite occupation : filterList) {
                    add(occupation);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
