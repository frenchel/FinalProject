package com.example.finalproject

import android.annotation.SuppressLint
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

class Profile : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
    }

    private fun showChangePasswordDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_new_password, null)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        dialogBuilder.setView(view)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val btnSaveChangePassword = view.findViewById<Button>(R.id.saveNewPassword)
        val btnCancelChangePassword = view.findViewById<Button>(R.id.cancelNewPassword)

        btnSaveChangePassword.setOnClickListener {

        }

        btnCancelChangePassword.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}