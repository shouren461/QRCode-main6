package com.drojian.qrcode.utillib.utils

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GMSUtil {

    @JvmStatic
    fun isGooglePlayServicesAvailable(context: Context) =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

}