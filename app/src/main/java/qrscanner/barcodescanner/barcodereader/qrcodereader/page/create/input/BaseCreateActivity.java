package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input;

import static qrscanner.barcodescanner.barcodereader.qrcodereader.data.ConstantKt.REQUEST_CODE_CREATE_RESULT_PAGE;
import static qrscanner.barcodescanner.barcodereader.qrcodereader.data.ConstantKt.RESULT_CODE_CLOSE_CREATE_PAGE;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.drojian.qrcode.createlib.create.CreateResultModel;
import com.drojian.qrcode.viewlib.toast.ToastUtil;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateResultActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

/**
 * 所有创建二维码输入页面的基类
 * 封装了通用的 Activity 启动逻辑、输入检查、以及结果显示逻辑
 */
public abstract class BaseCreateActivity extends BaseActivity {
    // 标记当前输入是否合法，是否可以点击“生成”按钮
    protected boolean isCreatable = false;
    // 当前正在创建的类型，默认为 YouTube
    public static CreateType createType = CreateType.YOUTUBE;
    // 存储创建结果的数据模型
    protected CreateResultModel baseResultModel;

    @Override
    protected abstract int getLayout();

    @Override
    protected abstract void initData();

    @Override
    protected abstract void initView();

    @Override
    protected abstract void initAction();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果从结果页返回，并且结果码是“关闭创建页”，则直接结束当前输入页面
        if (requestCode == REQUEST_CODE_CREATE_RESULT_PAGE && resultCode == RESULT_CODE_CLOSE_CREATE_PAGE) {
            this.finish();
        }
    }

    /**
     * 当用户点击生成但输入为空时，弹出提示
     */
    protected void showInputNullToast() {
        ToastUtil.show(getContext(), R.layout.layout_wifi_toast_failed, getResources().getString(R.string.toast_text_null));
    }

    /**
     * 从多个字符串参数中寻找第一个非空的内容，用于列表展示标题
     */
    protected String getDisplayContent(String... value) {
        for (int i = 0; i < value.length; i++) {
            if (!value[i].trim().isEmpty()) {
                return value[i];
            }
        }
        return "";
    }

    /**
     * 生成结果并跳转到结果展示页面
     * @param isHistory 标记是否是查看历史记录
     */
    protected void showResult(boolean isHistory) {
        // 对结果模型进行格式化（将输入字段组合成二维码协议格式）
        baseResultModel.formatResult();
        // 跳转到结果页
        CreateResultActivity.showMe(this, baseResultModel, isHistory);
    }

    /**
     * 统一的入口方法：根据类型跳转到具体的创建 Activity
     */
    public static void startBase(Context context, CreateType type) {
        createType = type;
        switch (type) {
            case CALENDAR:
                CalenderInputActivity.showMe(context);
                AnalyticsHelper.logCreateHome("calendar_click"); // 埋点：点击日历
                break;
            case YOUTUBE:
                YoutubeInputActivity.showMe(context);
                AnalyticsHelper.logCreateHome("youtube_click"); // 埋点：点击 YouTube
                break;
            default:
        }
    }
}
