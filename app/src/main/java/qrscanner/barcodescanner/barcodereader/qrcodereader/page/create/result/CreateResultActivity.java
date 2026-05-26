package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result;

import static qrscanner.barcodescanner.barcodereader.qrcodereader.data.ConstantKt.REQUEST_CODE_CREATE_RESULT_PAGE;
import static qrscanner.barcodescanner.barcodereader.qrcodereader.data.ConstantKt.RESULT_CODE_CLOSE_CREATE_PAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.drojian.qrcode.createlib.CreatorConfig;
import com.drojian.qrcode.createlib.CreatorHelper;
import com.drojian.qrcode.createlib.create.CreateResultModel;
import com.drojian.qrcode.utillib.extension.ThrowableEXT;
import com.drojian.qrcode.utillib.image.CacheImageUtil;
import com.drojian.qrcode.utillib.permission.OnPermissionCallback;
import com.drojian.qrcode.utillib.permission.Permission;
import com.drojian.qrcode.utillib.permission.PermissionHelper;
import com.drojian.qrcode.utillib.utils.DimensionUtil;
import com.drojian.qrcode.utillib.utils.SPUtil;
import com.drojian.qrcode.viewlib.toast.ToastUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.AppFileProvider;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.CreateHistoryManager;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.CreateCategory;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input.BaseCreateActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;


//二维码创建结果展示页面 ->负责渲染生成的二维码图片，保存到相册，分享以及保存到本地创建方式
public class CreateResultActivity extends BaseActivity implements View.OnClickListener {
    private static final String INTENT_EXTRA_RESULT = "intent_key_result";
    private static final String INTENT_EXTRA_FROM = "intent_key_from";
    private boolean isSaveAble = true;
    private int qrSaveTimes;
    private boolean isFromHistory, isSubscribe;
    private String qrMsg, showMsg;
    private ImageView mBackIv, mCloseIv, mQrCategoryIv, mQrIv;
    private TextView mQrShowMsgTv, mQrCategoryTv;
    private Button mSaveBtu, mShareBtu;
    private ConstraintLayout freeOptionCl;
    private Bitmap qrBitmap; //二维码位图
    private static CreateResultModel createResultModel; //二维码结果模型


    //启动结果页的静态方法 ->@param resultModel 包含生成信息的模型对象, @param isFromHistory 标记是否是从历史记录页跳转过来的
    public static void showMe(Activity activity, CreateResultModel resultModel, boolean isFromHistory) {
        createResultModel = resultModel;
        Intent intent = new Intent(activity, CreateResultActivity.class);
        intent.putExtra(INTENT_EXTRA_RESULT, createResultModel.toJson().toString());
        intent.putExtra(INTENT_EXTRA_FROM, isFromHistory);
        // 使用 startActivityForResult 启动，以便返回时能通知输入页关闭
        activity.startActivityForResult(intent, REQUEST_CODE_CREATE_RESULT_PAGE);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_create_result;
    }

