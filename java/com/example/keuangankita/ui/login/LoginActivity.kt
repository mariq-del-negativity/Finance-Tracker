package com.example.keuangankita.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.keuangankita.R
import com.example.keuangankita.model.User
import com.example.keuangankita.repository.UserRepository
import com.example.keuangankita.ui.home.HomeActivity
import com.example.keuangankita.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: AppCompatButton
    private lateinit var tvRegister: TextView
    private lateinit var tvTitle: TextView

    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            goToHome()
            return
        }

        setContentView(R.layout.activity_login)
        userRepository = UserRepository(this)

        etUsername  = findViewById(R.id.etUsername)
        etPassword  = findViewById(R.id.etPassword)
        btnLogin    = findViewById(R.id.btnLogin)
        tvRegister  = findViewById(R.id.tvRegister)
        tvTitle     = findViewById(R.id.tvTitle)

        btnLogin.setOnClickListener {
            if (isLoginMode) doLogin() else doRegister()
        }
        tvRegister.setOnClickListener { toggleMode() }
    }

    private fun doLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val user = userRepository.login(username, password)
        if (user != null) {
            sessionManager.saveLoginSession(user.id, user.username)
            goToHome()
        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doRegister() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            return
        }
        if (userRepository.isUsernameExist(username)) {
            Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show()
            return
        }

        val result = userRepository.register(User(username = username, password = password))
        if (result > 0) {
            Toast.makeText(this, "Registrasi berhasil! Silakan login", Toast.LENGTH_SHORT).show()
            toggleMode()
        } else {
            Toast.makeText(this, "Registrasi gagal, coba lagi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleMode() {
        isLoginMode = !isLoginMode
        if (isLoginMode) {
            tvTitle.text    = "Masuk"
            btnLogin.text   = "MASUK"
            tvRegister.text = "Belum punya akun? Daftar"
        } else {
            tvTitle.text    = "Daftar"
            btnLogin.text   = "DAFTAR"
            tvRegister.text = "Sudah punya akun? Masuk"
        }
        etUsername.text.clear()
        etPassword.text.clear()
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
