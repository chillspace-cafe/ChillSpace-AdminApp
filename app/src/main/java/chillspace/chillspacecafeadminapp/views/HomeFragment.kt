package chillspace.chillspacecafeadminapp.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import chillspace.chillspacecafeadminapp.R
import chillspace.chillspacecafeadminapp.models.CurrentTransaction
import chillspace.chillspacecafeadminapp.viewmodels.OTP_List_ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import chillspace.chillspacecafeadminapp.models.CompletedTransaction
import chillspace.chillspacecafeadminapp.models.GeneratedOTP

class HomeFragment : Fragment() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    var otpList = ArrayList<Int>()
    val PER_MINUTE_COST : Int = 1

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
            if(it!=null)
                otpList = it
        })



        btn_verify_otp.setOnClickListener {

            val otp = edit_otp_home.text.toString()

            dbRef.child("GeneratedOTPs").child(otp)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(activity,"No OTP found.",Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val generatedOTP = dataSnapshot.getValue(GeneratedOTP::class.java)

                    if(!generatedOTP?.isRunning!!){
                        val currTransac = CurrentTransaction(true,generatedOTP.uid,generatedOTP.startTime)

                        dbRef.child("CurrentTransactions").child(generatedOTP.uid!!).setValue(currTransac)
                    }else{
                        val playTimeInMinutes : Int  = (generatedOTP.playTime!! /60000).toInt()
                        val cost = playTimeInMinutes*PER_MINUTE_COST
                        val completedTransaction = CompletedTransaction(generatedOTP.uid,generatedOTP.startTime,playTimeInMinutes,cost)

                        dbRef.child("CompletedTransactions").child(generatedOTP.uid!!).push().setValue(completedTransaction).addOnSuccessListener {
                            dbRef.child("CurrentTransactions").child(generatedOTP.uid!!).removeValue()
                        }
                        txt_Payment.text = cost.toString()
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
}
