package com.facuu16.hp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.facuu16.hp.R;
import com.facuu16.hp.database.DatabaseConnection;
import com.facuu16.hp.fragment.MaintenanceFragment;
import com.facuu16.hp.fragment.RegisterFragment;
import com.facuu16.hp.fragment.SplashFragment;
import com.facuu16.hp.manager.UserManager;
import com.facuu16.hp.model.User;
import com.facuu16.hp.util.TaskUtil;

import java.sql.Connection;

public class LoginActivity extends AppCompatActivity {

    private static DatabaseConnection DATABASE_CONNECTION;

    private static User loggedUser;

    private PackageInfo info = null;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        try {
            this.info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        setContentView(R.layout.activity_login);
        replaceTransaction(new SplashFragment());

        TaskUtil.runAsyncTask(() -> DATABASE_CONNECTION = new DatabaseConnection("mysql-148175-0.cloudclusters.net", "10039", "hospital_piray", "admin", "Otj3tCyO"));

        MainActivity.getProperty("maintenance", isMaintenance -> runOnUiThread(() -> {
            if (Boolean.parseBoolean(isMaintenance)) {
                new Handler().postDelayed(() -> replaceTransaction(new MaintenanceFragment()), 5000);
                return;
            }

            final SharedPreferences preferences = getSharedPreferences("hp_settings", Context.MODE_PRIVATE);

            final String lastMail = preferences.getString("mail", null);
            final String lastPassword = preferences.getString("password", null);

            if (lastMail != null && lastPassword != null) {
                UserManager.getInstance().isMail(lastMail, isMail -> {
                    if (!isMail)
                        return;

                    UserManager.getInstance().getUser(lastMail, user -> {
                        if (!user.getPassword().equals(lastPassword))
                            return;

                        loggedUser = user;
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                });
            }

            new Handler().postDelayed(() -> {
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);

                if (fragment == null)
                    return;

                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .addToBackStack(null)
                        .commit();
            }, 5000);

            findViewById(R.id.login).setOnClickListener(view -> {
                final String mail = ((EditText) findViewById(R.id.mail)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showToast("¡Completa todos los campos obligatorios!");
                    return;
                }

                if (!UserManager.getInstance().validateMail(mail)) {
                    showToast("¡Debes ingresar un mail válido!");
                    return;
                }

                UserManager.getInstance().isMail(mail, exists  -> {
                    if (!exists) {
                        showToast("¡Ese mail no está registrado!");
                        return;
                    }

                    UserManager.getInstance().getUser(mail, user -> {
                        if (!user.getPassword().equals(password)) {
                            showToast("¡Contraseña incorrecta!");
                            return;
                        }

                        final SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("mail", mail);
                        editor.putString("password", password);
                        editor.apply();

                        showToast("¡Iniciaste sesión correctamente!");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                });
            });

            findViewById(R.id.imageView).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://salud.misiones.gob.ar"))));

            findViewById(R.id.password).setOnClickListener(view -> {
                final EditText password = findViewById(R.id.password);

                password.setInputType(password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            });

            findViewById(R.id.register).setOnClickListener(view -> replaceTransaction(new RegisterFragment()));
        }));
    }

    public String getVersionName() {
        return info.versionName;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    private void replaceTransaction(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    public static Connection getConnection() {
        return DATABASE_CONNECTION.getConnection();
    }
}
