package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;


//创建页列表的适配器  -> 负责将不同的创建类型(createType)渲染到列表项中(例如YouTube,日历等)
public class CreateRCVAdapter extends RecyclerView.Adapter {
    private Activity mActivity;                      // 上下文对象
    private LayoutInflater mLayoutInflater;          // 布局加载器
    private List<CreateType> mCreateItemList = new ArrayList<>(1);  // 创建类型列表
    private CreateItemClickListener mItemClickListener;  // 列表项点击监听器

    //构造函数 @param mActivity 上下文对象  @param mItemClickListener 列表项点击监听器
    public CreateRCVAdapter(Activity mActivity, CreateItemClickListener mItemClickListener) {
        this.mActivity = mActivity;
        this.mLayoutInflater = LayoutInflater.from(mActivity);
        this.mItemClickListener = mItemClickListener;
    }

    //创建列表项的布局容器(ViewHolder)
     //@param viewGroup 父容器 @param i 视图类型  @return 列表项的ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // 加载单个列表项的布局文件
        return new CreateViewHolder(mLayoutInflater.inflate(R.layout.item_rcv_creat_small, viewGroup, false));
    }

    //绑定数据到列表项控件上  @param viewHolder 列表项的视图持有者 @param i 当前项的位置
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        // 获取当前位置对应的创建类型
        CreateType createType = mCreateItemList.get(i);
        
        // 设置点击监听，点击后回调监听器
        viewHolder.itemView.setOnClickListener(v ->
                mItemClickListener.onCreateItemClick(viewHolder.getAdapterPosition(), createType));
        
        // 转换为具体的 ViewHolder 类型
        CreateViewHolder createViewHolder = (CreateViewHolder) viewHolder;
        
        // 根据创建类型设置对应的图标
        createViewHolder.icon.setImageResource(createType.getDrawableIcon());
        // 根据创建类型设置对应的标题文本
        createViewHolder.title.setText(this.mActivity.getString(createType.getStringSrc()));
    }


    //获取列表项总数 @return 列表项数量
    @Override
    public int getItemCount() {
        return mCreateItemList.size();
    }

    //刷新列表项数据 @param list 新的创建类型列表
    public void reloadData(List<CreateType> list) {
        // 更新数据源
        mCreateItemList = list;
        // 通知 RecyclerView 刷新列表
        notifyDataSetChanged();
    }

    //列表项视图持有者(ViewHolder) -> 用于缓存列表项中的控件引用，提高列表项滚动能力
    class CreateViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;   //类型图标（如二维码图标等）
        TextView title;  //类型标题（如"YouTube"、"日历"等）

        public CreateViewHolder(@NonNull View itemView) {
            super(itemView);
            // 绑定列表项布局中的控件
            icon = itemView.findViewById(R.id.iv_icon);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}