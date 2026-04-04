package com.example.keuangankita.repository

import android.content.ContentValues
import android.content.Context
import com.example.keuangankita.database.DatabaseContract.UserEntry
import com.example.keuangankita.database.DatabaseHelper
import com.example.keuangankita.model.User

class UserRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Registrasi user baru
    fun register(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserEntry.COLUMN_USERNAME, user.username)
            put(UserEntry.COLUMN_PASSWORD, user.password)
        }
        val result = db.insert(UserEntry.TABLE_NAME, null, values)
        db.close()
        return result
    }

    // Login: cek username & password
    fun login(username: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            UserEntry.TABLE_NAME,
            null,
            "${UserEntry.COLUMN_USERNAME} = ? AND ${UserEntry.COLUMN_PASSWORD} = ?",
            arrayOf(username, password),
            null, null, null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_PASSWORD))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // Cek apakah username sudah ada
    fun isUsernameExist(username: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            UserEntry.TABLE_NAME,
            null,
            "${UserEntry.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )
        val exist = cursor.count > 0
        cursor.close()
        db.close()
        return exist
    }
}
