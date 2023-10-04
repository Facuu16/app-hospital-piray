package com.facuu16.hp.activity;

import android.os.Bundle;

import com.facuu16.hp.R;
import com.facuu16.hp.databinding.ActivityMainBinding;
import com.facuu16.hp.util.TaskUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavigationUI.setupWithNavController(binding.navView, Navigation.findNavController(this, R.id.nav_host_fragment_activity_main));
    }

    public static void getProperty(String property, Consumer<String> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT value FROM properties WHERE (property=?)")) {
                statement.setString(1, property);

                final ResultSet result = statement.executeQuery();

                if (result.next())
                    consumer.accept(result.getString("value"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}