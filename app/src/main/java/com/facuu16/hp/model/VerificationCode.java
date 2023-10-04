package com.facuu16.hp.model;

import android.os.Handler;

import com.facuu16.hp.activity.MainActivity;
import com.facuu16.hp.util.MathUtil;
import com.facuu16.hp.util.TaskUtil;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VerificationCode {

    private static final long TIMER_DELAY = 600000;

    private final String mail, name;

    private String code;

    private Runnable runnable;
    private Handler handler;

    public VerificationCode(String mail, String name) {
        this.mail = mail;
        this.name = name;
        handler = new Handler();
    }

    public String getMail() {
        return mail;
    }

    public void send() {
        this.code = String.format("%06d", MathUtil.getRandom().nextInt(1000000));

        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        MainActivity.getProperty("mail", property -> {
            final String[] auth = property.split(";");

            TaskUtil.runAsyncTask(() -> {
                Session session;

                try {
                    session = Session.getInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(auth[0], auth[1]);
                        }
                    });
                } catch (IllegalStateException e) {
                    throw new RuntimeException(e);
                }

                try {
                    final Message message = new MimeMessage(session);

                    message.setFrom(new InternetAddress(auth[0]));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getMail()));
                    message.setSubject("Código de verificación de App Hospital Puerto Piray");
                    message.setText("Hola " + name + ", tu código de verificación es: " + code);

                    Transport.send(message);
                    if (runnable != null)
                        stopTimer();

                    runnable = () -> code = null;
                    handler.postDelayed(runnable, TIMER_DELAY);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public boolean validate(String code) {
        return this.code.equals(code);
    }

    public boolean isCode() {
        return code != null;
    }

    public void stopTimer() {
        handler.removeCallbacks(runnable);
    }

}
