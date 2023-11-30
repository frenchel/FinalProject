package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
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

                // Check if the email is a valid email address
                if (isValidEmail(email)) {

                    // Check each password requirement separately
                    val passwordRequirements = validatePasswordRequirements(password)

                    if (passwordRequirements == null) {

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
                        Toast.makeText(this, passwordRequirements, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
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

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePasswordRequirements(password: String): String? {
        // Password must be at least 8 characters
        if (password.length < 8) {
            return "Password must be at least 8 characters"
        }

        // Password must include at least one uppercase letter
        if (!password.any { it.isUpperCase() }) {
            return "Password must include at least one uppercase letter"
        }

        // Password must include at least one lowercase letter
        if (!password.any { it.isLowerCase() }) {
            return "Password must include at least one lowercase letter"
        }

        // Password must include at least one digit
        if (!password.any { it.isDigit() }) {
            return "Password must include at least one digit"
        }

        // Password must include at least one special character
        if (!password.any { !it.isLetterOrDigit() }) {
            return "Password must include at least one special character"
        }

        return null  // All password requirements met
    }

}