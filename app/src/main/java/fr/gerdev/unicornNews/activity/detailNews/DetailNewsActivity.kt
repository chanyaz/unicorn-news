package fr.gerdev.unicornNews.activity.detailNews

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import fr.gerdev.unicornNews.R
import kotlinx.android.synthetic.main.activity_detail_news.*
import timber.log.Timber


class DetailNewsActivity : AppCompatActivity() {

    companion object {
        const val URL_KEY = "URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreen()
        setContentView(R.layout.activity_detail_news)
        fab.setOnClickListener({
            if (articleWebView.canGoBack()) articleWebView.goBack()
            else finish()
        })
        fab.hide()
        loadArticle()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadArticle() {
        val articleUrl = intent.getStringExtra(URL_KEY)
        articleWebView.webViewClient = object : WebViewClient() {

            //not always triggered ?
            override fun onPageFinished(view: WebView, url: String) {
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                Timber.i(url)
                if (url == articleUrl) {
                    progress.visibility = View.GONE
                    fab.show()
                }
            }
        }

        val webSettings = articleWebView.settings
        webSettings.javaScriptEnabled = true
        articleWebView.setFab(fab)
        articleWebView.loadUrl(articleUrl)
    }

    private fun setFullscreen() {
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //Remove notification bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
