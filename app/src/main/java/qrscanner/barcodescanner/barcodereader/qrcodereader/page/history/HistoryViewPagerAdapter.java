package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * 历史页 ViewPager2 的适配器
 * 负责管理并切换“扫描历史”和“创建历史”两个子 Fragment
 */
public class HistoryViewPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> fragmentList;

    /**
     * 设置要展示的 Fragment 列表
     */
    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public HistoryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * 根据位置创建/获取对应的 Fragment 实例
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    /**
     * 获取页面总数
     */
    @Override
    public int getItemCount() {
        return fragmentList != null ? fragmentList.size() : 0;
    }
}
