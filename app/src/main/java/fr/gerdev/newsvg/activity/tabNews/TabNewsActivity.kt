package fr.gerdev.newsvg.activity.tabNews

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import fr.gerdev.newsvg.R
import fr.gerdev.newsvg.adapter.ArticleFragmentPagerAdapter
import fr.gerdev.newsvg.fragments.ArticleFragment
import fr.gerdev.newsvg.model.ArticleCategory
import kotlinx.android.synthetic.main.activity_tab_news.*


class TabNewsActivity : AppCompatActivity(), ArticleFragment.Listener, ArticleFragmentPagerAdapter.CategoryProvider {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_news)
        setSupportActionBar(my_toolbar)

        category = ArticleCategory.GENERAL
        bottomSheetBehavior = BottomSheetBehavior.from(bottomNavigation)

        viewPager.adapter = ArticleFragmentPagerAdapter(this, supportFragmentManager)
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

            refresh()

            //restart hide timer of bottom bar
            showBottomSheet(true)
            true
        }
        bottomNavigation.selectedItemId = R.id.all
        bottomSheetBehavior.isHideable = true
        showBottomSheet(true)
    }

    private fun showBottomSheet(show: Boolean) {
        if (show) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            countDown.cancel()
            countDown.start()
        } else bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tab_news_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_refresh) {
            refresh()
        }
        return true
    }

    private fun refresh() {
        val localIntent = Intent(ArticleFragment.INTENT_ACTION_REFRESH)
        localIntent.putExtra(ArticleFragment.EXTRA_CATEGORY, category.name)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }

    override fun provideCategory(): ArticleCategory = category
}
