package com.example.finalproject

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class UserAdapter(val c:Context,val userList:ArrayList<UserData>):RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{



    inner class UserViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView
        var mbNum: TextView
        var dateBorrowed: TextView
        var datePayment: TextView
        var mMenus: ImageView

        init {
            name = v.findViewById<TextView>(R.id.mTitle)
            mbNum = v.findViewById<TextView>(R.id.mSubTitle)
            dateBorrowed = v.findViewById<TextView>(R.id.mDateBorrowed)
            datePayment = v.findViewById<TextView>(R.id.mDatePayment)
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
        }

        private fun showDeleteConfirmationDialog(userData: UserData) {
            AlertDialog.Builder(c)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Are you sure you want to delete this information?")
                .setPositiveButton("Yes") { dialog, _ ->
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
    }

    override fun getItemCount(): Int {
        return  userList.size
    }
}