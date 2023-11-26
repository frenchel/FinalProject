package com.example.finalproject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var totalAmountTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*BUTTON FOR GOING TO PROFILE ACTIVITY*/
        val toProfileActivity = findViewById<ImageView>(R.id.userProfilePic)
        toProfileActivity.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        /*BUTTON FOR GOING TO ARCHIVE ACTIVITY*/
        val archiveImageView = findViewById<TextView>(R.id.ic_archive)
        archiveImageView.setOnClickListener {
            val intent = Intent(this@MainActivity, Archive::class.java)
            startActivity(intent)
        }

        /*SET LIST*/
        userList = ArrayList()
        dbHelper = DatabaseHandler(this)

        userList.addAll(dbHelper.viewNonArchivedTransaction())

        addsBtn = findViewById(R.id.addingBtn) //FINDING ID OF THE SET
        recv = findViewById(R.id.mRecycler)

        totalAmountTextView = findViewById(R.id.overviewTotalAmount)

        userAdapter = UserAdapter(this, dbHelper, userList, false) { updateTotalAmount() } //ADAPTER

        recv.layoutManager = LinearLayoutManager(this) //ADAPTER OF RECYCLER VIEW
        recv.adapter = userAdapter

        addsBtn.setOnClickListener { showNewDebtDialog() } //DIALOG

        updateTotalAmount()
        val totalTransactionsTextView = findViewById<TextView>(R.id.overviewTotalTransactions)
        totalTransactionsTextView.text = "${userList.size}"
        userAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                totalTransactionsTextView.text = "${userList.size}"
                updateTotalAmount()
            }
        })
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

        /*SAVE AND CANCEL BUTTON IN ADD ITEM DIALOG*/
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

            if (names.isNotEmpty() && number.isNotEmpty() && date?.isNotEmpty() == true && dueDate?.isNotEmpty() == true) {
                val userId = 0

                val userData = UserData(userId, names, number, date ?: "", dueDate ?: "")

                val success = dbHelper.addTransaction(userData)

                if (success != -1L) {
                    userData.userId = success.toInt()
                    userList.add(userData)
                    userAdapter.notifyDataSetChanged()
                    updateTotalAmount()
                    Toast.makeText(this, "Adding User Information Success", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error Adding User Information", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show required fields dialog for 2 seconds
                showRequiredFieldsDialog()

            }
        }

        btnCancelDebt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateTotalAmount() {
        val totalAmount = userList.sumByDouble { it.userMb.toDoubleOrNull() ?: 0.0 }
        totalAmountTextView.text = "₱ ${String.format("%.2f", totalAmount)}"
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

    /*WARNING DIALOG*/
    private fun showRequiredFieldsDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_fields_required, null)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(view)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Optional: To make corners round
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Optional: To remove the title

        dialog.show()

        Handler().postDelayed({
            dialog.dismiss()
        }, 1500)
    }
}