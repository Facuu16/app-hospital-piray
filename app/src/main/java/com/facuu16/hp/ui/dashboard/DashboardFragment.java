package com.facuu16.hp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facuu16.hp.activity.LoginActivity;
import com.facuu16.hp.adapter.AppointmentAdapter;
import com.facuu16.hp.callback.SwipeToDeleteCallback;
import com.facuu16.hp.fragment.AddAppointmentFragment;
import com.facuu16.hp.R;
import com.facuu16.hp.manager.AppointmentManager;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment {

    private RecyclerView recycler;

    private AppointmentAdapter adapter;

    public DashboardFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        view.findViewById(R.id.add_appointment).setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new AddAppointmentFragment())
                    .addToBackStack(null)
                    .commit();

            getActivity().findViewById(R.id.nav_view).setVisibility(View.INVISIBLE);
        });

        this.recycler = view.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.adapter = new AppointmentAdapter(this);
        recycler.setAdapter(adapter);

        final ItemTouchHelper helper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        helper.attachToRecyclerView(recycler);

        updateAppointments();

        return view;
    }

    public void updateAppointments() {
        AppointmentManager.getInstance().getAppointments(LoginActivity.getLoggedUser().getMail(), appointments -> getActivity().runOnUiThread(() -> adapter.setAppointments(appointments.stream()
                .filter(appointment -> {
                    try {
                        final Date date = appointment.getParsedDate();

                        return date != null && date.after(Calendar.getInstance().getTime());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparing(appointment -> {
                    try {
                        return appointment.getParsedDate();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .collect(Collectors.toList()))));
    }

}