package qrscanner.barcodescanner.barcodereader.qrcodereader.page

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity 对应的 ViewModel，用于处理与界面相关的逻辑和异步任务
 */
class MainViewModel : ViewModel() {
    /**
     * 开启一个 1 秒的延迟任务，并在 UI 线程执行回调
     * 该任务具有生命周期感知能力，仅在 LifecycleOwner（如 Activity）处于 RESUMED 状态时执行
     * @param lifecycleOwner 生命周期持有者
     * @param action 延迟结束后要执行的具体动作
     */
    fun startDelay1s(lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        // 标记位，确保即使在 Lifecycle 状态反复变化时，内部动作也只执行一次
        var handled = false
        
        // 在 IO 协程作用域中启动任务
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            // repeatOnLifecycle 会在生命周期进入 RESUMED 时开始执行，离开时挂起
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                // 如果已经处理过，直接跳过
                if (handled) {
                    return@repeatOnLifecycle
                }
                // 延迟 1000 毫秒（1秒）
                delay(1000)
                
                // 切换回主线程（Main）执行 UI 相关的动作
                withContext(Dispatchers.Main) {
                    action()
                    // 标记为已处理
                    handled = true
                }
            }
        }
    }
}