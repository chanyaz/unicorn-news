package fr.gerdev.newsvg.activity.detailNews

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebViewClient
import fr.gerdev.newsvg.R
import kotlinx.android.synthetic.main.activity_detail_news.*
import android.webkit.WebView
import kotlinx.android.synthetic.main.loadingindicator.*


class DetailNewsActivity : AppCompatActivity() {

    companion object {
        const val URL_KEY = "URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreen()
        setContentView(R.layout.activity_detail_news)
        loadArticle()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadArticle() {
        val url = intent.getStringExtra(URL_KEY)
        articleWebView.webViewClient = object : WebViewClient() {

            //not always triggered
            override fun onPageFinished(view: WebView, url: String) {
                progress.visibility=View.GONE
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                progress.visibility=View.GONE
            }
        }

        val webSettings = articleWebView.settings
        webSettings.javaScriptEnabled = true
        articleWebView.loadUrl(url)
    }

    private fun setFullscreen() {
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //Remove notification bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
