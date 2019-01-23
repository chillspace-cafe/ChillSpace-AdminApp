package chillspace.chillspacecafeadminapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chillspace.chillspacecafeadminapp.R
import chillspace.chillspacecafeadminapp.models.CurrentTransactionClientSide
import chillspace.chillspacecafeadminapp.views.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class CurrentTransactionRecyclerAdapter(val listTransaction: ArrayList<CurrentTransactionClientSide>) : RecyclerView.Adapter<CurrentTransactionRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.currenttransactionrow, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaction.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaction = listTransaction.get(position)

        //Millisecond to DateTime
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = transaction.startTime_in_milliSec!!.toLong()
        val dateformat = SimpleDateFormat("dd/MM/yy")
        val timeformat = SimpleDateFormat("HH:mm")
        val date = dateformat.format(cal.time)
        val time = timeformat.format(cal.time)

        holder.timeTextView.text = time
        holder.dateTextView.text = date
        val user = MainActivity.userMap[transaction.uid]
        holder.nameTextView.text = "Name : "+ user?.name
        holder.usernameTextView.text = "Username : " + user?.username
    }


    class MyViewHolder(val itemview: View) : RecyclerView.ViewHolder(itemview) {
        val dateTextView = itemview.findViewById<TextView>(R.id.date_transaction_row)
        val timeTextView = itemview.findViewById<TextView>(R.id.time_transaction_row)
        val nameTextView = itemview.findViewById<TextView>(R.id.name_transaction_row)
        val usernameTextView = itemView.findViewById<TextView>(R.id.username_transaction_row)
    }
}