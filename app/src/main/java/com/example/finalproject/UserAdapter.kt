package com.example.finalproject

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.util.Date
import java.util.Locale

class UserAdapter(val c:Context,val dbHelper: DatabaseHandler,val userList:ArrayList<UserData>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{

    inner class UserViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView
        var mbNum: TextView
        var dateBorrowed: TextView
        var datePayment: TextView
        lateinit var tvOverdue: TextView
        var mMenus: ImageView

        init {
            name = v.findViewById<TextView>(R.id.mTitle)
            mbNum = v.findViewById<TextView>(R.id.mSubTitle)
            dateBorrowed = v.findViewById<TextView>(R.id.mDateBorrowed)
            datePayment = v.findViewById<TextView>(R.id.mDatePayment)
            tvOverdue = v.findViewById(R.id.tvOverdue)
            mMenus = v.findViewById(R.id.mMenus)
            mMenus.setOnClickListener { popupMenus() }

        }

        private fun popupMenus() {
            val position = userList[adapterPosition]

            val popupMenus = PopupMenu(c, mMenus)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editText -> {
                        showEditDialog(position)
                        true
                    }
                    R.id.paid -> {

                        true
                    }
                    R.id.delete -> {
                        showDeleteConfirmationDialog(position)
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }

        private fun showEditDialog(userData: UserData) {
            val v = LayoutInflater.from(c).inflate(R.layout.add_item, null)
            val name = v.findViewById<EditText>(R.id.userName)
            val number = v.findViewById<EditText>(R.id.userNoAmount)
            val date = v.findViewById<TextInputEditText>(R.id.et_date)
            val dueDate = v.findViewById<TextInputEditText>(R.id.et_dueDate)

            // Set the values from UserData to the dialog
            name.setText(userData.userName)
            number.setText(userData.userMb)
            date.setText(userData.dateBorrowed)
            dueDate.setText(userData.datePayment)

            /*SHOWING DATE PICKER*/
            date.setOnClickListener {
                showDatePicker(date)
            }

            dueDate.setOnClickListener {
                showDatePicker(dueDate)
            }

            val saveButton = v.findViewById<Button>(R.id.saveAddDebt)
            val cancelButton = v.findViewById<Button>(R.id.cancelAddDebt)

            val dialog = AlertDialog.Builder(c)
                .setView(v)
                .create()

            saveButton.setOnClickListener {
                val newName = name.text.toString()
                val newNumber = number.text.toString()
                val newDate = date.text.toString()
                val newDueDate = dueDate.text.toString()

                // Update the original UserData
                userData.userName = newName
                userData.userMb = newNumber
                userData.dateBorrowed = newDate
                userData.datePayment = newDueDate

                dbHelper.updateTransaction(userData)

                notifyDataSetChanged()
                Toast.makeText(c, "User Information is Edited", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        /*private fun showEditDialog(userData: UserData) {
            val v = LayoutInflater.from(c).inflate(R.layout.add_item, null)
            val name = v.findViewById<EditText>(R.id.userName)
            val number = v.findViewById<EditText>(R.id.userNoAmount)
            val date = v.findViewById<TextInputEditText>(R.id.et_date)
            val dueDate = v.findViewById<TextInputEditText>(R.id.et_dueDate)

            // Set the values from UserData to the dialog
            name.setText(userData.userName)
            number.setText(userData.userMb)
            date.setText(userData.dateBorrowed)
            dueDate.setText(userData.datePayment)

            AlertDialog.Builder(c)
                .setView(v)
                .setPositiveButton("Ok") { dialog, _ ->
                    val newName = name.text.toString()
                    val newNumber = number.text.toString()
                    val newDate = date.text.toString()
                    val newDueDate = dueDate.text.toString()

                    // Update the original UserData
                    userData.userName = newName
                    userData.userMb = newNumber
                    userData.dateBorrowed = newDate
                    userData.datePayment = newDueDate

                    notifyDataSetChanged()
                    Toast.makeText(c, "User Information is Edited", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }*/

        private fun showDeleteConfirmationDialog(userData: UserData) {
            AlertDialog.Builder(c)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Are you sure you want to delete this information?")
                .setPositiveButton("Yes") { dialog, _ ->

                    dbHelper.deleteTransaction(userData.userId)
                    userList.remove(userData)
                    notifyDataSetChanged()
                    Toast.makeText(c, "Deleted this Information", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item,parent,false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = userList[position]
        holder.name.text = newList.userName
        holder.mbNum.text = newList.userMb
        holder.dateBorrowed.text = newList.dateBorrowed
        holder.datePayment.text = newList.datePayment

        // Compare current date with the due date
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMM. dd, yyyy", Locale.getDefault())
        val dueDate = dateFormat.parse(newList.datePayment)

        if (currentDate.after(dueDate)) {
            holder.tvOverdue.text = "OVERDUE"
            // You can customize the appearance or perform additional actions for overdue items
        } else {
            holder.tvOverdue.text = ""
        }
    }

    override fun getItemCount(): Int {
        return  userList.size
    }

    /*DATE PICKER*/
    private fun showDatePicker(etDate: TextInputEditText) {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            c,  // Use the 'c' parameter as the Context
            R.style.datePicker,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = java.util.Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = java.text.SimpleDateFormat("MMM. dd, yyyy", Locale.getDefault())
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