package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.HistoryItem;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.QRUtil;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.ResultFormatUtil;


/**
 * 创建历史列表的适配器
 * 与扫描历史适配器类似，但针对的是“生成的二维码”数据
 */
public class CreateHistoryRCVAdapter extends RecyclerView.Adapter {
    private LayoutInflater layoutInflater;

    private OnItemClickListener listener;

    private int selectModel = HistoryFragment.SELECT_MODEL_NORMAL;
    private List<HistoryItemViewModel> historyItemViewModelList = new ArrayList<>();

    public CreateHistoryRCVAdapter(Context context, OnItemClickListener listener) {
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据源
     */
    public void setData(Activity activity, List<HistoryItem> historyItemList) {
        retrofitData(activity, historyItemList);
        notifyDataSetChanged();
    }

    /**
     * 切换普通/编辑模式
     */
    public void setSelectModel(int selectModel) {
        if (this.selectModel != selectModel) {
            this.selectModel = selectModel;
            for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
                historyItemViewModel.isSelected = false;
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 全选逻辑
     */
    public void selectAll() {
        boolean isAllSelected = true;
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            if (!historyItemViewModel.isSelected) {
                isAllSelected = false;
                break;
            }
        }
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            historyItemViewModel.isSelected = !isAllSelected;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取选中项的索引
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
     * 获取选中项的总数
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
     * 数据加工：将原始 HistoryItem 转化为界面展示的 ViewModel
     */
    private void retrofitData(Activity activity, List<HistoryItem> historyItemList) {
        historyItemViewModelList = new ArrayList<>(historyItemList.size());
        try {
            for (HistoryItem historyItem : historyItemList) {
                HistoryItemViewModel historyItemViewModel = new HistoryItemViewModel();
                historyItemViewModel.historyItem = historyItem;

                if (historyItem.getResult() == null || null == historyItem.getResult().getBarcodeFormat()) {
                    continue;
                }

                // 根据条码格式反向推导出它是哪种“创建类型”（如 YouTube）
                CreateType createType = QRUtil.getCategoryByCategoryText(historyItem.getResult().getBarcodeFormat().toString());
                if (null != createType) {
                    historyItemViewModel.typeName = activity.getResources().getString(createType.getStringSrc());
                    historyItemViewModel.typeIconResId = QRUtil.getIconByCategoryText(historyItem.getResult().getBarcodeFormat().toString());
                    // 格式化预览内容
                    historyItemViewModel.content = ResultFormatUtil.extractCreateDisplayText(historyItem.getResult()).replace("\n", " ");
                    historyItemViewModel.isSelected = false;

                    historyItemViewModelList.add(historyItemViewModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            // 长按进入编辑模式
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

                    if (listener != null) {
                        listener.onItemSelectModeChanged(HistoryFragment.SELECT_MODEL_SELECTED);
                    }
                } else {
                    if (itemPosition < historyItemViewModelList.size()) {
                        historyItemViewModelList.get(itemPosition).isSelected = !historyItemViewModelList.get(itemPosition).isSelected;
                        notifyItemChanged(itemPosition);
                    }
                }
                return true;
            });

            // 点击逻辑
            viewHolder.itemView.setOnClickListener(view -> {
                int itemPosition = historyViewHolder.getAdapterPosition();
                if (itemPosition < 0) {
                    itemPosition = 0;
                }
                if (selectModel == HistoryFragment.SELECT_MODEL_NORMAL) {
                    if (listener != null && itemPosition < historyItemViewModelList.size()) {
                        listener.onItemClick(itemPosition, historyItemViewModelList.get(itemPosition).historyItem);
                    }
                } else {
                    if (itemPosition < historyItemViewModelList.size()) {
                        historyItemViewModelList.get(itemPosition).isSelected = !historyItemViewModelList.get(itemPosition).isSelected;
                        notifyItemChanged(itemPosition);
                    }
                }
            });

            try {
                HistoryItemViewModel historyItemViewModel = historyItemViewModelList.get(historyViewHolder.getAdapterPosition());

                historyViewHolder.typeIV.setImageResource(historyItemViewModel.typeIconResId);
                historyViewHolder.typeNameTV.setText(historyItemViewModel.typeName);
                historyViewHolder.contentTV.setText(historyItemViewModel.content);

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

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView typeIV;
        AppCompatTextView typeNameTV;
        AppCompatTextView contentTV;
        AppCompatImageView moreViewIV;
        AppCompatCheckBox selectCB;

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
        void onItemSelectModeChanged(int selectMode);

        void onItemClick(int position, HistoryItem historyItem);
    }

    class HistoryItemViewModel {
        HistoryItem historyItem;
        int typeIconResId;
        String typeName;
        String content;
        boolean isSelected;
    }
}
