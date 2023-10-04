package com.facuu16.hp.manager;

import com.facuu16.hp.activity.LoginActivity;
import com.facuu16.hp.model.User;
import com.facuu16.hp.util.TaskUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class UserManager {

    private static final Pattern MAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static UserManager instance;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();

        return instance;
    }

    public void insertUser(User user, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("INSERT INTO users VALUE (?, ?, ?, ?, ?)")) {
                statement.setString(1, user.getMail());
                statement.setString(2, user.getDNI());
                statement.setString(3, user.getName());
                statement.setString(4, user.getLastName());
                statement.setString(5, user.getPassword());
                consumer.accept(statement.executeUpdate() > 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void getUser(String mail, Consumer<User> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT mail, dni, name, last_name, password FROM users WHERE (mail=?)")) {
                statement.setString(1, mail);

                final ResultSet result = statement.executeQuery();

                if (result.next())
                    consumer.accept(new User(
                            result.getString("mail"),
                            result.getString("dni"),
                            result.getString("name"),
                            result.getString("last_name"),
                            result.getString("password")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void deleteUser(String mail, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("DELETE FROM users WHERE (mail=?)")) {
                statement.setString(1, mail);
                consumer.accept(statement.executeUpdate() > 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void isMail(String mail, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT * FROM users WHERE (mail=?)")) {
                statement.setString(1, mail);
                consumer.accept(statement.executeQuery().next());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void isDNI(String DNI, Consumer<Boolean> consumer) {
        TaskUtil.runAsyncTask(() -> {
            try (final PreparedStatement statement = LoginActivity.getConnection().prepareStatement("SELECT * FROM users WHERE (dni=?)")) {
                statement.setString(1, DNI);
                consumer.accept(statement.executeQuery().next());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean validateMail(String mail) {
        return MAIL_PATTERN.matcher(mail).matches();
    }

}