    //初始化数据 ->解析结果模型并生成二维码Bitmap
    @Override
    protected void initData() {
        Intent intent = getIntent();
        //1,结果模型不为空，获取展示信息
        if (null != createResultModel) {
            qrMsg = createResultModel.getResult();
            showMsg = createResultModel.getShowText();
        }
        //2,如果静态变量为空（如进程重启），尝试从 Intent 中恢复数据
        if (createResultModel == null) {
            String resultModelString = intent.getStringExtra(INTENT_EXTRA_RESULT);
            if (resultModelString != null) {
                try {
                    createResultModel = CreateResultModel.fromJson(resultModelString);
                } catch (Exception e) {
                    ThrowableEXT.log(e);
                }
            }
        }
        //3,判断是否来自历史记录界面
        isFromHistory = intent.getBooleanExtra(INTENT_EXTRA_FROM, false);
        if (createResultModel == null) {
            finish();
            return;
        }
        try {
            //4,调用底层库 CreatorHelper 根据模型生成二维码位图
            qrBitmap = CreatorHelper.getBitmap(createResultModel, new CreatorConfig());
            if (!isFromHistory) {
                AnalyticsHelper.logKeyCreateFormat(createResultModel.getCreateFormat().name());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        findView();
    }

    //初始化视图,设置图片和文本显示
    @Override
    protected void initView() {
        if (createResultModel == null) {
            return;
        }
        //2,动态调整二维码图片的大小以适配不同屏幕
        resizeQrSize();
        if (null != BaseCreateActivity.createType) {
            mQrCategoryIv.setImageResource(BaseCreateActivity.createType.getDrawableIcon());
            mQrCategoryTv.setText(this.getResources().getString(BaseCreateActivity.createType.getStringSrc()));
        }
        mQrShowMsgTv.setText(showMsg);
        mQrIv.setImageBitmap(qrBitmap);
        mQrIv.setScaleType(ImageView.ScaleType.FIT_XY);
        AnalyticsHelper.logCreateResultTotal("结果页展示数");
        //3,适配沉浸式系统栏边距
        enableInsetsView(findViewById(R.id.fl_toolbar_container), true, false);
        enableInsetsView(findViewById(R.id.main), false, true);
    }

    //初始化点击事件
    @Override
    protected void initAction() {
        if (createResultModel == null) {
            return;
        }
        mBackIv.setOnClickListener(this);
        mCloseIv.setOnClickListener(this);
        mSaveBtu.setOnClickListener(this);
        mShareBtu.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        //1,如果不是查看历史且未订阅，展示功能按钮区域
        if (!isSubscribe && !isFromHistory) {
            freeOptionCl.setVisibility(View.VISIBLE);
        }
        //2,如果是新生成的二维码，保存到本地历史数据库
        saveHistory();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        AnalyticsHelper.logCreateResultTotal("点击返回");
        closeMe();
    }
    //设置点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            AnalyticsHelper.logCreateResultTotal("点击返回");
            backMe();
        } else if (id == R.id.iv_close) {
            AnalyticsHelper.logCreateResultTotal("点击关闭");
            closeMe();
        } else if (id == R.id.btu_save) {
            AnalyticsHelper.logCreateResultTotal("点击保存");
            save();
        } else if (id == R.id.btu_share) {
            AnalyticsHelper.logCreateResultTotal("点击分享");
            shareBitmap();
        }
    }
    //初始化绑定视图控件
    private void findView() {
        mBackIv = findViewById(R.id.iv_back);
        mCloseIv = findViewById(R.id.iv_close);
        mQrCategoryIv = findViewById(R.id.iv_icon);
        mQrCategoryTv = findViewById(R.id.tv_category);
        mQrShowMsgTv = findViewById(R.id.tv_show_info);
        mQrIv = findViewById(R.id.iv_qr);
        mSaveBtu = findViewById(R.id.btu_save);
        mShareBtu = findViewById(R.id.btu_share);
        freeOptionCl = findViewById(R.id.layout_create_result_bottom);
    }

    //动态计算二维码容器的大小，确定在屏幕中占据合适的比例(约70%)
    private void resizeQrSize() {
        mQrIv.setAdjustViewBounds(true);
        mCloseIv.setAdjustViewBounds(true);
        mCloseIv.setScaleType(ImageView.ScaleType.CENTER);
        WindowManager m = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(outMetrics);
        // 获取屏幕短边，计算二维码大小
        int size = Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
        size = (int) ((size - DimensionUtil.dpTopx(getContext(), 32)) * 0.7);
        ViewGroup.LayoutParams layoutParams = mQrIv.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        mQrIv.setLayoutParams(layoutParams);
    }
    //将本次创建的二维码信息保存到本地SQL数据库
    private void saveHistory() {
        if (isFromHistory) {
            return;
        }
        //1,构造 Result 对象并存入数据库
        Result result = new Result(qrMsg, null, null, BarcodeFormat.QR_CODE);
        CreateHistoryManager createHistoryManager = new CreateHistoryManager(this);
        //2,传入当前创建的分类名称（如 Youtube、TEXT 等），以便历史列表识别图标和类型
        String createTypeName = BaseCreateActivity.createType != null ? BaseCreateActivity.createType.name() : "TEXT";
        createHistoryManager.addCreateHistoryItem(result, showMsg, createTypeName);
    }

