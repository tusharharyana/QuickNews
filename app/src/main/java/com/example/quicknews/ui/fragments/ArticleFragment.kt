package com.example.quicknews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.quicknews.R
import com.example.quicknews.databinding.FragmentArticleBinding
import com.example.quicknews.ui.NewsActivity
import com.example.quicknews.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var newsViewModel: NewsViewModel //API and responses are only in ViewModel.
    private val args: ArticleFragmentArgs by navArgs()//args: Accessing arguments passed to the fragments.
    private lateinit var binding: FragmentArticleBinding // Binding for fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentArticleBinding.bind(view)

        //Initialize newsViewModel
        newsViewModel = (activity as NewsActivity).newsViewModel
        var article = args.article //Name given to the argument

        binding.webView.apply {
            webViewClient = WebViewClient()//Responsible to handle various events in webView.
            //Like when a new url is loaded.

            article.url?.let{
                loadUrl(it)
            }
        }

        //Add favourite article to database.
        binding.fab.setOnClickListener {
            newsViewModel.addToFavourites(article)
            Snackbar.make(view, "Added to favourites", Snackbar.LENGTH_SHORT).show()//Like Toast
        }
    }
}