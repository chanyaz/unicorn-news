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
        var read: Boolean = false) {

    constructor() : this(
            id = 0,
            title = "",
            image = "",
            description = "",
            downloadDate = DateTime.now(),
            link = "",
            read = false
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false
        if (title != other.title) return false
        if (image != other.image) return false
        if (description != other.description) return false
        if (downloadDate != other.downloadDate) return false
        if (link != other.link) return false
        if (source != other.source) return false
        if (read != other.read) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + downloadDate.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + read.hashCode()
        return result
    }
}