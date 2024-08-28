package com.example.quicknews.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.adapters.NewsAdapter
import com.example.quicknews.databinding.FragmentSearchBinding
import com.example.quicknews.ui.NewsActivity
import com.example.quicknews.ui.NewsViewModel
import com.example.quicknews.util.Constants
import com.example.quicknews.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.quicknews.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemSearchError: CardView

    private lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        itemSearchError = view.findViewById(R.id.itemSearchError)

        val inflate = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflate.inflate(R.layout.item_error,null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setupSearchRecycler()
        //User click on recycler view item ite direct user to webview.
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articleFragment,bundle)
        }

        //Setup coroutines.
        var job: Job? = null
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                job?.cancel()
                job = CoroutineScope(Dispatchers.Main).launch {
                    delay(SEARCH_NEWS_TIME_DELAY)
                    editable?.let {
                        if (it.toString().isNotEmpty()) {
                            newsViewModel.searchNews(it.toString())
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })


        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->

            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMessage()
                    response?.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            binding.recyclerSearch.setPadding(0,0,0,0)
                        }
                    }

                }

                is Resource.Error<*> ->{
                    hideProgressBar()
                    response.message?.let {message->
                        Toast.makeText(activity,"Sorry error: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }

                is Resource.Loading<*> ->{
                    showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener {
            if(binding.searchEdit.text.toString().isNotEmpty()){
            newsViewModel.searchNews(binding.searchEdit.text.toString())
            }
            else{
                hideErrorMessage()
            }
        }


    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    //Pagination: helps to display small chunk of data at a time.
    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage(){
        itemSearchError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message:String){
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
        isError= true
    }


    val scrollListener = object : RecyclerView.OnScrollListener() {
        //called after scroll is completed.
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            /*
            Pagination involves loading data in chunks (pages) rather than all at once.
            This approach is often used in scenarios where you're fetching data from a remote source,
            like an API, or displaying large datasets in a RecyclerView.
            */

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoError = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoError && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.searchNews(binding.searchEdit.text.toString())//Changes made here
                isScrolling = false
            }


        }

        //called when scroll state is changed.
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupSearchRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

}