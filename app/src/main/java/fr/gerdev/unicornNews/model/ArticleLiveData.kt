package fr.gerdev.unicornNews.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import fr.gerdev.unicornNews.repository.ArticleRepository
import fr.gerdev.unicornNews.rest.RssService
import fr.gerdev.unicornNews.util.Prefs
import me.toptas.rssconverter.RssConverterFactory
import retrofit2.Retrofit
import timber.log.Timber

// ger 14/03/18
class ArticleLiveData(private val category: ArticleCategory,
                      private val device: ArticleDevice,
                      private val context: Context, private val forceRefresh: Boolean) : LiveData<List<Article>>() {

    private lateinit var wrappedLiveData: LiveData<List<Article>>
    private lateinit var articleRepository: ArticleRepository
    private var rssService: RssService = Retrofit.Builder()
            .baseUrl("http://www.toPreventRunTimeException.com")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create(RssService::class.java)

    override fun onInactive() {
        super.onInactive()
        Timber.e("ArticleLiveData on inactive")
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<List<Article>>) {
        super.observe(owner, observer)
        wrappedLiveData.observe(owner, Observer<List<Article>> { articles -> postValue(articles) })
    }

    override fun removeObservers(owner: LifecycleOwner) {
        super.removeObservers(owner)
        wrappedLiveData.removeObservers(owner)
    }


    override fun onActive() {
        super.onActive()

        Timber.i("ArticleLiveData on inactive")

        articleRepository = ArticleRepository(context, rssService)
        wrappedLiveData = articleRepository.readStoredArticles(filterSources(category, device))

        //refresh sources to refresh
        val sources = filterSources(category, device)
        if (forceRefresh)
            articleRepository.updateArticles(sources)
        else articleRepository.updateArticles(sources.filter { Prefs.shouldRefresh(context, it) })
    }

    private fun filterSources(category: ArticleCategory, device: ArticleDevice): List<ArticleSource> {
        val sources = ArticleSource.values().filter { it.category == category && it.device == device }
        return sources
    }
}