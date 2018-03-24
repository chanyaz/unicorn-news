package fr.gerdev.unicornNews.fragments

import android.arch.lifecycle.LiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.gerdev.unicornNews.R
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleCategory
import fr.gerdev.unicornNews.model.ArticleDevice
import fr.gerdev.unicornNews.model.ArticleSource
import fr.gerdev.unicornNews.repository.ArticleRepository
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : BaseArticleFragment() {

    private lateinit var category: ArticleCategory
    private lateinit var device: ArticleDevice
    private lateinit var receiver: BroadcastReceiver

    companion object {
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

    override fun parseIntentActionRefreshDataExtras(intent: Intent) {
        category = ArticleCategory.valueOf(intent.getStringExtra(EXTRA_CATEGORY))
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args != null) {
            category = ArticleCategory.valueOf(args.getString(EXTRA_CATEGORY))
            device = ArticleDevice.valueOf(args.getString(EXTRA_DEVICE))
        }
    }

    override fun getLiveData(forceRefresh: Boolean): LiveData<List<Article>> = vm.getArticles(category, device, forceRefresh)

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()

        receiver = buildReceiver()

        filter.addAction(ArticleRepository.INTENT_ACTION_DATA_FETCHED)
        filter.addAction(INTENT_ACTION_REFRESH_DATA)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, filter)
        getArticles()
    }

    private fun buildReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    INTENT_ACTION_REFRESH_DATA -> {
                        parseIntentActionRefreshDataExtras(intent)
                        getArticles(true)
                    }
                    ArticleRepository.INTENT_ACTION_DATA_FETCHED -> {

                        val refreshedCount = intent.getIntExtra(ArticleRepository.EXTRA_REFRESHED_SOURCES_COUNT, 0)
                        snackbarIfAllSourceRefreshed(refreshedCount)
                        refreshFinished()
                    }
                }
            }

            private fun snackbarIfAllSourceRefreshed(sourceCount: Int) {
                if (sourceCount == ArticleSource.values().size) {
                    Snackbar.make(activity?.findViewById(android.R.id.content)!!
                            , getString(R.string.all_sources_refreshed), Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
    }

    private fun refreshFinished() {
        swipeRefresh.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_article, container, false)
    }
}