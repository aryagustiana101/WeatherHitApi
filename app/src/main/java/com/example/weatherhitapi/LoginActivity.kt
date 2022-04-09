package com.example.weatherhitapi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import java.time.LocalDateTime

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class LoginActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val etEmail: String = findViewById<EditText>(R.id.etEmail).text.toString()
            val etPassword: String = findViewById<EditText>(R.id.etPassword).text.toString()

            val timeNow = LocalDateTime.now()

            if (etEmail.isEmpty()) {
                Toast.makeText(this, "Email Is Required!", Toast.LENGTH_SHORT).show()
            } else {
                if (!isEmailValid(etEmail)) {
                    Toast.makeText(this, "Email Is Not Valid!", Toast.LENGTH_SHORT).show()
                } else {
                    if (etPassword.isEmpty()) {
                        Toast.makeText(this, "Password Is Required!", Toast.LENGTH_SHORT).show()
                    } else {
                        PostRequestLogin(this, this, etEmail, etPassword).execute()
                        Log.i("resultLogin", timeNow.toString())
                    }
                }
            }
        }

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val tvBack = findViewById<TextView>(R.id.tvBack)
        tvBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}