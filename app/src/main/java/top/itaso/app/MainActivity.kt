package top.itaso.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import top.itaso.lib.alog.ALog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_debug).setOnClickListener {
            ALog.d("savedInstanceState:${savedInstanceState}")
            ALog.dTag("itaso", "savedInstanceState:%s", savedInstanceState)

        }
        findViewById<Button>(R.id.btn_debug2).setOnClickListener {
            ALog.init().resetWritePermission(true)
        }
    }
}