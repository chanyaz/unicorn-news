package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import fr.gerdev.unicornNews.fragments.ArticleFragment
import fr.gerdev.unicornNews.repository.ArticleRepository
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CategoryDeviceLiveData(private var category: ArticleCategory,
                             private var device: ArticleDevice,
                             private var context: Context, forceRefresh: Boolean) :
        LiveData<List<Article>>(), ArticleParseListener {

    private lateinit var articleRepository: ArticleRepository
    private lateinit var executor: ExecutorService
    private val parser = CategoryDeviceParser(category, device, forceRefresh, context, this)

    override fun onInactive() {

        //for parser part
        parser.stopParse()

        //for bdd part
        executor.shutdownNow()

        value = emptyList()
    }

    override fun onActive() {

        articleRepository = ArticleRepository(context)
        executor = Executors.newSingleThreadExecutor()
        executor.execute((Loader()))

        parser.parse()
    }

    override fun onParsed(articles: List<Article>) {
        //may be shutdown if we got call back after unactive
        if (!executor.isShutdown) {
            if (articles.isNotEmpty()) executor.execute((Loader()))

            val intent = Intent(ArticleRepository.INTENT_ACTION_DATA_FETCHED)

            intent.putExtra(ArticleFragment.EXTRA_CATEGORY, category.name)
            intent.putExtra(ArticleFragment.EXTRA_DEVICE, device.name)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    inner class Loader : Runnable {
        override fun run() {
            try {
                postValue(articleRepository.read(category, device))
            } catch (e: InterruptedException) {
                Timber.e("Loader with sources by category ${category.name} and device ${device.name} interrupted")
            }
        }
    }
}