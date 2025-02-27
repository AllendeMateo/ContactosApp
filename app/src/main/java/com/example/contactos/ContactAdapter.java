package com.example.contactos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private Context context;
    private OnContactClickListener listener;

    public interface OnContactClickListener {
        void onViewClick(Contact contact);
    }

    public ContactAdapter(Context context, List<Contact> contacts, OnContactClickListener listener) {
        this.context = context;
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.getName() + " " + (contact.getSurname() != null ? contact.getSurname() : ""));
        if (!contact.getPhones().isEmpty()) {
            holder.phoneTextView.setText(contact.getPhones().get(0).getNumber() + " (" + contact.getPhones().get(0).getType() + ")");
        } else {
            holder.phoneTextView.setText("");
        }
        if (!contact.getEmails().isEmpty()) {
            holder.emailTextView.setText(contact.getEmails().get(0).getAddress() + " (" + contact.getEmails().get(0).getType() + ")");
            holder.emailTextView.setVisibility(View.VISIBLE);
        } else {
            holder.emailTextView.setVisibility(View.GONE);
        }

        holder.iconView.setImageDrawable(getContactIcon(contact));
        holder.itemView.setOnClickListener(v -> listener.onViewClick(contact));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView, emailTextView;
        ImageView iconView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            emailTextView = itemView.findViewById(R.id.contact_email);
            iconView = itemView.findViewById(R.id.contact_icon);
        }
    }

    public void updateContacts(List<Contact> newContacts) {
        this.contacts = newContacts;
        notifyDataSetChanged();
    }

    private Drawable getContactIcon(Contact contact) {
        if (contact.getPhotoPath() != null && !contact.getPhotoPath().isEmpty()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(contact.getPhotoPath());
                if (bitmap != null) {
                    return new BitmapDrawable(context.getResources(), bitmap);
                }
            } catch (Exception e) {
                Log.e("Contactos", "Error loading photo: " + contact.getPhotoPath(), e);
            }
        }
        String firstLetter = contact.getName().substring(0, 1).toUpperCase();
        int size = 48; // dp
        float density = context.getResources().getDisplayMetrics().density;
        int pixelSize = (int) (size * density);

        Bitmap bitmap = Bitmap.createBitmap(pixelSize, pixelSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Random random = new Random(contact.getName().hashCode());
        int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        paint.setColor(color);
        canvas.drawCircle(pixelSize / 2f, pixelSize / 2f, pixelSize / 2f, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(24 * density);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float x = pixelSize / 2f;
        float y = pixelSize / 2f - (fm.ascent + fm.descent) / 2;
        canvas.drawText(firstLetter, x, y, paint);

        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
