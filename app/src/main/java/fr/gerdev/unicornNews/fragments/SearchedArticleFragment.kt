package fr.gerdev.unicornNews.fragments

import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.view.View
import fr.gerdev.unicornNews.model.Article
import kotlinx.android.synthetic.main.fragment_article.*

class SearchedArticleFragment : BaseArticleFragment() {

    companion object {
        fun newInstance(): SearchedArticleFragment {
            val fragment = SearchedArticleFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterSomething.visibility = View.VISIBLE
        loadingMsg.visibility = View.GONE
    }

    private lateinit var query: String

    override fun getLiveData(forceRefresh: Boolean): LiveData<List<Article>> {
        enterSomething.visibility = View.GONE
        loadingMsg.visibility = View.VISIBLE
        return vm.searchArticles(query)
    }

    fun onNewQuery(query: String) {
        this.query = query
        if (query.isNotEmpty()) {
            getArticles()
            enterSomething.visibility = View.GONE
        } else {
            enterSomething.visibility = View.VISIBLE
            clearArticles()
        }
    }

    override fun onArticlesReaded(articles: List<Article>) {
        super.onArticlesReaded(articles)
        swipeRefresh.isRefreshing = false
    }
}
