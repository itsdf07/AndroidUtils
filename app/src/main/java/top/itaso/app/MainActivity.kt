package top.itaso.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import top.itaso.lib.alog.ALog

class MainActivity : AppCompatActivity() {
    private val json:String = "{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"id\":\"644176904697548800\",\"url\":\"https://cloudvc.tpv-tech.com/adtest/api/common/open/quickfile/644176904697548800?uploadChannel=QUICK\",\"mqtt\":{\"serverAddress\":\"47.96.83.150\",\"port\":1883,\"websocketPort\":null,\"username\":\"quickfile\",\"password\":\"eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6InF1aWNrZmlsZSIsImlhdCI6MTYzMDkyMDk4M30.3r9BpD4FQGyJfgOPIOFL2FEmVWaLTVTqMFzXjm9fbcfhjZL_3RmXf2hTkf5YkcmTa0t-ettkf1AuWVagG4XrKw\",\"topic\":\"iwbshare/quickfile/644176904697548800\"}}}\n"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_debug).setOnClickListener {
            ALog.d("savedInstanceState:${savedInstanceState}")
            ALog.dTag("itaso", "savedInstanceState:%s", savedInstanceState)
            ALog.json(json)
        }
        findViewById<Button>(R.id.btn_debug2).setOnClickListener {
            ALog.init().resetWritePermission(true)
        }
    }
}