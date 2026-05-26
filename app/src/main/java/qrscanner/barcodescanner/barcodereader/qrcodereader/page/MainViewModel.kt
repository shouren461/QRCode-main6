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

//MainActivity对应的ViewModel ,用于处理与界面相关的逻辑与异步任务
class MainViewModel : ViewModel() {
    /*
    开启一个1秒的延迟任务，在UI线程执行回调,该任务具有生命你周期感知能力，仅在LifeCycleOwner(如在Activity)处于resume状态时执行
    @param lifecycleOwner 生命周期持有者   @param action 延迟结束后要执行的动作
    * */
    fun startDelay1s(lifeCycleOwner: LifecycleOwner,action:() -> Unit){
        //标记位，确保即使在LifeCycle状态发生变化时，内部动作也只能执行一次
        var handled  = false
        //在IO线程作用域中启动任务
        lifeCycleOwner.lifecycleScope.launch (Dispatchers.IO ){
            //repeatLifecycle会在生命周期进入Resume时开始执行，离开时挂起
            lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED){
                //1,如果已经处理过了直接跳过
                if (handled){

                }
                //延迟1秒(1000ms)
                delay(1000)
                //切回主线程(Main)执行UI相关的动作
                withContext(Dispatchers.Main){
                    action()
                    //标记为已处理
                    handled = true
                }

            }
        }
    }
}