package fr.gerdev.unicornNews.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.AsyncTask
import fr.gerdev.unicornNews.database.AppDatabase
import fr.gerdev.unicornNews.model.*
import fr.gerdev.unicornNews.rest.RssService
import fr.gerdev.unicornNews.util.Prefs
import timber.log.Timber

data class ArticleResult(val refreshFinished: Boolean, val articles: List<Article>)

class ArticleRepository(private val context: Context,
                        val rssService: RssService) {
    companion object {
        const val REFRESH_THRESHOLD = 1000 * 60 * 10
    }

    private var parser: ArticleParser? = null

    fun getArticles(category: ArticleCategory,
                    device: ArticleDevice,
                    forceRefresh: Boolean): LiveData<ArticleResult> {
        val articlesLiveData: MutableLiveData<ArticleResult> = MutableLiveData()
        AsyncTask.execute {
            val sources = ArticleSource.values()
                    .filter { it.category == category && it.device == device }

            if (!sources.isEmpty() && (forceRefresh || Prefs.shouldRefresh(context, sources))) {

                postDatabaseArticles(articlesLiveData, sources, false)

                if (!forceRefresh) sources.filter { Prefs.shouldRefresh(context, it) }
                Timber.i("download category : ${category.name} & device ${device.name}")
                Timber.i("${sources.size} source(s) to refresh")

                parser?.stopParse()

                parser = ArticleParser(object : ArticleParseListener {

                    override fun onParsedAndFiltered(articles: List<Article>) {
                        AsyncTask.execute {

                            val articleDao = AppDatabase
                                    .getInstance(context)
                                    .articleDao()

                            articleDao.insertAll(
                                    *articles
                                            .filter {
                                                !exists(it)
                                            }
                                            .toTypedArray())

                            sources.forEach {
                                Prefs.updateRefreshTime(context, it)
                            }
                        }
                        articlesLiveData.postValue(ArticleResult(false, articles))
                    }

                    override fun onParseFinished() {
                        articlesLiveData.postValue(ArticleResult(true, emptyList()))
                    }
                }, rssService, this)

                parser?.parse(sources)
            } else {
                postDatabaseArticles(articlesLiveData, sources, true)
            }

        }
        return articlesLiveData
    }

    fun exists(it: Article): Boolean {
        val articleDao = AppDatabase
                .getInstance(context)
                .articleDao()

        return (!it.description.isNullOrEmpty() && articleDao.sameDescriptionCount(it.description) > 0L)
                ||
                (!it.title.isNullOrEmpty() && articleDao.sameTitleCount(it.title) > 0L)
                ||
                articleDao.sameLinkCount(it.link) > 0L
    }

    private fun postDatabaseArticles(articles: MutableLiveData<ArticleResult>,
                                     sources: List<ArticleSource>,
                                     refreshFinished: Boolean) {
        articles.postValue(
                ArticleResult(
                        refreshFinished,
                        AppDatabase
                                .getInstance(context)
                                .articleDao()
                                .findBySources(sources.map { it.name }))
        )
    }
}