package chillspace.chillspacecafeadminapp.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import chillspace.chillspacecafeadminapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val EMAIL_ADMIN = "chillspace.irs@gmail.com"
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
