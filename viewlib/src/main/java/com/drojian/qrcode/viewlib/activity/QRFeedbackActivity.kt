package com.drojian.qrcode.viewlib.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.drojian.qrcode.utillib.image.ImageUtil
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.permission.OnPermissionCallback
import com.drojian.qrcode.utillib.permission.Permission
import com.drojian.qrcode.utillib.permission.PermissionHelper
import com.drojian.qrcode.utillib.utils.FeedbackUtil
import com.drojian.qrcode.utillib.utils.FeedbackUtil.FEEDBACK_SELECT_PHOTO
import com.drojian.qrcode.utillib.utils.FileUtil
import com.drojian.qrcode.viewlib.R
import com.drojian.qrcode.viewlib.dialog.FeedbackChoosePhotoDialog
import com.drojian.qrcode.viewlib.dialog.FeedbackThanksDialog
import com.drojian.qrcode.viewlib.dialog.PermissionCameraDialog
import com.drojian.qrcode.viewlib.feedback.FeedbackHelper
import com.drojian.qrcode.viewlib.feedback.ReasonAdapter
import com.drojian.qrcode.viewlib.feedback.ReasonItem
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.io.File

abstract class QRFeedbackActivity : AppCompatActivity() {

    var warningTV: TextView? = null
    var inputET: EditText? = null
    var photoIV: ImageView? = null
    var selectPhotoView: View? = null
    var selectPhotoTV: TextView? = null

    var submitTV: TextView? = null

    var reasonRV: RecyclerView? = null
    var reasonAdapter: ReasonAdapter? = null
    var reasonListState: Parcelable? = null

    var photoUri: Uri? = null


    open var feedbackHelper: FeedbackHelper = FeedbackHelper()

    /**
     * 反馈原因
     */
    open var reasonList: ArrayList<ReasonItem> = arrayListOf()

    /**
     * provider authorities
     */
    abstract fun getAuthority(): String

    /**
     * 点击提交
     */
    abstract fun onSubmit(@Nullable inputString: String?, @Nullable reasonList: ArrayList<ReasonItem>?, @Nullable photoList: Uri?)


    protected val getContracts = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        FileUtil.getImagePath(this, uri)?.let { path ->
            photoUri = FileProvider.getUriForFile(this, getAuthority(), File(path))
            photoIV?.visibility = View.INVISIBLE
            submitTV?.visibility = View.VISIBLE
            submitTV?.isEnabled = true
            selectPhotoView?.visibility = View.VISIBLE
            selectPhotoTV?.text = getFileNameFromUri(this, photoUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        } else if (Build.VERSION.SDK_INT < 26) { // enableEdgeToEdge 8.0以下导航栏还显示黑色
            window.navigationBarColor = Color.BLACK
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottom = if (imeVisible) imeInsets.bottom else systemInsets.bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }
        initView()
        initData()
    }

    open fun initView() {
        submitTV = findViewById(R.id.tv_submit)
        inputET = findViewById(R.id.et_input)
        reasonRV = findViewById(R.id.rv_reason)
        photoIV = findViewById(R.id.iv_photo)
        selectPhotoTV = findViewById(R.id.tv_feedback_img_name)
        selectPhotoView = findViewById(R.id.view_photo_select)
        warningTV = findViewById(R.id.tv_warning)

        inputET?.hint = getString(R.string.fb_please_tell_more, "6")
        inputET?.doAfterTextChanged {
            updateSubmitState()
        }

        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        submitTV?.setOnClickListener {
            reasonAdapter?.let {
                if (feedbackHelper.selectReasonToSubmit) { //必须选择Reason才能提交
                    if (it.getSelectData().size >= 1) {
                        submit()
                    } else {
                        warningTV?.visibility = View.VISIBLE
                    }
                } else {
                    submit()
                }
            }
        }

        photoIV?.setOnClickListener {
            selectView()
        }

        selectPhotoView?.setOnClickListener {
            selectView()
        }

        updateSubmitState()
    }

    private fun selectView() {
        @ColorInt
        val navigationBarColor = ContextCompat.getColor(this, R.color.feedback_page_bg)
        FeedbackChoosePhotoDialog.show(this, navigationBarColor, false, object : FeedbackChoosePhotoDialog.ChoosePhotoListener {
            override fun onClickCamera() {
                PermissionHelper.with(this@QRFeedbackActivity).permission(Permission.CAMERA).request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        photoUri = ImageUtil.imageCapture(this@QRFeedbackActivity, FEEDBACK_SELECT_PHOTO, getAuthority())
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        PermissionCameraDialog.showOnDeny(this@QRFeedbackActivity, true)
                    }
                })
            }

