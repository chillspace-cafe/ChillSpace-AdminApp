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

        //changing fragments when firebase auth changed
        firebaseAuth.addAuthStateListener {
            if (firebaseAuth.currentUser != null && firebaseAuth.currentUser!!.email == EMAIL_ADMIN)
                navController.navigate(R.id.action_signInAdmin_to_dest_home)
        }



        //setting title according to fragment
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            toolbar.title = navController.currentDestination?.label
        }
    }
}
