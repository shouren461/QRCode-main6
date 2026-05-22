package com.drojian.qrcode.scanresultlib.webview

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.SearchEngine.*
import com.drojian.qrcode.utillib.listener.SingleListener

class SearchEngineAdapter(
    val activity: Activity,
    var dataArrayList: ArrayList<HandlerUtils.SearchEngine>,
    var listener: SingleListener<HandlerUtils.SearchEngine>
) :
    RecyclerView.Adapter<ItemViewHolder>() {

    var selectDate: HandlerUtils.SearchEngine = dataArrayList[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.layout_adapter_web_view_search, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        try {
            val data = dataArrayList[position]
            holder.itemView.setBackgroundResource(R.drawable.lib_result_web_view_bg_search_item)
            when (data) {
                Naver -> R.drawable.web_view_svg_search_naver
                Google -> R.drawable.web_view_svg_search_google
                Yahoo -> R.drawable.web_view_svg_search_yahoo
                Bing -> R.drawable.web_view_svg_search_bing
                Duck -> R.drawable.web_view_svg_search_duckduckgo
                Ecosia -> R.drawable.web_view_png_search_ecosia
                Yandex -> R.drawable.web_view_svg_search_yandex
                else -> R.drawable.parse_action_icon_web_search
            }.let { holder.imageView?.setImageResource(it) }
            holder.textView?.text = data.name
            holder.itemView.setOnClickListener {
                selectDate = data
                listener.onCallBack(data)
                notifyDataSetChanged()
            }
            if (selectDate == data) {
                holder.itemView.setBackgroundResource(R.drawable.lib_bg_web_view_search_select)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView? = itemView.findViewById(R.id.image_view)
    val textView: TextView? = itemView.findViewById(R.id.text_view)
}