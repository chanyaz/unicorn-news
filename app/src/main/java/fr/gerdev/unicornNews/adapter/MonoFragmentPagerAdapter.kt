package fr.gerdev.unicornNews.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gerdev.unicornNews.fragments.SearchedArticleFragment


// ger 27/02/18
class MonoFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    val fragment = SearchedArticleFragment.newInstance()

    override fun getItem(position: Int): Fragment = fragment

    override fun getCount(): Int = 1

    override fun getPageTitle(position: Int): CharSequence? = ""

    fun onNewQuery(query: String) {
        fragment.onNewQuery(query)
    }
}