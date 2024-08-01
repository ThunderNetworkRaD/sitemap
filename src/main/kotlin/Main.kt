package org.thundernetwork.sitemap

import org.thundernetwork.sitemap.models.UrlEntry

fun main() {
    val urlEntries = listOf(
        UrlEntry("https://www.example.com/", "2024-07-01", "monthly", 1.0),
        UrlEntry("https://www.example.com/about", "2024-07-01", "monthly", 0.8),
        UrlEntry("https://www.example.com/contact", "2024-07-01", "monthly", 0.8)
    )

    val generator = SitemapGenerator(urlEntries)
    generator.generateSitemap("sitemap.xml")
}
