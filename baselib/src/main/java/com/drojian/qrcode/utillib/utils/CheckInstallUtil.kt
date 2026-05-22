package com.drojian.qrcode.utillib.utils

import android.content.Context
import android.content.pm.PackageManager
import com.drojian.qrcode.utillib.constant.PackageName

object CheckInstallUtil {
    @JvmStatic
    fun hasFacebook(context: Context) = isAppInstalled(context, PackageName.FACEBOOK)

    @JvmStatic
    fun hasYoutube(context: Context) = isAppInstalled(context, PackageName.YOUTUBE)

    @JvmStatic
    fun hasTwitter(context: Context) = isAppInstalled(context, PackageName.TWITTER)

    @JvmStatic
    fun hasInstagram(context: Context) = isAppInstalled(context, PackageName.INSTAGRAM)

    @JvmStatic
    fun hasEmail(context: Context) = isAppInstalled(context, PackageName.EMAILAPP)

    @JvmStatic
    fun hasGMail(context: Context) = isAppInstalled(context, PackageName.GMAIL)

    @JvmStatic
    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        val packageInfo= try {
            if (packageName != null) {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
            } else {
                null
            }
        } catch (e: Throwable) {
            null
        }
        return packageInfo != null
    }
}