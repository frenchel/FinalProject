package com.example.finalproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText

class Profile : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHandler
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHandler(this)

        /*NAVIGATION: BACK TO HOME*/
        val toHomeActivity = findViewById<ImageView>(R.id.backToHome)
        toHomeActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /*NAVIGATION: SIGN OUT*/
        val signOut = findViewById<Button>(R.id.btnSignOut)
        signOut.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        /*CHANGE PASSWORD*/
        val changePassword = findViewById<ConstraintLayout>(R.id.constraintChangePassword)
        changePassword.setOnClickListener{
            showChangePasswordDialog()
        }

        setAccountDetailsToViews()
    }

    private fun setAccountDetailsToViews() {
        // Find TextInputEditText views by their IDs
        val etAccountName = findViewById<TextInputEditText>(R.id.etAccountName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        // Get logged-in user's email
        val loggedInUserEmail = dbHelper.getLoggedInUserEmail()

        // Retrieve account details from the database
        if (loggedInUserEmail != null) {
            val loggedInUser = dbHelper.getAccountByEmail(loggedInUserEmail)

            if (loggedInUser != null) {
                // Set values to TextInputEditText views
                etAccountName.setText("${loggedInUser.firstname} ${loggedInUser.lastname}")
                etEmail.setText(loggedInUser.email)
                etPassword.setText(loggedInUser.password)
            } else {
                // Handle the case when the user is not found
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case when no user is logged in
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showChangePasswordDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_new_password, null)

        val etNewPassword = view.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmNewPassword = view.findViewById<TextInputEditText>(R.id.etConfirmNewPassword)
        val btnSaveChangePassword = view.findViewById<Button>(R.id.saveNewPassword)
        val btnCancelChangePassword = view.findViewById<Button>(R.id.cancelNewPassword)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(view)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        btnSaveChangePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()

            val passwordRequirements = validatePasswordRequirements(newPassword)

            if (passwordRequirements == null) {
                if (newPassword.isNotEmpty() && newPassword == confirmNewPassword) {
                    // Update the password in the database
                    val loggedInUserEmail = dbHelper.getLoggedInUserEmail()

                    if (loggedInUserEmail != null) {
                        val loggedInUser = dbHelper.getAccountByEmail(loggedInUserEmail)

                        if (loggedInUser != null) {
                            // Update the password
                            loggedInUser.password = newPassword
                            // Update the password in the database
                            dbHelper.updatePassword(loggedInUser.email, newPassword)

                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                            val intent = Intent(this, Login::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Display the specific password requirement that is not met
                Toast.makeText(this, passwordRequirements, Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelChangePassword.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
