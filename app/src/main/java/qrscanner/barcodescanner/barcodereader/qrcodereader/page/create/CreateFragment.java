package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create;

import android.view.View;

import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseFragment;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input.BaseCreateActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;


//创建页面的Fragment ->展示不同类型的二维码创建入口列表(如YouTube,日历等)
public class CreateFragment extends BaseFragment implements CreateItemClickListener {


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_create;
    }

    @Override
    protected void initData() {
        // 目前不需要初始化额外数据
    }
    //初始化视图，配置RecyclerView列表
    @Override
    protected void initView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.rcv_creat);
        // 使用网格布局，每行显示 2 个图标
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // 设置适配器并传入点击回调
        CreateRCVAdapter adapter = new CreateRCVAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        // 加载分类数据
        adapter.reloadData(CreateCategory.typeList);
        // 适配沉浸式状态栏顶距
        enableInsetsViewTop(root.findViewById(R.id.main));
    }

    //当Fragment显示给用户时触发
    @Override
    protected void iAmShowing() {
        super.iAmShowing();
        // 设置状态栏图标为深色（适用于浅色背景）
        if(getActivity()!=null && getActivity().getWindow()!=null){
            new WindowInsetsControllerCompat(getActivity().getWindow(), getActivity().getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        }
    }
    //列表项点击回调:根据选中的类型跳到对应的输入页面
    @Override
    public void onCreateItemClick(int position, CreateType type) {
        // 调用统一的启动方法，内部会根据 type 跳转到不同的 Activity
        BaseCreateActivity.startBase(getContext(), type);
    }

}
