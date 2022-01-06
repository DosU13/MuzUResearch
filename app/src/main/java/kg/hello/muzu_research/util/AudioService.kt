package kg.hello.muzu_research.util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import kg.hello.muzu_research.R
import kotlin.random.Random

class AudioService : Service() {
    private lateinit var musicPlayer: MediaPlayer
    private var mBinder: IBinder =MyBinder()
    private val fm = FilesManager()
    val songName get() = fm.name
    val artistName get() = fm.artist
    val audioId get() = fm.audio
    val muzUId get() = fm.muzU
    val duration get() = musicPlayer.duration
    val currentPosition get() = musicPlayer.currentPosition
    val isPlaying get() = musicPlayer.isPlaying
    val curPos: Double
        get() {return musicPlayer.currentPosition.toDouble()/1000.0}

    override fun onCreate() {
        super.onCreate()
        musicPlayer = MediaPlayer.create(this, audioId)
        musicPlayer.isLooping = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Music Service started by user", Toast.LENGTH_LONG).show()
        musicPlayer.start()
        return START_STICKY
    }

    fun playPauseBtnClicked(){
        if (isPlaying) musicPlayer.pause()
        else musicPlayer.start()
    }

    fun nextBtnClicked(){
        musicPlayer.stop()
        musicPlayer.release()
        fm.next()
        createMusicPlayer()
        musicPlayer.start()
    }

    fun prevBtnClicked(){
        musicPlayer.stop()
        musicPlayer.release()
        fm.prev()
        createMusicPlayer()
        musicPlayer.start()
    }

    fun seekTo(position : Int){
        musicPlayer.seekTo(position)
    }

    private fun createMusicPlayer() {
        musicPlayer = MediaPlayer.create(this, fm.audio)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.stop()
        Toast.makeText(this, "Music Service destroyed by user", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    inner class MyBinder: Binder(){
        val service : AudioService get() = this@AudioService
    }
}