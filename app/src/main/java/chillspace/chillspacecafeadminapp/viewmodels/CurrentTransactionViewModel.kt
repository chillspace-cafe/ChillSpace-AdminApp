package chillspace.chillspacecafeadminapp.viewmodels

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import chillspace.chillspacecafeadminapp.livedata.FirebaseDatabaseLiveData
import chillspace.chillspacecafeadminapp.models.CurrentTransactionClientSide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class CurrentTransactionViewModel : ViewModel(){
    private val dbRef = FirebaseDatabase.getInstance().reference.child("Current").child("CurrentTransactions")

    private val liveData = FirebaseDatabaseLiveData(dbRef as Query)

    private val currentTransactionLiveData = Transformations.map(liveData,Deserializer())

    private class Deserializer : Function<DataSnapshot, ArrayList<CurrentTransactionClientSide>> {
        override fun apply(dataSnapshot: DataSnapshot): ArrayList<CurrentTransactionClientSide>? {
            val list = ArrayList<CurrentTransactionClientSide>()
            for (children in dataSnapshot.children){
                val transaction = children.getValue(CurrentTransactionClientSide::class.java)!!
                if(transaction.isActive!!){
                    list.add(transaction)
                }
            }
            return list
        }
    }

    fun getCurrentTransactionLiveData(): LiveData<ArrayList<CurrentTransactionClientSide>> {
        return currentTransactionLiveData
    }
}