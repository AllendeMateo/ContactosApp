package com.example.contactos;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private DatabaseHelper db;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        } else {
            Log.e("Contactos", "Toolbar not found in activity_main.xml!");
        }

        db = new DatabaseHelper(this);
        contactList = db.getAllContacts();

        recyclerView = findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ContactAdapter(this, contactList, this);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("Contactos", "RecyclerView not found in activity_main.xml!");
        }

        FloatingActionButton addButton = findViewById(R.id.add_button);
        if (addButton != null) {
            addButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddContactActivity.class);
                startActivityForResult(intent, 3);
            });
        } else {
            Log.e("Contactos", "Add button not found in activity_main.xml!");
        }

        updateContactList();  // Initial sort
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_import) {
            importContacts();
            return true;
        } else if (id == R.id.action_export) {
            exportContacts();
            return true;
        } else if (id == R.id.action_sort) {
            sortContactsByFirstName();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddEditDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_contact, null);
        EditText nameEditText = view.findViewById(R.id.edit_name);
        EditText surnameEditText = view.findViewById(R.id.edit_surname);
        EditText phoneEditText = view.findViewById(R.id.edit_phone);
        Spinner phoneTypeSpinner = view.findViewById(R.id.spinner_phone_type);
        EditText emailEditText = view.findViewById(R.id.edit_email);
        Spinner emailTypeSpinner = view.findViewById(R.id.spinner_email_type);
        Button saveButton = view.findViewById(R.id.save_button);

        if (contact != null) {
            nameEditText.setText(contact.getName());
            surnameEditText.setText(contact.getSurname());
            if (!contact.getPhones().isEmpty()) {
                phoneEditText.setText(contact.getPhones().get(0).getNumber());
                setSpinnerSelection(phoneTypeSpinner, contact.getPhones().get(0).getType());
            }
            if (!contact.getEmails().isEmpty()) {
                emailEditText.setText(contact.getEmails().get(0).getAddress());
                setSpinnerSelection(emailTypeSpinner, contact.getEmails().get(0).getType());
            }
        }

        builder.setView(view);
        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String phoneType = phoneTypeSpinner.getSelectedItem().toString();
            String email = emailEditText.getText().toString().trim();
            String emailType = emailTypeSpinner.getSelectedItem().toString();

            if (!name.isEmpty() && !phone.isEmpty()) {
                List<Contact.Phone> phones = new ArrayList<>();
                phones.add(new Contact.Phone(phone, phoneType));
                List<Contact.Email> emails = new ArrayList<>();
                if (!email.isEmpty()) emails.add(new Contact.Email(email, emailType));
                Contact newContact = new Contact(
                    contact != null ? contact.getId() : 0,
                    name,
                    surname.isEmpty() ? null : surname,
                    phones,
                    emails,
                    contact != null ? contact.getPhotoPath() : null,
                    null, null, null
                );
                if (contact == null) {
                    db.addContact(newContact);
                } else {
                    db.updateContact(newContact);
                }
                updateContactList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Name and Phone are required", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            int position = adapter.getPosition(value);
            if (position >= 0) spinner.setSelection(position);
        }
    }

    private void updateContactList() {
        contactList = db.getAllContacts();
        sortContactsByFirstName();
    }

    private void sortContactsByFirstName() {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
        adapter.updateContacts(contactList);
    }

    @Override
    public void onViewClick(Contact contact) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("contact", contact);
        startActivityForResult(intent, 2);
    }

    private void importContacts() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    private void exportContacts() {
        StringBuilder vcf = new StringBuilder();
        for (Contact contact : contactList) {
            vcf.append("BEGIN:VCARD\nVERSION:3.0\nN:")
                .append(contact.getSurname() != null ? contact.getSurname() : "").append(";")
                .append(contact.getName()).append(";;;\n");
            for (Contact.Phone phone : contact.getPhones()) {
                vcf.append("TEL;TYPE=").append(phone.getType()).append(":").append(phone.getNumber()).append("\n");
            }
            for (Contact.Email email : contact.getEmails()) {
                vcf.append("EMAIL;TYPE=").append(email.getType()).append(":").append(email.getAddress()).append("\n");
            }
            vcf.append("END:VCARD\n");
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/x-vcard");
        intent.putExtra(Intent.EXTRA_TITLE, "contactos.vcf");
        intent.putExtra(Intent.EXTRA_TEXT, vcf.toString());
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {  // Import VCF
                try {
                    Uri uri = data.getData();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
                    StringBuilder vcf = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        vcf.append(line).append("\n");
                    }
                    reader.close();
                    Log.d("Contactos", "VCF Content: " + vcf.toString());
                    parseVcf(vcf.toString());
                } catch (Exception e) {
                    Toast.makeText(this, "Error importing contacts: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Contactos", "Import error", e);
                }
            } else if (requestCode == 2) {  // ContactDetailActivity
                if (data.getBooleanExtra("deleted", false) || data.getBooleanExtra("updated", false)) {
                    updateContactList();
                }
            } else if (requestCode == 3) {  // AddContactActivity
                if (data.getBooleanExtra("added", false)) {
                    updateContactList();
                }
            }
        }
    }

    private void parseVcf(String vcf) {
        String[] cards = vcf.split("END:VCARD");
        int importedCount = 0;
        for (String card : cards) {
            if (card.trim().startsWith("BEGIN:VCARD")) {
                String name = "", surname = "";
                List<Contact.Phone> phones = new ArrayList<>();
                List<Contact.Email> emails = new ArrayList<>();
                String[] lines = card.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    Log.d("Contactos", "Parsing line: " + line);
                    if (line.startsWith("N:")) {
                        String[] parts = line.substring(2).split(";");
                        surname = parts.length > 0 ? parts[0].trim() : "";
                        name = parts.length > 1 ? parts[1].trim() : "";
                    } else if (line.startsWith("TEL")) {
                        String[] telParts = line.split(":");
                        if (telParts.length > 1) {
                            String phone = telParts[1].trim();
                            String phoneType = "Mobile";
                            if (line.contains("TYPE=")) {
                                phoneType = line.split("TYPE=")[1].split("[;:]")[0].trim();
                            }
                            phones.add(new Contact.Phone(phone, phoneType));
                        }
                    } else if (line.startsWith("EMAIL")) {
                        String[] emailParts = line.split(":");
                        if (emailParts.length > 1) {
                            String email = emailParts[1].trim();
                            String emailType = "Personal";
                            if (line.contains("TYPE=")) {
                                emailType = line.split("TYPE=")[1].split("[;:]")[0].trim();
                            }
                            emails.add(new Contact.Email(email, emailType));
                        }
                    }
                }
                if (!phones.isEmpty()) {
                    if (name.isEmpty()) name = "Unknown";
                    Contact contact = new Contact(0, name, surname, phones, emails, null, null, null, null);
                    db.addContact(contact);
                    Log.d("Contactos", "Imported: " + name + " " + surname);
                    importedCount++;
                }
            }
        }
        updateContactList();
        Toast.makeText(this, "Imported " + importedCount + " contacts", Toast.LENGTH_SHORT).show();
    }
}
