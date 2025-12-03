A Notes 📝

A Notes is a modern, feature-rich, and elegant note-taking application for Android built using Kotlin and Jetpack Compose. It follows Clean Architecture principles and the MVVM pattern to ensure scalability, testability, and maintainability.

The app provides a seamless user experience with smooth animations, drag-and-drop reordering, folder management, and robust backup capabilities.

✨ Features

📝 Create & Edit Notes: Rich text editing experience with customizable text sizes.

🎨 Color Coding: Organize your thoughts by assigning different colors to notes.

Staggered Grid Layout: Beautiful, responsive layout similar to Google Keep.

🔄 Drag & Drop Reordering: Long-press and drag to rearrange your notes exactly how you want them.

Folders / Labels: Create custom folders to categorize your notes. Move notes between folders easily.

📌 Pin Notes: Keep important information at the top of your list.

📦 Archive & Trash: Archive notes to keep your workspace clean, or delete them to the trash (with permanent deletion options).

🔍 Search: Instantly find notes by title or content.

💾 Backup & Restore: Export your data to a JSON file and import it back on any device.

🌓 Dark/Light Theme: Fully supported dynamic theming that persists across sessions.

⚡ Smooth Animations: Custom navigation transitions for a fluid feel.

🛠️ Tech Stack

Kotlin: 100% Kotlin.

Jetpack Compose: Modern toolkit for building native UI.

Material Design 3: Latest Android design system.

Hilt: Dependency Injection.

Room Database: Local data persistence using SQLite.

Coroutines & Flow: Asynchronous programming and reactive streams.

Gson: JSON serialization/deserialization for backups.

Reorderable: Library for drag-and-drop interactions in Compose grids.

🏗️ Architecture

This project follows Clean Architecture with MVVM (Model-View-ViewModel):

Domain Layer: Contains the business logic (Use Cases) and models. It is independent of any Android framework.

Models: Note, Folder

Repository Interfaces: NoteRepository

Use Cases: GetNotesUseCase, DeleteFolderUseCase, etc.

Data Layer: Handles data retrieval and storage.

Local: Room Database (NoteDao, FolderDao), Entities.

Repository Implementation: NoteRepositoryImpl.

Presentation Layer: UI and State management.

ViewModels: NotesListViewModel, FolderNotesViewModel.

UI: Composable screens (NotesListScreen, NoteDetailScreen).

📸 Screenshots

Light Mode

Dark Mode

Note Detail

Navigation Drawer

<!-- Add Screenshot -->

<!-- Add Screenshot -->

<!-- Add Screenshot -->

<!-- Add Screenshot -->



🚀 Getting Started

Clone the repository:

git clone [https://github.com/yourusername/a-notes.git](https://github.com/yourusername/a-notes.git)


Open in Android Studio:
Open the project using the latest version of Android Studio (Koala or later recommended).

Sync Gradle:
Allow Gradle to download dependencies.

Run the App:
Connect a device or start an emulator and hit Run.

🤝 Contributing

Contributions are welcome! If you have ideas for new features or bug fixes:

Fork the repository.

Create a new branch (git checkout -b feature/AmazingFeature).

Commit your changes (git commit -m 'Add some AmazingFeature').

Push to the branch (git push origin feature/AmazingFeature).

Open a Pull Request.

📄 License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.
