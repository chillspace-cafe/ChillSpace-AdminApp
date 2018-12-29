package chillspace.chillspacecafeadminapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CompletedTransaction(var uid : String? = null,
                                val startTime : String? = null,
                                val elapsedTimeInMinutes : Int? = null,
                                val cost : Int? = null)