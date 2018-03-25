package fr.gerdev.unicornNews.activity.tabNews

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import fr.gerdev.unicornNews.model.*


class TabNewsVM(private val app: Application) : AndroidViewModel(app) {

    fun getArticles(category: ArticleCategory, device: ArticleDevice, forceRefresh: Boolean): LiveData<List<Article>> {
        return CategoryDeviceLiveData(category, device, app, forceRefresh)
    }

    fun searchArticles(query: String): LiveData<List<Article>> {
        return SearchLiveData(query, app)
    }

    fun updateAllArticles(): LiveData<List<Article>> {
        return RefreshAllLiveData(app)
    }
}

