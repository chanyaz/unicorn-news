package fr.gerdev.unicornNews.extension

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.gerdev.unicornNews.model.AbstractParser
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleSource
import me.toptas.rssconverter.RssItem
import org.joda.time.DateTime

// ger 24/02/18
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun RssItem.toArticleEntry(articleSource: ArticleSource): Article {
    return Article(
            title = this.title,
            image = this.image ?: AbstractParser.parseImage(this),
            downloadDate = DateTime.now(),
            link = this.link,
            description = this.description,
            source = articleSource
    )
}