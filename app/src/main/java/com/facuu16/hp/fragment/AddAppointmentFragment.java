package com.facuu16.hp.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facuu16.hp.R;
import com.facuu16.hp.activity.LoginActivity;
import com.facuu16.hp.manager.AppointmentManager;
import com.facuu16.hp.model.Appointment;
import com.facuu16.hp.ui.dashboard.DashboardFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class AddAppointmentFragment extends Fragment {

    private int year, month, day, hour, minute;

    public AddAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View view = inflater.inflate(R.layout.fragment_add_appointment, container, false);

        final Spinner spinner = view.findViewById(R.id.spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Arrays.asList("Dr. Juan", "Dr. Pedro", "Dr. Gonzalo"));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        view.findViewById(R.id.date_picker).setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            final DatePickerDialog date = new DatePickerDialog(
                    requireContext(),
                    (vi, year, month, day) -> {
                        this.year = year;
                        this.month = month + 1;
                        this.day = day;
                        ((TextView) view.findViewById(R.id.display_date)).setText(String.format("%04d-%02d-%02d", year, this.month, day));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            date.show();
        });

        view.findViewById(R.id.hour_picker).setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            final TimePickerDialog time = new TimePickerDialog(
                    requireContext(),
                    (vi, hour, minute) -> {
                        this.hour = hour;
                        this.minute = minute;
                        ((TextView) view.findViewById(R.id.display_hour)).setText(String.format("%02d:%02d", hour, minute));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);

            time.show();
        });

        view.findViewById(R.id.create).setOnClickListener(v -> {
            final String doctor = ((Spinner) getView().findViewById(R.id.spinner)).getSelectedItem().toString();

            final Calendar date = Calendar.getInstance();
            date.set(year, month - 1, day);

            if (date.before(Calendar.getInstance())) {
                showToast("No puedes seleccionar una fecha anterior a la fecha actual.");
                return;
            }

            if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                showToast("Los turnos solo están disponibles de lunes a viernes.");
                return;
            }

            if (hour < 8 || (hour == 12 && minute > 0)) {
                showToast("Los turnos solo están disponibles entre las 08:00 AM y las 12:00 PM.");
                return;
            }

            final String formatted = String.format("%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute);

            AppointmentManager.getInstance().isDateAvailable(formatted, doctor, isAvailable -> {
                if (!isAvailable) {
                    showToast("¡Ya hay un turno en la fecha y hora seleccionada!");
                    return;
                }

                AppointmentManager.getInstance().insertAppointment(new Appointment(UUID.randomUUID(), LoginActivity.getLoggedUser().getMail(), formatted, doctor), success -> {
                    if (!success) {
                        showToast("Ocurrió un error al intentar crear el turno.");
                        return;
                    }

                    showToast("¡Turno solicitado para " + doctor + " el " + day + "/" + this.month + "/" + year + " a las " + hour + ":" + minute);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new DashboardFragment())
                            .addToBackStack(null)
                            .commit();

                    getActivity().runOnUiThread(() -> getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE));
                });
            });
        });

        return view;
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

}