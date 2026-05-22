package com.drojian.qrcode.viewlib.faq

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.viewlib.R

class FAQVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var clickView: View = itemView.findViewById(R.id.view_item)
    var kindIV: ImageView = itemView.findViewById(R.id.iv_start)
    var titleTV: TextView = itemView.findViewById(R.id.tv_mid)
    var stateIV: ImageView = itemView.findViewById(R.id.iv_end)
    var contentTV: TextView = itemView.findViewById(R.id.tv_content)
    var lineTV: TextView = itemView.findViewById(R.id.tv_line)
}

class FAQItem(@DrawableRes val drawableRes: Int, val title: String, val content: String, var openDefault: Boolean = false)