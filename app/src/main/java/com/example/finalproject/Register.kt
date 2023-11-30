package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Register : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHandler(this)

        /*CREATE ACCOUNT*/
        val createAccountButton: Button = findViewById(R.id.btnCreateAccount)
        createAccountButton.setOnClickListener {

            val firstname = findViewById<EditText>(R.id.etFirstName).text.toString()
            val lastname = findViewById<EditText>(R.id.etLastName).text.toString()
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            // Validate if required fields are not empty
            if (firstname.isNotEmpty() && lastname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Check if the email is not already registered
                val existingAccount = dbHelper.getAccountByEmail(email)
                if (existingAccount == null) {
                    // Create an AccountData object and add it to the database
                    val account = AccountData(firstname, lastname, email, password)
                    val success = dbHelper.addAccount(account)

                    if (success != -1L) {
                        Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error Creating Account", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        val loginTextView: TextView = findViewById(R.id.tvLogin)
        loginTextView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}