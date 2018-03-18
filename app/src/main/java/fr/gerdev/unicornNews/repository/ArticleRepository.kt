package fr.gerdev.unicornNews.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import fr.gerdev.unicornNews.database.AppDatabase
import fr.gerdev.unicornNews.fragments.ArticleFragment
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleParseListener
import fr.gerdev.unicornNews.model.ArticleParser
import fr.gerdev.unicornNews.model.ArticleSource
import fr.gerdev.unicornNews.rest.RssService
import fr.gerdev.unicornNews.util.Prefs
import timber.log.Timber

data class ArticleResult(val refreshFinished: Boolean, val articles: List<Article>)

class ArticleRepository(private val context: Context,
                        private val rssService: RssService) {

    private var parser: ArticleParser? = null

    fun readStoredArticles(sources: List<ArticleSource>): LiveData<List<Article>> {
        return AppDatabase.getInstance(context).articleDao().asLiveDataBySource(sources.map { it.name })
    }

    fun updateArticles(sources: List<ArticleSource>) {
        Timber.i("${sources.size} source(s) to refresh")

        parser?.stopParse()
        parser = ArticleParser(object : ArticleParseListener {

            override fun onParsedAndFiltered(articles: List<Article>) {

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

            override fun onParseFinished() {
                val localIntent = Intent(ArticleFragment.INTENT_ACTION_REFRESHED)
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
            }
        }, rssService, this)

        parser?.parse(sources)
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
}