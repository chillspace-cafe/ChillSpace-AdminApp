package chillspace.chillspacecafeadminapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CurrentTransaction(var isActive : Boolean? = null,
                              var uid : String? = null,
                              var startTime_in_milliSec : Long? = null)