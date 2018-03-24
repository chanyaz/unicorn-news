package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context
import fr.gerdev.unicornNews.repository.ArticleRepository
import fr.gerdev.unicornNews.util.Prefs
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ArticleLiveData(private val category: ArticleCategory,
                      private val device: ArticleDevice,
                      private val context: Context, private val forceRefresh: Boolean) : LiveData<List<Article>>() {

    private lateinit var articleRepository: ArticleRepository

    private lateinit var executor: ExecutorService

    override fun onInactive() {
        articleRepository.stopParse()
        executor.shutdownNow()
    }

    override fun onActive() {
        articleRepository = ArticleRepository(context)

        executor = Executors.newSingleThreadExecutor()
        executor.execute(Loader())
    }

    private fun filterSources(category: ArticleCategory, device: ArticleDevice): List<ArticleSource> {
        return ArticleSource.values().filter { it.category == category && it.device == device }
    }

    inner class Loader : Runnable {
        override fun run() {
            val sources = filterSources(category, device)
            try {
                if (forceRefresh)
                    articleRepository.updateArticles(sources)
                else articleRepository.updateArticles(sources.filter {
                    Prefs.shouldRefresh(context, it)
                })
                postValue(articleRepository.readStoredArticles(filterSources(category, device)))
            } catch (e: InterruptedException) {
                Timber.e("ArticleSearchLoader with ${sources.joinToString(" ")} interrupted")
            }
        }
    }
}