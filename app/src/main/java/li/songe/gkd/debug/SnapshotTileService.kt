package li.songe.gkd.debug

import android.accessibilityservice.AccessibilityService
import android.service.quicksettings.TileService
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.delay
import li.songe.gkd.appScope
import li.songe.gkd.debug.SnapshotExt.captureSnapshot
import li.songe.gkd.service.GkdAbService
import li.songe.gkd.service.topActivityFlow
import li.songe.gkd.util.launchTry

class SnapshotTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val service = GkdAbService.service
        if (service == null) {
            ToastUtils.showShort("无障碍没有开启")
            return
        }
        val oldAppId = topActivityFlow.value?.appId
        appScope.launchTry {
            val interval = 500L
            val waitTime = 3000L
            var i = 0
            while (topActivityFlow.value?.appId == oldAppId) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                delay(interval)
                i++
                if (i * interval > waitTime) {
                    ToastUtils.showShort("没有检测到界面切换,捕获失败")
                    return@launchTry
                }
            }
            captureSnapshot()
        }
    }

}