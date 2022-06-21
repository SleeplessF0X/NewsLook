package com.example.newslook.news.ui.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newslook.core.ui.ViewState
import com.example.newslook.core.ui.base.BaseActivity
import com.example.newslook.core.utils.observeNotNull
import com.example.newslook.databinding.ActivityMainBinding
import com.example.newslook.news.ui.adapter.NewsArticlesAdapter
import com.example.newslook.news.ui.viewmodel.NewsArticleViewModel


class NewsActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private val newsArticleViewModel: NewsArticleViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: NewsArticlesAdapter

    val listThemes: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        // Setting up Spinner and adapter
        listThemes.add("technology")
        listThemes.add("Apple")
        listThemes.add("Android")
        binding.listThemes.onItemSelectedListener
        // Create an ArrayAdapter using a simple spinner layout and languages array
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, listThemes)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.listThemes.adapter = arrayAdapter
        // Setting up RecyclerView and adapter
        binding.newsList.setEmptyView(binding.emptyLayout.emptyView)
        binding.newsList.setProgressView(binding.progressLayout.progressView)
        adapter = NewsArticlesAdapter { position ->
            onItemClick(position)
        }
        binding.newsList.adapter = adapter
        binding.newsList.layoutManager = LinearLayoutManager(this)
        // Update the UI on state change
        newsArticleViewModel.getNewsArticles().observeNotNull(this) { state ->
            when (state) {
                is ViewState.Success -> adapter.submitList(state.data)
                is ViewState.Loading -> binding.newsList.showLoading()
            }
        }
    }
    private fun onItemClick(position: Int) {
        newsArticleViewModel.getNewsArticles().observeNotNull(this) { state ->
            when (state) {
                is ViewState.Success -> {
                    val url: String? = state.data[position].url
                    val newsIntent = Intent(this, WebViewActivity::class.java)
                    newsIntent.putExtra("url", url)
                    startActivity(newsIntent)
                }
                else -> {}
            }
        }
    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        newsArticleViewModel.saveToDataStore(listThemes[position])
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}