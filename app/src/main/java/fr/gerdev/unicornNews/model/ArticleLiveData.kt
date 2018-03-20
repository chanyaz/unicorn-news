package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context
import fr.gerdev.unicornNews.repository.ArticleRepository
import fr.gerdev.unicornNews.rest.RssService
import fr.gerdev.unicornNews.util.Prefs
import me.toptas.rssconverter.RssConverterFactory
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// ger 14/03/18

class ArticleLiveData(private val category: ArticleCategory,
                      private val device: ArticleDevice,
                      private val context: Context, private val forceRefresh: Boolean) : LiveData<List<Article>>() {

    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var articleRepository: ArticleRepository
    private var rssService: RssService = Retrofit.Builder()
            .baseUrl("http://www.toPreventRunTimeException.com")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create(RssService::class.java)

    override fun onInactive() {
        Timber.e("ArticleLiveData on inactive, shut down executor now.")
        executor.shutdownNow()
    }

    override fun onActive() {
        val sources = filterSources(category, device)
        executor.execute(ArticleLoader(sources))
    }

    private fun filterSources(category: ArticleCategory, device: ArticleDevice): List<ArticleSource> {
        return ArticleSource.values().filter { it.category == category && it.device == device }
    }

    inner class ArticleLoader(private val sources: List<ArticleSource>) : Runnable {
        override fun run() {
            try {
                if (forceRefresh)
                    articleRepository.updateArticles(sources)
                else articleRepository.updateArticles(sources.filter {
                    Prefs.shouldRefresh(context, it)
                })
                postValue(ArticleRepository(context, rssService)
                        .readStoredArticles(filterSources(category, device)))
            } catch (e: InterruptedException) {
                Timber.e("ArticleLoader with ${sources.joinToString(" ")} interrupted")
            }
        }
    }
}