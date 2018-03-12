package fr.gerdev.unicornNews.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import fr.gerdev.unicornNews.model.Article

// ger 03/03/18
@Dao
interface ArticleDao {
    @get:Query("SELECT * FROM article")
    val all: List<Article>

    @Query("SELECT * FROM article WHERE id IN (:arg0)")
    fun loadAllByIds(articleIds: IntArray): List<Article>

    @Query("SELECT * FROM article WHERE source IN (:arg0) ORDER BY downloadDate desc")
    fun findBySources(sourceNames: List<String>): List<Article>

    @Query("SELECT COUNT(id) FROM article WHERE title  = (:arg0)")
    fun sameTitleCount(title: String?): Long

    @Query("SELECT COUNT(id) FROM article WHERE description  = (:arg0)")
    fun sameDescriptionCount(title: String?): Long

    @Query("SELECT COUNT(id) FROM article WHERE link  = (:arg0)")
    fun sameLinkCount(link: String?): Long

    @Query("UPDATE article SET read = 1 WHERE link  = (:arg0)")
    fun setReaded(link: String)

    @Insert
    fun insertAll(vararg article: Article)

    @Delete
    fun delete(article: Article)
}
