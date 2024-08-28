package com.example.quicknews.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.adapters.NewsAdapter
import com.example.quicknews.databinding.FragmentHeadlinesBinding
import com.example.quicknews.ui.NewsActivity
import com.example.quicknews.ui.NewsViewModel
import com.example.quicknews.util.Constants
import com.example.quicknews.util.Resource

//Setup recycler view and all the articles fetch from API.
//Progress bar and error message.

class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadlinesError: CardView //In any internet error this UI component will be use.

    private lateinit var binding: FragmentHeadlinesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentHeadlinesBinding.bind(view)

        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError)

        val inflate = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflate.inflate(R.layout.item_error,null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setupHeadlinesRecycler()

        //User click on recycler view item ite direct user to webview.
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_headlinesFragment_to_articleFragment,bundle)
        }

        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response ->

            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMessage()
                    response?.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.headlinesPage == totalPages
                        if(isLastPage){
                            binding.recyclerHeadlines.setPadding(0,0,0,0)
                        }
                    }

                }

                is Resource.Error<*> ->{
                    hideProgressBar()
                    response.message?.let {message->
                        Toast.makeText(activity,"Sorry error: $message",Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }

                is Resource.Loading<*> ->{
                    showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener {
            newsViewModel.getHeadlines("headlines")
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
        itemHeadlinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message:String){
        itemHeadlinesError.visibility = View.VISIBLE
        errorText.text = message
        isError= true
    }

    val scrollListener = object :RecyclerView.OnScrollListener() {
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
                newsViewModel.getHeadlines("headlines")
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
        private fun setupHeadlinesRecycler(){
            newsAdapter = NewsAdapter()
            binding.recyclerHeadlines.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@HeadlinesFragment.scrollListener)
            }
        }

    }
