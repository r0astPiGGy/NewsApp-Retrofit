package com.rodev.newsapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rodev.newsapp.data.entity.Article
import com.rodev.newsapp.data.entity.Channel
import org.jsoup.Jsoup
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.http.GET
import retrofit2.http.Path

class RetrofitArticleService(
    private val api: API
) : ArticleService {

    private val mutableLiveData = MutableLiveData<Channel>()

    override fun getChannel(): LiveData<Channel> = mutableLiveData

    override suspend fun fetchByChannel(channelId: String) {
        val wrapper = try {
            api.getChannel(channelId)
        } catch (e: Exception) {
            throw e
        }
        val channelEntity = wrapper.channel ?: return

        val articles = ArrayList<Article>()
        channelEntity.articles?.forEach {
            articles.add(Article(
                title = it.title!!,
                link = it.link!!,
                description = parseDescription(it.description!!),
                pubDate = it.pubDate!!
            ))
        }

        val channel = Channel(
            channelTitle = channelEntity.title!!,
            articles = articles
        )

        mutableLiveData.postValue(channel)
    }

    private fun parseDescription(desc: String): String {
        return Jsoup.parse(desc).select("p").eachText().joinToString { it }
    }

    interface API {

        @GET("{channelId}")
        suspend fun getChannel(@Path("channelId") channelId: String): ChannelWrapperEntity

    }

    @Root(name = "item", strict = false)
    data class ArticleEntity @JvmOverloads constructor(
        @field:Element(name = "title")
        var title: String? = null,

        @field:Element(name = "description")
        var description: String? = null,

        @field:Element(name = "link")
        var link: String? = null,

        @field:Element(name = "pubDate")
        var pubDate: String? = null
    )

    @Root(name = "channel", strict = false)
    data class ChannelEntity @JvmOverloads constructor(
        @field:Element(name = "title")
        var title: String? = null,

        @field: ElementList(name = "item", inline = true)
        var articles: List<ArticleEntity>? = null,
    )

    @Root(name = "rss", strict = false)
    data class ChannelWrapperEntity @JvmOverloads constructor(
        @field: Element(name = "channel")
        var channel: ChannelEntity? = null
    )


}