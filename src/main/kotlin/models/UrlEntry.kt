package org.thundernetwork.sitemap.models

data class UrlEntry(
    val loc: String,
    val lastmod: String? = null,
    val changefreq: String? = null,
    val priority: Double? = null
)