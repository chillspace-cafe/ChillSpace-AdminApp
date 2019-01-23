package chillspace.chillspacecafeadminapp.views


import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import chillspace.chillspacecafeadminapp.R
import chillspace.chillspacecafeadminapp.models.CurrentTransactionAdminSide
import chillspace.chillspacecafeadminapp.viewmodels.OTP_List_ViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import chillspace.chillspacecafeadminapp.interfaces.CallbackInterface
import chillspace.chillspacecafeadminapp.models.CompletedTransaction
import chillspace.chillspacecafeadminapp.models.GeneratedOTP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    var otpList = ArrayList<Int>()

    val COST_PER_QUARTERHOUR: Int = 15
    val COST_PER_HALFHOUR: Int = 20
    val COST_PER_HOUR: Int = 40

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //allowing menu in toolbar
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otpListViewModel = ViewModelProviders.of(this).get(OTP_List_ViewModel::class.java)
        val otpListLiveData = otpListViewModel.getOTPListLiveData()

        otpListLiveData.observe(this, Observer {
            if (it != null)
                otpList = it
        })

        btn_verify_otp.setOnClickListener {

            val otp = edit_otp_home.text.toString()

            //handle wrong otp
            if (otp.length != 4) {
                Toast.makeText(activity, "Enter a valid OTP.", Toast.LENGTH_SHORT).show()
            } else {
                dbRef.child("GeneratedOTPs")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Toast.makeText(activity, "No OTP found.", Toast.LENGTH_SHORT).show()
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.hasChild(otp)) {
                                    val snapshot = dataSnapshot.child(otp)

                                    val generatedOTP = snapshot.getValue(GeneratedOTP::class.java)

                                    if (!generatedOTP?.isRunning!!) {
                                        val currTransaction = CurrentTransactionAdminSide(true, generatedOTP.uid, ServerValue.TIMESTAMP)

                                        dbRef.child("Current").child("CurrentTransactions").child(generatedOTP.uid!!).setValue(currTransaction)
                                    } else {
                                        calculateTimeDetails(generatedOTP.uid, object : CallbackInterface<Array<Long>> {
                                            override fun callback(data: Array<Long>) {
                                                val playTimeInMinutes: Int = ((data[1] - data[0]) / 60000).toInt()
                                                val cost = costCalculator(playTimeInMinutes)
                                                val completedTransaction = CompletedTransaction(generatedOTP.uid, data[0], playTimeInMinutes, cost)

                                                dbRef.child("CompletedTransactions").child(generatedOTP.uid!!).push().setValue(completedTransaction).addOnSuccessListener {
                                                    dbRef.child("Current").child("CurrentTransactions").child(generatedOTP.uid!!).child("active").setValue(false)
                                                }
                                                txt_Payment.text = "Payable amount Rs. " + cost.toString()
                                            }
                                        })
                                    }

                                    //removing otp from db
                                    dbRef.child("GeneratedOTPs").child(edit_otp_home.text.toString()).removeValue()
                                    otpList.remove(Integer.parseInt(otp))
                                    dbRef.child("OTP_List").setValue(otpList)
                                    edit_otp_home.setText("")
                                } else {
                                    Toast.makeText(activity, "Enter a valid OTP.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
            }
        }
    }

    private fun calculateTimeDetails(uid: String?, callbackInterface: CallbackInterface<Array<Long>>) {
        dbRef.child("Current").child("CurrentTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            dbRef.child("Current").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(activity, "Can't access database.", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val timeDetails = arrayOf(0L, 0L)
                    timeDetails[0] = dataSnapshot.child("CurrentTransactions").child(uid!!).child("startTime_in_milliSec").value.toString().toLong()
                    timeDetails[1] = dataSnapshot.child("CurrentTime").value.toString().toLong()
                    callbackInterface.callback(timeDetails)
                }

            })
        }
    }

    private fun costCalculator(playTimeInMinutes: Int): Int {
        var cost: Int = 0

        val hours = playTimeInMinutes / 60
        val halfHours = (playTimeInMinutes - (hours * 60)) / 30
        val quarterHours = (playTimeInMinutes - (hours * 60) - (halfHours * 30)) / 15
        val leftMinutes = (playTimeInMinutes - (hours * 60) - (halfHours * 30) - (quarterHours * 15))

        return if (playTimeInMinutes < 15) {
            cost = 15
            cost
        } else {
            cost = hours * COST_PER_HOUR + halfHours * COST_PER_HALFHOUR + quarterHours * COST_PER_QUARTERHOUR
            when {
                leftMinutes <= 5 -> cost += 0
                leftMinutes > 5 -> cost += 15
            }
            cost
        }
    }

    //creating menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //NOTE : You may use NavigationUI if you want to navigate always to the frag with same id as menu id
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item!!.itemId == R.id.logout_home ->
                FirebaseAuth.getInstance().signOut()
            item.itemId == R.id.dest_current_transactions ->
                Navigation.findNavController(activity as Activity, R.id.nav_host_fragment).navigate(R.id.dest_current_transactions)
        }
        return super.onOptionsItemSelected(item)
    }
}
