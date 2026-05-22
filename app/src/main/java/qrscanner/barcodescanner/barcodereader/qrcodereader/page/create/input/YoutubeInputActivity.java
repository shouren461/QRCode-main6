package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drojian.qrcode.createlib.create.format.CreateYoutubeModel;
import com.drojian.qrcode.utillib.extension.EditTextKt;
import com.drojian.qrcode.utillib.utils.ClipboardUtil;
import com.drojian.qrcode.utillib.utils.OpenAppUtil;
import com.drojian.qrcode.utillib.utils.SoftInputUtil;
import com.drojian.qrcode.utillib.utils.StringUtils;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

/**
 * YouTube 二维码创建输入页面
 * 支持通过 URL、视频 ID 或 频道 ID 生成二维码
 */
public class YoutubeInputActivity extends BaseCreateActivity implements View.OnClickListener {
    // 定义三种不同的 YouTube 输入模式
    private final int CATEGORY_URL = 1;      // 输入完整的 URL
    private final int CATEGORY_VIDEO = 2;    // 只输入视频 ID
    private final int CATEGORY_CHANNEL = 3;  // 只输入频道 ID

    private ImageView iv_back, iv_icon, iv_clipboard, mCreateImageView;
    private TextView tv_title, tv_category_url, tv_category_video, tv_category_channel, tv_clipboard, mCreateTextView, mOpenTv;
    private EditText et_input;
    private LinearLayout ll_mode;
    private int category = CATEGORY_VIDEO; // 默认选择视频 ID 模式
    private String clipboard = ""; // 记录粘贴板内容