    //关闭当前页并通知上一页(输入页)也一并关闭
    private void closeMe() {
        setResult(RESULT_CODE_CLOSE_CREATE_PAGE);
        this.finish();
    }

    private void backMe() {
        this.finish();
    }

    //保存图片到相册，处理 Android 10 (Q) 及以上版本的存储适配
    private void save() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //1,Android 10+ 使用 MediaStore API 进行保存（无需申请权限）
                CacheImageUtil.saveBitmap(getContext(), qrBitmap, String.valueOf(System.currentTimeMillis()), "QR Code");
                toastSaveSuccess();
            } else {
                // 2,ndroid 10 以下需要申请 SD 卡写入权限
                PermissionHelper.with(this).permission(Permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        //授权成功
                        saveBeforeQ();
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        toastNoPermission();
                    }
                });
            }
        } catch (Exception e) {
            AnalyticsHelper.logException(e);
        }
    }
    //Android 10 之前的传统保存逻辑：弹出重命名对话框，然后保存到磁盘
    @SuppressLint("SetTextI18n")
    private void saveBeforeQ() {
        try {
            AlertDialog qrSaveDialog;
            final boolean[] isNameExit = {false};
            final String[] saveName = {""};
            LayoutInflater layoutInflater = LayoutInflater.from(CreateResultActivity.this);
            View view = layoutInflater.inflate(R.layout.layout_creat_save_alert, null);
            final EditText editText = view.findViewById(R.id.et_input);
            final TextView textView = view.findViewById(R.id.tv_file_exit_hint);

            //1,获取该类型已保存过的次数，用于自动生成默认文件名（如 YouTube_1）
            qrSaveTimes = SPUtil.getInstance().get(CreateCategory.PREF + BaseCreateActivity.createType.name(), 0);
            editText.setText(getString(BaseCreateActivity.createType.getStringSrc()) + "_" + (qrSaveTimes + 1));
            final ImageView imageView = view.findViewById(R.id.iv_clear);
            imageView.setOnClickListener(v -> editText.setText(""));

            //2,设置初始文件名并检查重名
            File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File myFile = new File(appDir.getPath() + "/QR Code/");
            if (!myFile.exists()) {
                myFile.mkdir();
            }
            String fileName = editText.getText().toString() + ".PNG";
            final File file = new File(myFile, fileName);
            if (file.exists()) {
                isNameExit[0] = true;
                textView.setVisibility(View.VISIBLE);
                isSaveAble = false;
            } else {
                isNameExit[0] = false;
                textView.setVisibility(View.GONE);
                isSaveAble = true;
            }

            //3,监听输入，实时检查文件名冲突
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File myFile = new File(appDir.getPath() + "/QR Code/");
                    if (!myFile.exists()) {
                        myFile.mkdir();
                    }
                    String fileName = s + ".PNG";
                    final File file = new File(myFile, fileName);
                    if (file.exists()) {
                        isNameExit[0] = true;
                        textView.setVisibility(View.VISIBLE);
                        isSaveAble = false;
                    } else {
                        isNameExit[0] = false;
                        textView.setVisibility(View.GONE);
                        isSaveAble = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            qrSaveDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.file_name)).setView(view).setCancelable(false).setPositiveButton(getString(R.string.save), null).setNegativeButton(getString(R.string.button_cancel), null).show();

            Button b = qrSaveDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view1 -> {
                if (isSaveAble) {
                    if (editText.getText().toString().length() < 1) {
                        ToastUtil.show(getContext(), R.layout.layout_wifi_toast_failed, getString(R.string.hint_save_qrcode));
                        return;
                    }
                    saveName[0] = editText.getText().toString();

                    if (qrSaveDialog != null) {
                        qrSaveDialog.dismiss();
                    }
                    saveBitmap(qrBitmap, saveName[0]);
                } else {
                    ToastUtil.show(getContext(), R.layout.layout_wifi_toast_failed, getString(R.string.already_in_use));
                }
            });
        } catch (Exception e) {
            AnalyticsHelper.logException(e);
        }
    }
    //将位图持久化到存储空间，并通知相册刷新
    private void saveBitmap(Bitmap bm, String picName) {
        try {
            //1,首先尝试保存到 Pictures 目录下的 QR Code 文件夹
            File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File myFile = new File(appDir.getPath() + "/QR Code/");
            if (!myFile.exists()) {
                myFile.mkdir();
            }
            String fileName = picName + ".PNG";
            File file = new File(myFile, fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 此处虽然文件名叫 PNG，但压缩用了 JPEG
                fos.flush();
            } catch (Exception e) {
                //2,如果 Pictures 目录不可写（某些特殊机型），降级保存到 SD 卡根目录
                appDir = Environment.getExternalStorageDirectory();
                myFile = new File(appDir.getPath() + "/QR Code/");
                if (!myFile.exists()) {
                    myFile.mkdir();
                }
                fileName = picName + ".PNG";
                file = new File(myFile, fileName);
                fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                } catch (Exception ee) {
                    ee.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception eee) {
                        e.printStackTrace();
                    }
                }
            } finally {
                //关闭输出流和打印异常信息
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //3,,更新该类型的保存次数
            qrSaveTimes++;
            SPUtil.getInstance().set(CreateCategory.PREF + BaseCreateActivity.createType.name(), qrSaveTimes);

            //4,关键：通知系统扫描新文件，使其立即在相册中可见
            File newFile = file;
            if (file != null && file.length() > 0) {
                new Thread(() -> MediaScannerConnection.scanFile(getContext(), new String[]{newFile.getAbsolutePath()}, null, (path, uri) -> runOnUiThread(this::toastSaveSuccess))).start();
            }
        } catch (Exception e) {
            AnalyticsHelper.logException(e);
        }
    }

    //分享逻辑:将图片保存到私有目录,生成conetent://URL 并通过Intent分享
    private void shareBitmap() {
        try {
            if (qrBitmap == null) {
                return;
            }
            //1,存到应用的私有文件目录，安全性更高
            File appDir = getContext().getFilesDir();
            File myFile = new File(appDir.getPath());
            if (!myFile.exists()) {
                myFile.mkdir();
            }
            String fileName = "share.PNG";
            final File file = new File(myFile, fileName);
            FileOutputStream fos = null;
            try {
                //2,利用输出流实现创建分享图片到本地
                fos = new FileOutputStream(file);
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭流
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //3,构建分享 Intent
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);
            share_intent.setType("image/*");
            //4,授权接收方应用临时读取该 URI 的权限
            share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //5,通过 FileProvider 获取安全 URI
            share_intent.putExtra(Intent.EXTRA_STREAM, AppFileProvider.getUriFromFile(getContext(), file));
            //6,附带一段推荐下载应用的文本
            share_intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.qr_share_text_1, "https://play.google.com/store/apps/details?id=qrscanner.barcodescanner.barcodereader.qrcodereader"));
            //7,弹出系统分享选择器
            share_intent = Intent.createChooser(share_intent, getResources().getString(R.string.button_share));
            startActivity(share_intent);
        } catch (Exception e) {
            AnalyticsHelper.logException(e);
        }
    }

    private void toastNoPermission() {
        ToastUtil.show(getContext(), R.layout.layout_wifi_toast_failed, getString(R.string.toast_no_storage_permission));
    }

    private void toastSaveSuccess() {
        ToastUtil.show(getContext(), R.layout.layout_wifi_toast, getResources().getString(R.string.saved_to_gallery));
    }

}