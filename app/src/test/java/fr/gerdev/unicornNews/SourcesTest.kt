package fr.gerdev.unicornNews

import fr.gerdev.unicornNews.model.ArticleSource
import fr.gerdev.unicornNews.rest.RssService
import me.toptas.rssconverter.RssConverterFactory
import me.toptas.rssconverter.RssFeed
import org.junit.Assert
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SourcesTest {

    private val lock = CountDownLatch(ArticleSource.values().size)

    private var rssService: RssService = Retrofit.Builder()
            .baseUrl("http://www.toPreventRunTimeException.com")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create(RssService::class.java)

    @Test
    fun source_all_respond() {
        var count: Int = ArticleSource.values().size
        val sourceError: MutableList<String> = mutableListOf()
        ArticleSource.values().forEach {
            rssService.getRss(it.url).enqueue(object : Callback<RssFeed> {
                override fun onFailure(call: Call<RssFeed>, t: Throwable?) {
                    sourceError.add(call.request().url().toString())
                    count--
                    lock.countDown()
                }

                override fun onResponse(call: Call<RssFeed>, response: Response<RssFeed>?) {
                    if (response?.body()?.items?.isEmpty() == true) {
                        sourceError.add(call.request().url().toString())
                    }
                    count--
                    lock.countDown()
                }
            })
        }

        lock.await(60, TimeUnit.SECONDS)

        System.out.println("source in error : ${sourceError.size}")
        sourceError.forEach {
            Timber.e(it)
        }
        Assert.assertTrue(sourceError.size == 0)

        if (count != 0) System.out.println("$count request(s) not executed du to unit test timeout")
        Assert.assertTrue(count == 0)
    }
}

