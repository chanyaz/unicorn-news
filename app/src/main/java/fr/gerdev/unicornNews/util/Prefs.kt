package fr.gerdev.unicornNews.util

import android.content.Context
import android.content.SharedPreferences
import fr.gerdev.unicornNews.model.ArticleSource

// ger 09/03/18
class Prefs {
    companion object {
        const val LAST_REFRESH_PREF = "last_refresh_prefs"
        const val REFRESH_THRESHOLD = 10 * 60 * 1000

        fun shouldRefresh(context: Context, sources: List<ArticleSource>): Boolean {
            sources.forEach {
                if (shouldRefresh(context, it)) return true
            }
            return false
        }

        fun shouldRefresh(context: Context, source: ArticleSource): Boolean {
            val refreshTimestamp = getSharedPrefs(context).getLong(source.name, 0)
            return refreshTimestamp < System.currentTimeMillis() - REFRESH_THRESHOLD
        }

        fun updateRefreshTime(context: Context, source: ArticleSource) {
            getSharedPrefs(context)
                    .edit()
                    .putLong(source.name, System.currentTimeMillis()).apply()
        }

        private fun getSharedPrefs(context: Context): SharedPreferences =
                context.getSharedPreferences(LAST_REFRESH_PREF, Context.MODE_PRIVATE)
    }
}