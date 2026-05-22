package com.drojian.qrcode.utillib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.drojian.qrcode.baselib.R
import com.drojian.qrcode.utillib.constant.PackageName
import com.drojian.qrcode.utillib.log.LogFile
import com.drojian.qrcode.utillib.log.LogHelper.log
import java.util.TimeZone


object FeedbackUtil {

    const val FEEDBACK_REQUEST_CODE = 1001
    const val FEEDBACK_SELECT_PHOTO = 1002

    @JvmStatic
    fun send(activity: Activity, feedbackEmail: String, appName: String, content: String?, authority: String, imageUris: ArrayList<Uri>, otherString: String? = "") {
        try {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "application/octet-stream"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(feedbackEmail))
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                String.format(activity.getString(R.string.td_feedback_email_title), appName)
            )
            intent.putExtra(Intent.EXTRA_TEXT, getDevicesInformation(activity, content, otherString))
            val extraUriList = arrayListOf<Uri>()
            imageUris.let { extraUriList.addAll(it) }
            LogFile.getLogFile(activity).let {
                if (it.exists() && it.length() > 0) {
                    FileProvider.getUriForFile(activity, authority, it)?.let { uri -> extraUriList.add(uri) }
                }
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, extraUriList)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            if (CheckInstallUtil.hasGMail(activity)) {
                intent.setPackage(PackageName.GMAIL)
            } else if (CheckInstallUtil.hasEmail(activity)) {
                intent.setPackage(PackageName.EMAILAPP)
            }
            activity.startActivityForResult(intent, FEEDBACK_REQUEST_CODE)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$feedbackEmail")
                intent.putExtra(Intent.EXTRA_SUBJECT, String.format(activity.getString(R.string.td_feedback_email_title), appName))
                intent.putExtra(Intent.EXTRA_TEXT, getDevicesInformation(activity, content, otherString))
                activity.startActivityForResult(intent, FEEDBACK_REQUEST_CODE)
            } catch (e1: Exception) {
                e1.log()
            }

        }
    }


    @JvmStatic
    fun getDevicesInformation(context: Context, feedbackContent: String?, otherString: String? = "") =
        try {
            val sb = if (feedbackContent.isNullOrEmpty()) StringBuffer() else StringBuffer(feedbackContent)
            sb.append("\n\n")
            sb.append("(App ${VersionUtil.getVersionNameWithSmall(context)}")
            sb.append(", Model " + Build.MODEL)
            sb.append(", OS v" + Build.VERSION.RELEASE)
            sb.append(", Screen ")
            sb.append(context.resources.displayMetrics.widthPixels.toString() + "x" + context.resources.displayMetrics.heightPixels)
            sb.append(", ")
            val locale = context.resources.configuration.locale
            sb.append(locale.language + " _ " + locale.country)
            sb.append(", ")
            sb.append(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT))
            sb.append(", ")
            sb.append(otherString)
            sb.append(")")
            sb.toString()
        } catch (e: Exception) {
            ""
        }
}