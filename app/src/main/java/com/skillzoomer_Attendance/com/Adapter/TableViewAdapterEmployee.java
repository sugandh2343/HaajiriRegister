package com.skillzoomer_Attendance.com.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.evrencoskun.tableview.sort.SortState;
import com.skillzoomer_Attendance.com.Model.Cell;
import com.skillzoomer_Attendance.com.Model.CellViewHolder;
import com.skillzoomer_Attendance.com.Model.ColumnHeader;
import com.skillzoomer_Attendance.com.Model.ColumnHeaderViewHolder;
import com.skillzoomer_Attendance.com.Model.RowHeader;
import com.skillzoomer_Attendance.com.Model.RowHeaderViewHolder;
import com.skillzoomer_Attendance.com.R;

public class TableViewAdapterEmployee extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {
    private static final String LOG_TAG = TableViewAdapter.class.getSimpleName();

    private final TableViewModelEmployee mTableViewModel;

    public TableViewAdapterEmployee(@NonNull TableViewModelEmployee tableViewModel) {
        super();
        this.mTableViewModel = tableViewModel;
    }
    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(LOG_TAG, " onCreateCellViewHolder has been called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout;
        layout = inflater.inflate(R.layout.table_view_cell_layout, parent, false);

        // Create a Cell ViewHolder
        return new CellViewHolder(layout);
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Cell cellItemModel, int columnPosition, int rowPosition) {
        CellViewHolder viewHolder = (CellViewHolder) holder;
        Log.e("CIVM",cellItemModel.getData().toString());

        viewHolder.setCell(cellItemModel);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_column_header_layout, parent, false);

        // Create a ColumnHeader ViewHolder
        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable ColumnHeader columnHeaderItemModel, int columnPosition) {
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.setColumnHeader(columnHeaderItemModel);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_row_header_layout, parent, false);

        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable RowHeader rowHeaderItemModel, int rowPosition) {
        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(String.valueOf(rowHeaderItemModel.getData()));
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        View corner = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_corner_layout, parent, false);
        corner.setOnClickListener(view -> {
            SortState sortState = TableViewAdapterEmployee.this.getTableView()
                    .getRowHeaderSortingStatus();
            if (sortState != SortState.ASCENDING) {
                Log.d("TableViewAdapter", "Order Ascending");
                TableViewAdapterEmployee.this.getTableView().sortRowHeader(SortState.ASCENDING);
            } else {
                Log.d("TableViewAdapter", "Order Descending");
                TableViewAdapterEmployee.this.getTableView().sortRowHeader(SortState.DESCENDING);
            }
        });
        return corner;
    }
}
