package com.facuu16.hp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.facuu16.hp.R;
import com.facuu16.hp.manager.UserManager;
import com.facuu16.hp.model.User;
import com.facuu16.hp.model.VerificationCode;

public class VerificationFragment extends Fragment {

    private final User user;

    public VerificationFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_verification, container, false);
        final VerificationCode code = new VerificationCode(user.getMail(), user.getName());

        code.send();
        view.findViewById(R.id.accept).setOnClickListener(v -> {
            final String input = ((EditText) view.findViewById(R.id.verification_code)).getText().toString();

            if (input.isEmpty()) {
                showToast("¡Completa todos los campos obligatorios!");
                return;
            }

            if (!code.isCode()) {
                showToast("El código de verificación venció, reenviando uno nuevo...");
                code.send();
                return;
            }

            if (!code.validate(input)) {
                showToast("¡Código de verificación incorrecto!");
                return;
            }

            showToast("Registrando usuario...");
            UserManager.getInstance().insertUser(user, success -> {
                if (!success) {
                    showToast("Ocurrió un error al registrar el usuario.");
                    return;
                }

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .addToBackStack(null)
                        .commit();
                showToast("¡Usuario registrado con éxito!");
                code.stopTimer();
            });
        });

        view.findViewById(R.id.resend_code).setOnClickListener(v -> {
            code.send();
            showToast("¡Código reenviado!");
        });

        return view;
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

}