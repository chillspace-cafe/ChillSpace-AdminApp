package chillspace.chillspacecafeadminapp.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import chillspace.chillspacecafeadminapp.R
import chillspace.chillspacecafeadminapp.models.CurrentTransactionClientSide
import chillspace.chillspacecafeadminapp.models.User
import chillspace.chillspacecafeadminapp.viewmodels.AllUsersViewModel
import chillspace.chillspacecafeadminapp.viewmodels.CurrentTransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_current_transactions.*

class MainActivity : AppCompatActivity() {

    companion object {
        val EMAIL_ADMIN = "chillspace.irs@gmail.com"
        var userMap = mutableMapOf<String,User>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val firebaseAuth = FirebaseAuth.getInstance()

        //for toolbar // required for menu
        setSupportActionBar(toolbar)

        //changing fragments when firebase auth changed
        firebaseAuth.addAuthStateListener {
            if (firebaseAuth.currentUser == null)
                navController.navigate(R.id.dest_sign_in_admin)
            if (firebaseAuth.currentUser != null && firebaseAuth.currentUser!!.email == EMAIL_ADMIN) {
                navController.navigate(R.id.dest_home)

                //getting user map
                val userMapViewModel = ViewModelProviders.of(this).get(AllUsersViewModel::class.java)

                val userMapLiveData : LiveData<MutableMap<String,User>> = userMapViewModel.getUserMapLiveData()

                userMapLiveData.observe(this, Observer {
                    userMap = it
                })
            }
        }


        //setting title according to fragment
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            toolbar.title = navController.currentDestination?.label
        }
    }

    //assists navigation
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        if (navController.currentDestination?.id == R.id.dest_home
                || navController.currentDestination?.id == R.id.dest_sign_in_admin) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
