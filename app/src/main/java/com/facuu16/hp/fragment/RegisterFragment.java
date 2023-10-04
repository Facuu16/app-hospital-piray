package com.facuu16.hp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.facuu16.hp.R;
import com.facuu16.hp.manager.UserManager;
import com.facuu16.hp.model.User;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);

        view.findViewById(R.id.password).setOnClickListener(v -> {
            final EditText password = view.findViewById(R.id.password);

            password.setInputType(password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        });

        view.findViewById(R.id.back).setOnClickListener(v -> getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .addToBackStack(null)
                .commit());

        view.findViewById(R.id.register).setOnClickListener(v -> {
            final String mail = ((EditText) view.findViewById(R.id.mail)).getText().toString();
            final String dni = ((EditText) view.findViewById(R.id.dni)).getText().toString();
            final String name = ((EditText) view.findViewById(R.id.name)).getText().toString();
            final String lastName = ((EditText) view.findViewById(R.id.last_name)).getText().toString();
            final String password = ((EditText) view.findViewById(R.id.password)).getText().toString();

            if (mail.isEmpty() || dni.isEmpty() || name.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
                showToast("¡Completa todos los campos obligatorios!");
                return;
            }

            if (!UserManager.getInstance().validateMail(mail)) {
                showToast("¡Debes ingresar un mail válido!");
                return;
            }

            UserManager.getInstance().isMail(mail, isMail  -> {
                if (isMail) {
                    showToast("¡Ese mail ya está registrado!");
                    return;
                }

                UserManager.getInstance().isDNI(mail, isDNI  -> {
                    if (isDNI) {
                        showToast("¡Ese DNI ya está registrado!");
                        return;
                    }

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new VerificationFragment(new User(mail, dni, name, lastName, password)))
                            .addToBackStack(null)
                            .commit();
                });
            });
        });

        return view;
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

}