            override fun onClickGallery() {
                getContracts.launch("image/*")
            }
        })
    }

    private fun submit() {
        if (feedbackHelper.returnAllReasonList) {
            onSubmit(inputET?.text.toString(), reasonAdapter?.getReasonList(), photoUri)
        } else {
            onSubmit(inputET?.text.toString(), reasonAdapter?.getSelectData(), photoUri)
        }
    }

    open fun initData() {
        super.onResume()
        // reason
        val reasonLayoutManager = FlexboxLayoutManager(this)
        reasonLayoutManager.flexDirection = FlexDirection.ROW
        reasonLayoutManager.justifyContent = JustifyContent.FLEX_START
        reasonRV?.layoutManager = reasonLayoutManager

        reasonAdapter = ReasonAdapter(reasonList, object : ReasonAdapter.ReasonListener {
            override fun onClickReason() {
                warningTV?.visibility = View.GONE
            }
        })

        reasonRV?.adapter = reasonAdapter
    }

    override fun onResume() {
        super.onResume()
        reasonListState?.let {
            reasonRV?.layoutManager?.onRestoreInstanceState(it)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FeedbackUtil.FEEDBACK_REQUEST_CODE) {
            FeedbackThanksDialog.show(this, ContextCompat.getColor(this, R.color.feedback_page_bg), false, object : DialogListener {
                override fun onDisMiss() {
                    finish()
                }
            })
        } else if (requestCode == FEEDBACK_SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                photoIV?.setImageResource(R.drawable.feedback_ic_photo)
                submitTV?.visibility = View.VISIBLE
                submitTV?.isEnabled = true
                photoIV?.visibility = View.INVISIBLE
                selectPhotoView?.visibility = View.VISIBLE
                selectPhotoTV?.text = getFileNameFromUri(this, photoUri)
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Thread {
            deleteDir(cacheDir)
        }.start()
    }

    @Synchronized
    private fun deleteDir(dir: File): Boolean {
        try {
            if (dir.isDirectory) {
                dir.listFiles()?.let {
                    for (child in it) {
                        if (!deleteDir(child)) {
                            return false
                        }
                    }
                }
            }
            if (dir.name.startsWith("feedback_")) {
                dir.delete()
            }
            return true
        } catch (e: Exception) {
            e.log()
        }
        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            inputET?.let {
                outState.putString(EXTRA_FEEDBACK_CONTENT, it.text.toString())
            }
            outState.putString(EXTRA_FEEDBACK_CAMERA, photoUri.toString())
        } catch (e: Exception) {
            e.log()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        try {
            savedInstanceState.getString(EXTRA_FEEDBACK_CONTENT)?.let {
                if (it.isNotEmpty()) {
                    inputET?.setText(it)
                }
            }
            photoUri = savedInstanceState.getParcelable(EXTRA_FEEDBACK_IMAGE)
            savedInstanceState.getString(EXTRA_FEEDBACK_CAMERA)?.let {
                photoUri = Uri.parse(it)
            }
        } catch (e: Exception) {
            e.log()
        }
    }

    private fun getFileNameFromUri(context: Context?, fileUri: Uri?): String? {
        if (context == null || fileUri == null) return null
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                val documentFile = DocumentFile.fromSingleUri(context, fileUri) ?: return null
                return documentFile.name
            } else {
                return fileUri.toString().trim().let { it.substring(it.lastIndexOf("/") + 1); }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 提交按钮状态
     */
    open fun updateSubmitState() {
        submitTV?.let { it ->
            val hasContent = (inputET?.let { it.text.trim().length >= 6 } ?: let { false })
            if (hasContent) {
                it.visibility = View.VISIBLE
                it.isEnabled = true
            } else {
                it.isEnabled = false
                it.isVisible = !feedbackHelper.submitGoneOnFalse
            }
        }
    }


    companion object {
        const val EXTRA_FEEDBACK_CONTENT = "extra_feedback_content"
        const val EXTRA_FEEDBACK_IMAGE = "extra_feedback_image"
        const val EXTRA_FEEDBACK_CAMERA = "extra_feedback_camera"
    }
}