package top.itaso.app

import android.app.Application
import top.itaso.lib.alog.ALog
import top.itaso.lib.alog.ALogLevel
import top.itaso.lib.net.NetInit

/**
 * @Description:
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2021/9/19
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ALog.init().logLevel = ALogLevel.FULL
        NetInit.init(this)
            .withApiHost("https://cloudvc.tpv-tech.com")
            .showLog(true).configure()
    }
}