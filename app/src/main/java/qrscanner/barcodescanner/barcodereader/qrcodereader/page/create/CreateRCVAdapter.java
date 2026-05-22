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

/**
 * 创建页列表的适配器
 * 负责将不同的创建类型（CreateType）渲染到列表项中
 */
public class CreateRCVAdapter extends RecyclerView.Adapter {
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private List<CreateType> mCreateItemList = new ArrayList<>(1);
    private CreateItemClickListener mItemClickListener;

    public CreateRCVAdapter(Activity mActivity, CreateItemClickListener mItemClickListener) {
        this.mActivity = mActivity;
        this.mLayoutInflater = LayoutInflater.from(mActivity);
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * 创建列表项的布局容器
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CreateViewHolder(mLayoutInflater.inflate(R.layout.item_rcv_creat_small, viewGroup, false));
    }

    /**
     * 绑定数据到列表项控件上
     */
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        CreateType createType = mCreateItemList.get(i);
        // 设置点击监听
        viewHolder.itemView.setOnClickListener(v ->
                mItemClickListener.onCreateItemClick(viewHolder.getAdapterPosition(), createType));
        
        CreateViewHolder createViewHolder = (CreateViewHolder) viewHolder;
        // 根据类型设置对应的图标和标题文本
        createViewHolder.icon.setImageResource(createType.getDrawableIcon());
        createViewHolder.title.setText(this.mActivity.getString(createType.getStringSrc()));
    }

    @Override
    public int getItemCount() {
        return mCreateItemList.size();
    }

    /**
     * 刷新列表数据
     */
    public void reloadData(List<CreateType> list) {
        mCreateItemList = list;
        notifyDataSetChanged();
    }


    /**
     * 列表项视图持有者，保存控件引用
     */
    class CreateViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public CreateViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}
