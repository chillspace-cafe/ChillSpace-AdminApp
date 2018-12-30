package chillspace.chillspacecafeadminapp.viewmodels

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import chillspace.chillspacecafeadminapp.livedata.FirebaseDatabaseLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class OTP_List_ViewModel : ViewModel() {
    private val dbRef = FirebaseDatabase.getInstance().reference.child("OTP_List")

    private val liveData = FirebaseDatabaseLiveData(dbRef as Query)

    private val otpListLiveData = Transformations.map(liveData, Deserializer())

    private class Deserializer : Function<DataSnapshot, ArrayList<Int>> {
        override fun apply(dataSnapshot: DataSnapshot): ArrayList<Int>? {
            val list = ArrayList<Int>()

            for (snapshot in dataSnapshot.children) {
                list.add(snapshot.getValue(Int::class.java)!!)
            }

            return list
        }
    }

    fun getOTPListLiveData(): LiveData<ArrayList<Int>> {
        return otpListLiveData
    }
}