    /**
     * 跳转到本页面的静态方法
     */
    public static void showMe(Context context) {
        Intent intent = new Intent(context, YoutubeInputActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_input_youtube;
    }

    @Override
    protected void initData() {
        // ... UI 控件绑定 ...
        iv_back = findViewById(R.id.iv_back);
        iv_icon = findViewById(R.id.iv_icon);
        tv_title = findViewById(R.id.tv_title);
        tv_category_url = findViewById(R.id.tv_category_url);
        tv_category_video = findViewById(R.id.tv_category_video);
        tv_category_channel = findViewById(R.id.tv_category_channel);
        et_input = findViewById(R.id.et_input);
        iv_clipboard = findViewById(R.id.iv_clipboard);
        tv_clipboard = findViewById(R.id.tv_clipboard);
        ll_mode = findViewById(R.id.ll_mode);
        mCreateTextView = findViewById(R.id.tv_create);
        mCreateImageView = findViewById(R.id.iv_create);
        mOpenTv = findViewById(R.id.tv_open);

        findViewById(R.id.view_create).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        // 设置顶部的图标和标题
        iv_icon.setImageResource(R.drawable.vector_ic_youtube);
        iv_icon.setBackgroundResource(R.drawable.bg_creat_input_icon);
        tv_title.setText(R.string.youtube);
    }

    @Override
    protected void initAction() {
        // 检查粘贴板
        setClipboard();
        mOpenTv.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_category_url.setOnClickListener(this);
        tv_category_video.setOnClickListener(this);
        tv_category_channel.setOnClickListener(this);
        tv_clipboard.setOnClickListener(this);
        
        // 监听输入框变化，实时验证内容是否合法
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 检查输入是否为空或全是空格
                if (s.length() < 1) {
                    setCreatable(false);
                } else {
                    setCreatable(!StringUtils.isOnlySpace(et_input.getText().toString()));
                }
                
                // 粘贴板提示逻辑：如果用户输入的内容与粘贴板内容前缀匹配，且包含 YouTube 关键字，则显示快捷粘贴提示
                if (s.length() < clipboard.length() && s.length() > 0) {
                    if (clipboard.substring(0, s.length()).equalsIgnoreCase(String.valueOf(s))) {
                        if ((clipboard.toLowerCase().contains("youtube.com")) ||
                                (clipboard.toLowerCase().contains("youtu.be"))) {
                            iv_clipboard.setVisibility(View.VISIBLE);
                            tv_clipboard.setVisibility(View.VISIBLE);
                        }
                    } else {
                        iv_clipboard.setVisibility(View.GONE);
                        tv_clipboard.setVisibility(View.GONE);
                    }
                } else if ((s.length() == 0 && clipboard.toLowerCase().contains("youtube.com")) ||
                        (s.length() == 0 && clipboard.toLowerCase().contains("youtu.be"))) {
                    iv_clipboard.setVisibility(View.VISIBLE);
                    tv_clipboard.setVisibility(View.VISIBLE);

                } else {
                    iv_clipboard.setVisibility(View.GONE);
                    tv_clipboard.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 设置按钮是否可点击的状态及外观
     */
    public void setCreatable(boolean creatable) {
        isCreatable = creatable;
        if (creatable) {
            mCreateTextView.setTextColor(Color.parseColor("#4880FF"));
            mCreateImageView.setImageResource(R.drawable.ic_check_blue);
        } else {
            mCreateTextView.setTextColor(Color.parseColor("#9AA7B9"));
            mCreateImageView.setImageResource(R.drawable.ic_check_black);
        }
    }


    /**
     * 获取系统粘贴板内容，如果是 YouTube 链接则提示用户
     */
    private void setClipboard() {
        ClipboardUtil.get(this, text -> {
            clipboard = text;
            if (null != clipboard && (clipboard.toLowerCase().contains("youtube.com") || clipboard.toLowerCase().contains("youtu.be"))) {
                iv_clipboard.setVisibility(View.VISIBLE);
                tv_clipboard.setVisibility(View.VISIBLE);
                tv_clipboard.setText(clipboard);
            } else {
                iv_clipboard.setVisibility(View.GONE);
                tv_clipboard.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 自动弹出键盘
        SoftInputUtil.show(et_input);
    }

    /**
     * 核心逻辑：执行二维码创建并显示结果
     */
    private void createQR() {
        submitEventTracking(); // 埋点统计
        String inputEtStr = EditTextKt.getEditTextString(et_input);
        baseResultModel = new CreateYoutubeModel();
        // 根据当前选中的模式（URL、视频或频道）设置对应的数据
        if (category == CATEGORY_URL) {
            ((CreateYoutubeModel) baseResultModel).setUrl(inputEtStr);
        } else if (category == CATEGORY_VIDEO) {
            ((CreateYoutubeModel) baseResultModel).setVideoId(inputEtStr);
        } else if (category == CATEGORY_CHANNEL) {
            ((CreateYoutubeModel) baseResultModel).setChannelId(inputEtStr);
        }
        // 设置列表页展示的精简标题
        baseResultModel.setShowText(getDisplayContent(inputEtStr));
        showResult(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            YoutubeInputActivity.this.finish();
        } else if (v.getId() == R.id.view_create) {
            // 点击生成按钮
            if (isCreatable) {
                createQR();
            } else {
                showInputNullToast();
            }
        } else if (v.getId() == R.id.tv_category_url) {
            // 切换到 URL 模式
            category = CATEGORY_URL;
            tv_category_url.setBackgroundResource(R.drawable.shape_creat_wifi_mode_item_bg_select);
            tv_category_url.setTextColor(getResources().getColor(R.color.white));
            tv_category_video.setBackgroundColor(Color.parseColor("#00000000"));
            tv_category_video.setTextColor(Color.parseColor("#757575"));
            tv_category_channel.setBackgroundColor(Color.parseColor("#00000000"));
            tv_category_channel.setTextColor(Color.parseColor("#757575"));
            et_input.setHint(getString(R.string.enter_youtube_url));
            ll_mode.setBackgroundResource(R.drawable.shape_creat_wifi_mode_bg);
        } else if (v.getId() == R.id.tv_category_video) {
            // 切换到视频 ID 模式
            category = CATEGORY_VIDEO;
            tv_category_url.setBackgroundColor(Color.parseColor("#00000000"));
            tv_category_url.setTextColor(Color.parseColor("#757575"));
            tv_category_video.setBackgroundResource(R.drawable.shape_creat_wifi_mode_item_bg_select);
            tv_category_video.setTextColor(getResources().getColor(R.color.white));
            tv_category_channel.setBackgroundColor(Color.parseColor("#00000000"));
            tv_category_channel.setTextColor(Color.parseColor("#757575"));
            et_input.setHint(getString(R.string.enter_youtube_video_id));
            ll_mode.setBackgroundResource(R.drawable.shape_creat_wifi_mode_bg);
        } else if (v.getId() == R.id.tv_category_channel) {
            // 切换到频道 ID 模式
            category = CATEGORY_CHANNEL;
            tv_category_url.setBackgroundColor(Color.parseColor("#00000000"));
            tv_category_url.setTextColor(Color.parseColor("#757575"));
            tv_category_video.setBackgroundColor(Color.parseColor("#00000000"));
            tv_category_video.setTextColor(Color.parseColor("#757575"));
            tv_category_channel.setBackgroundResource(R.drawable.shape_creat_wifi_mode_item_bg_select);
            tv_category_channel.setTextColor(getResources().getColor(R.color.white));
            et_input.setHint(getString(R.string.enter_your_youtube_channel_id));
            ll_mode.setBackgroundResource(R.drawable.shape_creat_wifi_mode_bg);
        } else if (v.getId() == R.id.tv_clipboard) {
            // 点击粘贴内容
            et_input.setText(clipboard);
            et_input.setSelection(clipboard.length());
        } else if (v.getId() == R.id.tv_open) {
            // 埋点并直接打开 YouTube App
            AnalyticsHelper.logYoutubePremiumResult("youtube_click");
            OpenAppUtil.openYoutube(this);
        }
    }


    /**
     * 埋点统计：分析用户输入的 YouTube 类型和内容深度
     */
    private void submitEventTracking() {
        if (et_input.getText().toString().length() > 0) {
            if (et_input.getText().toString().contains("youtube.com") || et_input.getText().toString().contains("youtu.be")) {
                AnalyticsHelper.logYoutubePremiumResult("type_URL");
                if (et_input.getText().toString().contains("/channel")) {
                    AnalyticsHelper.logYoutubePremiumResult("URL-https://www.youtube.com/channel/");
                }
                if (et_input.getText().toString().contains("/watch")) {
                    AnalyticsHelper.logYoutubePremiumResult("URL-https://www.youtube.com/watch?v=");
                }
            } else {
                if (category == CATEGORY_CHANNEL) {
                    AnalyticsHelper.logYoutubePremiumResult("type_channel_id");
                } else {
                    AnalyticsHelper.logYoutubePremiumResult("type_video_id");
                }
            }
        }
        AnalyticsHelper.logCreateResultNumber("youtube");
    }


}
