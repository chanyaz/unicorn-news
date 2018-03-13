package fr.gerdev.unicornNews.util

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.webkit.WebView
import timber.log.Timber

// ger 13/03/18
class FabWebView
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr) {

    companion object {
        private const val SCROLL_THRESHOLD = 12
    }

    var floatingActionButton: FloatingActionButton? = null

    fun setFab(floatingActionButton: FloatingActionButton) {
        this.floatingActionButton = floatingActionButton
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        Timber.e("old $oldt / new $t")
        if (t - oldt > SCROLL_THRESHOLD) floatingActionButton?.hide()
        else if (oldt - t > SCROLL_THRESHOLD) floatingActionButton?.show()
    }
}