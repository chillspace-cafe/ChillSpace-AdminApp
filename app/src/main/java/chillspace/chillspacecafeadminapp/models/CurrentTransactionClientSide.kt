package chillspace.chillspacecafeadminapp.models

import com.google.firebase.database.IgnoreExtraProperties

//note this data class is different from admin
@IgnoreExtraProperties
data class CurrentTransactionClientSide(var isActive: Boolean? = null,
                                        var uid: String? = null,
                                        var startTime_in_milliSec: Long? = null)