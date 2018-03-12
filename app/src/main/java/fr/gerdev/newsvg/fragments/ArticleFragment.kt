package fr.gerdev.newsvg.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.gerdev.newsvg.R
import fr.gerdev.newsvg.activity.tabNews.TabNewsVM
import fr.gerdev.newsvg.adapter.ArticleAdapter
import fr.gerdev.newsvg.model.Article
import fr.gerdev.newsvg.model.ArticleCategory
import fr.gerdev.newsvg.model.ArticleDevice
import fr.gerdev.newsvg.repository.ArticleResult
import kotlinx.android.synthetic.main.fragment_article.*
import timber.log.Timber

class ArticleFragment : Fragment() {

    private val articles: MutableList<Article> = mutableListOf()

    private var adapter: ArticleAdapter? = null
    private var refreshing: Boolean = false
    private var listener: Listener? = null

    private lateinit var receiver: BroadcastReceiver
    private lateinit var category: ArticleCategory
    private lateinit var device: ArticleDevice
    private lateinit var articleLiveData: LiveData<ArticleResult>

    companion object {
        const val INTENT_ACTION_REFRESH = "intent_action_refresh"

        const val EXTRA_CATEGORY = "extra_category"
        private const val EXTRA_DEVICE = "extra_device"

        fun newInstance(category: ArticleCategory, device: ArticleDevice): ArticleFragment {
            val fragment = ArticleFragment()
            val args = Bundle()
            args.putString(EXTRA_CATEGORY, category.name)
            args.putString(EXTRA_DEVICE, device.name)
            fragment.arguments = args
            return fragment
        }
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

        category = ArticleCategory.valueOf(arguments?.getString(EXTRA_CATEGORY)!!)
        device = ArticleDevice.valueOf(arguments?.getString(EXTRA_DEVICE)!!)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                category = ArticleCategory.valueOf(intent.getStringExtra(EXTRA_CATEGORY))
                forceRefresh()
            }
        }

        observeArticles()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter(INTENT_ACTION_REFRESH))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
    }

    private fun observeArticles(forceRefresh: Boolean = false) {
        articleLiveData = ViewModelProviders.of(this).get(TabNewsVM::class.java).observeArticles(category, device, forceRefresh)
        setRefreshing(true)
        articleLiveData.observe(this, Observer<ArticleResult> { result ->
            if (result?.articles?.isNotEmpty() == true) {
                onNewArticles(result.articles)
            }

            if (this.articles.isNotEmpty()) {
                emptyMsg.visibility = View.GONE
            } else {
                emptyMsg.visibility = View.VISIBLE
            }

            if (result?.refreshFinished == true) {
                setRefreshing(false)
                scrollToUp()
            } else {
                setRefreshing(true)
            }
        })
    }

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
            forceRefresh()
        }

        swipeRefresh.isRefreshing = refreshing

        emptyMsg.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun forceRefresh() {
        articles.clear()
        adapter?.notifyDataSetChanged()

        articleLiveData.removeObservers(this)
        observeArticles(true)
    }

    private fun color(@ColorRes color: Int) = ContextCompat.getColor(context!!, color)

    private fun onNewArticles(articles: List<Article>) {
        val descriptions = articles.map { it.description }
        val newArticles = articles.filter { descriptions.contains(it.description) }
        Timber.i("${newArticles.count()} added to adapter")
        this.articles.addAll(newArticles)
        this.articles.sortByDescending { it.downloadDate }
        adapter?.notifyItemRangeInserted(0, articles.count())
    }

    private fun scrollToUp() {
        if (articlesView?.adapter?.itemCount != 0)
            articlesView?.scrollToPosition(0)
    }

    interface Listener

    private fun setRefreshing(refreshing: Boolean) {
        this.refreshing = refreshing
        if (swipeRefresh != null) {
            swipeRefresh.isRefreshing = refreshing
        }
    }
}