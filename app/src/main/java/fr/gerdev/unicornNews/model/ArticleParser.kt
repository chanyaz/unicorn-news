package fr.gerdev.unicornNews.model

import fr.gerdev.unicornNews.extension.toArticleEntry
import fr.gerdev.unicornNews.repository.ArticleRepository
import fr.gerdev.unicornNews.rest.RssService
import me.toptas.rssconverter.RssFeed
import retrofit2.Call
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.*


// ger 09/03/18
class ArticleParser(private var listener: ArticleParseListener,
                    private val rssService: RssService,
                    private val articleRepository: ArticleRepository) : AbstractParser, ParseRunnable.Listener {
    companion object {
        const val MAX_THREADS = 4
        const val EXECUTOR_SHUTDOWN_TIMEOUT: Long = 1 * 1000
        const val READ_RSS_SOURCE_TIMEOUT: Long = 20 * 1000
    }

    private val executor: ExecutorService = Executors.newFixedThreadPool(MAX_THREADS)
    private val restCallables = ConcurrentLinkedQueue<Callable<Any>>()

    override fun parse(sources: List<ArticleSource>) {

        sources.forEach {
            val callable = Executors.callable(ParseRunnable(this, it, rssService))
            restCallables.add(callable)
        }

        try {
            executor.invokeAll(restCallables, READ_RSS_SOURCE_TIMEOUT, TimeUnit.MILLISECONDS)
            listener.onParseFinished(sources.size)
        } catch (e: InterruptedException) {
            shutdownExecutor()
        }
    }

    override fun stopParse() {
        Timber.w("stop article parsing")
        shutdownExecutor()
    }

    override fun onArticleParsed(source: ArticleSource, articles: List<Article>) {
        listener.onParsedAndFiltered(articles.filter { !articleRepository.exists(it) })
    }

    private fun shutdownExecutor() {
        //shutdown executor
        executor.shutdown()
        try {
            executor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
        }
        if (!executor.isShutdown) {
            executor.shutdownNow()
            Timber.e("Force killed executor")
        }
    }
}

class ParseRunnable(private val listener: Listener,
                    private val source: ArticleSource,
                    private val rssService: RssService) : Runnable {


    override fun run() {
        try {
            val call: Call<RssFeed> = rssService.getRss(source.url)

            try {
                val response = call.execute()

                if (response.isSuccessful) {
                    val rssFeed = response.body()
                    val rssItems = rssFeed?.items
                    if (rssItems == null || rssItems.isEmpty()) {
                        Timber.w("RSS parse success but empty data ${source.url}")
                    } else {
                        Timber.i("Rss parse success : ${rssItems.size} articles ${source.url}")
                        listener.onArticleParsed(source, rssItems.map { it.toArticleEntry(source) })
                    }

                } else {
                    Timber.e("RSS server response error : ${source.url} ${response.body()}")
                }
            } catch (e: IOException) {
                Timber.e("Rss Network failure ${source.url}")
            }
        } catch (e: InterruptedException) {
            Timber.w("Rss parsing interrupted : ${source.url}")
        }
    }

    interface Listener {
        fun onArticleParsed(source: ArticleSource, articles: List<Article>)
    }
}