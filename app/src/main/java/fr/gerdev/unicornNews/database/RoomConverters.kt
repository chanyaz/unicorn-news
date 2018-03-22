package fr.gerdev.unicornNews.database

import android.arch.persistence.room.TypeConverter
import fr.gerdev.unicornNews.model.ArticleSource
import org.joda.time.DateTime


// ger 03/03/18
class RoomConverters {
    @TypeConverter
    fun stringToArticleSource(articleSource: String?): ArticleSource? {
        return try {
            ArticleSource.valueOf(articleSource ?: "")
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun articleSourceToString(articleSource: ArticleSource?): String {
        return articleSource?.name ?: ""
    }

    @TypeConverter
    fun stringToDatetime(dt: String?): DateTime? {
        return if (!dt.isNullOrEmpty()) DateTime.parse(dt) else null
    }

    @TypeConverter
    fun datetimeToString(dt: DateTime?): String? {
        return dt?.toString() ?: ""
    }
}