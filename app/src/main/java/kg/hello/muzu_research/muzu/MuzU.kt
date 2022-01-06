package kg.hello.muzu_research.muzu

import org.jdom2.Document
import org.jdom2.input.DOMBuilder
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class MuzU(file: InputStream) {
    val tracks: List<Track>
    val margin: Double
    init {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val w3doc = db.parse(file)

        val doc: Document = DOMBuilder().build(w3doc)
        val root = doc.rootElement
        val tracksEl = root.getChild("tracks")
        tracks = tracksEl.children.map { element -> element.toTrack() }
        margin = root.getAttribute("margin")?.doubleValue?:0.0
    }
}