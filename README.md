# Finance Tracker (KeuanganKita)

A simple yet comprehensive Android finance tracker application for recording, managing, and monitoring personal finances. The app runs entirely locally on your device.

## About the Application

**Finance Tracker** (internal name: **KeuanganKita**) is an Android-based mobile application designed to help users manage their personal finances with greater discipline. The application allows users to record income and expense transactions, categorize them, visualize financial data through charts, and generate monthly reports.

## Key Features

- Real-time recording of income and expense transactions
- Categorization of transactions
- Data visualization through charts and reports
- Monthly budget management
- Summary of balance and savings progress
- Simple and user-friendly interface

## Technologies Used

- Kotlin
- Android Studio
- SQLite (SQLiteOpenHelper)
- RecyclerView
- Material FloatingActionButton
- Custom Canvas Chart (without external libraries)
- Repository Pattern
- SharedPreferences (for session management)

## Project Folder Structure

This repository contains the contents of the `app/src/main` folder from the original Android Studio project. The folder structure is shown below:

<img width="841" height="432" alt="Screenshot_20260404_231622" src="https://github.com/user-attachments/assets/f602205e-6643-43c4-a8af-134b6c1b0655" />

> **Note:** This repository only includes the contents of the `app/src/main` folder from the original Android Studio project. To run the complete project, it is recommended to open it in Android Studio after cloning.

## Setup Instructions

- Open Android Studio and create a new project. Select **Empty Views Activity** and name the project **KeuanganKita**.
- Open the terminal in Android Studio and run the following command:
  ```bash
  git clone https://github.com/mariq-del-negativity/Finance-Tracker.git
Using your file manager, locate the cloned folder.
Replace the entire contents of the ~/AndroidStudioProjects/KeuanganKita/app/src/main/ folder with the contents from the cloned Finance Tracker repository.
Return to Android Studio, sync Gradle, and run the app.
