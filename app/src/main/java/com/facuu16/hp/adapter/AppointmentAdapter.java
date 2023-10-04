package com.facuu16.hp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facuu16.hp.R;
import com.facuu16.hp.manager.AppointmentManager;
import com.facuu16.hp.model.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointments = new ArrayList<>();

    private final Fragment fragment;

    public AppointmentAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public void removeAppointment(int position) {
        final Appointment appointment = appointments.get(position);

        AppointmentManager.getInstance().deleteAppointment(appointment.getUuid(), success -> {
            if (success) {
                appointments.remove(appointment);
                fragment.getActivity().runOnUiThread(() -> {
                    notifyDataSetChanged();
                    Toast.makeText(fragment.getActivity().getApplicationContext(), "¡Cancelaste correctamente el turno!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void showDeleteConfirmationDialog(int position, Consumer<Boolean> consumer) {
        new AlertDialog.Builder(fragment.getContext())
                .setTitle("Confirmar cancelación")
                .setMessage("¿Estás seguro de que deseas cancelar este turno?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    removeAppointment(position);
                    consumer.accept(true);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                    consumer.accept(false);
                })
                .create()
                .show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public Fragment getFragment() {
        return fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView doctor;
        private final TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctor = itemView.findViewById(R.id.appointment_doctor);
            date = itemView.findViewById(R.id.appointment_date);
        }

        public void bind(Appointment appointment) {
            doctor.setText(appointment.getDoctor());
            date.setText(appointment.getDate());
        }
    }

}

