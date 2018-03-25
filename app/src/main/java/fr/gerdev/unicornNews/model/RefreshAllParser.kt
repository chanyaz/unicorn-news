package fr.gerdev.unicornNews.model

import android.content.Context


// ger 09/03/18
class RefreshAllParser(context: Context,
                       listener: ArticleParseListener) : AbstractParser(context, listener) {

    override fun getSources(): List<ArticleSource> = ArticleSource.values().toList()
}