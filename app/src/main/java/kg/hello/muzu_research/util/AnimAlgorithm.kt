package kg.hello.muzu_research.util

import android.graphics.Color
import android.util.Log
import kg.hello.muzu_research.muzu.MuzU
import kg.hello.muzu_research.muzu.Note
import java.io.InputStream

class AnimAlgorithm(muzUFile: InputStream) {
    private val notes: List<Note>
    private val margin: Double
    init {
        val muzU = MuzU(muzUFile)
        margin = muzU.margin
        val notesCanBeEqual = muzU.tracks.flatMap { t -> t.notes }.sortedWith(compareBy { n -> n.time })
        val resNotes = mutableListOf<Note>()
        var temp = notesCanBeEqual[0]
        notesCanBeEqual.forEach{ n ->
            if(n.time == temp.time){
                if(temp.velocity < n.velocity) temp = n
            }else{
                resNotes.add(temp)
                temp = n
            }
        }
        resNotes.add(temp)
        notes = resNotes
    }

    private val maxPianoKey = 96
    private val minPianoKey = 46
    private val animDur = 0.2
    private val animFactorMin = 0.6
    private val defaultClr = Color.rgb((0.0*animFactorMin).toInt(), (255.0*animFactorMin).toInt(), (127.5*animFactorMin).toInt())

    fun getBackGrClr(duration: Double) : Int{
        val dur = duration + margin
        val curInd = getCurInd(dur)
        if(curInd == -1) return defaultClr
        val pianoKey = notes[curInd].note
        val rgb = getRgb(pianoKey)
//        val minClrMultiplier : Double = (pianoKey - minPianoKey).toDouble()/rangePianoKey
//        val maxClrMultiplier : Double = (maxPianoKey - pianoKey).toDouble()/rangePianoKey
//        var r =  (minKeyClr.red*minClrMultiplier+maxKeyClr.red*maxClrMultiplier).toInt()
//        var g = (minKeyClr.green*minClrMultiplier+maxKeyClr.green*maxClrMultiplier).toInt()
//        var b = (minKeyClr.blue*minClrMultiplier+maxKeyClr.blue*maxClrMultiplier).toInt()
        val animFactor = getAnimFactor(dur)
        rgb[0] = (rgb[0].toDouble()*animFactor).toInt()
        rgb[1] = (rgb[1].toDouble()*animFactor).toInt()
        rgb[2] = (rgb[2].toDouble()*animFactor).toInt()
        return Color.rgb(rgb[0], rgb[1], rgb[2])
    }

    fun getAnimFactor(duration: Double): Double{
        val dur = duration + margin
        val curInd = getCurInd(dur)
        if(curInd == -1) return animFactorMin
        val animPos = dur - notes[curInd].time
        var animFactor = animFactorMin
        if(animPos < animDur) {
            animFactor = animFactorMin + (1 - animFactorMin) * (animDur - animPos)
        }
        return animFactor
    }

    private var lastInd = 0
    private fun getCurInd(dur: Double): Int{
        return if(dur < notes[0].time) {
            -1
        }else if(notes[lastInd+1].time >= dur && notes[lastInd].time <= dur) {
            lastInd
        }else if(notes[lastInd+1].time <= dur && (dur - notes[lastInd+1].time) < 5) {
            while (notes[lastInd+1].time < dur) {lastInd++}
            lastInd
        }else {
            searchInd(0, notes.size-1, dur)
        }
    }

    private fun searchInd(l: Int, r: Int, dur: Double): Int {
        Log.e("Search index", "$l $r")
        return if(r >= l){
            val mid: Int = (l + r) / 2
            when {
                notes[mid].time <= dur && dur < notes[mid+1].time -> mid
                notes[mid].time > dur -> searchInd(l, mid-1, dur)
                notes[mid+1].time <= dur -> searchInd(mid+1, r, dur)
                else -> -2
            }
        }else -2
    }

    private fun getRgb(pianoKey: Int): IntArray {
        val key = pianoKey - minPianoKey
        val whichColor = key/10 //f00 ff0 0f0 0ff 00f f0f
        val colorValue = ((key%10)/10.0*255.0).toInt()
        var r=0; var g=0; var b=0
        when(whichColor){
            0->{
                r = 255
                g = colorValue
                b = 0
            }1->{
            r = 255 - colorValue
            g = 255
            b = 0
        }2->{
            r = 0
            g = 255
            b = colorValue
        }3->{
            r = 0
            g = 255 - colorValue
            b = 255
        }4->{
            r = colorValue
            g = 0
            b = 255
        }5->{
            r = 255
            g = 0
            b = 255
        }
        }
        return intArrayOf(r, g, b)
    }
}