package com.rodev.newsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rodev.newsapp.adapter.ArticleViewHolderAdapter
import com.rodev.newsapp.data.entity.Article
import com.rodev.newsapp.databinding.ActivityMainBinding
import com.rodev.newsapp.viewmodel.ChannelReference
import com.rodev.newsapp.viewmodel.ChannelViewModel

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ChannelViewModel by viewModels()
    private val adapter by lazy { ArticleViewHolderAdapter() }
    private lateinit var binding: ActivityMainBinding

    private var currentSelectedChannel: ChannelReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.data.observe(this) {
            binding.swipeRefreshLayout.isRefreshing = false
            adapter.setArticles(it.articles)
        }

        populateTags(binding.chipGroup)

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            onChipChecked(group.findViewById(checkedId))
        }

        binding.swipeRefreshLayout.setOnRefreshListener(this)

        adapter.onArticleClickListener = ::onArticleClicked
        binding.recyclerView.adapter = adapter

        setSupportActionBar(binding.toolbar)
    }

    private fun onChipChecked(chip: Chip) {
        currentSelectedChannel = chip.tag as ChannelReference

        binding.collapsingToolbar.title = currentSelectedChannel?.name

        binding.swipeRefreshLayout.isRefreshing = true
        onRefresh()
    }

    private fun populateTags(chipGroup: ChipGroup) {
        viewModel.getChannelReferences()
            .map(::channelToChip)
            .forEach(chipGroup::addView)
    }

    private fun channelToChip(channelReference: ChannelReference): Chip {
        val chip = Chip(this)

        chip.text = channelReference.name
        chip.tag = channelReference
        chip.id = ViewCompat.generateViewId()
        chip.isCheckable = true

        return chip
    }

    private fun onArticleClicked(article: Article) {
        openLink(Uri.parse(article.link))
    }

    private fun openLink(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)

        startActivity(intent)
    }

    override fun onRefresh() {
        val tag = currentSelectedChannel?.path
        if (tag != null) {
            viewModel.fetchByChannel(tag)
        } else {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}