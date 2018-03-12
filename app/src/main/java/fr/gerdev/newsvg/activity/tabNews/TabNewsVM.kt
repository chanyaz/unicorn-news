package fr.gerdev.newsvg.activity.tabNews

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import fr.gerdev.newsvg.model.ArticleCategory
import fr.gerdev.newsvg.model.ArticleDevice
import fr.gerdev.newsvg.repository.ArticleRepository
import fr.gerdev.newsvg.repository.ArticleResult
import fr.gerdev.newsvg.rest.RssService
import me.toptas.rssconverter.RssConverterFactory
import retrofit2.Retrofit


class TabNewsVM(app: Application) : AndroidViewModel(app) {

    private var rssService: RssService = Retrofit.Builder()
            .baseUrl("http://www.toPreventRunTimeException.com")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create(RssService::class.java)

    private var articleRepository: ArticleRepository = ArticleRepository(app, rssService)

    fun observeArticles(category: ArticleCategory, device: ArticleDevice, forceRefresh: Boolean): LiveData<ArticleResult> {
        return articleRepository.getArticles(category, device, forceRefresh)
    }
}

