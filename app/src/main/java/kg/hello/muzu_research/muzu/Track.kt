package kg.hello.muzu_research.muzu

import org.jdom2.Element

data class Track(
    val notes: List<Note>
)

fun Element.toTrack(): Track {
    return Track(this.children.map { element -> element.toNote() })
}