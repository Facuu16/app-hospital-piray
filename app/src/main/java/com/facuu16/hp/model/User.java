package com.facuu16.hp.model;

public class User {

    private String DNI, mail, name, lastName, password;

    public User(String mail, String DNI, String name, String lastName, String password) {
        this.mail = mail;
        this.DNI = DNI;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public String getDNI() {
        return DNI;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

}
