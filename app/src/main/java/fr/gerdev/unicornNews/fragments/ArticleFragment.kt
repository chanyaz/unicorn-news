package fr.gerdev.unicornNews.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
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
import fr.gerdev.unicornNews.R
import fr.gerdev.unicornNews.activity.tabNews.TabNewsVM
import fr.gerdev.unicornNews.adapter.ArticleAdapter
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleCategory
import fr.gerdev.unicornNews.model.ArticleDevice
import kotlinx.android.synthetic.main.fragment_article.*
import java.util.*

class ArticleFragment : Fragment() {

    private val articles: LinkedHashSet<Article> = linkedSetOf()

    private var adapter: ArticleAdapter? = null
    private var listener: Listener? = null
    private var articleLiveData: LiveData<List<Article>>? = null

    private lateinit var vm: TabNewsVM
    private lateinit var receiver: BroadcastReceiver
    private lateinit var category: ArticleCategory
    private lateinit var device: ArticleDevice

    companion object {
        const val INTENT_ACTION_REFRESHED = "intent_action_refreshed"
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
                when (intent.action) {
                    INTENT_ACTION_REFRESH -> {
                        category = ArticleCategory.valueOf(intent.getStringExtra(EXTRA_CATEGORY))
                        forceRefresh()
                    }
                    INTENT_ACTION_REFRESHED -> refreshFinished()
                }

            }
        }

        observeArticles()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter(INTENT_ACTION_REFRESH, INTENT_ACTION_REFRESHED))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
    }

    private fun observeArticles() {
        articleLiveData?.removeObservers(this)
        articleLiveData = vm.getArticles(category, device)
        swipeRefresh?.isRefreshing = true

        articleLiveData?.observe(this, Observer<List<Article>> { result ->
            onArticlesReaded(result!!)
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

        emptyMsg.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun unicoooooooornnnNews() {
        val number = Random().nextInt(12)
        if (number > 0) unicorn.visibility = View.GONE
        else unicorn.visibility = View.VISIBLE
    }

    private fun forceRefresh() {
        articles.clear()
        adapter?.notifyDataSetChanged()
        vm.getRefreshedArticles(category, device)
    }

    private fun color(@ColorRes color: Int) = ContextCompat.getColor(context!!, color)

    private fun onArticlesReaded(articles: List<Article>) {
        unicoooooooornnnNews()
        this.articles.clear()
        val previousSize = this.articles.size
        this.articles.addAll(articles.sortedByDescending { it.downloadDate })
        adapter?.notifyItemRangeInserted(0, this.articles.size - previousSize)
    }

    private fun refreshFinished() {
        swipeRefresh.isRefreshing = false
        if (articlesView?.adapter?.itemCount != 0)
            articlesView?.scrollToPosition(0)
    }

    interface Listener
}