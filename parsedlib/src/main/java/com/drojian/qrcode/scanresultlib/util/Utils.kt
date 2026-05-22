package com.drojian.qrcode.scanresultlib.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.drojian.qrcode.scanresultlib.R

/**
 * @author yangfengfan 2020-10-19
 */

const val PACKAGE_OFFICE: String = "com.microsoft.office.officehubrow"
const val PACKAGE_GOOGLE_DOCS: String = "com.google.android.apps.docs.editors.docs"
const val PACKAGE_LINE: String = "jp.naver.line.android"
const val PACKAGE_GMAIL: String = "com.google.android.gm"
const val PACKAGE_EMAIL_APP = "com.android.email"
const val PACKAGE_INSTAGRAM = "com.instagram.android"
const val PACKAGE_WHATSAPP = "com.whatsapp"
const val PACKAGE_SPOTIFY = "com.spotify.music"
const val PACKAGE_PAYPAL = "com.paypal.android.p2pmobile"
const val PACKAGE_VIBER = "com.viber.voip"
const val PACKAGE_FACEBOOK = "com.facebook.katana"
const val PACKAGE_TWITTER = "com.twitter.android"
const val PACKAGE_YOUTUBE = "com.google.android.youtube"
const val PACKAGE_GOOGLE_PLAY = "com.android.vending"


const val URL_INS = "https://www.instagram.com"
const val URL_FB = "https://www.facebook.com"
const val URL_Twitter = "https://www.twitter.com"
const val URL_YOUTUBE = "https://www.youtube.com"


fun hasOffice(context: Context) = checkAppInstalled(context, PACKAGE_OFFICE)

fun hasGoogleDocs(context: Context) = checkAppInstalled(context, PACKAGE_GOOGLE_DOCS)

fun hasLine(context: Context) = checkAppInstalled(context, PACKAGE_LINE)

fun hasGooglePlay(context: Context) = checkAppInstalled(context, PACKAGE_GOOGLE_PLAY)

fun hasGmail(context: Context) = checkAppInstalled(context, PACKAGE_GMAIL)

fun hasEmail(context: Context) = checkAppInstalled(context, PACKAGE_EMAIL_APP)

fun hasFacebook(context: Context) = checkAppInstalled(context, PACKAGE_FACEBOOK)

fun hasInstagram(context: Context) = checkAppInstalled(context, PACKAGE_INSTAGRAM)

fun hasWhatsapp(context: Context) = checkAppInstalled(context, PACKAGE_WHATSAPP)

fun hasYoutube(context: Context) = checkAppInstalled(context, PACKAGE_YOUTUBE)

fun hasSpotify(context: Context) = checkAppInstalled(context, PACKAGE_SPOTIFY)

fun hasPaypal(context: Context) = checkAppInstalled(context, PACKAGE_PAYPAL)

fun hasViber(context: Context) = checkAppInstalled(context, PACKAGE_VIBER)

fun hasTwitter(context: Context) = checkAppInstalled(context, PACKAGE_TWITTER)

/**
 * 用系统浏览器打开链接
 */
fun openBrowser(context: Context, url: String) {
    try {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    } catch (e: Exception) {
        // 处理小米手机打不开浏览器
        try {
            var url1 = url
            if (!url1.startsWith("http://") || !url1.startsWith("https://")) {
                url1 = "http://$url1"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url1))
            intent.addFlags(Intents.FLAG_NEW_DOC)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                AlertDialog.Builder(context)
                    .setMessage(R.string.lh_intent_failed)
                    .setPositiveButton(R.string.lh_button_ok, null)
                    .show()
            } catch (e: Exception) {
                // ignore
            }

        }
    }
}

/**
 * 检查应用是否安装
 */
fun checkAppInstalled(context: Context, packageName: String): Boolean {
    val packageInfo= try {
        context.packageManager.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
    } catch (e: Throwable) {
        null
    }
    return packageInfo != null
}
