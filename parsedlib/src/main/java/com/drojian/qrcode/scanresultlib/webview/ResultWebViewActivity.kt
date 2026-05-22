package com.drojian.qrcode.scanresultlib.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.utillib.analytics.BaseAnalytics
import com.drojian.qrcode.utillib.constant.CountryCode
import com.drojian.qrcode.utillib.listener.SingleListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.ClipboardUtil
import com.drojian.qrcode.utillib.utils.ShareUtil
import com.drojian.qrcode.viewlib.toast.ToastUtil


abstract class ResultWebViewActivity : AppCompatActivity() {
    var backIV: ImageView? = null
    var urlTV: TextView? = null
    var refreshIV: ImageView? = null
    var faqOrFavoriteIV: ImageView? = null
    var moreIV: ImageView? = null
    var searchRV: RecyclerView? = null
    var webView: WebView? = null
    var topMaskView: View? = null
    var bottomMaskView: View? = null
    var errorView: View? = null
    var errorIV: ImageView? = null
    var errorTV: TextView? = null
    var errorRetryView: View? = null
    var progressView: View? = null

    var isFavorite = false
    var searchAdapter: SearchEngineAdapter? = null

    private val cachedPagePlugins: List<WebViewPagePlugin> by lazy { getPagePlugins() }


    abstract fun onClickFAQ()

