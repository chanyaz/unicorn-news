package fr.gerdev.unicornNews.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.gerdev.unicornNews.R
import fr.gerdev.unicornNews.activity.tabNews.TabNewsVM
import fr.gerdev.unicornNews.adapter.ArticleAdapter
import fr.gerdev.unicornNews.model.Article
import kotlinx.android.synthetic.main.fragment_article.*
import java.util.*

abstract class BaseArticleFragment : Fragment() {

    private val articles: MutableList<Article> = mutableListOf()

    protected var adapter: ArticleAdapter? = null
    private var listener: Listener? = null
    private var articleLiveData: LiveData<List<Article>>? = null

    protected lateinit var vm: TabNewsVM

    companion object {
        const val INTENT_ACTION_REFRESH_DATA = "intent_action_refresh"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as Listener
        adapter = ArticleAdapter(context, articles)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        adapter = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this).get(TabNewsVM::class.java)
    }

    open fun parseIntentActionRefreshDataExtras(intent: Intent) {}

    protected fun getArticles(forceRefresh: Boolean = false) {
        swipeRefresh?.isRefreshing = true

        clearArticles()

        loadingMsg.visibility = View.VISIBLE
        emptyMsg.visibility = View.GONE

        articleLiveData?.removeObservers(this)
        articleLiveData = getLiveData(forceRefresh)
        articleLiveData?.observe(this, Observer<List<Article>> { result ->
            onArticlesReaded(result!!)
        })
    }

    protected fun clearArticles() {
        articles.clear()
        adapter?.notifyDataSetChanged()
    }

    abstract fun getLiveData(forceRefresh: Boolean): LiveData<List<Article>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articlesView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        articlesView.layoutManager = layoutManager

        swipeRefresh.setColorSchemeColors(
                color(R.color.colorPrimaryDark), Color.BLUE, Color.BLACK)
        swipeRefresh.setProgressBackgroundColorSchemeColor(color(R.color.colorPrimaryBackground))

        swipeRefresh.setOnRefreshListener {
            getArticles(true)
        }
    }

    private fun unicoooooooornnnNews() {
        val number = Random().nextInt(12)
        if (number > 0) unicorn.visibility = View.GONE
        else unicorn.visibility = View.VISIBLE
    }

    private fun color(@ColorRes color: Int) = ContextCompat.getColor(context!!, color)

    open fun onArticlesReaded(articles: List<Article>) {

        loadingMsg.visibility = View.GONE
        unicoooooooornnnNews()
        this.articles.clear()
        val previousSize = this.articles.size
        this.articles.addAll(articles.sortedByDescending { it.downloadDate })

        //happens when backing to searchArticleFragment after clicking previously on article
        // notifyItemRangeInserted must not notify 0, other wis there is run time exception
        if (this.articles.size - previousSize != 0) {
            adapter?.notifyItemRangeInserted(0, this.articles.size - previousSize)
        }

        if (articles.isNotEmpty()) emptyMsg.visibility = View.GONE
        else emptyMsg.visibility = View.VISIBLE
    }

    interface Listener
}