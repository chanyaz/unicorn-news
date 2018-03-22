package fr.gerdev.unicornNews.repository

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import fr.gerdev.unicornNews.database.AppDatabase
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleParseListener
import fr.gerdev.unicornNews.model.ArticleParser
import fr.gerdev.unicornNews.model.ArticleSource
import fr.gerdev.unicornNews.rest.RssService
import fr.gerdev.unicornNews.util.Prefs
import me.toptas.rssconverter.RssConverterFactory
import retrofit2.Retrofit
import timber.log.Timber

class ArticleRepository(private val context: Context) {

    companion object {
        const val INTENT_ACTION_DATA_FETCHED = "intent_action_refreshed"
        const val EXTRA_REFRESHED_SOURCES_COUNT: String = "extra_refreshed_sources_count"
    }

    private var parser: ArticleParser? = null
    private var rssService: RssService = Retrofit.Builder()
            .baseUrl("http://www.toPreventRunTimeException.com")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create(RssService::class.java)

    fun readStoredArticles(sources: List<ArticleSource>): List<Article> {
        return AppDatabase.getInstance(context).articleDao().getBySources(sources.map { it.name })
    }

    fun searchArticles(query: String): List<Article> {
        val words: List<String> = query.split(" ")
        val articleDao = AppDatabase.getInstance(context).articleDao()
        return when (words.size) {
            0 -> articleDao.search()
            1 -> articleDao.search("%" + words[0] + "%")
            2 -> articleDao.search("%" + words[0] + "%", "%" + words[1] + "%")
            3 -> articleDao.search("%" + words[0] + "%", "%" + words[1] + "%", "%" + words[2] + "%")
            else -> articleDao.search("%" + words[0] + "%", "%" + words[1] + "%", "%" + words[2] + "%", "%" + words[3] + "%")
        }
    }

    fun updateArticles(sources: List<ArticleSource>) {
        Timber.i("${sources.size} source(s) to refresh")

        stopParse()
        parser = ArticleParser(object : ArticleParseListener {

            override fun onParsedAndFiltered(articles: List<Article>) {

                val articleDao = AppDatabase
                        .getInstance(context)
                        .articleDao()

                articleDao.insertAll(
                        *articles.toTypedArray())

                sources.forEach {
                    Prefs.updateRefreshTime(context, it)
                }
            }

            override fun onParseFinished(refreshedSourcesCount: Int) {
                val localIntent = Intent(INTENT_ACTION_DATA_FETCHED)
                localIntent.putExtra(EXTRA_REFRESHED_SOURCES_COUNT, refreshedSourcesCount)
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
            }
        }, rssService, this)

        parser?.parse(sources)
    }

    fun updateAllArticles() {
        updateArticles(ArticleSource.values().toList())
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

    fun stopParse() {
        parser?.stopParse()
    }
}