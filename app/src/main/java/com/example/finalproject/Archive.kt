package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Archive : AppCompatActivity() {
    private lateinit var mRecyclerArchive: RecyclerView
    private lateinit var archivedList: ArrayList<UserData>
    private lateinit var archiveAdapter: UserAdapter
    private lateinit var dbHelper: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        val toHome = findViewById<ImageView>(R.id.backToHome)
        mRecyclerArchive = findViewById(R.id.mRecyclerArchive) // Make sure the ID matches your layout

        toHome.setOnClickListener {
            val intent = Intent(this@Archive, MainActivity::class.java)
            startActivity(intent)
        }

        // Initialize database and archived records list
        dbHelper = DatabaseHandler(this)
        archivedList = dbHelper.viewArchivedTransaction()

        // Initialize and set up the adapter for the archived records
        archiveAdapter = UserAdapter(this, dbHelper, archivedList) { /* Any additional callbacks if needed */ }
        mRecyclerArchive.layoutManager = LinearLayoutManager(this)
        mRecyclerArchive.adapter = archiveAdapter
    }
}