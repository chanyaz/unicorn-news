package fr.gerdev.newsvg.model


interface ArticleParseListener {
    fun onParsedAndFiltered(articles: List<Article>)
    fun onParseFinished()
}
