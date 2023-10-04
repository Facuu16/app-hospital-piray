package com.facuu16.hp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Appointment {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private UUID uuid;

    private String date, mail, doctor;

    public Appointment(UUID uuid, String mail, String date, String doctor) {
        this.uuid = uuid;
        this.mail = mail;
        this.date = date;
        this.doctor = doctor;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getMail() {
        return mail;
    }

    public String getDate() {
        return date;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public Date getParsedDate() throws ParseException {
        return DATE_FORMAT.parse(date);
    }

}
