package com.facuu16.hp.manager;

import com.facuu16.hp.activity.LoginActivity;
import com.facuu16.hp.model.Appointment;
import com.facuu16.hp.util.TaskUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class AppointmentManager {

    private static AppointmentManager instance;

    private AppointmentManager() {}

    public static AppointmentManager getInstance() {
        if (instance == null)
            instance = new AppointmentManager();

        return instance;
    }

    public void insertAppointment(Appointment appointment, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("INSERT INTO appointments VALUE (?, ?, ?, ?)")) {
                statement.setString(1, appointment.getUuid().toString());
                statement.setString(2, appointment.getMail());
                statement.setString(3, appointment.getDate());
                statement.setString(4, appointment.getDoctor());
                consumer.accept(statement.executeUpdate() > 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void getAppointment(UUID uuid, Consumer<Appointment> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT id, mail, date, doctor FROM appointments WHERE (id=?)")) {
                statement.setString(1, uuid.toString());

                final ResultSet result = statement.executeQuery();

                if (result.next())
                    consumer.accept(new Appointment(
                            UUID.fromString(result.getString("id")),
                            result.getString("mail"),
                            result.getString("date"),
                            result.getString("doctor")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void getAppointments(String mail, Consumer<List<Appointment>> consumer) {
        TaskUtil.runAsyncTask(() -> {
            final List<Appointment> appointments = new ArrayList<>();

            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT id, mail, date, doctor FROM appointments WHERE mail=?")) {
                statement.setString(1, mail);

                final ResultSet result = statement.executeQuery();

                while (result.next()) {
                    appointments.add(new Appointment(
                            UUID.fromString(result.getString("id")),
                            result.getString("mail"),
                            result.getString("date"),
                            result.getString("doctor")));
                }

                consumer.accept(appointments);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void deleteAppointment(UUID uuid, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("DELETE FROM appointments WHERE (id=?)")) {
                statement.setString(1, uuid.toString());
                consumer.accept(statement.executeUpdate() > 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void isAppointment(UUID uuid, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT * FROM appointments WHERE (id=?)")) {
                statement.setString(1, uuid.toString());
                consumer.accept(statement.executeQuery().next());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void isDateAvailable(String date, String doctor, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT date FROM appointments WHERE date = ? AND doctor = ?")) {
                statement.setString(1, date);
                statement.setString(2, doctor);
                consumer.accept(!statement.executeQuery().next());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
