package kg.hello.muzu_research.util

import android.app.ActivityManager
import android.content.Context
import kg.hello.muzu_research.ui.MainActivity

class AudioServiceManager(private val mainActivity: MainActivity){
    fun isMyServiceRunning(): Boolean {
        val manager : ActivityManager = mainActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getRunningServices(Integer.MAX_VALUE)
            .forEach { service : ActivityManager.RunningServiceInfo ->
                if(AudioService::class.java.name.equals(service.service.className)){
                    return true
                }
            }
        return false
    }
}