package chillspace.chillspacecafeadminapp.viewmodels

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import chillspace.chillspacecafeadminapp.livedata.FirebaseDatabaseLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class CurrentTimeViewModel : ViewModel(){
    private val dbRef = FirebaseDatabase.getInstance().reference.child("CurrentTime")

    private val liveData = FirebaseDatabaseLiveData(dbRef as Query)

    private val currentTimeLiveData = Transformations.map(liveData,Deserializer())

    private class Deserializer : Function<DataSnapshot, Long> {
        override fun apply(dataSnapshot: DataSnapshot): Long? {
            return dataSnapshot.value.toString().toLong()
        }
    }

    fun getCurrentTimeLiveData(): LiveData<Long> {
        return currentTimeLiveData
    }
}