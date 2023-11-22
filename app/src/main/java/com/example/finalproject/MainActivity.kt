package com.example.finalproject

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<UserData>
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Set List */
        userList = ArrayList()

        /** Set find Id */
        addsBtn = findViewById(R.id.addingBtn)
        recv = findViewById(R.id.mRecycler)

        /** Set Adapter */
        userAdapter = UserAdapter(this, userList)

        /** Set Recycler view Adapter */
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = userAdapter

        /** Set Dialog */
        addsBtn.setOnClickListener { showNewDebtDialog() }
    }

    private fun showNewDebtDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item, null)

        val userName = view.findViewById<EditText>(R.id.userName)
        val userNo = view.findViewById<EditText>(R.id.userNoAmount)
        val etDate = view.findViewById<TextInputEditText>(R.id.et_date)
        val etDueDate = view.findViewById<TextInputEditText>(R.id.et_dueDate)

        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        etDueDate.setOnClickListener {
            showDatePicker(etDueDate)
        }

        val btnAddDebt = view.findViewById<Button>(R.id.saveAddDebt)
        val btnCancelDebt = view.findViewById<Button>(R.id.cancelAddDebt)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(view)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Optional: To make corners round
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Optional: To remove the title

        btnAddDebt.setOnClickListener {
            val names = userName.text.toString()
            val number = userNo.text.toString()
            val date = etDate.text?.toString()
            val dueDate = etDueDate.text?.toString()

            userList.add(UserData("$names", "$number", "$date", "$dueDate"))
            userAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Adding User Information Success", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancelDebt.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showDatePicker(etDate: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update the TextInputEditText with the selected date
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                etDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}


/*
package com.example.finalproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

import com.google.android.material.textfield.TextInputLayout

import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<UserData>
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        */
/**set List*//*

        userList = ArrayList()
        */
/**set find Id*//*

        addsBtn = findViewById(R.id.addingBtn)
        recv = findViewById(R.id.mRecycler)
        */
/**set Adapter*//*

        userAdapter = UserAdapter(this, userList)
        */
/**setRecycler view Adapter*//*

        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = userAdapter
        */
/**set Dialog*//*

        addsBtn.setOnClickListener { addInfo() }

    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_item, null)
        */
/**set view*//*

        val userName = v.findViewById<EditText>(R.id.userName)
        val userNo = v.findViewById<EditText>(R.id.userNoAmount)
        val etDate = v.findViewById<TextInputEditText>(R.id.et_date)
        val etDueDate = v.findViewById<TextInputEditText>(R.id.et_dueDate)

        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        etDueDate.setOnClickListener {
            showDatePicker(etDueDate)
        }

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val names = userName.text.toString()
            val number = userNo.text.toString()
            val date = etDate.text?.toString()
            val dueDate = etDueDate.text?.toString()

            userList.add(UserData("$names", "$number", "$date", "$dueDate"))
            userAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Adding User Information Success", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun showDatePicker(etDate: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update the TextInputEditText with the selected date
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                etDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}
*/
