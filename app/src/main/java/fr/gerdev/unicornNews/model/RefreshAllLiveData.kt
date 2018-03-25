package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask

class RefreshAllLiveData(context: Context) : LiveData<List<Article>>()
        , ArticleParseListener {

    private val parser = RefreshAllParser(context, this)

    override fun onInactive() {
        parser.stopParse()
    }

    override fun onActive() {
        AsyncTask.execute { parser.parse() }
    }

    override fun onParsed(articles: List<Article>) {
        postValue(articles)
    }
}