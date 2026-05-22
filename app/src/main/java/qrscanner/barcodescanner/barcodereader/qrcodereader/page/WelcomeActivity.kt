package qrscanner.barcodescanner.barcodereader.qrcodereader.page

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.log.Logcat
import com.drojian.qrcode.utillib.utils.DimensionUtil
import com.drojian.qrcode.utillib.utils.StatusBarUtil
import qrscanner.barcodescanner.barcodereader.qrcodereader.R
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.App
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseActivity
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.Constant
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.OnceConfig
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.QRCodeHelper
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.QRCodeServerData

class WelcomeActivity : BaseActivity() {

    private fun isFoldScreen(): Boolean {
        Logcat.d(
            "size:  " + DimensionUtil.screenHeightPixels(this) + "x " + DimensionUtil.screenWidthPixels(
                this
            )
        )
        return DimensionUtil.screenHeightPixels(this) / DimensionUtil.screenWidthPixels(this)
            .toFloat() < 1.5
    }

    override fun getLayout() =
        if (isFoldScreen()) R.layout.activity_welcome_2 else R.layout.activity_welcome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBarText(this)
        StatusBarUtil.openFullScreenModel(this)
    }

    override fun initData() {
        QRCodeServerData.ifNeedUpdate(this)
        QRCodeHelper.getInstance(this)
        App.isFirstOpen = !OnceConfig.showWelcome

        startActivity(Intent(this, MainActivity::class.java).apply {
            action = intent.action
            putExtra(
                Constant.EXTRA_IS_FROM_SHORTCUT,
                intent.getBooleanExtra(Constant.EXTRA_IS_FROM_SHORTCUT, false)
            )
        })
        this.finish()
    }


    override fun initView() {
        enableInsetsView(findViewById<View>(R.id.main), setTop = false, setBottom = true)
    }

    override fun initAction() {
        findViewById<View>(R.id.tv_continue).setOnClickListener {
            OnceConfig.showWelcome = true
            startActivity(Intent(this, MainActivity::class.java).apply {
                action = intent.action
                putExtra(
                    Constant.EXTRA_IS_FROM_SHORTCUT,
                    intent.getBooleanExtra(Constant.EXTRA_IS_FROM_SHORTCUT, false)
                )
            })
            this.finish()
        }

        try {
            val tvPrivacy = findViewById<TextView>(R.id.tv_privacy)
            val privacyPolicy = "Privacy policy"
            val text = getString(R.string.privacy_policy_tip, privacyPolicy)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }
            }
            val style = SpannableStringBuilder(text)
            val startIndex = text.indexOf(privacyPolicy)
            style.setSpan(
                clickableSpan,
                startIndex,
                startIndex + privacyPolicy.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#4991FF"))
            style.setSpan(
                foregroundColorSpan,
                startIndex,
                startIndex + privacyPolicy.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvPrivacy?.movementMethod = LinkMovementMethod.getInstance()
            tvPrivacy?.text = style
        } catch (e: Exception) {
            e.log()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getIntent().action = intent.action
    }

    companion object {

        @JvmStatic
        fun startMe(context: Context, action: String?, isFromShortcut: Boolean) {
            val starter = Intent(context, WelcomeActivity::class.java).apply {
                this.action = action
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(Constant.EXTRA_IS_FROM_SHORTCUT, isFromShortcut)
            }
            context.startActivity(starter)
        }
    }


}