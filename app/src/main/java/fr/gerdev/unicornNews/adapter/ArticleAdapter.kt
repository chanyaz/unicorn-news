package fr.gerdev.unicornNews.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import fr.gerdev.unicornNews.R
import fr.gerdev.unicornNews.activity.detailNews.DetailNewsActivity
import fr.gerdev.unicornNews.database.AppDatabase
import fr.gerdev.unicornNews.extension.inflate
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.util.DateUtil
import kotlinx.android.synthetic.main.article_item_row.view.*


class ArticleAdapter(private val context: Context, private val articles: List<Article>)
    : RecyclerView.Adapter<ArticleAdapter.ArticlesVH>() {

    class ArticlesVH(private val context: Context, v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var article: Article? = null

        init {
            v.setOnClickListener(this)
        }

        fun bindArticle(article: Article) {
            this.article = article
            view.articlesTextView.text = article.title
            setTextColor()

            if (article.image != null) {
                view.articleImageContainer.visibility = View.VISIBLE

                Picasso.get()
                        .load(article.image)
                        .fit()
                        .centerCrop()
                        .into(view.articleImage)


            } else {
                view.articleImageContainer.visibility = View.GONE
            }

            view.articleDate.text = DateUtil.agoWithinOneWeek(article.downloadDate)

            view.articleSource.text = article.source?.description
        }

        private fun setTextColor() = view.articlesTextView.setTextColor(ContextCompat.getColor(context, if (article!!.read) R.color.colorPrimaryLight else R.color.colorPrimaryDark))

        override fun onClick(v: View) {
            val context = itemView.context
            val webIntent = Intent(context, DetailNewsActivity::class.java)
            webIntent.putExtra(DetailNewsActivity.URL_KEY, article?.link)
            context.startActivity(webIntent)
            AsyncTask.execute {
                AppDatabase.getInstance(context).articleDao().setReaded(article?.link ?: "")
            }
            article?.read = true
            setTextColor()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesVH {
        val vh = parent.inflate(R.layout.article_item_row, false)
        return ArticlesVH(context, vh)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ArticlesVH, position: Int) {
        val article = articles.toTypedArray()[position]
        holder.bindArticle(article)
    }
}