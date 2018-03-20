package fr.gerdev.unicornNews.activity.tabNews

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleCategory
import fr.gerdev.unicornNews.model.ArticleDevice
import fr.gerdev.unicornNews.model.ArticleLiveData


class TabNewsVM(private val app: Application) : AndroidViewModel(app) {

    fun getArticles(category: ArticleCategory, device: ArticleDevice, forceRefresh: Boolean): LiveData<List<Article>> {
        return ArticleLiveData(category, device, app, forceRefresh)
    }
}

