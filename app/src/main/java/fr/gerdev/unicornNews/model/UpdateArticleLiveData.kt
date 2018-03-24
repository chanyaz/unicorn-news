package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context
import fr.gerdev.unicornNews.repository.ArticleRepository
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UpdateArticleLiveData(private val context: Context) : LiveData<Boolean>() {

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

    inner class Loader : Runnable {
        override fun run() {
            try {
                articleRepository.updateAllArticles()
                postValue(true)
            } catch (e: InterruptedException) {
                Timber.e("ArticleUpdateLoader with ${ArticleSource.values().toList().joinToString(" ")} interrupted")
            } catch (e: Exception) {
                postValue(false)
            }
        }
    }
}