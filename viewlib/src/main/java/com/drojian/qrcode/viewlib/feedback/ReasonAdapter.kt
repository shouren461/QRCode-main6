package com.drojian.qrcode.viewlib.feedback

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.viewlib.R

class ReasonAdapter(private val data: ArrayList<ReasonItem>, private val reasonListener: ReasonListener? = null) :
    RecyclerView.Adapter<ReasonAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_adapter_feedback_reason, parent, false))

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = data[position]
        holder.typeTv.text = item.content
        updateViewState(holder.typeTv, item.selected)
        holder.itemView.setOnClickListener {
            item.selected = !item.selected
            updateViewState(holder.typeTv, item.selected)
            reasonListener?.onClickReason()
        }
    }

    override fun getItemCount() = data.size

    fun getSelectData(): ArrayList<ReasonItem> {
        val selectArray: ArrayList<ReasonItem> = ArrayList()
        for (item in data) {
            if (item.selected) {
                selectArray.add(item)
            }
        }
        return selectArray
    }

    fun getReasonList(): ArrayList<ReasonItem> {
        return data
    }

    private fun updateViewState(textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.feedback_reason_bg_select)
            textView.setTextColor(textView.context.resources.getColor(R.color.feedback_reason_text_select))
        } else {
            textView.setBackgroundResource(R.drawable.feedback_reason_bg_unselect)
            textView.setTextColor(textView.context.resources.getColor(R.color.feedback_reason_text_unselect))
        }
    }

    inner class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var typeTv: TextView = view.findViewById(R.id.tv_type)
    }

    interface ReasonListener {
        fun onClickReason()
    }

}