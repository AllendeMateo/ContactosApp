package com.example.contactos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ContactosDB";
    private static final int DATABASE_VERSION = 2;  // Increment for schema change
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_PHONES = "phones";
    private static final String TABLE_EMAILS = "emails";

    // Contacts table columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_PHOTO_PATH = "photo_path";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_COMPANY = "company";

    // Phones table columns
    private static final String KEY_PHONE_ID = "phone_id";
    private static final String KEY_CONTACT_ID = "contact_id";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PHONE_TYPE = "phone_type";

    // Emails table columns
    private static final String KEY_EMAIL_ID = "email_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_EMAIL_TYPE = "email_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_SURNAME + " TEXT,"
                + KEY_PHOTO_PATH + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_NOTES + " TEXT,"
                + KEY_COMPANY + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_PHONES_TABLE = "CREATE TABLE " + TABLE_PHONES + "("
                + KEY_PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTACT_ID + " INTEGER,"
                + KEY_PHONE + " TEXT,"
                + KEY_PHONE_TYPE + " TEXT,"
                + "FOREIGN KEY(" + KEY_CONTACT_ID + ") REFERENCES " + TABLE_CONTACTS + "(" + KEY_ID + "))";
        db.execSQL(CREATE_PHONES_TABLE);

        String CREATE_EMAILS_TABLE = "CREATE TABLE " + TABLE_EMAILS + "("
                + KEY_EMAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTACT_ID + " INTEGER,"
                + KEY_EMAIL + " TEXT,"
                + KEY_EMAIL_TYPE + " TEXT,"
                + "FOREIGN KEY(" + KEY_CONTACT_ID + ") REFERENCES " + TABLE_CONTACTS + "(" + KEY_ID + "))";
        db.execSQL(CREATE_EMAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, contact.getName());
            values.put(KEY_SURNAME, contact.getSurname());
            values.put(KEY_PHOTO_PATH, contact.getPhotoPath());
            values.put(KEY_ADDRESS, contact.getAddress());
            values.put(KEY_NOTES, contact.getNotes());
            values.put(KEY_COMPANY, contact.getCompany());
            long contactId = db.insert(TABLE_CONTACTS, null, values);

            for (Contact.Phone phone : contact.getPhones()) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(KEY_CONTACT_ID, contactId);
                phoneValues.put(KEY_PHONE, phone.getNumber());
                phoneValues.put(KEY_PHONE_TYPE, phone.getType());
                db.insert(TABLE_PHONES, null, phoneValues);
            }

            for (Contact.Email email : contact.getEmails()) {
                ContentValues emailValues = new ContentValues();
                emailValues.put(KEY_CONTACT_ID, contactId);
                emailValues.put(KEY_EMAIL, email.getAddress());
                emailValues.put(KEY_EMAIL_TYPE, email.getType());
                db.insert(TABLE_EMAILS, null, emailValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String surname = cursor.getString(2);
                String photoPath = cursor.getString(3);
                String address = cursor.getString(4);
                String notes = cursor.getString(5);
                String company = cursor.getString(6);

                List<Contact.Phone> phones = getPhonesForContact(id, db);
                List<Contact.Email> emails = getEmailsForContact(id, db);

                Contact contact = new Contact(id, name, surname, phones, emails, photoPath, address, notes, company);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, contact.getName());
            values.put(KEY_SURNAME, contact.getSurname());
            values.put(KEY_PHOTO_PATH, contact.getPhotoPath());
            values.put(KEY_ADDRESS, contact.getAddress());
            values.put(KEY_NOTES, contact.getNotes());
            values.put(KEY_COMPANY, contact.getCompany());
            db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{String.valueOf(contact.getId())});

            // Delete existing phones/emails and re-insert
            db.delete(TABLE_PHONES, KEY_CONTACT_ID + " = ?", new String[]{String.valueOf(contact.getId())});
            db.delete(TABLE_EMAILS, KEY_CONTACT_ID + " = ?", new String[]{String.valueOf(contact.getId())});

            for (Contact.Phone phone : contact.getPhones()) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(KEY_CONTACT_ID, contact.getId());
                phoneValues.put(KEY_PHONE, phone.getNumber());
                phoneValues.put(KEY_PHONE_TYPE, phone.getType());
                db.insert(TABLE_PHONES, null, phoneValues);
            }

            for (Contact.Email email : contact.getEmails()) {
                ContentValues emailValues = new ContentValues();
                emailValues.put(KEY_CONTACT_ID, contact.getId());
                emailValues.put(KEY_EMAIL, email.getAddress());
                emailValues.put(KEY_EMAIL_TYPE, email.getType());
                db.insert(TABLE_EMAILS, null, emailValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_PHONES, KEY_CONTACT_ID + " = ?", new String[]{String.valueOf(id)});
            db.delete(TABLE_EMAILS, KEY_CONTACT_ID + " = ?", new String[]{String.valueOf(id)});
            db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private List<Contact.Phone> getPhonesForContact(int contactId, SQLiteDatabase db) {
        List<Contact.Phone> phones = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT " + KEY_PHONE + ", " + KEY_PHONE_TYPE + " FROM " + TABLE_PHONES +
                " WHERE " + KEY_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)});
        if (cursor.moveToFirst()) {
            do {
                phones.add(new Contact.Phone(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return phones;
    }

    private List<Contact.Email> getEmailsForContact(int contactId, SQLiteDatabase db) {
        List<Contact.Email> emails = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT " + KEY_EMAIL + ", " + KEY_EMAIL_TYPE + " FROM " + TABLE_EMAILS +
                " WHERE " + KEY_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)});
        if (cursor.moveToFirst()) {
            do {
                emails.add(new Contact.Email(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return emails;
    }
}