    /**
     * 业务层可覆写此方法返回页面定制插件。默认空列表；lib 不内置任何站点策略。
     */
    protected open fun getPagePlugins(): List<WebViewPagePlugin> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intentFrom == null || message == null) {
            try {
                message = intent.getStringExtra("message")
                intentFrom = intent.getStringExtra("intentFrom")?.let { IntentFrom.valueOf(it) }
            } catch (e: Exception) {
            }
        }
        if (message == null || intentFrom == null) {
            finish()
        }
        setContentView(R.layout.activty_webview_result)

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        } else if (Build.VERSION.SDK_INT < 26) { // enableEdgeToEdge 8.0以下导航栏还显示黑色
            window.navigationBarColor = Color.BLACK
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.drojian.qrcode.viewlib.R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottom = if (imeVisible) imeInsets.bottom else systemInsets.bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }

        topMaskView = findViewById(R.id.view_mask_top)
        bottomMaskView = findViewById(R.id.view_mask_bottom)
        backIV = findViewById(R.id.iv_back)
        urlTV = findViewById(R.id.tv_url)
        refreshIV = findViewById(R.id.iv_refresh)
        faqOrFavoriteIV = findViewById(R.id.iv_faq_or_favorite)
        moreIV = findViewById(R.id.iv_more)
        searchRV = findViewById(R.id.rv_search_engine)
        progressView = findViewById(R.id.lottie_loading)
        webView = findViewById(R.id.webView)

        errorView = findViewById(R.id.view_load_error)
        errorIV = findViewById(R.id.iv_error)
        errorTV = findViewById(R.id.tv_error)
        errorRetryView = findViewById(R.id.tv_error_retry)

        backIV?.setOnClickListener {
            setResult(REQUEST_CODE, Intent().putExtra(RESULT_KEY_IS_FAVORITE, isFavorite))
            finish()
        }
        refreshIV?.setOnClickListener {
            errorView?.isVisible = false
            webView?.reload()
        }
        errorRetryView?.setOnClickListener {
            errorView?.isVisible = false
            webView?.reload()
        }

        moreIV?.setOnClickListener {
            it.let { view ->
                WebViewMoreDialog.show(view, object : SingleListener<Int> {
                    override fun onCallBack(value: Int) {
                        when (value) {
                            R.id.view_copy_content -> {
                                message?.let { content ->
                                    ClipboardUtil.set(this@ResultWebViewActivity, content)
                                }
                                ToastUtil.show(this@ResultWebViewActivity, getString(R.string.copied_to_clipboard))
                            }
                            R.id.view_copy_link -> {
                                webView?.url?.let { url ->
                                    ClipboardUtil.set(this@ResultWebViewActivity, url)
                                    ToastUtil.show(this@ResultWebViewActivity, getString(R.string.copied_to_clipboard))
                                }
                            }
                            R.id.view_share -> {
                                webView?.url?.let { url -> ShareUtil.shareText(this@ResultWebViewActivity, url) }
                            }
                            R.id.view_faq -> {
                                onClickFAQ()
                            }
                        }
                    }
                }, intentFrom == IntentFrom.Product)
            }
        }


        val url = if (message?.let { Patterns.WEB_URL.matcher(it).matches() } == true) {
            message
        } else {
            getQueryUrl(message)
        }

        if (intentFrom == IntentFrom.URL) {
            searchRV?.visibility = View.GONE
            urlTV?.text = message

            faqOrFavoriteIV?.setImageResource(R.drawable.web_view_svg_star)
            faqOrFavoriteIV?.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) {
                    faqOrFavoriteIV?.setImageResource(R.drawable.web_view_svg_star_on)
                } else {
                    faqOrFavoriteIV?.setImageResource(R.drawable.web_view_svg_star)
                }
            }
        } else {
            searchRV?.post { searchRV?.let { SearchGuideBubble.show(it, topMaskView, bottomMaskView) } }
            faqOrFavoriteIV?.setImageResource(R.drawable.web_view_svg_faq_toolbar)
            faqOrFavoriteIV?.setOnClickListener {
                onClickFAQ()
            }
            urlTV?.visibility = View.INVISIBLE
            searchRV?.visibility = View.VISIBLE

            searchAdapter = SearchEngineAdapter(this,
                getSearchEngineList(),
                object : SingleListener<HandlerUtils.SearchEngine> {
                    override fun onCallBack(id: HandlerUtils.SearchEngine) {
                        if (id != searchEngine) {
                            searchEngine = id
                            updateSearchEngine()
                        }
                    }
                })
            searchRV?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            searchAdapter?.selectDate = searchEngine
            searchRV?.adapter = searchAdapter
        }

        searchRV?.addItemDecoration(SearchItemDecoration())
        searchRV?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        searchRV?.adapter = searchAdapter

        url?.let { initWebView(it) }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String) {
        try {
            val webSettings = webView?.settings

            webView?.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    urlTV?.text = url
                    if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                startActivity(Intent.parseUri(url, 0))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            e.printStackTrace()
                        }
                        return true
                    }

                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    progressView?.isVisible = true
                    super.onPageStarted(view, url, favicon)
                    cachedPagePlugins.forEach { runCatching { it.onPageStarted(view, url) }.onFailure { it.log() } }
                }

                override fun onPageFinished(view: WebView, url: String) {
                    progressView?.isVisible = false
                    super.onPageFinished(view, url)
                    cachedPagePlugins.forEach { runCatching { it.onPageFinished(view, url) }.onFailure { it.log() } }
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        when (error?.errorCode) {
                            ERROR_HOST_LOOKUP, ERROR_CONNECT, ERROR_TIMEOUT -> {
                                if (!isAvailable(this@ResultWebViewActivity)) {
                                    errorView?.isVisible = true
                                    errorIV?.isVisible = true
                                    errorIV?.setImageResource(R.drawable.web_view_no_wifi)
                                    errorTV?.text = getString(R.string.no_internet_des)
                                }
                            }
                        }
                    }
                }

                override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val statusCode = errorResponse?.statusCode
                        if (404 == statusCode || 500 == statusCode) {
                            if (request?.url.toString() == webView?.url) {
                                if (!isAvailable(this@ResultWebViewActivity)) {
                                    errorView?.isVisible = true
                                    errorIV?.isVisible = true
                                    errorIV?.setImageResource(R.drawable.web_view_no_wifi)
                                    errorTV?.text = getString(R.string.no_internet_des)
                                }
                                webView?.let {
                                    try {
                                        BaseAnalytics().logSelectContent(
                                            "WebViewURLBlocked",
                                            it.url.toString()
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            webView?.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) {
                        progressView?.visibility = View.GONE
                    } else {
                        progressView?.visibility = View.VISIBLE
                    }
                }
            }


            webSettings?.setNeedInitialFocus(false)
//            webSettings?.setAppCacheEnabled(true)
            webSettings?.loadWithOverviewMode = true

            webSettings?.javaScriptCanOpenWindowsAutomatically = true
            webSettings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            webSettings?.databaseEnabled = true
            webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE
            webSettings?.setSupportMultipleWindows(true)
            webSettings?.blockNetworkImage = false
            webSettings?.allowContentAccess = true
            webSettings?.allowFileAccess = true
            webSettings?.allowFileAccessFromFileURLs = true
            webSettings?.allowUniversalAccessFromFileURLs = true
            webSettings?.mediaPlaybackRequiresUserGesture = false
            webSettings?.loadsImagesAutomatically = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings?.mixedContentMode = 0
            }
            webSettings?.domStorageEnabled = true
            webSettings?.javaScriptEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            }
            webView?.loadUrl(url)
        } catch (e: Exception) {
            e.log()
        }
    }

    override fun onBackPressed() {
        try {
            errorView?.isVisible = false
            if (webView?.canGoBack() == true) {
                webView?.goBack()
                searchAdapter?.selectDate = getQueryEngine(webView?.originalUrl)
                searchAdapter?.notifyDataSetChanged()
            } else {
                setResult(REQUEST_CODE, Intent().putExtra(RESULT_KEY_IS_FAVORITE, isFavorite))
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    open fun isAvailable(context: Context): Boolean {
        var flag = false
        try {
            val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT < 21) {
                val networkInfo = connectivityManager.allNetworkInfo
                for (i in networkInfo.indices) {
                    if (networkInfo[i] != null && networkInfo[i]!!.isConnected) {
                        flag = true
                        break
                    }
                }
            } else {
                val networks = connectivityManager.allNetworks
                for (i in networks.indices) {
                    val networkInfo = connectivityManager.getNetworkInfo(networks[i])
                    if (networkInfo != null && networkInfo.isConnected) {
                        flag = true
                        break
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return flag
    }


    fun updateSearchEngine() {
        webView?.loadUrl(getQueryUrl(message))
        webView?.computeScroll()
    }

    open fun getSearchEngineList(): ArrayList<HandlerUtils.SearchEngine> {
        return when (resultHandlerConfig.countryCode) {
            CountryCode.Japan -> {
                arrayListOf(
                    HandlerUtils.SearchEngine.Yahoo,
                    HandlerUtils.SearchEngine.Google,
                    HandlerUtils.SearchEngine.Naver,
                    HandlerUtils.SearchEngine.Bing,
                    HandlerUtils.SearchEngine.Duck,
                    HandlerUtils.SearchEngine.Ecosia,
                    HandlerUtils.SearchEngine.Yandex
                )
            }
            CountryCode.SouthKorea -> {
                arrayListOf(
                    HandlerUtils.SearchEngine.Naver,
                    HandlerUtils.SearchEngine.Google,
                    HandlerUtils.SearchEngine.Yahoo,
                    HandlerUtils.SearchEngine.Bing,
                    HandlerUtils.SearchEngine.Duck,
                    HandlerUtils.SearchEngine.Ecosia,
                    HandlerUtils.SearchEngine.Yandex
                )
            }
            CountryCode.Russia -> {
                arrayListOf(
                    HandlerUtils.SearchEngine.Yandex,
                    HandlerUtils.SearchEngine.Google,
                    HandlerUtils.SearchEngine.Bing,
                    HandlerUtils.SearchEngine.Duck,
                    HandlerUtils.SearchEngine.Ecosia,
                    HandlerUtils.SearchEngine.Yahoo,
                    HandlerUtils.SearchEngine.Naver
                )
            }
            else -> {
                arrayListOf(
                    HandlerUtils.SearchEngine.Google,
                    HandlerUtils.SearchEngine.Bing,
                    HandlerUtils.SearchEngine.Duck,
                    HandlerUtils.SearchEngine.Yahoo,
                    HandlerUtils.SearchEngine.Naver,
                    HandlerUtils.SearchEngine.Ecosia,
                    HandlerUtils.SearchEngine.Yandex
                )
            }
        }
    }

    private fun getQueryUrl(query: String?): String {
        val url = when (searchEngine) {
            HandlerUtils.SearchEngine.Google -> {
                HandlerUtils.googleSearch + query
            }
            HandlerUtils.SearchEngine.Bing -> {
                HandlerUtils.bingSearch + query
            }
            HandlerUtils.SearchEngine.Yahoo -> {
                HandlerUtils.yahooSearch + query
            }
            HandlerUtils.SearchEngine.Duck -> {
                HandlerUtils.duckGoSearch + query
            }
            HandlerUtils.SearchEngine.Ecosia -> {
                HandlerUtils.ecosiaSearch + query
            }
            HandlerUtils.SearchEngine.Yandex -> {
                HandlerUtils.yandexSearch + query
            }
            HandlerUtils.SearchEngine.Amazon -> {
                HandlerUtils.amazonSearch + query
            }
            HandlerUtils.SearchEngine.Ebay -> {
                HandlerUtils.ebaySearch + query
            }
            HandlerUtils.SearchEngine.Naver -> {
                HandlerUtils.naverSearch + query
            }
        }
        return url
    }

    private fun getQueryEngine(url: String?): HandlerUtils.SearchEngine {
        return when {
            url?.startsWith(HandlerUtils.googleSearch) == true -> {
                HandlerUtils.SearchEngine.Google
            }
            url?.startsWith(HandlerUtils.bingSearch) == true -> {
                HandlerUtils.SearchEngine.Bing
            }
            url?.startsWith(HandlerUtils.yahooSearch) == true -> {
                HandlerUtils.SearchEngine.Yahoo
            }
            url?.startsWith(HandlerUtils.duckGoSearch) == true -> {
                HandlerUtils.SearchEngine.Duck
            }
            url?.startsWith(HandlerUtils.ecosiaSearch) == true -> {
                HandlerUtils.SearchEngine.Ecosia
            }
            url?.startsWith(HandlerUtils.yandexSearch) == true -> {
                HandlerUtils.SearchEngine.Yandex
            }
            url?.startsWith(HandlerUtils.amazonSearch) == true -> {
                HandlerUtils.SearchEngine.Amazon
            }
            url?.startsWith(HandlerUtils.ebaySearch) == true -> {
                HandlerUtils.SearchEngine.Ebay
            }
            url?.startsWith(HandlerUtils.naverSearch) == true -> {
                HandlerUtils.SearchEngine.Naver
            }
            else -> {
                HandlerUtils.SearchEngine.Google
            }
        }

    }


    enum class IntentFrom { URL, Product }

    inner class SearchItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val res = view.context.resources
            if (position == 0) {
                outRect.left = res.getDimensionPixelSize(R.dimen.qr_dp_8)
                outRect.right = res.getDimensionPixelSize(R.dimen.qr_dp_4)
            } else {
                outRect.left = res.getDimensionPixelSize(R.dimen.qr_dp_4)
                outRect.right = res.getDimensionPixelSize(R.dimen.qr_dp_4)
            }
        }
    }

    private fun releaseWebView(){
        runCatching {
            webView?.run {
                loadUrl("about:blank")
                (parent as? ViewGroup)?.removeView(this)
                stopLoading()
                settings.javaScriptEnabled = false
                clearHistory()
                clearCache(true)
                removeAllViewsInLayout()
                removeAllViews()
                webChromeClient = null
                destroy()
            }
        }.onFailure {
            it.log()
        }
    }

    override fun onDestroy() {
        releaseWebView()
        super.onDestroy()
    }

    companion object {
        const val RESULT_KEY_IS_FAVORITE = "is_favorite"
        const val REQUEST_CODE = 101
        var intentFrom: IntentFrom? = null
        var message: String? = null
        var resultHandlerConfig: ResultHandlerConfig = ResultHandlerConfig()
        var searchEngine: HandlerUtils.SearchEngine = HandlerUtils.SearchEngine.Google

        @JvmStatic
        fun startMe(activity: Activity?, intentFrom: IntentFrom, message: String, resultHandlerConfig: ResultHandlerConfig) {
            try {
                Companion.intentFrom = intentFrom
                Companion.message = message
                Companion.resultHandlerConfig = resultHandlerConfig
                searchEngine = resultHandlerConfig.searchEngine
                val webViewIntent = Intent(activity, resultHandlerConfig.webViewClass)
                webViewIntent.putExtra("intentFrom", intentFrom)
                if (intentFrom == IntentFrom.Product) {
                    if (resultHandlerConfig.productName != null) {
                        webViewIntent.putExtra("message", resultHandlerConfig.productName)
                        Companion.message = resultHandlerConfig.productName
                    } else {
                        webViewIntent.putExtra("message", message)
                    }
                } else {
                    webViewIntent.putExtra("message", message)
                }
                activity?.startActivityForResult(webViewIntent, REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}