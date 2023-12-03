package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Login : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHandler(this)

        /*LOGIN*/
        val signInButton: Button = findViewById(R.id.btnLogin)
        signInButton.setOnClickListener {

            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            // Validate if required fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Check if the email and password match an existing account
                val account = dbHelper.getAccountByEmailAndPassword(email, password)

                if (account != null) {
                    dbHelper.setLoggedInUserEmail(account.email)
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
                } else {
                    Toast.makeText(this, "Email and Password does not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show()
            }
        }

        val registerTextView: TextView = findViewById(R.id.tvRegister)
        registerTextView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}