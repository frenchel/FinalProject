package com.example.finalproject

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<UserData>
    private lateinit var userAdapter: UserAdapter
    private lateinit var dbHelper: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*SET LIST*/
        userList = ArrayList()
        dbHelper = DatabaseHandler(this)

        userList.addAll(dbHelper.viewTransaction())

        addsBtn = findViewById(R.id.addingBtn) //FINDING ID OF THE SET
        recv = findViewById(R.id.mRecycler)

        userAdapter = UserAdapter(this, dbHelper, userList) //ADAPTER

        recv.layoutManager = LinearLayoutManager(this) //ADAPTER OF RECYCLER VIEW
        recv.adapter = userAdapter

        addsBtn.setOnClickListener { showNewDebtDialog() } //DIALOG

        /*BUTTON FOR GOING TO ARCHIVE ACTIVITY*/
        val archiveImageView = findViewById<ImageView>(R.id.ic_archive)
        archiveImageView.setOnClickListener {
            val intent = Intent(this@MainActivity, Archive::class.java)
            startActivity(intent)
        }
    }

    /*PLUS BUTTON FOR ADDING NEW DEBT RECORD*/
    private fun showNewDebtDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item, null)

        val userName = view.findViewById<EditText>(R.id.userName)
        val userNo = view.findViewById<EditText>(R.id.userNoAmount)
        val etDate = view.findViewById<TextInputEditText>(R.id.et_date)
        val etDueDate = view.findViewById<TextInputEditText>(R.id.et_dueDate)

        /*SHOWING DATE PICKER*/
        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        etDueDate.setOnClickListener {
            showDatePicker(etDueDate)
        }

        /*SAVE AND CANCEL BUTTON IN THE DIALOG*/
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

            val userId = 0

            val userData = UserData(userId, names, number, date ?: "", dueDate ?: "")

            val success = dbHelper.addTransaction(userData)

            if (success != -1L) {
                userData.userId = success.toInt()
                userList.add(
                    UserData(
                        0,
                        names,
                        String.format("₱%.2f", number.toDouble()),
                        date ?: "",
                        dueDate ?: ""
                    )
                )
                userAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Adding User Information Success", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Error Adding User Information", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelDebt.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun viewTransactions(): ArrayList<UserData> {
        val transactions = dbHelper.viewTransaction()
        userList.clear()
        userList.addAll(transactions)
        return transactions
    }

    /*DATE PICKER*/
    private fun showDatePicker(etDate: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.datePicker,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("MMM. dd, yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                etDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
        datePickerDialog.show()
    }
}



    /*private fun showDatePicker(etDate: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(this, R.style.datePicker),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                etDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }*/

    /*private fun showDatePicker(etDate: TextInputEditText) {
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
    }*/



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
