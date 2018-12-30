package chillspace.chillspacecafeadminapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GeneratedOTP(var uid : String? = null, var isRunning : Boolean? = null, var startTime : Long? = null, var playTime: Long? = null)