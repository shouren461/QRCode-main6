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

// 创建历史列表的适配器 -> 与扫描历史适配器类似，但针对的是"生成的二维码数据"
public class CreateHistoryRCVAdapter extends RecyclerView.Adapter {
    private LayoutInflater layoutInflater;   //布局加载器
    private OnItemClickListener  listener ;//类表项点击和状态切换的回调接口
    //当前选择模式 普通模式 或者编辑多选模式
    private int selectModel = HistoryFragment.SELECT_MODEL_NORMAL;
    //转换后的视图模型 列表
    private List<HistoryItemViewModel> historyItemViewModelList = new ArrayList<>();
    //构造函数
    //@param context 上下文对象  @param listener 点击事件回调监听器
    public CreateHistoryRCVAdapter(Context context, OnItemClickListener listener){
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    //设置数据源:将数据库实体转换为UI专用的ViewModel
    //@param activity 上下文对象   @param  historyItemList 数据库原始数据列表
    public void setData(final Activity activity, final List<HistoryItem> historyItemList){
        //1,数据转换:将数据库数据转换为视图模型
        retrofitData(activity,historyItemList);
        //2,通知RecyclerView刷新数据
        //确保在主线程调用 notifyDataSetChanged
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
    //切换选择模式 @param selectModel 目标模式
    public void setSelectModel(int selectModel){
        //如果模式没有变化，不做处理
        if (this.selectModel != selectModel){
            this.selectModel = selectModel;
            for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
                historyItemViewModel.isSelected = false;
            }
            notifyDataSetChanged();
        }
    }
    //全选或取消全选逻辑(根据当前选择状态自动切换)
    public void selectAll(){
        boolean  isAllSelected  =true;
        //先检查是否已经全部选中
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            if (!historyItemViewModel.isSelected){
                isAllSelected = false;
                break;
            }
        }
        //如果已经全选，则全部取消;否则全部选中
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            historyItemViewModel.isSelected = !isAllSelected;
        }
        notifyDataSetChanged();
    }
    //获取当前被选中的项在列表中的位置索引
    //@return 返回选中项的列表位置
    public List<Integer> getSelectedItemPositionList(){
        List<Integer> selectedItemPositionList = new ArrayList<>();
        if (historyItemViewModelList != null && !historyItemViewModelList.isEmpty()){
            for (int i = 0; i < historyItemViewModelList.size(); i++) {
                if (historyItemViewModelList.get(i).isSelected){
                    selectedItemPositionList.add(i);
                }
            }
        }
        return selectedItemPositionList;
    }
    //获取当前选中项的总数  @return 选中项数量
    public int getSelectedItemCount(){
        //1,如果集合为空,直接返回0
        if (historyItemViewModelList == null || historyItemViewModelList.isEmpty()){
            return 0;
        }
        //2,如果集合不为空，则遍历所有视图并累累加
        int count = 0;
        for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
            if (historyItemViewModel.isSelected){
                count++;
            }
        }
        return count;
    }

    //数据加工:将数据库原始数据转化为UI 显示所需的ViewModel ,与扫描历史不同这里是根据条码格式反向推导创建类型
    //@param activity 上下文对象 @param historyItemList 数据库原始数据列表
    private void retrofitData(Activity activity,List<HistoryItem> historyItemList){
        //1,创建新的视图模型列表
        historyItemViewModelList = new ArrayList<>(historyItemList != null ? historyItemList.size() : 0);
        try{
            if (historyItemList == null || historyItemList.isEmpty()) {
                return;
            }
            //2,遍历数据库数据，逐个转换
            for (HistoryItem historyItem : historyItemList) {
                HistoryItemViewModel historyItemViewModel = new HistoryItemViewModel();
                historyItemViewModel.historyItem = historyItem;
                //3,如果结果为空，跳过这条记录
                if (historyItem.getResult() == null) {
                    continue;
                }
                //4,使用数据库中存储的创建类型（如 Youtube、TEXT 等）
                String createTypeName = historyItem.getCreateType();
                if (createTypeName == null) {
                    createTypeName = "TEXT";
                }
                //5,根据创建类型获取对应的枚举值
                CreateType createType = QRUtil.getCategoryByCategoryText(createTypeName);
                if (createType == null) {
                    createType = CreateType.TEXT;
                }
                //4.1 设置类型名称
                historyItemViewModel.typeName = activity.getResources().getString(createType.getStringSrc());
                //4.2 设置类型图标
                historyItemViewModel.typeIconResId = QRUtil.getIconByCategoryText(createTypeName);
                if (historyItemViewModel.typeIconResId == 0) {
                    historyItemViewModel.typeIconResId = QRUtil.getIconByCategoryText("TEXT");
                }
                //4.3 格式化预览内容,去除换行符，方便单行展示
                String displayText;
                // 根据创建类型使用专用提取方法精简显示内容
                if ("YOUTUBE".equalsIgnoreCase(createTypeName)) {
                    displayText = ResultFormatUtil.extractYoutube(historyItem.getResult().getText());
                } else if ("CALENDAR".equalsIgnoreCase(createTypeName)) {
                    displayText = ResultFormatUtil.extractCalendar(historyItem.getResult().getText());
                } else {
                    displayText = ResultFormatUtil.extractCreateDisplayText(historyItem.getResult());
                }
                if (displayText != null) {
                    historyItemViewModel.content = displayText.replace("\n", " ");
                } else if (historyItem.getResult().getText() != null) {
                    historyItemViewModel.content = historyItem.getResult().getText().replace("\n", " ");
                }
                //4.4 默认未选中
                historyItemViewModel.isSelected  = false;
                //4.5 添加到列表
                historyItemViewModelList.add(historyItemViewModel);
            }
        }catch (Exception e){
            //捕获异常，防止遍历过程出错
            e.printStackTrace();
        }
    }

    //创建ViewHolderL:加载列表项布局
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        android.util.Log.e("CreateHistoryRCVAdapter", "onCreateViewHolder called");
        return new HistoryViewHolder(layoutInflater.inflate(R.layout.item_rcv_history,viewGroup,false));
    }
    //绑定数据到ViewHolder:设置类表项内容和事件
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        android.util.Log.e("CreateHistoryRCVAdapter", "onBindViewHolder called, position=" + position);
        if (viewHolder instanceof HistoryViewHolder){
            final HistoryViewHolder historyViewHolder = (HistoryViewHolder) viewHolder;
            //1,长按事件处理逻辑
            viewHolder.itemView.setOnLongClickListener(view -> {
                int itemPosition = historyViewHolder.getAdapterPosition();
                if (itemPosition <0){
                    itemPosition = 0;
                }
                if (selectModel ==HistoryFragment.SELECT_MODEL_NORMAL){
                    //1.1,普通模式下长按:进入编辑模式并选中当前项
                    selectModel = HistoryFragment.SELECT_MODEL_SELECTED;
                    //1.2,先取消所有选中项的状态
                    for (HistoryItemViewModel historyItemViewModel : historyItemViewModelList) {
                        historyItemViewModel.isSelected = false;
                    }
                    //1.3,选中当前项
                    if (itemPosition <historyItemViewModelList.size()){
                        historyItemViewModelList.get(itemPosition).isSelected =true;
                        notifyDataSetChanged();
                    }
                    //1.4,通知外部Fragment更新顶菜单UI
                    if (listener != null){
                        listener.onItemSelectModeChanged(HistoryFragment.SELECT_MODEL_SELECTED);
                    }
                }else {
                    //编辑模式下长按:反选当前项
                    if (itemPosition <historyItemViewModelList.size()){
                        historyItemViewModelList.get(itemPosition).isSelected = !historyItemViewModelList.get(itemPosition).isSelected;
                        notifyItemChanged(itemPosition);
                    }
                }
                return true;
            });
            //2,点击事件处理逻辑
            viewHolder.itemView.setOnClickListener( view -> {
                int itemPosition = historyViewHolder.getAdapterPosition();
                if (itemPosition <0){
                    itemPosition = 0;
                }
                //2.1 普通模式下点击查看详情或者跳转到结果页
                if (selectModel == HistoryFragment.SELECT_MODEL_NORMAL){
                    if (listener != null && itemPosition <historyItemViewModelList.size()){
                        listener.onItemClick(itemPosition,historyItemViewModelList.get(itemPosition).historyItem);
                    }
                }else {
                    //2.2 选择模式下点击:切换候选状态
                    if (itemPosition<historyItemViewModelList.size()){
                        historyItemViewModelList.get(itemPosition).isSelected = !historyItemViewModelList.get(itemPosition).isSelected;
                        notifyItemChanged(itemPosition);
                    }
                }

            });
            //3,设置列表项内容
            try {
                HistoryItemViewModel historyItemViewModel = historyItemViewModelList.get(historyViewHolder.getAdapterPosition());
                //3.1 设置类型图标
                historyViewHolder.typeIV.setImageResource(historyItemViewModel.typeIconResId);
                //3.2 设置类型名称
                historyViewHolder.typeNameTV.setText(historyItemViewModel.typeName);
                //3.3 设置预览内容
                historyViewHolder.contentTV.setText(historyItemViewModel.content);
                //3.4 根据当前模式切换右侧图标
                if (selectModel  == HistoryFragment.SELECT_MODEL_NORMAL){
                    //如果是正常模式，显示"更多"跳转箭头
                    historyViewHolder.moreViewIV.setVisibility(View.VISIBLE);
                    historyViewHolder.selectCB.setVisibility(View.GONE);
                }else{
                    //如果是编辑模式:显示勾选框
                    historyViewHolder.moreViewIV.setVisibility(View.GONE);
                    historyViewHolder.selectCB.setVisibility(View.VISIBLE);
                    historyViewHolder.selectCB.setChecked(historyItemViewModel.isSelected);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //获取列表项总数
    @Override
    public int getItemCount() {
        android.util.Log.e("CreateHistoryRCVAdapter", "getItemCount called, returning: " + historyItemViewModelList.size());
        return historyItemViewModelList.size();
    }

    //ViewHolder:持有列表项中的所有控件引用 ->用于RecyclerView的视图复用
    class HistoryViewHolder extends RecyclerView.ViewHolder{
        AppCompatImageView typeIV;   //类型图标
        AppCompatTextView typeNameTV; //类型名称
        AppCompatTextView contentTV;   //预览内容
        AppCompatImageView moreViewIV;   //右侧箭头
        AppCompatCheckBox selectCB;   //编辑模式下的勾选框

        public  HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            //绑定控件
            typeIV = itemView.findViewById(R.id.iv_type);
            typeNameTV =itemView.findViewById(R.id.tv_type);
            contentTV = itemView.findViewById(R.id.tv_second_value);
            moreViewIV = itemView.findViewById(R.id.iv_right);
            selectCB = itemView.findViewById(R.id.cb_select);
        }
    }

    //点击事件回调接口  -> 用于与外部Fragment通信
    public interface  OnItemClickListener{
        //当列表切换到多选/普通模式时回调
        void onItemSelectModeChanged(int selectModel);

        //普通模式下的列表项回调
        void  onItemClick(int position,HistoryItem historyItem);
    }

    //UI专用数据模型:保存HistoryItem以及解析后的UI展示信息
    class HistoryItemViewModel{
        HistoryItem historyItem;  //原始数据库数据
        int typeIconResId;       //类型对应的图标资源ID
        String typeName;         //类型名称字符串
        String content ;          //格式化化后的预览文本
        boolean isSelected;       //标记在编辑模式下是否被选
    }
}