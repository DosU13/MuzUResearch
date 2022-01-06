package kg.hello.muzu_research.muzu

import org.jdom2.Element

data class Note(
    val time: Double,
    val note: Int,
    val velocity: Int
)

fun Element.toNote(): Note {
    return Note(
        getAttribute("time")?.doubleValue ?: 0.0,
        getAttribute("note")?.intValue ?: 0,
        getAttribute("velocity")?.intValue ?: 0
    )
}