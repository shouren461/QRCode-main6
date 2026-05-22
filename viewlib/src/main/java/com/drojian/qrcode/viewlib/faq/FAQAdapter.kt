package com.drojian.qrcode.viewlib.faq

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

class FAQAdapter(private val items: List<FAQItem>, private val faqAdapterListener: FAQAdapterListener? = null) : RecyclerView.Adapter<FAQVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FAQVH(LayoutInflater.from(parent.context).inflate(R.layout.layout_adapter_faq, parent, false))


    override fun onBindViewHolder(holder: FAQVH, position: Int) {
        try {
            holder.kindIV.setImageResource(items[position].drawableRes)
            holder.titleTV.text = items[position].title
            if (position == items.size - 1) {
                holder.lineTV.visibility = View.INVISIBLE
            } else {
                holder.lineTV.visibility = View.VISIBLE
            }

            if (items[position].openDefault) {
                holder.contentTV.movementMethod = LinkMovementMethod.getInstance()
                holder.contentTV.text = textBold(holder.contentTV.context, items[position].content)
                holder.contentTV.visibility = View.VISIBLE
                holder.stateIV.setImageResource(R.drawable.faq_svg_up)
                if (position != items.size - 1) {
                    holder.lineTV.visibility = View.INVISIBLE
                }
            } else {
                holder.contentTV.movementMethod = null
                holder.contentTV.text = ""
                holder.contentTV.visibility = View.GONE
                holder.stateIV.setImageResource(R.drawable.faq_svg_down)
                if (position != items.size - 1) {
                    holder.lineTV.visibility = View.VISIBLE
                }
            }

            holder.clickView.setOnClickListener {
                if (TextUtils.isEmpty(holder.contentTV.text)) {
                    holder.contentTV.movementMethod = LinkMovementMethod.getInstance()
                    holder.contentTV.text = textBold(holder.contentTV.context, items[position].content)
                    holder.contentTV.visibility = View.VISIBLE
                    holder.stateIV.setImageResource(R.drawable.faq_svg_up)
                    faqAdapterListener?.onClickFaq(position, true)
                } else {
                    holder.contentTV.movementMethod = null
                    holder.contentTV.text = ""
                    holder.contentTV.visibility = View.GONE
                    holder.stateIV.setImageResource(R.drawable.faq_svg_down)
                    faqAdapterListener?.onClickFaq(position, false)
                }
            }
        } catch (e: Exception) {
            e.log()
        }
    }

    override fun getItemCount() = items.size


    private fun textBold(context: Context, sources: CharSequence): SpannableString {
        var startTag = "<b>"
        var endTag = "</b>"

        var startList = arrayListOf<Int>()
        var endList = arrayListOf<Int>()
        try {
            var str = sources.toString()

            var startIndex = 0
            var endIndex = 0

            while (str.contains(startTag) && str.contains(endTag)) {
                startIndex = str.indexOf(startTag)
                str = str.replaceFirst(startTag, "")
                endIndex = str.indexOf(endTag)
                str = str.replaceFirst(endTag, "")
                if (startIndex > endIndex) {
                    startIndex += endIndex
                    endIndex = startIndex - endIndex
                    startIndex -= endIndex
                }
                if (startIndex != 0 && endIndex != 0) {
                    startList.add(startIndex)
                    endList.add(endIndex)
                }
            }

            if (startIndex == 0 || endIndex == 0) {
                startTag = "$"
                endTag = "$$"
                startList = arrayListOf()
                endList = arrayListOf()

                while (str.contains(startTag) && str.contains(endTag)) {
                    startIndex = str.indexOf(startTag)
                    str = str.replaceFirst(startTag, "")
                    endIndex = str.indexOf(endTag)
                    str = str.replaceFirst(endTag, "")
                    if (startIndex > endIndex) {
                        startIndex += endIndex
                        endIndex = startIndex - endIndex
                        startIndex -= endIndex
                    }
                    if (startIndex != 0 && endIndex != 0) {
                        startList.add(startIndex)
                        endList.add(endIndex)
                    }
                }
            }

            val linkStart = str.indexOf("%s")
            str = str.replaceFirst("%s", "")
            val linkEnd = str.lastIndexOf("%s")
            str = str.replaceFirst("%s", "")

            val spannableString = SpannableString(str)
            for (i in startList.indices) {
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.faq_text_strong)),
                    startList[i],
                    endList[i],
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(StyleSpan(Typeface.BOLD), startList[i], endList[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            try {
                if (linkStart > -1 && linkEnd > -1 && linkStart < linkEnd) {
                    val clickableSpan: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            faqAdapterListener?.onScanFAQFeedback()
                        }
                    }
                    spannableString.setSpan(clickableSpan, linkStart, linkEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.faq_text_feedback)),
                        linkStart,
                        linkEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } catch (e: Exception) {
                e.log()
            }
            return spannableString
        } catch (e: Exception) {
            e.log()
        }
        return SpannableString(sources)
    }

    interface FAQAdapterListener {
        /**
         * @param position 点击的位置
         * @param visible 内容是否可见
         */
        fun onClickFaq(position: Int, visible: Boolean)

        fun onScanFAQFeedback() {}

    }
}