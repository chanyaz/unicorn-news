package fr.gerdev.newsvg.rest

import me.toptas.rssconverter.RssFeed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


// ger 10/03/18
interface RssService {
    /** @param url RSS feed url
     * @return Retrofit Call
     */
    @GET
    fun getRss(@Url url: String): Call<RssFeed>
}