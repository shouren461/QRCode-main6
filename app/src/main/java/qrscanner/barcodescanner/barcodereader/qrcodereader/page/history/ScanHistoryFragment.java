package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history;

import android.view.View;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.Result;

import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseFragment;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.HistoryItem;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.HistoryManager;

/**
 * “扫描历史”列表页面的 Fragment
 * 负责从数据库加载已扫描的条码记录并展示在 RecyclerView 中
 */
public class ScanHistoryFragment extends BaseFragment implements HistoryRCVAdapter.OnItemClickListener {
    private Group noHistoryGroup; // 无数据时显示的“空状态”视图组
    private RecyclerView historyRCV; // 历史记录列表控件
    private HistoryRCVAdapter historyRCVAdapter; // 列表适配器
    private List<HistoryItem> scanHistoryItemList; // 缓存当前的列表数据

    private HistoryManager scanHistoryManager; // 数据库操作管理类

    private OnScanSelectedModeChangeListener selectedModeChangeListener; // 通知父容器状态变化的监听器

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_history_viewpager;
    }

    @Override
    protected void initData() {
        scanHistoryManager = new HistoryManager(getActivity());
        historyRCVAdapter = new HistoryRCVAdapter(getActivity(), this);
    }

    @Override
    protected void initView(View root) {
        noHistoryGroup = root.findViewById(R.id.group_no_history);

        historyRCV = root.findViewById(R.id.rcv_history);
        historyRCV.setAdapter(historyRCVAdapter);
        historyRCV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 每次回到页面时刷新数据，确保删除或新增后列表是最新的
     */
    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    /**
     * 刷新列表数据的核心逻辑
     */
    private void reloadData() {
        // 从数据库读取最新数据
        List<HistoryItem> maybeNewDataList = HistoryManager.buildHistoryItems(getActivity());

        // 小优化：对比新旧数据的第一条记录时间戳，如果一致且数量没变，则认为数据未更新，跳过 UI 刷新
        boolean isNewData = false;
        if (maybeNewDataList != null && scanHistoryItemList != null && scanHistoryItemList.size() == maybeNewDataList.size() && !maybeNewDataList.isEmpty()) {
            Result result1 = maybeNewDataList.get(0).getResult();
            Result result2 = scanHistoryItemList.get(0).getResult();
            if (result1 != null && result2 != null && result1.getTimestamp() == result2.getTimestamp()) {
                isNewData = true;
            }
        }
        if (isNewData) {
            return;
        }

        scanHistoryItemList = maybeNewDataList;
        // 如果没有数据，隐藏列表并显示“暂无记录”提示
        if (scanHistoryItemList == null || scanHistoryItemList.isEmpty()) {
            historyRCV.setVisibility(View.GONE);
            noHistoryGroup.setVisibility(View.VISIBLE);
        } else {
            historyRCVAdapter.setData(getActivity(), scanHistoryItemList);
            historyRCV.setVisibility(View.VISIBLE);
            noHistoryGroup.setVisibility(View.GONE);
        }

        // 通知父 Activity 记录总数的变化，以便更新顶部删除按钮状态
        if (selectedModeChangeListener != null && scanHistoryItemList != null) {
            selectedModeChangeListener.onScanHistoryItemCountChanged(scanHistoryItemList.size());
        }
    }

    /**
     * 当列表项的长按/多选状态改变时回调
     */
    @Override
    public void onItemSelectModeChanged(int selectMode) {
        if (selectedModeChangeListener != null) {
            selectedModeChangeListener.onSelectModeChanged(selectMode);
        }
    }

    @Override
    public void onItemClick(int position, HistoryItem historyItem) {
        // 此处原逻辑为查看详情，目前已根据需求移除
    }

    //由父容器 HistoryFragment 调用的公共方法

    /**
     * 执行全选操作
     */
    public void selectAll() {
        if (historyRCVAdapter != null) {
            historyRCVAdapter.selectAll();
        }
    }

    /**
     * 获取当前被勾选的项目数量
     */
    public int getSelectedItemCount() {
        if (historyRCVAdapter == null) {
            return 0;
        }
        return historyRCVAdapter.getSelectedItemCount();
    }

    /**
     * 删除选中的项目并刷新 UI
     */
    public void deleteHistoryItem() {
        if (historyRCVAdapter == null || scanHistoryItemList == null || scanHistoryItemList.isEmpty()) {
            return;
        }
        if (scanHistoryManager != null) {
            // 获取所有选中项的索引，通知数据库执行物理删除
            scanHistoryManager.deleteHistoryItemByIndexList(historyRCVAdapter.getSelectedItemPositionList());
            reloadData(); // 重新加载以刷新列表
        }
    }

    /**
     * 切换普通/编辑模式
     */
    public void changeSelectModel(int selectMode) {
        if (historyRCVAdapter != null) {
            historyRCVAdapter.setSelectModel(selectMode);
        }
    }

    /**
     * 设置状态变化监听器
     */
    public void setSelectStateListener(OnScanSelectedModeChangeListener selectedModeChangeListener) {
        this.selectedModeChangeListener = selectedModeChangeListener;
    }
    //定义状态变化的接口
    public interface OnScanSelectedModeChangeListener {
        void onSelectModeChanged(int selectMode);

        void onScanHistoryItemCountChanged(int count);
    }
}
