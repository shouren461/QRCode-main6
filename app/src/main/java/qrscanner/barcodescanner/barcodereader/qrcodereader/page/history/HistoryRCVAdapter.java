package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat;
import com.drojian.qrcode.scanresultlib.BaseResultHandler;
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig;
import com.drojian.qrcode.scanresultlib.ResultHandlerFactory;
import com.drojian.qrcode.zxinglib.ZXingUtil;

import java.util.ArrayList;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.HistoryItem;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.QRUtil;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.ResultFormatUtil;


/**
 * 扫描历史列表的适配器
 * 负责将数据库中的原始扫描记录（HistoryItem）转换为带图标、类型名称和预览内容的视图模型（HistoryItemViewModel）并渲染
 */
public class HistoryRCVAdapter extends RecyclerView.Adapter {
    private LayoutInflater layoutInflater;

    private OnItemClickListener listener; // 列表项点击和状态切换的回调
    private Activity activity;
    private int selectModel = HistoryFragment.SELECT_MODEL_NORMAL; // 当前选择模式（普通或多选编辑）
    private List<HistoryItemViewModel> historyItemViewModelList = new ArrayList<>(); // 转换后的视图模型列表

    public HistoryRCVAdapter(Activity context, OnItemClickListener listener) {
        this.listener = listener;
        this.activity = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据源：将数据库实体转换为 UI 专用的 ViewModel
     */
    public void setData(Activity activity, List<HistoryItem> historyItemList) {
        retrofitData(activity, historyItemList);
        notifyDataSetChanged();
    }

    /**
     * 切换选择模式（普通模式 vs 编辑模式）
     */
    public void setSelectModel(int selectModel) {
        if (this.selectModel != selectModel) {
            this.selectModel = selectModel;
            // 切换模式时重置所有选中状态
            for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
                historyItemViewModel.isSelected = false;
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 全选或全取消逻辑
     */
    public void selectAll() {
        boolean isAllSelected = true;
        // 先检查是否已经全选
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            if (!historyItemViewModel.isSelected) {
                isAllSelected = false;
                break;
            }
        }
        // 如果已经全选，则全取消；否则全选
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            historyItemViewModel.isSelected = !isAllSelected;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取当前被选中的项在列表中的位置索引
     */
    public List<Integer> getSelectedItemPositionList() {
        List<Integer> selectedItemPositionList = new ArrayList<>();
        if (historyItemViewModelList != null && !historyItemViewModelList.isEmpty()) {
            for (int i = 0; i < historyItemViewModelList.size(); i++) {
                if (historyItemViewModelList.get(i).isSelected) {
                    selectedItemPositionList.add(i);
                }
            }
        }
        return selectedItemPositionList;
    }

    /**
     * 获取当前选中的项总数
     */
    public int getSelectedItemCount() {
        if (historyItemViewModelList == null || historyItemViewModelList.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            if (historyItemViewModel.isSelected) {
                count++;
            }
        }
        return count;
    }

    /**
     * 数据加工：将数据库原始 Data 转化为 UI 展示所需的 Model
     */
    private void retrofitData(Activity activity, List<HistoryItem> historyItemList) {
        historyItemViewModelList = new ArrayList<>(historyItemList.size());

        for (HistoryItem historyItem : historyItemList) {
            HistoryItemViewModel historyItemViewModel = new HistoryItemViewModel();
            historyItemViewModel.historyItem = historyItem;

            if (historyItem.getResult() == null) {
                continue;
            }

            // 调用 ZXing 库解析结果，判断条码类型（URL, WiFi, Text 等）
            ScanResultModel model = ZXingUtil.retrofitResult(historyItem.getResult());
            if (null != model){
                // 根据类型获取对应的图标资源和名称
                BaseResultHandler baseResultHandler = ResultHandlerFactory.makeResultHandler(activity, model, new ResultHandlerConfig());
                ParsedFormat parsedFormat = baseResultHandler.getBaseParseModel().getParsedFormat();
                historyItemViewModel.typeIconResId = QRUtil.getResultIcon(parsedFormat, model);
                historyItemViewModel.typeName = activity.getResources().getString(QRUtil.getResultName(parsedFormat, model));
                // 格式化预览内容，去除换行符，方便单行展示
                historyItemViewModel.content = ResultFormatUtil.extractScanDisplayText(historyItem.getResult(), parsedFormat).replace("\n", " ");
                historyItemViewModel.isSelected = false;

                historyItemViewModelList.add(historyItemViewModel);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistoryViewHolder(layoutInflater.inflate(R.layout.item_rcv_history, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof HistoryViewHolder) {
            final HistoryViewHolder historyViewHolder = (HistoryViewHolder) viewHolder;

            // 长按事件：如果在普通模式下长按，则进入编辑模式并选中当前项
            viewHolder.itemView.setOnLongClickListener(view -> {
                int itemPosition = historyViewHolder.getAdapterPosition();
                if (itemPosition < 0) {
                    itemPosition = 0;
                }
                if (selectModel == HistoryFragment.SELECT_MODEL_NORMAL) {
                    selectModel = HistoryFragment.SELECT_MODEL_SELECTED;

                    for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
                        historyItemViewModel.isSelected = false;
                    }
                    if (itemPosition < historyItemViewModelList.size()) {
                        historyItemViewModelList.get(itemPosition).isSelected = true;
                    }
                    notifyDataSetChanged();

                    // 通知外部 Fragment 更新顶部菜单 UI
                    if (listener != null) {
                        listener.onItemSelectModeChanged(HistoryFragment.SELECT_MODEL_SELECTED);
                    }
                } else {
                    // 如果已经是编辑模式，长按等同于反选
                    if (itemPosition < historyItemViewModelList.size()) {
                        historyItemViewModelList.get(itemPosition).isSelected = !historyItemViewModelList.get(itemPosition).isSelected;
                        notifyItemChanged(itemPosition);
                    }
                }
                return true;
            });

            // 点击事件逻辑
            viewHolder.itemView.setOnClickListener(view -> {
                int itemPosition = historyViewHolder.getAdapterPosition();
                if (itemPosition < 0) {
                    itemPosition = 0;
                }
                if (selectModel == HistoryFragment.SELECT_MODEL_NORMAL) {
                    // 普通模式点击：查看详情或跳转结果页
                    if (listener != null && itemPosition < historyItemViewModelList.size()) {
                        listener.onItemClick(itemPosition, historyItemViewModelList.get(itemPosition).historyItem);
                    }
                } else {
                    // 编辑模式点击：切换勾选状态
                    if (itemPosition < historyItemViewModelList.size()) {
                        historyItemViewModelList.get(itemPosition).isSelected = !historyItemViewModelList.get(itemPosition).isSelected;
                        notifyItemChanged(itemPosition);
                    }
                }
            });

            try {
                HistoryItemViewModel historyItemViewModel = historyItemViewModelList.get(historyViewHolder.getAdapterPosition());

                // 设置图标、标题和内容
                historyViewHolder.typeIV.setImageResource(historyItemViewModel.typeIconResId);
                historyViewHolder.typeNameTV.setText(historyItemViewModel.typeName);
                historyViewHolder.contentTV.setText(historyItemViewModel.content);

                // 根据当前模式切换右侧图标：普通模式显示“更多/跳转”箭头，编辑模式显示“勾选框”
                if (selectModel == HistoryFragment.SELECT_MODEL_NORMAL) {
                    historyViewHolder.moreViewIV.setVisibility(View.VISIBLE);
                    historyViewHolder.selectCB.setVisibility(View.GONE);
                } else {
                    historyViewHolder.moreViewIV.setVisibility(View.GONE);
                    historyViewHolder.selectCB.setVisibility(View.VISIBLE);
                    historyViewHolder.selectCB.setChecked(historyItemViewModel.isSelected);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return historyItemViewModelList.size();
    }

    /**
     * ViewHolder：持有列表项中的所有控件引用
     */
    class HistoryViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView typeIV;     // 类型图标
        AppCompatTextView typeNameTV;  // 类型名称（如：网址、文本）
        AppCompatTextView contentTV;   // 预览内容
        AppCompatImageView moreViewIV; // 右侧箭头
        AppCompatCheckBox selectCB;    // 编辑模式下的勾选框

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            typeIV = itemView.findViewById(R.id.iv_type);
            typeNameTV = itemView.findViewById(R.id.tv_type);
            contentTV = itemView.findViewById(R.id.tv_second_value);
            moreViewIV = itemView.findViewById(R.id.iv_right);
            selectCB = itemView.findViewById(R.id.cb_select);
        }
    }

    public interface OnItemClickListener {
        // 当列表切换到多选/普通模式时回调
        void onItemSelectModeChanged(int selectMode);

        // 普通模式下的列表项点击回调
        void onItemClick(int position, HistoryItem historyItem);
    }

    /**
     * UI 专用数据模型：保存 HistoryItem 以及解析后的 UI 展示信息
     */
    class HistoryItemViewModel {
        HistoryItem historyItem; // 原始数据库数据
        int typeIconResId;      // 类型对应的图标 ID
        String typeName;        // 类型名称字符串
        String content;         // 格式化后的预览文本
        boolean isSelected;     // 标记在编辑模式下是否被勾选
    }
}
