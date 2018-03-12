package fr.gerdev.newsvg.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fr.gerdev.newsvg.fragments.ArticleFragment
import fr.gerdev.newsvg.model.ArticleCategory
import fr.gerdev.newsvg.model.ArticleDevice


// ger 27/02/18
class ArticleFragmentPagerAdapter(private val listener: CategoryProvider, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return ArticleFragment.newInstance(listener.provideCategory(), ArticleDevice.values()[position])
    }

    override fun getCount(): Int {
        return ArticleDevice.values().size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return ArticleDevice.values()[position].name
    }

    interface CategoryProvider {
        fun provideCategory(): ArticleCategory
    }
}