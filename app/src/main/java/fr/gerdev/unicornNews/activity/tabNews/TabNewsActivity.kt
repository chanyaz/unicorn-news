package fr.gerdev.unicornNews.activity.tabNews

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import fr.gerdev.unicornNews.R
import fr.gerdev.unicornNews.adapter.ArticleFragmentPagerAdapter
import fr.gerdev.unicornNews.adapter.MonoFragmentPagerAdapter
import fr.gerdev.unicornNews.fragments.ArticleFragment
import fr.gerdev.unicornNews.fragments.BaseArticleFragment
import fr.gerdev.unicornNews.model.Article
import fr.gerdev.unicornNews.model.ArticleCategory
import fr.gerdev.unicornNews.model.ArticleSource
import kotlinx.android.synthetic.main.activity_tab_news.*
import timber.log.Timber


class TabNewsActivity : AppCompatActivity(), BaseArticleFragment.Listener, ArticleFragmentPagerAdapter.CategoryProvider {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<BottomNavigationView>
    private lateinit var category: ArticleCategory
    private val countDown = object : CountDownTimer(5000, 1000) {

        override fun onFinish() {
            showBottomSheet(false)
        }

        override fun onTick(p0: Long) {
        }
    }
    private var verticalAppbarOffset = 0
    private var bottomSheetShowable = true

    private var updateLiveData: LiveData<List<Article>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_news)
        setSupportActionBar(my_toolbar)
        my_toolbar.setLogo(R.drawable.ic_unicorn)

        category = ArticleCategory.GENERAL
        bottomSheetBehavior = BottomSheetBehavior.from(bottomNavigation)

        setPagerAdapter()
        tabLayout.setViewPager(viewPager)
        tabLayout.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                showBottomSheet(true)
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        setupAppBar()
        setupBottomMenu()
    }

    override fun onPause() {
        super.onPause()
        updateLiveData?.removeObservers(this)
    }

    private fun setPagerAdapter() {
        viewPager.adapter = ArticleFragmentPagerAdapter(this, supportFragmentManager)
    }

    private fun setupAppBar() {

        appbar.addOnOffsetChangedListener({ _, verticalOffset ->
            val scroll = verticalAppbarOffset - verticalOffset
            if (scroll != 0) {
                showBottomSheet(scroll < 0)
            }
            verticalAppbarOffset = verticalOffset
        })
    }

    private fun setupBottomMenu() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            val indexCategory = (0 until bottomNavigation.menu.size())
                    .lastOrNull {
                        bottomNavigation.menu.getItem(it).itemId == menuItem.itemId
                    }
                    ?: 0
            category = ArticleCategory.values()[indexCategory]

            broadcastRefresh()

            //restart hide timer of bottom bar
            showBottomSheet(true)
            true
        }
        bottomNavigation.selectedItemId = R.id.all
        bottomSheetBehavior.isHideable = true
        showBottomSheet(true)
    }

    private fun showBottomSheet(show: Boolean) {
        if (show && bottomSheetShowable) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            countDown.cancel()
            countDown.start()
        } else bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tab_news_menu, menu)

        val refreshItem = menu.findItem(R.id.menu_refresh)

        val actionSearchItem = menu.findItem(R.id.search)
        val searchView = actionSearchItem.actionView as SearchView
        actionSearchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                refreshItem?.isVisible = false
                searchView.requestFocus()
                val params = my_toolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = 0

                bottomSheetShowable = false
                showBottomSheet(false)

                tabLayout.visibility = View.GONE

                viewPager.adapter = MonoFragmentPagerAdapter(supportFragmentManager)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                refreshItem?.isVisible = true
                searchView.clearFocus()
                searchView.setQuery("", false)
                val params = my_toolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL

                bottomSheetShowable = true

                tabLayout.visibility = View.VISIBLE

                setPagerAdapter()

                return true
            }
        })
        searchView.isIconified = false
        searchView.setOnClickListener { Timber.e("on click listener") }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val adapter: MonoFragmentPagerAdapter = viewPager.adapter as MonoFragmentPagerAdapter
                adapter.onNewQuery(newText)
                return true
            }
        })
        searchView.setOnSearchClickListener { refreshItem.isVisible = true }
        searchView.setOnCloseListener {
            actionSearchItem.collapseActionView()
            true
        }
        searchView.queryHint = getString(R.string.search_hint)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_refresh) {

            val vm = ViewModelProviders.of(this).get(TabNewsVM::class.java)
            updateLiveData?.removeObservers(this)
            if (isOnline()) {

                snackbar(getString(R.string.sources_refreshed_start, ArticleSource.values().size))

                updateLiveData = vm.updateAllArticles()
                updateLiveData?.observe(this, Observer<List<Article>> { r ->
                    if (r != null) {
                        snackbar(when {
                            r.size > 1 -> getString(R.string.sources_refreshed_end, r.size)
                            r.size == 1 -> getString(R.string.sources_refreshed_end_one_article)
                            else -> getString(R.string.sources_refreshed_end_no_new_article)
                        })
                    }
                })
                broadcastRefresh()
            } else {
                snackbar(getString(R.string.sources_refreshed_no_internet))
            }
        }
        return true
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    private fun snackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content)!!, message, Snackbar.LENGTH_LONG).show()
    }

    private fun broadcastRefresh() {
        val localIntent = Intent(BaseArticleFragment.INTENT_ACTION_REFRESH_DATA)
        localIntent.putExtra(ArticleFragment.EXTRA_CATEGORY, category.name)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }

    override fun provideCategory(): ArticleCategory = category
}
