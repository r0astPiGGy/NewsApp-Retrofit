package com.rodev.newsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodev.newsapp.data.entity.Article
import com.rodev.newsapp.databinding.ArticleLayoutBinding

typealias OnArticleClickListener = (Article) -> Unit

class ArticleViewHolderAdapter : RecyclerView.Adapter<ArticleViewHolderAdapter.ArticleViewHolder>() {

    private var articles: List<Article> = emptyList()
    var onArticleClickListener: OnArticleClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ArticleLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.onBind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    @SuppressLint("NotifyDataSetChanged")
    fun setArticles(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(
        private val binding: ArticleLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(article: Article) {
            with(binding) {
                articleHeader.text = article.title
                articleCreationDate.text = article.pubDate
                articleDescription.text = article.description
            }

            itemView.setOnClickListener {
                onArticleClickListener?.invoke(article)
            }
        }
    }

}