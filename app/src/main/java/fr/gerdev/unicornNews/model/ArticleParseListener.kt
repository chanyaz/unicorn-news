package fr.gerdev.unicornNews.model


interface ArticleParseListener {
    fun onParsed(articles: List<Article>)
}
