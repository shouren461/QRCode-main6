package com.drojian.qrcode.viewlib.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R
import com.drojian.qrcode.viewlib.faq.FAQAdapter
import com.drojian.qrcode.viewlib.faq.FAQItem

abstract class QRFAQActivity : AppCompatActivity(), FAQAdapter.FAQAdapterListener {

    var backIV: ImageView? = null
    var feedbackView: View? = null
    open var recyclerView: RecyclerView? = null
    open var openDefault = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        } else if (Build.VERSION.SDK_INT < 26) { // enableEdgeToEdge 8.0以下导航栏还显示黑色
            window.navigationBarColor = Color.BLACK
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backIV = findViewById(R.id.iv_back)
        feedbackView = findViewById(R.id.view_feedback)
        recyclerView = findViewById(R.id.recyclerView)

        setListener()
        setFAQAdapter()
    }

    private fun setListener() {
        backIV?.setOnClickListener {
            finish()
        }
    }

    private fun setFAQAdapter() {
        val faqList: ArrayList<FAQItem> = ArrayList()
        try {
            var faqItem = getText(R.string.code_cannot_be_read_faq).split('#', ignoreCase = false, limit = 2)
            FAQItem(R.drawable.faq_svg_item_qrcode, faqItem[0], faqItem[1].trim()).also {
                faqList.add(it)
            }

            faqItem = getText(R.string.cannot_connect_to_wifi_faq).split('#', ignoreCase = false, limit = 2)
            FAQItem(R.drawable.faq_svg_item_wifi, faqItem[0], faqItem[1].trim()).also {
                faqList.add(it)
            }

            faqItem = getText(R.string.have_problem_with_the_link_faq).split('#', ignoreCase = false, limit = 2)
            FAQItem(R.drawable.faq_svg_item_link, faqItem[0], faqItem[1].trim()).also {
                faqList.add(it)
            }

            faqItem = getText(R.string.need_more_information_faq).split('#', ignoreCase = false, limit = 2)
            FAQItem(R.drawable.faq_svg_item_search, faqItem[0], faqItem[1].trim()).also {
                faqList.add(it)
            }

            faqItem = getText(R.string.convert_picture_to_qr_code_faq).split('#', ignoreCase = false, limit = 2)
            FAQItem(R.drawable.faq_svg_item_img, faqItem[0], faqItem[1].trim()).also {
                faqList.add(it)
            }

            if (openDefault >= 0 && openDefault < faqList.size) {
                faqList[openDefault].openDefault = true
            }

        } catch (e: Exception) {
            e.log()
        }

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = FAQAdapter(faqList, this)
    }
}