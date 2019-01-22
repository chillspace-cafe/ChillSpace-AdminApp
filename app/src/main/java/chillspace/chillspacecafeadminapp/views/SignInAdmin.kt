package chillspace.chillspacecafeadminapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import chillspace.chillspacecafeadminapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in_admin.*

class SignInAdmin : Fragment() {

    private val EMAIL_ADMIN = "chillspace.irs@gmail.com"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignInAdmin.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(MainActivity.EMAIL_ADMIN,editAdminPassword.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(activity,"Successful sign in.",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(activity,"Couldn't sign in. Try again.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
