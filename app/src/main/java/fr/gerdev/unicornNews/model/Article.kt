package fr.gerdev.unicornNews.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.joda.time.DateTime

@Entity
class Article(
        @PrimaryKey var id: Long? = null,
        var title: String?,
        var image: String?,
        var description: String?,
        var downloadDate: DateTime,
        var link: String,
        var source: ArticleSource? = null,
        var read: Boolean = false
) {
    constructor() : this(
            id = 0,
            title = "",
            image = "",
            description = "",
            downloadDate = DateTime.now(),
            link = "",
            read = false
    )
}