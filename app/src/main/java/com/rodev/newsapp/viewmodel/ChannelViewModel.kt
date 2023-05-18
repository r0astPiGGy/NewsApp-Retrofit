package com.rodev.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodev.newsapp.data.ArticleService
import com.rodev.newsapp.data.entity.Channel
import kotlinx.coroutines.launch

class ChannelViewModel : ViewModel() {

    private val articleService = ArticleService.getInstance()
    val data: LiveData<Channel> = articleService.getChannel()

    fun fetchByChannel(channel: String) {
        viewModelScope.launch {
            articleService.fetchByChannel(channel)
        }
    }

    fun getChannelReferences(): List<ChannelReference> {
        return listOf(
            ChannelReference(
                path = "flows/admin",
                name = "Администрирование"
            ),
            ChannelReference(
                path = "flows/design",
                name = "Дизайн"
            ),
            ChannelReference(
                path = "flows/develop",
                name = "Разработка"
            ),
            ChannelReference(
                path = "interesting",
                name = "Разное"
            )
        )
    }

}