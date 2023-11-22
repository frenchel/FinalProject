package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Archive : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        val toHome = findViewById<ImageView>(R.id.backToHome)

        toHome.setOnClickListener {
            val intent = Intent(this@Archive, MainActivity::class.java)
            startActivity(intent)
        }
    }
}