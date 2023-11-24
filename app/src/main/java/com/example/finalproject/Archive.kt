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
        mRecyclerArchive = findViewById(R.id.mRecyclerArchive)

        toHome.setOnClickListener {
            val intent = Intent(this@Archive, MainActivity::class.java)
            startActivity(intent)
        }

        dbHelper = DatabaseHandler(this)
        archivedList = dbHelper.viewArchivedTransaction()

        // Use the UserAdapter with isArchiveAdapter parameter set to true
        archiveAdapter = UserAdapter(this, dbHelper, archivedList, true) { /* Any additional callbacks if needed */ }
        mRecyclerArchive.layoutManager = LinearLayoutManager(this)
        mRecyclerArchive.adapter = archiveAdapter
    }
}
