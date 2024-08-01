package org.thundernetwork

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class SitemapGenerator(private val directoryPath: String) {
    private val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().apply {
        setOutputProperty(OutputKeys.INDENT, "yes")
        setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "2")
    }
    private val maxUrlsPerSitemap = 50000
    private var sitemapCount = 0
    private val sitemapIndexFile = File("$directoryPath/sitemap_index.xml")

    init {
        if (!sitemapIndexFile.exists()) {
            val doc = docBuilder.newDocument()
            val rootElement = doc.createElement("sitemapindex")
            rootElement.setAttribute("xmlns", "https://www.sitemaps.org/schemas/sitemap/0.9")
            doc.appendChild(rootElement)
            saveDocument(doc, sitemapIndexFile)
        } else {
            val doc = docBuilder.parse(sitemapIndexFile)
            sitemapCount = doc.getElementsByTagName("sitemap").length
        }
    }

    private fun getSitemapFile(index: Int): File {
        return File("$directoryPath/sitemap_$index.xml")
    }

    private fun getDocument(file: File): org.w3c.dom.Document {
        return if (file.exists()) {
            docBuilder.parse(file)
        } else {
            val doc = docBuilder.newDocument()
            val rootElement = doc.createElement("urlset")
            rootElement.setAttribute("xmlns", "https://www.sitemaps.org/schemas/sitemap/0.9")
            doc.appendChild(rootElement)
            doc
        }
    }

    fun addUrl(url: String, lastmod: String? = null, changefreq: String? = null, priority: String? = null) {
        val currentSitemapFile = getSitemapFile(sitemapCount)
        var doc = getDocument(currentSitemapFile)
        var root = doc.documentElement

        if (root.getElementsByTagName("url").length >= maxUrlsPerSitemap) {
            sitemapCount++
            doc = getDocument(getSitemapFile(sitemapCount))
            root = doc.documentElement
            updateSitemapIndex()
        }

        val urlElement = doc.createElement("url")
        val locElement = doc.createElement("loc")
        locElement.textContent = url
        urlElement.appendChild(locElement)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val now = LocalDateTime.now().format(formatter)
        val lastmodElement = doc.createElement("lastmod")
        lastmodElement.textContent = lastmod ?: now
        urlElement.appendChild(lastmodElement)

        changefreq?.let {
            val changefreqElement = doc.createElement("changefreq")
            changefreqElement.textContent = it
            urlElement.appendChild(changefreqElement)
        }

        priority?.let {
            val priorityElement = doc.createElement("priority")
            priorityElement.textContent = it
            urlElement.appendChild(priorityElement)
        }

        root.appendChild(urlElement)
        saveDocument(doc, currentSitemapFile)
    }

    fun removeUrl(url: String) {
        for (i in 0..sitemapCount) {
            val sitemapFile = getSitemapFile(i)
            if (sitemapFile.exists()) {
                val doc = getDocument(sitemapFile)
                val root = doc.documentElement
                val urlNodes = root.getElementsByTagName("url")
                for (j in 0 until urlNodes.length) {
                    val urlNode = urlNodes.item(j)
                    val locNode = urlNode.childNodes.item(1)
                    if (locNode.textContent == url) {
                        root.removeChild(urlNode)
                        saveDocument(doc, sitemapFile)
                        return
                    }
                }
            }
        }
    }

    private fun updateSitemapIndex() {
        val doc = docBuilder.parse(sitemapIndexFile)
        val root = doc.documentElement

        val sitemapElement = doc.createElement("sitemap")
        val locElement = doc.createElement("loc")
        locElement.textContent = "sitemap_$sitemapCount.xml"
        sitemapElement.appendChild(locElement)

        root.appendChild(sitemapElement)
        saveDocument(doc, sitemapIndexFile)
    }

    private fun saveDocument(doc: org.w3c.dom.Document, file: File) {
        val source = DOMSource(doc)
        val result = StreamResult(file)
        transformer.transform(source, result)
    }
}
