package chillspace.chillspacecafeadminapp.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import chillspace.chillspacecafeadminapp.R
import chillspace.chillspacecafeadminapp.models.CurrentTransactionAdminSide
import chillspace.chillspacecafeadminapp.viewmodels.OTP_List_ViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import chillspace.chillspacecafeadminapp.interfaces.CallbackInterface
import chillspace.chillspacecafeadminapp.models.CompletedTransaction
import chillspace.chillspacecafeadminapp.models.GeneratedOTP
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    var otpList = ArrayList<Int>()

    val COST_PER_QUARTERHOUR: Int = 15
    val COST_PER_HALFHOUR: Int = 20
    val COST_PER_HOUR: Int = 40

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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

            dbRef.child("GeneratedOTPs").child(otp)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(activity, "No OTP found.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val generatedOTP = dataSnapshot.getValue(GeneratedOTP::class.java)

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
                                        txt_Payment.text = cost.toString()
                                    }
                                })
                            }

                            //removing otp from db
                            dbRef.child("GeneratedOTPs").child(edit_otp_home.text.toString()).removeValue()
                            otpList.remove(Integer.parseInt(otp))
                            dbRef.child("OTP_List").setValue(otpList)
                            edit_otp_home.setText("")
                        }
                    })
        }
    }

    private fun calculateTimeDetails(uid: String?, callbackInterface: CallbackInterface<Array<Long>>) {
        dbRef.child("Current").child("CurrentTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            dbRef.child("Current").addListenerForSingleValueEvent(object : ValueEventListener{

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
}
