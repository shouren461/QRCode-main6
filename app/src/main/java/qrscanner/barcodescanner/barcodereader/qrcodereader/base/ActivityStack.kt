package qrscanner.barcodescanner.barcodereader.qrcodereader.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.drojian.qrcode.utillib.log.LogHelper.log
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.MainActivity
import java.util.Stack

/**
 * Activity 栈管理类，负责监控应用内所有 Activity 的状态
 */
object ActivityStack {
    // 存储 Activity 类名的栈
    private val stack = Stack<String>()
    // 存储待执行动作的映射（例如从外部跳转进来时）
    private val activityActionMap = mutableMapOf<String, String>()
    // 存储当前活跃的 Activity 对象引用
    private val activities = mutableListOf<Activity>()

    /**
     * 在 Application 中调用，注册生命周期回调
     */
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (fromShare(activity)) {
                return
            }
            // 新建 Activity 时入栈
            stack.push(activity.javaClass.simpleName)
            activities.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            // Activity 恢复可见时，检查是否有待执行的动作
            val action = activityActionMap[activity.javaClass.simpleName]
            handleAction(activity, action)
        }

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            // Activity 销毁时出栈
            stack.remove(activity.javaClass.simpleName)
            activities.remove(activity)
        }
    }

    private fun fromShare(activity: Activity): Boolean {
        return false
    }

    /**
     * 处理特定 Activity 的动作（如跳转到指定的 Tab）
     */
    private fun handleAction(activity: Activity, action: String?) {
        when (activity) {
            is MainActivity -> {
                if (action.isNullOrEmpty()) {
                    return
                }
                val tabIndex = action.toIntOrNull() ?: -1
                if (tabIndex != -1) {
                    activity.onBottomTabSelect(tabIndex)
                }
                // 执行完后移除动作，防止重复执行
                activityActionMap.remove(activity.javaClass.simpleName)
            }
        }
    }

    /**
     * 检查某个 Activity 是否还在栈中（存活）
     */
    fun isAlive(activityName: String): Boolean {
        return stack.contains(activityName)
    }

    /**
     * 检查某个 Activity 是否在栈顶（当前正在显示）
     */
    fun isAtTop(activityName: String): Boolean {
        if (stack.isEmpty()) return false
        val top = stack.last()
        return top == activityName
    }

    /**
     * 结束除指定列表以外的所有 Activity
     */
    fun finishAllActivitiesExclude(excludeActivities: List<String>) {
        try {
            activities.filter {
                !it.isFinishing && !it.isDestroyed && it.javaClass.simpleName !in excludeActivities
            }.onEach { it.finish() }
        } catch (e: Exception) {
            e.printStackTrace()
            e.log()
        }
    }
    
    /**
     * 应用退出时清理数据
     */
    @JvmStatic
    fun onAppExit() {
        activities.clear()
        stack.clear()
        activityActionMap.clear()
    }

    /**
     * 设置当某个 Activity 恢复时要执行的动作
     */
    fun actionWhenResumed(activityName: String, action: String) {
        activityActionMap[activityName] = action
    }
}