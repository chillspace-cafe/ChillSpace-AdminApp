package chillspace.chillspacecafeadminapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(var email : String? = "", var username : String? = "", var name : String? = "")