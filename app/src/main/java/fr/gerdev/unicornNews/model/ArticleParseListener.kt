package fr.gerdev.unicornNews.model


interface ArticleParseListener {
    fun onParsedAndFiltered(articles: List<Article>)
    fun onParseFinished()
}
