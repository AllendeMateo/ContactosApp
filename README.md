# ContactosApp

![Contactos Icon](app/src/main/res/drawable/ic_contact_icon.png)

**ContactosApp** is a sleek, powerful, and customizable Android contacts manager designed to be a private and seperate Contacts app. Built from scratch with modern Android development practices, it offers a rich feature set, intuitive UI, and room for endless enhancements. Whether you’re organizing your personal network or managing professional contacts, ContactosApp has you covered!

---

## Features

- **Modern Contact Management**:
  - Add, edit, and delete contacts with ease.
  - Support for multiple phone numbers and emails per contact, with types (e.g., Mobile, Home, Work).
  - Extra fields: address, notes, and company details.

- **Elegant UI**:
  - Full-screen add/edit screens with black text on a white background for readability.
  - AOSP-inspired detail view with call/message buttons for each phone number.
  - Contact icons with photo support or a colorful initial-based fallback.

- **Import/Export**:
  - Import contacts from VCF files with multi-number/email support.
  - Export all contacts to a single VCF file for backup or sharing.

- **Smart Sorting**:
  - Alphabetical sorting by first name, with a menu option to re-sort on demand.

- **Database-Driven**:
  - SQLite backend with separate tables for contacts, phones, and emails, ensuring scalability.
  - Does NOT use Android Contacts Storage.  Uses SEPERATE database for privacy. 

- **Built for Expansion**:
  - Ready for future features like dark mode, favorites, email/maps integration, and more!

---

## Screenshots

*Coming Soon*
- Main Contact List
- Add Contact Screen
- Contact Details

---

## Getting Started

### Prerequisites
- **Android Studio**: Latest version (e.g., Koala | 2024.1.1).
- **Java**: OpenJDK 17 (or compatible JDK).
- **Android Device/Emulator**: API 21+ (Lollipop or higher).

### Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/<your-username>/ContactosApp.git
   cd ContactosApp
2.  Open in Android Studio:
Launch Android Studio and select "Open an existing project."
Navigate to the ContactosApp folder and open it.

3.  Build the Project:
Sync the project with Gradle:
   
./gradlew build

Build and run:

./gradlew installDebug

4.  Run on Device/Emulator:
Connect an Android device via USB (with USB Debugging enabled) or start an emulator.
Click "Run" in Android Studio, or use:
  
adb install app/build/outputs/apk/debug/app-debug.apk

Usage
Add a Contact: Tap the floating action button (+) to open the full-screen add screen. Fill in details and save.
View/Edit: Tap a contact in the list to see details, then hit the edit pencil to modify.
Delete: Use the trash icon in the detail view to remove a contact.
Import/Export: Access via the 3-dot menu—import from a VCF file or export all contacts.
Enhancements in Progress
Thanks to collaboration with Grok (xAI), we’re planning these awesome upgrades:

Photo Picker: Add contact photos from gallery or camera.
Email/Maps Integration: Quick-launch email or maps from contact details.
Dark Theme: Toggleable dark mode for nighttime vibes.
Favorites: Star your top contacts for quick access.
Search: Filter contacts instantly with a search bar.

Contributing
Want to make Contactos even better? Contributions are welcome!

Fork the repo.
Create a branch (git checkout -b feature/awesome-idea).
Commit your changes (git commit -m "Add awesome idea").
Push to your fork (git push origin feature/awesome-idea).
Open a Pull Request.
License
This project is open-source under the GPL-3 License. Feel free to use, modify, and share!

Credits
Built by [Mateo Allende] with invaluable assistance from Grok (xAI)—a coding companion smarter than your average AI!
Inspired by AOSP Contacts, but with additional privacy features. 
