package kg.hello.muzu_research.util

import kg.hello.muzu_research.R

class FilesManager {
    private val names = arrayListOf("Anesthesia", "Candyland", "China-X", "Cloud 9",
                                    "Highscore", "Luv U need U", "Vision", "Windfall")
    private val artists = arrayListOf("Vexento & Dexento", "Tobu", "YUAN", "Itro & Tobu",
                                    "Terminite & Panda Eyes", "Slushii", "Lost Sky", "TheFatRat")
    private val audioIds = arrayListOf(R.raw.audio_anesthesia, R.raw.audio_candyland, R.raw.audio_chinax, R.raw.audio_cloud9,
                                    R.raw.audio_highscore, R.raw.audio_luvuneedu, R.raw.audio_vision, R.raw.audio_windfall)
    private val muzUIds = arrayListOf(R.raw.anesthesia, R.raw.candyland, R.raw.china_x, R.raw.cloud9,
                                    R.raw.highscore, R.raw.luvuneedu, R.raw.vision, R.raw.windfall)

    private val songCount = 8

    private var currentIndex = 0

    fun next() {
        currentIndex++
        if(currentIndex >= songCount) currentIndex -= songCount
    }

    fun prev(){
        currentIndex--
        if(currentIndex < 0) currentIndex += songCount
    }

    val name get() = names[currentIndex]
    val artist get() = artists[currentIndex]
    val audio get() = audioIds[currentIndex]
    val muzU get() = muzUIds[currentIndex]
}