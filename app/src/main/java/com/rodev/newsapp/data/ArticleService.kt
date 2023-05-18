package com.rodev.newsapp.data

import androidx.lifecycle.LiveData
import com.rodev.newsapp.data.entity.Channel
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

interface ArticleService {

    fun getChannel(): LiveData<Channel>

    suspend fun fetchByChannel(channelId: String)

    companion object {

        @Volatile
        var articleService: ArticleService? = null

        fun getInstance(): ArticleService {
            if (articleService == null) {
                synchronized(this::class.java) {
                    articleService = initArticleService()
                }
            }

            return articleService!!
        }

        private fun initArticleService(): ArticleService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://habr.com/ru/rss/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

            val api = retrofit.create(RetrofitArticleService.API::class.java)

            return RetrofitArticleService(api)
        }

    }

}