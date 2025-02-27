package com.example.contactos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contact implements Serializable {
    private int id;
    private String name;
    private String surname;
    private List<Phone> phones;
    private List<Email> emails;
    private String photoPath;
    private String address;
    private String notes;
    private String company;

    public Contact(int id, String name, String surname, List<Phone> phones, List<Email> emails, String photoPath,
                   String address, String notes, String company) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phones = phones != null ? phones : new ArrayList<>();
        this.emails = emails != null ? emails : new ArrayList<>();
        this.photoPath = photoPath;
        this.address = address;
        this.notes = notes;
        this.company = company;
    }

    // Legacy constructor for backward compatibility
    public Contact(int id, String name, String surname, String phone, String phoneType, String email, String emailType,
                   String photoPath, String address, String notes, String company) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phones = new ArrayList<>();
        if (phone != null && !phone.isEmpty()) this.phones.add(new Phone(phone, phoneType));
        this.emails = new ArrayList<>();
        if (email != null && !email.isEmpty()) this.emails.add(new Email(email, emailType));
        this.photoPath = photoPath;
        this.address = address;
        this.notes = notes;
        this.company = company;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public List<Phone> getPhones() { return phones; }
    public List<Email> getEmails() { return emails; }
    public String getPhotoPath() { return photoPath; }
    public String getAddress() { return address; }
    public String getNotes() { return notes; }
    public String getCompany() { return company; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPhones(List<Phone> phones) { this.phones = phones; }
    public void setEmails(List<Email> emails) { this.emails = emails; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setAddress(String address) { this.address = address; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCompany(String company) { this.company = company; }

    // Helper classes for phones and emails
    public static class Phone implements Serializable {
        private String number;
        private String type;

        public Phone(String number, String type) {
            this.number = number;
            this.type = type;
        }

        public String getNumber() { return number; }
        public String getType() { return type; }
    }

    public static class Email implements Serializable {
        private String address;
        private String type;

        public Email(String address, String type) {
            this.address = address;
            this.type = type;
        }

        public String getAddress() { return address; }
        public String getType() { return type; }
    }
}
