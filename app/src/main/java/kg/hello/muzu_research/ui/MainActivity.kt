package kg.hello.muzu_research.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kg.hello.muzu_research.R
import kg.hello.muzu_research.util.AudioService
import kg.hello.muzu_research.util.AudioServiceManager
import kg.hello.muzu_research.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ServiceConnection{
    private lateinit var binding: ActivityMainBinding
    private var audioService: AudioService? = null
    private lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        initViews()
    }

    private lateinit var rhythmFragment: RhythmFragment
    private fun initViews(){
        binding.playPause.setOnClickListener {playPauseBtnClicked()}
        binding.prevBtn.setOnClickListener { prevBtnClicked() }
        binding.nextBtn.setOnClickListener{ nextBtnClicked()}
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBarNotMy: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioService?.let {
                        it.seekTo(progress * 1000)
                        val mCurrentPosition = it.currentPosition / 1000
                        binding.seekbar.progress = mCurrentPosition
                        binding.durationPlayed.text = formattedTime(mCurrentPosition)
                    }
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        this@MainActivity.runOnUiThread(runnable {
            audioService?.let{
                val mCurrentPosition = it.currentPosition/1000
                binding.seekbar.progress = mCurrentPosition
                binding.durationPlayed.text = formattedTime(mCurrentPosition)
            }
            handler.postDelayed(this, 1000)
        })

        binding.tabDots.setupWithViewPager(binding.viewpagerPlayer, true)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragments(InfoFragment(), "")
        rhythmFragment = RhythmFragment()
        viewPagerAdapter.addFragments(rhythmFragment, "")
        binding.viewpagerPlayer.adapter = viewPagerAdapter
        binding.viewpagerPlayer.currentItem = 1
    }

    private fun refreshViews(){
        audioService?.let{
            binding.playPause.setBackgroundResource(
                if(it.isPlaying) R.drawable.ic_baseline_pause_24
                else R.drawable.ic_baseline_play_arrow_24)
            binding.seekbar.max = it.duration / 1000
            val mCurrentPosition = it.currentPosition/1000
            binding.seekbar.progress = mCurrentPosition
            binding.durationPlayed.text = formattedTime(mCurrentPosition)
            binding.songName.text = it.songName
            binding.songArtist.text = it.artistName
        }
        rhythmFragment.refreshViews()
    }

    private fun nextBtnClicked() {
        audioService?.nextBtnClicked()
        refreshViews()
    }

    private fun prevBtnClicked() {
        audioService?.prevBtnClicked()
        refreshViews()
    }

    private fun playPauseBtnClicked() {
        audioService?.let {
            binding.playPause.setBackgroundResource(if(it.isPlaying) R.drawable.ic_baseline_play_arrow_24
                                                    else R.drawable.ic_baseline_pause_24)
            it.playPauseBtnClicked()
            return
        }
        if(audioService == null) {
            startService()
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }
    }

    private fun formattedTime(mCurrentPosition: Int): String {
        val seconds : String = (mCurrentPosition % 60).toString()
        val minutes : String = (mCurrentPosition / 60).toString()
        val totalOut = "$minutes:$seconds"
        val totalNew = "$minutes:0$seconds"
        return if (seconds.length == 1){
            totalNew
        } else {
            totalOut
        }
    }

    private fun startService(){
        val audioManager = AudioServiceManager(this)
        if(!audioManager.isMyServiceRunning()) {
            val intent = Intent(this, AudioService::class.java)
            applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    private fun buttonPressed() {
        val audioManager = AudioServiceManager(this)
        if(!audioManager.isMyServiceRunning()){
            val intent = Intent(this, AudioService::class.java)
            applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
            startService(intent)
        } else{
            val intent = Intent(this, AudioService::class.java)
            applicationContext.unbindService(this)
            stopService(intent)
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.e("Main Activity", "OnServiceConnected called")
        val myBinder = service as AudioService.MyBinder
        audioService = myBinder.service
        rhythmFragment.audioService = audioService
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        audioService = null
        rhythmFragment.audioService = null
    }

    private fun runnable(body: Runnable.(Runnable)->Unit) = object : Runnable {
        override fun run() {
            this.body(this)
        }
    }
}