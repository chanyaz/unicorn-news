package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context
import fr.gerdev.unicornNews.repository.ArticleRepository
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UpdateArticleLiveData(private val sources: List<ArticleSource>,
                            private val context: Context) : LiveData<Boolean>() {

    private lateinit var articleRepository: ArticleRepository
    private lateinit var executor: ExecutorService

    override fun onInactive() {
        articleRepository.stopParse()
        executor.shutdownNow()
    }

    override fun onActive() {
        articleRepository = ArticleRepository(context)

        executor = Executors.newSingleThreadExecutor()
        executor.execute(ArticleLoader())
    }

    inner class ArticleLoader : Runnable {
        override fun run() {
            try {
                articleRepository.updateArticles(sources)
                postValue(true)
            } catch (e: InterruptedException) {
                Timber.e("ArticleUpdateLoader with ${sources.joinToString(" ")} interrupted")
            } catch (e: Exception) {
                postValue(false)
            }
        }
    }
}