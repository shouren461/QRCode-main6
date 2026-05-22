package com.drojian.qrcode.baselib

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.drojian.qrcode.utillib.extension.hideNavigationBars
import com.drojian.qrcode.utillib.log.LogHelper
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.log.Logcat
import com.drojian.qrcode.utillib.utils.DimensionUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

object BaseDialog {

    @JvmStatic
    fun getDialog(context: Context, resource: Int, theme: Int = 0): Dialog {
        val appCompatDialog = AppCompatDialog(context, theme)
        try {
            val view = LayoutInflater.from(context).inflate(resource, null)
            appCompatDialog.setContentView(view)
            var widthDP: Float = (DimensionUtil.screenWidthDps(context) * 0.9).toFloat()
            if (widthDP > 400) {
                widthDP = 400f
            }
            appCompatDialog.window?.setLayout(DimensionUtil.dpTopx(context, widthDP), ViewGroup.LayoutParams.WRAP_CONTENT)
            appCompatDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            if (appCompatDialog.window != null && QRLibUtil.shouldHideDialogNavigationBar) {
                appCompatDialog.window?.hideNavigationBars()
            }
        } catch (e: Exception) {
            e.log()
        }
        return appCompatDialog
    }

    @JvmStatic
    fun getBottomDialog(context: Context, resource: Int, navigationBarColor: Int, isDark: Boolean = false, theme: Int? = R.style.App_Dialog_Bottom_Animation): Dialog {
        val bottomSheetDialog = if (theme == null) {
            BottomSheetDialog(context)
        } else {
            BottomSheetDialog(context, theme)
        }
        try {
            val view = LayoutInflater.from(context).inflate(resource, null)
            bottomSheetDialog.setContentView(view)
            val parent = view.parent as View
            parent.setBackgroundResource(android.R.color.transparent)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(parent)
            view.measure(0, 0)
            behavior.peekHeight = DimensionUtil.screenWidthPixels(context)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            parent.layoutParams = params

            runCatching {
                bottomSheetDialog.apply {
                    WindowCompat.setDecorFitsSystemWindows(window!!, false)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android10以上，导航栏跟弹窗颜色一致
                        if (QRLibUtil.shouldHideDialogNavigationBar) {
                            window?.hideNavigationBars()
                            removeNavigationBarSpace()
                        }else{
                            window?.setNavigationBarContrastEnforced(false)
                            window?.navigationBarColor = navigationBarColor
                            WindowInsetsControllerCompat(window!!, window!!.decorView).isAppearanceLightNavigationBars = !isDark
                        }
                    } else {  // Android10以下，导航栏固定黑色
                        if (QRLibUtil.shouldHideDialogNavigationBar) {
                            window?.hideNavigationBars()
                            removeNavigationBarSpace()
                        }else{
                            window?.navigationBarColor = Color.BLACK
                            WindowInsetsControllerCompat(window!!, window!!.decorView).isAppearanceLightNavigationBars = false
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.log()
        }
        return bottomSheetDialog
    }

    private fun BottomSheetDialog.removeNavigationBarSpace() {
        try {
            findViewById<View>(android.R.id.content)?.let { contentView ->
                ViewCompat.setOnApplyWindowInsetsListener(contentView) { view, insets ->
                    // 获取键盘高度，用于设置底部内边距
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

                    // 设置内边距：只保留键盘高度作为底部内边距
                    view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, imeInsets.bottom)

                    // 消费系统栏的insets，告诉系统不再为系统栏预留空间
                    WindowInsetsCompat.Builder(insets)
                        .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(0, 0, 0, 0))
                        .build()
                }
            }
        } catch (e: Throwable) {
            Logcat.e("BaseDialog", "removeNavigationBarSpace error: ${e.message}")
        }
    }

}