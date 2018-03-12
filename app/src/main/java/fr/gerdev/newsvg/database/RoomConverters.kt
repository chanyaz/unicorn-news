package fr.gerdev.newsvg.database

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.gerdev.newsvg.model.ArticleSource
import org.joda.time.DateTime


// ger 03/03/18
class RoomConverters {
    @TypeConverter
    fun stringToArticleSource(articleSource: String?): ArticleSource? {
        return ArticleSource.valueOf(articleSource ?: "")
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

    @TypeConverter
    fun stringtoList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun ListToString(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}