package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.AppUtil
import com.drojian.qrcode.utillib.utils.MemoryUtil
import com.drojian.qrcode.viewlib.R

/**
 * 空间检测弹窗
 */
object AppLowStorageDialog {

    @JvmStatic
    fun show(activity: Activity?, navigationBarColor: Int, isDark: Boolean = false) {
        activity?.let {
            try {
                val dialog = BaseDialog.getBottomDialog(it, R.layout.layout_dialog_app_low_storage, navigationBarColor, isDark)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)

                val mNowSpace = dialog.findViewById<TextView>(R.id.tv_remain_space)
                val mReqSpace = dialog.findViewById<TextView>(R.id.tv_needed_space)
                val infoRemain = activity.getString(R.string.remaining_space, MemoryUtil.getAvailableMemory().toString() + "M")
                val infoReq = activity.getString(R.string.free_space_needed, "60M")

                mNowSpace.text = getColorString(infoRemain, MemoryUtil.getAvailableMemory().toString() + "M", -0x64ad)
                mReqSpace.text = getColorString(infoReq, "60M", -0xd44499)

                dialog.findViewById<View>(R.id.tv_positive).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.setOnDismissListener {
                    startFileManager(activity)
                    AppUtil.finishApp(activity)
                }

                dialog.show()
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    private fun getColorString(full: String, colored: String, color: Int): CharSequence? {
        val spanned = SpannableString(full)
        try {
            var len = colored.length
            var start = full.indexOf(colored)
            if (start < 0) {
                start = 0
                len = 0
            }
            val colorStateList = ColorStateList.valueOf(color)
            spanned.setSpan(TextAppearanceSpan(null, 0, -1, colorStateList, null), start, start + len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } catch (e: Exception) {
            e.log()
        }
        return spanned
    }

    // 寻找处理GET_CONTENT的Activity，并且是系统内置的，且包名和activity名带有file的作为内置文件管理器
    // 找不到时打开桌面
    fun startFileManager(context: Context) {
        try {
            val innerIntent = Intent(Intent.ACTION_GET_CONTENT)
            innerIntent.type = "*/*"
            innerIntent.addCategory(Intent.CATEGORY_OPENABLE)
            val pm = context.packageManager
            val list = pm.queryIntentActivities(innerIntent, PackageManager.MATCH_DEFAULT_ONLY)
            var activityStarted = false
            var pkgName: String? = null
            var bestPkgName: String? = null
            val pkgSet: MutableSet<String?> = HashSet()
            for (resolveInfo in list) {
                if (resolveInfo.activityInfo != null) {
                    val activityInfo = resolveInfo.activityInfo
                    pkgName = activityInfo.packageName
                    if (TextUtils.isEmpty(pkgName) || pkgSet.contains(pkgName)) {
                        continue
                    }
                    pkgSet.add(pkgName)
                    var weight = 0
                    if (pkgName.lowercase().contains("file")) {
                        weight += 10
                    }
                    if (activityInfo.name != null) if (activityInfo.name.lowercase().contains("file")) {
                        weight += 10
                    }
                    if (activityInfo.applicationInfo != null
                        && activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    ) {
                        weight += 40
                    }
                    if (pkgName.startsWith("com.android")) {
                        weight -= 1
                    }
                    if (weight >= 50) {
                        bestPkgName = pkgName
                        break
                    }
                }
            }
            if (!TextUtils.isEmpty(bestPkgName)) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.setPackage(bestPkgName)
                try {
                    val listLauncher = pm.queryIntentActivities(intent, 0)
                    if (listLauncher.size > 0) {
                        val info = listLauncher[0].activityInfo
                        if (info != null) {
                            intent.setClassName(info.packageName, info.name)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            activityStarted = true
                        }
                    }
                } catch (ignored: java.lang.Exception) {
                }
            }
            if (!activityStarted) {
                val home = Intent(Intent.ACTION_MAIN)
                home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                home.addCategory(Intent.CATEGORY_HOME)
                context.startActivity(home)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}
