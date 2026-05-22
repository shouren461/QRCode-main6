package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history;

import android.view.View;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drojian.qrcode.createlib.create.CreateResultModel;
import com.drojian.qrcode.createlib.create.format.CreateCalendarModel;
import com.drojian.qrcode.zxinglib.ZXingFormatUtil;
import com.google.zxing.Result;

import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseFragment;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.CreateHistoryManager;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.HistoryItem;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.CreateCategory;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input.BaseCreateActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateResultActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.QRUtil;

/**
 * “创建历史”列表页面的 Fragment
 * 展示用户在应用内生成的二维码记录，并支持重新查看和删除
 */
public class CreateHistoryFragment extends BaseFragment implements CreateHistoryRCVAdapter.OnItemClickListener {
    private Group noHistoryGroup; // 空状态视图
    private RecyclerView historyRCV; // 列表控件
    private CreateHistoryRCVAdapter historyRCVAdapter; // 适配器
    private List<HistoryItem> createHistoryItemList; // 内存数据缓存

    private CreateHistoryManager createHistoryManager; // 创建历史专用数据库管理器

    private OnCreateSelectedModeChangeListener selectedModeChangeListener; // 状态变化回调

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_history_viewpager;
    }

    @Override
    protected void initData() {
        createHistoryManager = new CreateHistoryManager(getActivity());
        historyRCVAdapter = new CreateHistoryRCVAdapter(getActivity(), this);
    }

    @Override
    protected void initView(View root) {
        noHistoryGroup = root.findViewById(R.id.group_no_history);

        historyRCV = root.findViewById(R.id.rcv_history);
        historyRCV.setAdapter(historyRCVAdapter);
        historyRCV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    /**
     * 刷新列表逻辑（带简单的增量更新优化）
     */
    private void reloadData() {
        List<HistoryItem> maybeNewDataList = createHistoryManager.buildHistoryItems();

        // 优化点：通过比较第一条数据的时间戳和列表长度，判断数据是否真的发生了变化
        boolean isNewData = false;
        if (maybeNewDataList != null && createHistoryItemList != null && createHistoryItemList.size() == maybeNewDataList.size() && !maybeNewDataList.isEmpty()) {
            Result result1 = maybeNewDataList.get(0).getResult();
            Result result2 = createHistoryItemList.get(0).getResult();
            if (result1 != null && result2 != null && result1.getTimestamp() == result2.getTimestamp()) {
                isNewData = true;
            }
        }
        if (isNewData) {
            return;
        }

        createHistoryItemList = maybeNewDataList;
        if (createHistoryItemList == null || createHistoryItemList.isEmpty()) {
            historyRCV.setVisibility(View.GONE);
            noHistoryGroup.setVisibility(View.VISIBLE);
        } else {
            historyRCVAdapter.setData(getActivity(), createHistoryItemList);
            historyRCV.setVisibility(View.VISIBLE);
            noHistoryGroup.setVisibility(View.GONE);
        }

        // 通知父 Activity
        if (selectedModeChangeListener != null && createHistoryItemList != null) {
            selectedModeChangeListener.onCreateHistoryItemCountChanged(createHistoryItemList.size());
        }
    }

    @Override
    public void onItemSelectModeChanged(int selectMode) {
        if (selectedModeChangeListener != null) {
            selectedModeChangeListener.onSelectModeChanged(selectMode);
        }
    }

    /**
     * 列表项点击：重新跳转到结果展示页面
     */
    @Override
    public void onItemClick(int position, HistoryItem historyItem) {
        if (historyItem != null && historyItem.getResult() != null) {
            try {
                if (null != historyItem.getResult().getBarcodeFormat()) {
                    // 恢复创建类型
                    BaseCreateActivity.createType = QRUtil.getCategoryByCategoryText(historyItem.getResult().getBarcodeFormat().toString());
                    // 构造结果模型
                    CreateResultModel resultModel = new CreateCalendarModel();
                    resultModel.setResult(historyItem.getResult().getText());
                    resultModel.setShowText(historyItem.getDisplay());
                    resultModel.setCodeFormat(ZXingFormatUtil.conversion(historyItem.getResult().getBarcodeFormat()));
                    resultModel.setCreateTimeMillis(historyItem.getResult().getTimestamp());
                    resultModel.setCreateFormat(CreateCategory.transCreateWithType(BaseCreateActivity.createType));
                    // 跳转到结果页，标记 isFromHistory 为 true
                    CreateResultActivity.showMe(getActivity(), resultModel, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //是否全选
    public void selectAll() {
        if (historyRCVAdapter != null) {
            historyRCVAdapter.selectAll();
        }
    }

    public int getSelectedItemCount() {
        if (historyRCVAdapter == null) {
            return 0;
        }
        return historyRCVAdapter.getSelectedItemCount();
    }

    public void deleteHistoryItem() {
        if (historyRCVAdapter == null || createHistoryItemList == null || createHistoryItemList.isEmpty()) {
            return;
        }
        if (createHistoryManager != null) {
            // 根据索引批量删除
            createHistoryManager.deleteHistoryItemByIndexList(historyRCVAdapter.getSelectedItemPositionList());
            reloadData();
        }
    }

    public void changeSelectModel(int selectMode) {
        if (historyRCVAdapter != null) {
            historyRCVAdapter.setSelectModel(selectMode);
        }
    }

    public void setSelectStateListener(OnCreateSelectedModeChangeListener selectedModeChangeListener) {
        this.selectedModeChangeListener = selectedModeChangeListener;
    }

    public interface OnCreateSelectedModeChangeListener {
        void onSelectModeChanged(int selectMode);

        void onCreateHistoryItemCountChanged(int count);
    }
}
