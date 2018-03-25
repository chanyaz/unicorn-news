package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context

class RefreshAllLiveData(context: Context) : LiveData<List<Article>>()
        , ArticleParseListener {

    private val parser = RefreshAllParser(context, this)

    override fun onInactive() {
        parser.stopParse()
        value = emptyList()
    }

    override fun onActive() {
        parser.parse()
    }

    override fun onParsed(articles: List<Article>) {
        if (hasActiveObservers()) postValue(articles)
    }
}