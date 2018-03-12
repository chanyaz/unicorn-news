package fr.gerdev.unicornNews.model

import me.toptas.rssconverter.RssItem
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

// ger 09/03/18
interface AbstractParser {

    companion object {
        private val IMAGE_HTTP_REGEX = """(https?://.*\.(?:png|jpg|gif))""".toRegex()
        val DAY_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yy HH:mm")

        fun parseImage(item: RssItem): String? {
            var imageUrl = item.image
            if (imageUrl == null && item.description != null) {
                imageUrl = IMAGE_HTTP_REGEX.find(item.description)?.groups?.get(0)?.value
            }
            if (imageUrl == null && item.title != null) {
                imageUrl = IMAGE_HTTP_REGEX.find(item.title)?.groups?.get(0)?.value
            }
            return imageUrl
        }
    }

    fun parse(sources: List<ArticleSource>)

    fun stopParse()
}