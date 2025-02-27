package com.example.contactos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactDetailActivity extends AppCompatActivity {
    private Contact contact;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contact = (Contact) getIntent().getSerializableExtra("contact");
        db = new DatabaseHelper(this);

        TextView nameText = findViewById(R.id.detail_name);
        LinearLayout phoneContainer = findViewById(R.id.phone_container);
        LinearLayout emailContainer = findViewById(R.id.email_container);
        TextView addressText = findViewById(R.id.detail_address);
        TextView notesText = findViewById(R.id.detail_notes);
        TextView companyText = findViewById(R.id.detail_company);

        nameText.setText(contact.getName() + " " + (contact.getSurname() != null ? contact.getSurname() : ""));

        phoneContainer.removeAllViews();
        for (Contact.Phone phone : contact.getPhones()) {
            View phoneView = getLayoutInflater().inflate(R.layout.phone_detail_field, null);
            TextView phoneText = phoneView.findViewById(R.id.detail_phone);
            ImageView callButton = phoneView.findViewById(R.id.call_button);
            ImageView messageButton = phoneView.findViewById(R.id.message_button);
            phoneText.setText(phone.getNumber() + " (" + phone.getType() + ")");
            callButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone.getNumber()));
                startActivity(intent);
            });
            messageButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + phone.getNumber()));
                startActivity(intent);
            });
            phoneContainer.addView(phoneView);
        }

        emailContainer.removeAllViews();
        for (Contact.Email email : contact.getEmails()) {
            View emailView = getLayoutInflater().inflate(R.layout.email_detail_field, null);
            TextView emailText = emailView.findViewById(R.id.detail_email);
            emailText.setText(email.getAddress() + " (" + email.getType() + ")");
            emailContainer.addView(emailView);
        }

        if (contact.getAddress() != null && !contact.getAddress().isEmpty()) {
            addressText.setText("Address: " + contact.getAddress());
            addressText.setVisibility(View.VISIBLE);
        } else {
            addressText.setVisibility(View.GONE);
        }

        if (contact.getNotes() != null && !contact.getNotes().isEmpty()) {
            notesText.setText("Notes: " + contact.getNotes());
            notesText.setVisibility(View.VISIBLE);
        } else {
            notesText.setVisibility(View.GONE);
        }

        if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
            companyText.setText("Company: " + contact.getCompany());
            companyText.setVisibility(View.VISIBLE);
        } else {
            companyText.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditContactActivity.class);
            intent.putExtra("contact", contact);
            startActivityForResult(intent, 1);
            return true;
        } else if (id == R.id.action_favorite) {
            Toast.makeText(this, "Favorite not implemented yet", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_delete) {
            db.deleteContact(contact.getId());
            Intent result = new Intent();
            result.putExtra("deleted", true);
            setResult(RESULT_OK, result);
            finish();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Contact updatedContact = (Contact) data.getSerializableExtra("updated_contact");
            if (updatedContact != null) {
                contact = updatedContact;
                db.updateContact(contact);
                Intent result = new Intent();
                result.putExtra("updated", true);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }
}
