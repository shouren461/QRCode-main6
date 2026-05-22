package com.drojian.qrcode.utillib.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.drojian.qrcode.baselib.R
import com.drojian.qrcode.utillib.constant.OfficialWebsite
import com.drojian.qrcode.utillib.constant.PackageName
import com.drojian.qrcode.utillib.log.LogHelper.log

object OpenAppUtil {

    @JvmStatic
    fun openFacebook(activity: Activity) {
        if (CheckInstallUtil.hasFacebook(activity)) {
            openPackageName(activity, PackageName.FACEBOOK)
        } else {
            openURL(activity, OfficialWebsite.FACEBOOK)
        }
    }

    @JvmStatic
    fun openYoutube(activity: Activity) {
        if (CheckInstallUtil.hasYoutube(activity)) {
            openPackageName(activity, PackageName.YOUTUBE)
        } else {
            openURL(activity, OfficialWebsite.YOUTUBE)
        }
    }

    @JvmStatic
    fun openTwitter(activity: Activity) {
        if (CheckInstallUtil.hasTwitter(activity)) {
            openPackageName(activity, PackageName.TWITTER)
        } else {
            openURL(activity, OfficialWebsite.TWITTER)
        }
    }

    @JvmStatic
    fun openInstagram(activity: Activity) {
        if (CheckInstallUtil.hasInstagram(activity)) {
            openPackageName(activity, PackageName.INSTAGRAM)
        } else {
            openURL(activity, OfficialWebsite.INSTAGRAM)
        }
    }


    @JvmStatic
    fun openPackageName(context: Context, packageName: String) {
        try {
            context.startActivity(context.packageManager.getLaunchIntentForPackage(packageName))
        } catch (e: Exception) {
            showActivityNotFound(context)
        }
    }

    @JvmStatic
    fun openURL(activity: Activity, url: String?) {
        try {
            url?.let {
                val uri = when {
                    it.startsWith("HTTP://") -> Uri.parse("http" + it.substring(4))
                    it.startsWith("HTTPS://") -> Uri.parse("https" + it.substring(5))
                    else -> Uri.parse(it)
                }
                val intent = Intent(Intent.ACTION_VIEW, uri)
                launchIntent(activity, intent)
            }
        } catch (e: Exception) {
            showActivityNotFound(activity)
        }
    }

    private fun launchIntent(activity: Activity, intent: Intent?) {
        try {
            rawLaunchIntent(activity, intent)
        } catch (e: Exception) {
            showActivityNotFound(activity)
        }
    }

    private fun showActivityNotFound(context: Context?) {
        try {
            context?.let {
                AlertDialog.Builder(it).setMessage(R.string.msg_intent_failed).setPositiveButton(R.string.ok, null).show()
            }
        } catch (e: Exception) {
            e.log()
        }
    }

    @Throws(ActivityNotFoundException::class)
    private fun rawLaunchIntent(activity: Activity, intent: Intent?) {
        intent?.let {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            activity.startActivity(it)
        }
    }
}