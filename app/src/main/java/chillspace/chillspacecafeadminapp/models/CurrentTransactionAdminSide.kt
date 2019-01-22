package chillspace.chillspacecafeadminapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CurrentTransactionAdminSide(var isActive: Boolean? = null,
                                       var uid: String? = null,
                                       var startTime_in_milliSec: MutableMap<String, String>? = null)