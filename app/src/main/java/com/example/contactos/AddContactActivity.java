package com.example.contactos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private List<EditText> phoneFields = new ArrayList<>();
    private List<Spinner> phoneTypeSpinners = new ArrayList<>();
    private List<EditText> emailFields = new ArrayList<>();
    private List<Spinner> emailTypeSpinners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        db = new DatabaseHelper(this);

        EditText nameEditText = findViewById(R.id.edit_name);
        EditText surnameEditText = findViewById(R.id.edit_surname);
        EditText phoneEditText = findViewById(R.id.edit_phone);
        Spinner phoneTypeSpinner = findViewById(R.id.spinner_phone_type);
        EditText emailEditText = findViewById(R.id.edit_email);
        Spinner emailTypeSpinner = findViewById(R.id.spinner_email_type);
        EditText addressEditText = findViewById(R.id.edit_address);
        EditText notesEditText = findViewById(R.id.edit_notes);
        EditText companyEditText = findViewById(R.id.edit_company);
        Button addPhoneButton = findViewById(R.id.add_phone_button);
        Button addEmailButton = findViewById(R.id.add_email_button);
        Button saveButton = findViewById(R.id.save_button);

        phoneFields.add(phoneEditText);
        phoneTypeSpinners.add(phoneTypeSpinner);
        emailFields.add(emailEditText);
        emailTypeSpinners.add(emailTypeSpinner);

        addPhoneButton.setOnClickListener(v -> addPhoneField());
        addEmailButton.setOnClickListener(v -> addEmailField());

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            List<Contact.Phone> phones = new ArrayList<>();
            for (int i = 0; i < phoneFields.size(); i++) {
                String phone = phoneFields.get(i).getText().toString().trim();
                if (!phone.isEmpty()) {
                    phones.add(new Contact.Phone(phone, phoneTypeSpinners.get(i).getSelectedItem().toString()));
                }
            }
            List<Contact.Email> emails = new ArrayList<>();
            for (int i = 0; i < emailFields.size(); i++) {
                String email = emailFields.get(i).getText().toString().trim();
                if (!email.isEmpty()) {
                    emails.add(new Contact.Email(email, emailTypeSpinners.get(i).getSelectedItem().toString()));
                }
            }
            String address = addressEditText.getText().toString().trim();
            String notes = notesEditText.getText().toString().trim();
            String company = companyEditText.getText().toString().trim();

            if (!name.isEmpty() && !phones.isEmpty()) {
                Contact newContact = new Contact(
                    0, name, surname, phones, emails, null,
                    address.isEmpty() ? null : address,
                    notes.isEmpty() ? null : notes,
                    company.isEmpty() ? null : company
                );
                db.addContact(newContact);
                Intent result = new Intent();
                result.putExtra("added", true);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Name and at least one Phone are required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPhoneField() {
        LinearLayout phoneContainer = findViewById(R.id.phone_container);
        View phoneView = LayoutInflater.from(this).inflate(R.layout.phone_field, null);
        EditText newPhoneEditText = phoneView.findViewById(R.id.edit_phone);
        Spinner newPhoneTypeSpinner = phoneView.findViewById(R.id.spinner_phone_type);
        phoneFields.add(newPhoneEditText);
        phoneTypeSpinners.add(newPhoneTypeSpinner);
        phoneContainer.addView(phoneView);
    }

    private void addEmailField() {
        LinearLayout emailContainer = findViewById(R.id.email_container);
        View emailView = LayoutInflater.from(this).inflate(R.layout.email_field, null);
        EditText newEmailEditText = emailView.findViewById(R.id.edit_email);
        Spinner newEmailTypeSpinner = emailView.findViewById(R.id.spinner_email_type);
        emailFields.add(newEmailEditText);
        emailTypeSpinners.add(newEmailTypeSpinner);
        emailContainer.addView(emailView);
    }
}
