package qrscanner.barcodescanner.barcodereader.qrcodereader.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.drojian.qrcode.utillib.log.LogHelper.log
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.MainActivity
import java.util.Stack

//Activity栈管理类，负责监控应用内所有Activity的状态
object ActivityStack {
    //存储Activity类名的栈
    private val stack = Stack<String>()
    //存储待执行动作的映射(例如从外跳转进来的)
    private val activityActionMap = mutableMapOf<String, String>()
    //存储当前活跃的Activity对象引用
    private val  activities = mutableListOf<Activity>();

    //在Application中调用，注册生命周期回调
    fun init(application: Application){
        application.registerActivityLifecycleCallbacks(activityLifecyclesCallbacks)
    }
    //Activity生命周期状态声明
    private val  activityLifecyclesCallbacks = object: Application.ActivityLifecycleCallbacks{
        override fun onActivityCreated(activity: Activity, saveInstanceState: Bundle?) {
            //如果是分享相关的界面，不计入Activity
            if (fromShare(activity)){
                return
            }
            //新建Activity时入栈
            stack.push(activity.javaClass.simpleName)
            activities.add(activity)
        }
        //开启可见期
        override fun onActivityStarted(activity: Activity) {
        }
        //开启页面交互
        override fun onActivityResumed(activity: Activity) {
            val action = activityActionMap[activity.javaClass.simpleName]
            if (action != null) {
                handleAction(activity, action)
            }
        }
        //暂停页面交互
        override fun onActivityPaused(activity: Activity) {
        }
        //关闭可见期释放资源
        override fun onActivityStopped(activity: Activity) {
        }
        //保存已经添加的数据
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }
        //销毁Activity活动
        override fun onActivityDestroyed(activity: Activity) {
            //Activity销毁时出栈
            stack.remove(activity.javaClass.simpleName)
            activities.remove(activity)
        }
    }
    private fun fromShare(activity: Activity): Boolean {
        return false
    }
    //处理特定Activity的动作(如跳转到指定的Tab)
    private fun handleAction(activity: Activity,action: String){
        when(activity){
            is MainActivity -> {
                if (action.isNullOrEmpty()){
                    return
                }
                val tabIndex = action.toIntOrNull() ?: -1
                if (tabIndex != -1){
                    activity.onBottomTabSelect(tabIndex)
                }
                //执行完之后移除动作，防止重复执行
                activityActionMap.remove(activity.javaClass.simpleName)
            }
        }
    }

    //检查某个Activity是否孩还在某个栈中(存活)
    fun isAlive(activityName: String): Boolean{
        return stack.contains(activityName)
    }
    //检查某个Activity是否在栈顶(当前正在显示)
    fun isAtTop(activityName: String): Boolean{
        if (stack.isEmpty()){
            return false
        }
        val top = stack.last()
        return top == activityName
    }
    //应用退出时清理数据
    @JvmStatic
    fun onAppExit(){
        activities.clear()
        stack.clear()
        activityActionMap.clear()
    }
    //恢复某个Activity恢复时要执行的动作
    fun actionWhenResumed(activityName: String,action: String){
        activityActionMap[activityName] = action
    }

}