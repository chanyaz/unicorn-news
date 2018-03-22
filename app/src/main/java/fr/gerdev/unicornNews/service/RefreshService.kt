package fr.gerdev.unicornNews.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import fr.gerdev.unicornNews.repository.ArticleRepository

class RefreshService : IntentService("RefreshService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_REFRESH_ALL_SOURCES == action) {
                refreshAllSources()
            }
        }
    }

    private fun refreshAllSources() {
        ArticleRepository(this).updateAllArticles()
    }

    companion object {
        private const val ACTION_REFRESH_ALL_SOURCES = "fr.gerdev.unicornNews.service.action.refresh_all_sources"

        fun refreshAllSources(context: Context) {
            val intent = Intent(context, RefreshService::class.java)
            intent.action = ACTION_REFRESH_ALL_SOURCES
            context.startService(intent)
        }
    }
}
