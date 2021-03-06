package fr.gerdev.unicornNews.repository

import android.content.Context
import fr.gerdev.unicornNews.database.AppDatabase
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleCategory
import fr.gerdev.unicornNews.model.ArticleDevice
import fr.gerdev.unicornNews.model.ArticleSource

class ArticleRepository(private val context: Context) {

    companion object {
        const val INTENT_ACTION_DATA_FETCHED = "intent_action_refreshed"
    }

    private val articleDao = AppDatabase
            .getInstance(context)
            .articleDao()

    fun read(category: ArticleCategory, device: ArticleDevice): List<Article> {
        return articleDao
                .getBySources(
                        ArticleSource
                                .values()
                                .filter { it.category == category && it.device == device }
                                .map { it.name }
                )
    }

    fun searchArticles(query: String): List<Article> {
        val words: List<String> = query.split(" ")
        return when (words.size) {
            0 -> articleDao.search()
            1 -> articleDao.search("%" + words[0] + "%")
            2 -> articleDao.search("%" + words[0] + "%", "%" + words[1] + "%")
            3 -> articleDao.search("%" + words[0] + "%", "%" + words[1] + "%", "%" + words[2] + "%")
            else -> articleDao.search("%" + words[0] + "%", "%" + words[1] + "%", "%" + words[2] + "%", "%" + words[3] + "%")
        }
    }

    fun exists(it: Article): Boolean = !(articleDao.linkNotExist(it.description) && articleDao.descriptionNotExist(it.description) && articleDao.titleNotExist(it.title))


    fun insertAll(articles: List<Article>) {
        articleDao
                .insertAll(*articles.toTypedArray())
    }
}