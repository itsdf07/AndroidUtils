package top.itaso.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import top.itaso.lib.alog.ALog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ALog.init().writeLog(this).writeCrash(this)
        ALog.d(">>>>>>savedInstanceState:${savedInstanceState}")
        ALog.d(">>>>>>savedInstanceState:%s", savedInstanceState)
    }
}