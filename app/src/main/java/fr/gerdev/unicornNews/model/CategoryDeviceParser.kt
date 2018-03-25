package fr.gerdev.unicornNews.model

import android.content.Context
import fr.gerdev.unicornNews.util.Prefs
import timber.log.Timber


// ger 09/03/18
class CategoryDeviceParser(private var category: ArticleCategory,
                           private var device: ArticleDevice,
                           private var forceRefresh: Boolean,
                           private var context: Context,
                           listener: ArticleParseListener) : AbstractParser(context, listener) {

    override fun getSources(): List<ArticleSource> {
        val sources = ArticleSource.values()
                .filter { it.category == category && it.device == device }
                .filter { if (forceRefresh) true else Prefs.shouldRefresh(context, it) }
        Timber.i("${sources.size} source(s) to refresh")
        return sources
    }
}