package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history;

import android.view.View;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drojian.qrcode.createlib.create.CreateResultModel;
import com.drojian.qrcode.createlib.create.format.CreateCalendarModel;
import com.drojian.qrcode.createlib.create.format.CreateYoutubeModel;
import com.drojian.qrcode.zxinglib.ZXingFormatUtil;
import com.google.zxing.Result;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseFragment;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.CreateHistoryManager;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.db.HistoryItem;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.CreateCategory;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input.BaseCreateActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateResultActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.QRUtil;

//创建历史界面的Fragment  -> 展示用户在应用内生成的二维码记录，并支持重新查看和删除
public class CreateHistoryFragment extends BaseFragment implements CreateHistoryRCVAdapter.OnItemClickListener {
    private Group noHistoryGroup;   //空状态视图
    private RecyclerView historyRCV;  //列表控件
    private CreateHistoryRCVAdapter historyRCVAdapter;//适配器
    private List<HistoryItem> createHistoryItemList;  //内存数据缓存
    private CreateHistoryManager createHistoryManager;//创建历史专用数据库管理器
    private OnCreateSelectedModeChangeListener  selectedModeChangeListener;  //状态变化回调

    //获取初始化布局资源ID
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_history_viewpager;
    }
    //获取初始化数据逻辑 :
    @Override
    protected void initData() {
        //创建数据库操作管理器
        createHistoryManager = new CreateHistoryManager(getActivity());
        //创建列表适配器，传入点击事件监听器
        historyRCVAdapter = new CreateHistoryRCVAdapter(getActivity(),this);
    }
    //初始化视图控件:绑定控件并配置列表
    @Override
    protected void initView(@Nullable View root) {
        if (root == null) {
            return;
        }
        //绑定空状态视图
        noHistoryGroup = root.findViewById(R.id.group_no_history);
        //配置列表控件
        historyRCV = root.findViewById(R.id.rcv_history);
        historyRCV.setAdapter(historyRCVAdapter);
        //设置垂直线性布局管理器
        historyRCV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
    }
    //Fragment恢复可见时刷新数据，确保每次回到这个页面时数据都是最新的
    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }
    //刷新列表逻辑：每次都强制刷新，确保数据最新
    private void reloadData() {
        //1,从数据库查询最新的创建历史数据
        List<HistoryItem> maybeNewDataList = createHistoryManager.buildHistoryItems();
        
        //暂时移除增量更新逻辑，每次都强制刷新，确保数据正确显示
        //4,更新内容缓存
        createHistoryItemList = maybeNewDataList;
        //5,根据数据情况切换显示状态
        if (createHistoryItemList == null || createHistoryItemList.isEmpty()){
            //无数据:显示空状态，隐藏列表
            historyRCV.setVisibility(View.GONE);
            noHistoryGroup.setVisibility(View.VISIBLE);
        }else {
            //有数据:刷新列表，显示列表，隐藏空状态
            //重新创建适配器并设置，确保RecyclerView能正确刷新
            historyRCVAdapter = new CreateHistoryRCVAdapter(getActivity(), this);
            historyRCVAdapter.setData(getActivity(), createHistoryItemList);
            historyRCV.setAdapter(historyRCVAdapter);
            
            historyRCV.setVisibility(View.VISIBLE);
            noHistoryGroup.setVisibility(View.GONE);
        }
        //6,通知父Activity数据数量变化(用于更新底部操作栏)
        if (selectedModeChangeListener != null && createHistoryItemList != null){
            selectedModeChangeListener.onCreateHistoryItemCountChanged(createHistoryItemList.size());
        }
    }

    //选择模式回调(由适配器触发)，将模式变化传递给父Activity
    @Override
    public void onItemSelectModeChanged(int selectModel) {
        if (selectedModeChangeListener != null)    {
            selectedModeChangeListener.onSelectModeChanged(selectModel);
        }
    }
    //列表项点击:重新跳转到结果展示界面查看详情
    //@param position 点击位置  @Parma historyItem 点击的历史记录项
    @Override
    public void onItemClick(int position, HistoryItem historyItem) {
        if (historyItem !=  null && historyItem.getResult() != null){
            try {
                if (historyItem.getResult().getBarcodeFormat() != null){
                    //1,根据条码格式恢复创建类型（如文本，YouTube,日历格式）
                    BaseCreateActivity.createType = QRUtil.getCategoryByCategoryText(
                            historyItem.getResult().getBarcodeFormat().toString());
                    //2,构造结果模型，封装历史数据
                    CreateResultModel resultModel;
                    if (BaseCreateActivity.createType == CreateType.YOUTUBE) {
                        resultModel = new CreateYoutubeModel();
                    } else {
                        resultModel = new CreateCalendarModel();
                    }
                    resultModel.setResult(historyItem.getResult().getText());//二维码内容
                    resultModel.setShowText(historyItem.getDisplay());   //展示文本
                    resultModel.setCodeFormat(ZXingFormatUtil.conversion(historyItem.getResult().getBarcodeFormat()));//格式
                    resultModel.setCreateTimeMillis(historyItem.getResult().getTimestamp()); //创建时间
                    resultModel.setCreateFormat(CreateCategory.transCreateWithType(BaseCreateActivity.createType));  //分类
                    //3,跳转到结果展示界面，标记isFromHistory 为true(表示来自历史记录)
                    CreateResultActivity.showMe(getActivity(),resultModel,true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //是否全选（调用适配器方法）
    public void selectAll(){
        if (historyRCVAdapter != null){
            historyRCVAdapter.selectAll();
        }
    }
    //获取当前选中项数量
    public int getSelectedItemCount(){
        if (historyRCVAdapter == null){
            return 0;
        }
        return historyRCVAdapter.getSelectedItemCount();
    }

    //删除 选中的创建历史记录
    public void deleteHistoryItem(){
        //如果没有适配器，或者集合为空，直接返回null
        if (historyRCVAdapter == null|createHistoryItemList == null ||createHistoryItemList.isEmpty()){
            return;
        }
        if (createHistoryManager != null){
            //根据选中的索引列表批量删除数据库记录
            createHistoryManager.deleteHistoryItemByIndexList(historyRCVAdapter.getSelectedItemPositionList());
            //刷新数据
            reloadData();
        }

    }
    //切换选择模式(普通模式/编辑模式)  @param selectMode 目标模式
    public void changeSelectModel(int selectMode){
        if (historyRCVAdapter != null){
            historyRCVAdapter.setSelectModel(selectMode);
        }
    }
    //设置选择状态监听器(用于与父Activity通信)
    public void setSelectStateListener(OnCreateSelectedModeChangeListener selectedModeChangeListener){
        this.selectedModeChangeListener  =selectedModeChangeListener;
    }
    //监听状态变化回调接口  -> 用于Fragment与父Activity之间的通信
    public interface OnCreateSelectedModeChangeListener{
        void onSelectModeChanged(int selectMode);
        void onCreateHistoryItemCountChanged(int count);
    }
}