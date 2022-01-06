package kg.hello.muzu_research.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kg.hello.muzu_research.util.AnimAlgorithm
import kg.hello.muzu_research.util.AudioService
import kg.hello.muzu_research.util.MidiTxtReader
import kg.hello.muzu_research.databinding.FragmentRhythmBinding

class RhythmFragment() : Fragment() {
    private var _binding: FragmentRhythmBinding? = null
    private val binding get() = _binding!!
    private lateinit var handler: Handler
    var audioService: AudioService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRhythmBinding.inflate(inflater, container, false)

        val midiTxtReader = MidiTxtReader(requireContext())
        var animAlgorithm : AnimAlgorithm? = null
        val lastId = -1;
        handler = Handler(Looper.getMainLooper())
        requireActivity().runOnUiThread(runnable {
            handler.postDelayed(this, 20)
             audioService?.let{
                if(lastId != it.muzUId){
                    animAlgorithm = AnimAlgorithm(resources.openRawResource(it.muzUId))
                }
                binding.rectangle1.setBackgroundColor(animAlgorithm!!.getBackGrClr(audioService!!.curPos))
                binding.rectangle2.setBackgroundColor(animAlgorithm!!.getBackGrClr(audioService!!.curPos))
                binding.rectangle3.setBackgroundColor(animAlgorithm!!.getBackGrClr(audioService!!.curPos))
                var imgSize = if(binding.mainLayout.width < binding.mainLayout.height) binding.mainLayout.width
                                else binding.mainLayout.height
                imgSize = (imgSize * animAlgorithm!!.getAnimFactor(audioService!!.curPos)).toInt()
                //Log.e("Before", binding.logo.layoutParams.width.toString())
                binding.logo.layoutParams.width = imgSize
                binding.logo.layoutParams.height = imgSize
                binding.logo.requestLayout()
                //Log.e("After", binding.logo.layoutParams.width.toString())
                //makeTxt(imgSize.toString())
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun runnable(body: Runnable.(Runnable)->Unit) = object : Runnable {
        override fun run() {
            this.body(this)
        }
    }

    fun refreshViews() {

    }
}