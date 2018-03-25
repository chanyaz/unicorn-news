package fr.gerdev.unicornNews.model

import android.content.Context
import android.os.AsyncTask
import fr.gerdev.unicornNews.extension.toArticleEntry
import fr.gerdev.unicornNews.repository.ArticleRepository
import fr.gerdev.unicornNews.rest.RssService
import fr.gerdev.unicornNews.util.Prefs
import me.toptas.rssconverter.RssConverterFactory
import me.toptas.rssconverter.RssFeed
import me.toptas.rssconverter.RssItem
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

// ger 09/03/18
abstract class AbstractParser(private var context: Context,
                              private var listener: ArticleParseListener) {

    companion object {
        private const val MAX_SIMULATENOUS_CALL = 4
        private val IMAGE_HTTP_REGEX = """(https?://.*\.(?:png|jpg|gif))""".toRegex()
        val DAY_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yy HH:mm")

        fun parseImage(item: RssItem): String? {
            var imageUrl = item.image
            if (imageUrl == null && item.description != null) {
                imageUrl = IMAGE_HTTP_REGEX.find(item.description)?.groups?.get(0)?.value
            }
            if (imageUrl == null && item.title != null) {
                imageUrl = IMAGE_HTTP_REGEX.find(item.title)?.groups?.get(0)?.value
            }
            return imageUrl
        }
    }

    private var rssService: RssService = Retrofit.Builder()
            .baseUrl("http://www.toPreventRunTimeException.com")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create(RssService::class.java)

    private val articleRepository = ArticleRepository(context)


    private val callsQueue = ConcurrentLinkedQueue<Call<RssFeed>>()
    private val callsProcessing = ConcurrentLinkedQueue<Call<RssFeed>>()
    private val articleParsed = ConcurrentLinkedQueue<Article>()

    private val finished = AtomicBoolean(false)

    abstract fun getSources(): List<ArticleSource>

    fun parse() {

        for (source in getSources()) {
            callsQueue.add(rssService.getRss(source.url))
        }

        (1..MAX_SIMULATENOUS_CALL).forEach {
            proceedNext()
        }
    }

    fun stopParse() {
        // cancel pending requests
        callsQueue.forEach { it.cancel() }
        val queueSize = callsQueue.size

        callsProcessing.forEach { it.cancel() }
        val processingSize = callsProcessing.size

        // stop bdd access properly
        finished.set(true)

        if (queueSize > 0 || processingSize > 0) Timber.e("canceling ${callsQueue.size} calls, ${callsProcessing.size} beeing performed")
    }

    private fun proceedNext() {
        if (callsQueue.size == 0 && callsProcessing.size == 0) finish()
        else if (callsQueue.size != 0 && callsProcessing.size < MAX_SIMULATENOUS_CALL) {
            val newCall = callsQueue.poll()
            callsProcessing.offer(newCall)
            newCall.enqueue(
                    object : Callback<RssFeed> {
                        override fun onFailure(call: Call<RssFeed>?, t: Throwable?) {
                            next(newCall)
                        }

                        override fun onResponse(call: Call<RssFeed>?, response: Response<RssFeed>?) {
                            val body = response?.body()

                            if (response?.isSuccessful == true) {
                                val url = call?.request()?.url().toString()
                                if (!url.isEmpty()) Prefs.updateRefreshTime(context, ArticleSource.byUrl(url)!!)
                                if (body != null &&
                                        body.items.size != 0) {

                                    body.items.forEach {

                                        val url = newCall.request().url().toString()
                                        articleParsed.offer(it.toArticleEntry(
                                                ArticleSource.byUrl(url)!!

                                        ))
                                    }

                                    Timber.w("RSS parsed ${body.items.size} items")
                                } else {
                                    Timber.w("RSS server response not successfull, skipping")
                                }
                            }
                            next(newCall)
                        }
                    })
        }
    }

    private fun next(finishedCall: Call<RssFeed>) {
        callsProcessing.remove(finishedCall)
        proceedNext()
    }


    private fun finish() {
        AsyncTask.execute {
            if (articleParsed.size > 0) {
                val iterator = articleParsed.iterator()
                val start = System.currentTimeMillis()
                Timber.e("start filtering ${articleParsed.size} articles")
                while (iterator.hasNext()) {

                    //stop filtering if finished, checking article exist if a long task
                    if (!finished.get()) {

                        val article = iterator.next()

                        //remove if exists
                        if (articleRepository.exists(article)) iterator.remove()
                    } else break
                }
                Timber.e("filtering articles took ${System.currentTimeMillis() - start} ms")

                //prevent inserting
                if (!finished.get()) {

                    if (articleParsed.isNotEmpty()) {
                        articleRepository.insertAll(articleParsed.toList())
                        Timber.e("inserted ${articleParsed.size} articles")
                    }
                }
            }
            finished.set(true)
            listener.onParsed(articleParsed.toList())
        }
    }
}