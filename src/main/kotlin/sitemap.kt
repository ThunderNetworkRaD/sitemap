package org.thundernetwork.sitemap

import org.thundernetwork.sitemap.models.UrlEntry

fun test() {
    val large = true  // Imposta questo valore in base alle tue esigenze
    val path = "sitemaps.xml"  // Imposta il percorso dove salvare le sitemaps

    val generator = SitemapGenerator(large, path)

    // Carica una sitemap esistente
    generator.loadSitemap("sitemap.xml")

    // Aggiungi nuovi URL
    generator.addUrl(UrlEntry("https://www.example.com/about", "2024-08-01", "weekly", 0.9))

    // Rimuovi un URL
    generator.removeUrl("https://www.example.com/old-page")

    // Genera la sitemap aggiornata
    generator.generateSitemap()
}
