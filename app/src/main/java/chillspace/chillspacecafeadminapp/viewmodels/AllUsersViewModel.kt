package chillspace.chillspacecafeadminapp.viewmodels

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import chillspace.chillspacecafeadminapp.livedata.FirebaseDatabaseLiveData
import chillspace.chillspacecafeadminapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class AllUsersViewModel : ViewModel(){
    private val dbRef = FirebaseDatabase.getInstance().reference.child("User")

    private val liveData = FirebaseDatabaseLiveData(dbRef as Query)

    private val userMapLiveData = Transformations.map(liveData,Deserializer())

    private class Deserializer : Function<DataSnapshot, MutableMap<String,User>> {
        override fun apply(dataSnapshot: DataSnapshot): MutableMap<String,User>? {
            val map = mutableMapOf<String, User>()
            for (children in dataSnapshot.children){
                map[children.key.toString()] = children.getValue(User::class.java)!!
            }
            return map
        }
    }

    fun getUserMapLiveData(): LiveData<MutableMap<String,User>> {
        return userMapLiveData
    }
}