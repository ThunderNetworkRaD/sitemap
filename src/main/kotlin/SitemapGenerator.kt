package org.thundernetwork.sitemap

import org.thundernetwork.sitemap.models.UrlEntry
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class SitemapGenerator(private val large: Boolean, private val path: String) {

    private val urlEntries = mutableListOf<UrlEntry>()

    fun loadSitemap(filePath: String) {
        val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docFactory.parse(File(filePath))
        val urlNodes = doc.getElementsByTagName("url")

        for (i in 0 until urlNodes.length) {
            val urlElement = urlNodes.item(i) as Element

            val loc = urlElement.getElementsByTagName("loc").item(0).textContent
            val lastmod = urlElement.getElementsByTagName("lastmod")?.item(0)?.textContent
            val changefreq = urlElement.getElementsByTagName("changefreq")?.item(0)?.textContent
            val priority = urlElement.getElementsByTagName("priority")?.item(0)?.textContent?.toDouble()

            urlEntries.add(UrlEntry(loc, lastmod, changefreq, priority))
        }
    }

    fun addUrl(urlEntry: UrlEntry) {
        urlEntries.add(urlEntry)
    }

    fun removeUrl(loc: String) {
        urlEntries.removeAll { it.loc == loc }
    }

    fun generateSitemap() {
        if (large && urlEntries.size > 10000) {
            generateLargeSitemap()
        } else {
            generateSingleSitemap(path)
        }
    }

    private fun generateSingleSitemap(filePath: String) {
        val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docFactory.newDocument()

        // Root element
        val urlset: Element = doc.createElement("urlset")
        urlset.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9")
        doc.appendChild(urlset)

        for (urlEntry in urlEntries) {
            val url: Element = doc.createElement("url")

            val loc: Element = doc.createElement("loc")
            loc.appendChild(doc.createTextNode(urlEntry.loc))
            url.appendChild(loc)

            urlEntry.lastmod?.let {
                val lastmod: Element = doc.createElement("lastmod")
                lastmod.appendChild(doc.createTextNode(it))
                url.appendChild(lastmod)
            }

            urlEntry.changefreq?.let {
                val changefreq: Element = doc.createElement("changefreq")
                changefreq.appendChild(doc.createTextNode(it))
                url.appendChild(changefreq)
            }

            urlEntry.priority?.let {
                val priority: Element = doc.createElement("priority")
                priority.appendChild(doc.createTextNode(it.toString()))
                url.appendChild(priority)
            }

            urlset.appendChild(url)
        }

        // Write the content into XML file
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val source = DOMSource(doc)
        val result = StreamResult(File(filePath))
        transformer.transform(source, result)
    }

    private fun generateLargeSitemap() {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val numSitemaps = (urlEntries.size + 9999) / 10000
        val sitemapIndexDocFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val sitemapIndexDoc: Document = sitemapIndexDocFactory.newDocument()
        val sitemapIndex: Element = sitemapIndexDoc.createElement("sitemapindex")
        sitemapIndex.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9")
        sitemapIndexDoc.appendChild(sitemapIndex)

        urlEntries.chunked(10000).forEachIndexed { index, chunk ->
            val sitemapFilePath = "$path/sitemap-$index.xml"
            generateSingleSitemap(sitemapFilePath)

            val sitemap: Element = sitemapIndexDoc.createElement("sitemap")
            val loc: Element = sitemapIndexDoc.createElement("loc")
            loc.appendChild(sitemapIndexDoc.createTextNode("$path/sitemap-$index.xml"))
            sitemap.appendChild(loc)
            sitemapIndex.appendChild(sitemap)
        }

        val indexFilePath = "$path.xml"
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val source = DOMSource(sitemapIndexDoc)
        val result = StreamResult(File(indexFilePath))
        transformer.transform(source, result)
    }
}
