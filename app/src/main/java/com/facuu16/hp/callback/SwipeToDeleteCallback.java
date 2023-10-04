package com.facuu16.hp.callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.facuu16.hp.adapter.AppointmentAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    private final AppointmentAdapter adapter;

    public SwipeToDeleteCallback(AppointmentAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.showDeleteConfirmationDialog(viewHolder.getAdapterPosition(), isDeleted -> {
            if (!isDeleted)
                adapter.getFragment().getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
