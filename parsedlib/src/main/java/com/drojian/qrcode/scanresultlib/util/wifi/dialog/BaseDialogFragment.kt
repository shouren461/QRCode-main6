package com.drojian.qrcode.scanresultlib.util.wifi.dialog

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.drojian.qrcode.scanresultlib.R

abstract class BaseDialogFragment : DialogFragment() {

    private var width = 0

    open fun setWidth(width: Int) {
        this.width = width
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.DialogEdgeToEdge)
        super.onCreate(savedInstanceState)
    }

    fun show(fragmentManager: FragmentManager) {
        try {
            show(fragmentManager, javaClass.simpleName)
        } catch (e: Exception) {
            try {
                val ft = fragmentManager.beginTransaction()
                ft.add(this, javaClass.simpleName)
                ft.commitAllowingStateLoss()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            dialog?.let { dialog ->
                dialog.window?.let { window ->
                    if (width > 0) {
                        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                    } else {
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    }
                    window.setBackgroundDrawableResource(android.R.color.transparent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissMe() {
        try {
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}